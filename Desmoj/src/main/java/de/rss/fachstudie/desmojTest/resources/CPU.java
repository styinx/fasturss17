package de.rss.fachstudie.desmojTest.resources;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.Operation;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.*;

import java.util.ArrayList;
import java.util.List;

public class CPU extends Event<Thread> {
    enum CB_STATE  {OPEN, HALFOPEN, CLOSED};

    private MainModelClass model;
    private int id = -1;
    private int capacity = 0;
    private double robinTime = 10;
    private double cycleTime = 0;
    private double lastThreadEntry;
    private double smallestThread = 0.0;
    private List<Double> cpuUsageMean;

    private Queue<Thread> activeThreads;
    private Queue<Thread> waitingThreads;
    private Queue<Thread> existingThreads;
    private boolean hasThreadPool = false;
    private int threadPoolSize = 0;
    private boolean hasThreadQueue = false;
    private int threadQueueSize = 0;

    private boolean hasCircuitBreaker = false;
    private CB_STATE cbState = CB_STATE.OPEN;
    private boolean trialSent = false;
    private Thread trialThread = null;
    private double circuitBreakerTriggered = 0;
    private double retryTime = 1;
    private double responseTimelimit = 10;
    private double maxResponseTime = 0;

    public CPU (Model owner, String name, boolean showInTrace, int id, int capacity) {
        super(owner, name, showInTrace);

        model = (MainModelClass) owner;
        this.id = id;
        this.capacity = capacity;
        lastThreadEntry = 0;
        cpuUsageMean = new ArrayList<>();
        existingThreads = new Queue<Thread>(owner, "", false, false);

        if(model.allMicroservices.get(id).hasPattern("Thread Pool")) {
            threadPoolSize = model.allMicroservices.get(id).getPattern("Thread Pool");
            activeThreads = new Queue<>(owner, "", QueueBased.FIFO, 0, false, false);
            hasThreadPool = true;
        } else {
            activeThreads = new Queue<>(owner, "", false, false);
        }

        if(model.allMicroservices.get(id).hasPattern("Thread Queue")) {
            threadQueueSize = model.allMicroservices.get(id).getPattern("Thread Queue");
            waitingThreads = new Queue<>(owner, "", QueueBased.FIFO, threadQueueSize, false, false);
            hasThreadQueue = true;
        }
    }

    @Override
    public void eventRoutine(Thread threadToEnd) throws SuspendExecution {
        for(Thread thread : activeThreads) {
            thread.subtractDemand((int) smallestThread);
            if(thread.getDemand() == 0 || thread == threadToEnd) {
                thread.scheduleEndEvent();
                activeThreads.remove(thread);
            }
        }
        calculateMin();
    }

    public void addThread(Thread thread, Operation operation) {
        // update all threads that are currently in the active queue
        int robins = (int) Math.round((model.presentTime().getTimeAsDouble() - lastThreadEntry) * 1000 / robinTime);
        for (int i = 0; i < robins; i++) {
            if (activeThreads.size() > 0) {
                Thread activeThread = activeThreads.get(i % activeThreads.size());
                if (activeThread.getDemand() == 0) {
                    activeThread.scheduleEndEvent();
                    activeThreads.remove(activeThread);
                } else {
                    activeThread.subtractDemand(robinTime);
                }
            }
        }

        lastThreadEntry = this.model.presentTime().getTimeAsDouble();
        hasCircuitBreaker = operation.hasPattern("Circuit Breaker");

        for(Thread activeThread : activeThreads) {
            if(activeThread.getCreationTime() > circuitBreakerTriggered) {
                if(model.presentTime().getTimeAsDouble() - activeThread.getCreationTime() > maxResponseTime) {
                    maxResponseTime = model.presentTime().getTimeAsDouble() - activeThread.getCreationTime();
                }
            }
        }


        if(maxResponseTime > responseTimelimit && cbState == CB_STATE.OPEN) {
            cbState = CB_STATE.CLOSED;
            maxResponseTime = 0;
            circuitBreakerTriggered = model.presentTime().getTimeAsDouble();
        }

        model.log(circuitBreakerTriggered + "");

        if(cbState == CB_STATE.CLOSED && model.presentTime().getTimeAsDouble() > (circuitBreakerTriggered  + retryTime)) {
            //cbState = CB_STATE.OPEN;
            cbState = CB_STATE.HALFOPEN;
        }

        if(cbState == CB_STATE.HALFOPEN && trialThread != null && trialThread.getDemand() == 0) {
            trialSent = false;
            model.log("trail thread fertig: " + (model.presentTime().getTimeAsDouble() - trialThread.getCreationTime()));
            if(model.presentTime().getTimeAsDouble() - trialThread.getCreationTime() < responseTimelimit) {
                //model.log("trial thread ist fertig und liegt im limit");
                cbState = CB_STATE.OPEN;
            } else {
                cbState = CB_STATE.HALFOPEN;
                //model.log("trial thread ist fertig und liegt nicht im limit");
            }
        }

        if(!hasCircuitBreaker || (cbState == CB_STATE.OPEN || (cbState == CB_STATE.HALFOPEN && !trialSent) )) {
            //model.log("first");
            if(!trialSent && cbState == CB_STATE.HALFOPEN) {
                trialThread = thread;
                model.log(trialThread + " trial thread was started");
                trialSent = true;
            }

            // check for patterns
            if (!hasThreadPool || activeThreads.size() < threadPoolSize) {
                // cpu has no thread pool, or the size of the thread pool is big enough
                activeThreads.insert(thread);
            } else {
                if (hasThreadQueue) {
                    if (waitingThreads.size() < threadQueueSize) {

                        // a thread queue exists and the size is big enough
                        waitingThreads.insert(thread);
                    } else {

                        // thread waiting queue is too big, send default response
                        thread.scheduleEndEvent();

                        // statistics
                        double last = 0;
                        List<Double> values = model.threadQueueStatistics.get(thread.getId()).get(thread.getSid()).getDataValues();
                        if (values != null)
                            last = values.get(values.size() - 1);
                        model.threadQueueStatistics.get(thread.getId()).get(thread.getSid()).update(last + 1);
                    }
                } else {
                    // thread pool is too big, send default response
                    thread.scheduleEndEvent();

                    // statistics
                    double last = 0;
                    List<Double> values = model.threadPoolStatistics.get(thread.getId()).get(thread.getSid()).getDataValues();
                    if (values != null)
                        last = values.get(values.size() - 1);
                    model.threadPoolStatistics.get(thread.getId()).get(thread.getSid()).update(last + 1);
                }
            }
        } else {
            //model.log("second");
            thread.scheduleEndEvent();
        }

        // Shift from waiting queue to the active queue
        if (hasThreadQueue) {
            int freeSlots = threadPoolSize - activeThreads.size();
            for (int index = 0; index < freeSlots; ++index) {
                activeThreads.insert(waitingThreads.first());
                waitingThreads.removeFirst();
            }
        }
        calculateMin();
    }

    private void calculateMin() {
        Thread smallestThreadInstance = null;
        if (activeThreads.size() > 0) {
            smallestThreadInstance = activeThreads.get(0);
            smallestThread = Double.POSITIVE_INFINITY;
            for (Thread t : activeThreads) {
                if (t.getDemand() < smallestThread) {
                    smallestThread = t.getDemand();
                    smallestThreadInstance = t;
                }
            }
        }

        // schedule to time when smallest thread is done
        if(!activeThreads.isEmpty()) {
            cycleTime = (activeThreads.size() * smallestThread) / capacity;

            if(isScheduled()) {
                reSchedule(new TimeInstant(cycleTime + model.presentTime().getTimeAsDouble(), model.getTimeUnit()));
            } else {
                schedule(smallestThreadInstance, new TimeInstant(cycleTime + model.presentTime().getTimeAsDouble(), model.getTimeUnit()));
            }
        }
    }

    public void releaseUnfinishedThreads() {
        for (int thread = activeThreads.size() - 1; thread >= 0; thread--) {
            activeThreads.get(thread).scheduleEndEvent();
        }
    }

    public Queue<Thread> getExistingThreads() {
        return existingThreads;
    }

    public void addExistingThread(Thread thread) {
        existingThreads.insert(thread);
    }

    public void removeExistingThread(Thread thread) {
        existingThreads.remove(thread);
    }

    public Queue<Thread> getActiveThreads() {
        return activeThreads;
    }

    public int getCapacity() {
        return capacity;
    }

    public void collectUsage() {
        cpuUsageMean.add(getUsage());
    }

    public double getMeanUsage(int values) {
        double collected = 0;
        double usage = 0;

        for (int i = cpuUsageMean.size() - 1; i > 0 && values > 0; --i) {
            usage += cpuUsageMean.get(i);
            collected++;
            values--;
        }
        return usage / collected;
    }

    public double getUsage() {
        double timeSinceLastAdd = model.presentTime().getTimeAsDouble() - lastThreadEntry;
        double availPower = capacity * timeSinceLastAdd;
        if (activeThreads.size() > 0)
            return 1.0;
        else
            return 0.0;
    }
}
