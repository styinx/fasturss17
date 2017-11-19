package de.rss.fachstudie.desmojTest.resources;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.*;

import java.util.List;

public class CPU extends Event {
    private MainModelClass model;
    private int id = -1;
    private int capacity = 0;
    private double robinTime = 10;
    private double cycleTime = 0;
    private double doneWork = 0;
    private double lastThreadEntry;
    private double smallestThread = 0.0;

    private Queue<Thread> activeThreads;
    private Queue<Thread> waitingThreads;
    private Queue<Thread> existingThreads;
    private boolean hasThreadPool = false;
    private int threadPoolSize = 0;
    private boolean hasThreadQueue = false;
    private int threadQueueSize = 0;


    public CPU (Model owner, String name, boolean showInTrace, int id, int capacity) {
        super(owner, name, showInTrace);

        model = (MainModelClass) owner;
        this.id = id;
        this.capacity = capacity;
        lastThreadEntry = 0;
        existingThreads = new Queue<Thread>(owner, "", false, false);

        if(model.allMicroservices.get(id).hasPattern("Thread Pool")) {
            threadPoolSize = model.allMicroservices.get(id).getPattern("Thread Pool");
            activeThreads = new Queue<>(owner, "", QueueBased.FIFO, threadPoolSize, false, false);
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

    public void eventRoutine(Entity entity) throws SuspendExecution {
        for(Thread thread : activeThreads) {
            thread.subtractDemand((int) smallestThread);
            doneWork += smallestThread;
            if(thread.getDemand() == 0) {
                thread.scheduleEndEvent();
                activeThreads.remove(thread);
            }
        }
        calculateMin();
    }

    public void addThread(Thread thread) {
        doneWork = 0;

        // update all threads that are currently in the active queue
        int robins = (int) Math.round((model.presentTime().getTimeAsDouble() - lastThreadEntry) / robinTime);
        for (int i = 0; i < robins; i++) {
            if (activeThreads.size() > 0) {
                if (activeThreads.get(i % activeThreads.size()).getDemand() == 0) {
                    activeThreads.get(i % activeThreads.size()).scheduleEndEvent();
                    activeThreads.remove(activeThreads.get(i % activeThreads.size()));
                } else {
                    activeThreads.get(i % activeThreads.size()).subtractDemand(robinTime);
                }
            }
        }

        lastThreadEntry = this.model.presentTime().getTimeAsDouble();
        doneWork = robins * robinTime;

        // check for patterns
        if(!hasThreadPool || existingThreads.size() < threadPoolSize) {

            // cpu has no thread pool, or the size of the thread pool is big enough
            activeThreads.insert(thread);
        } else {

            if(hasThreadQueue) {
                if(waitingThreads.size() < threadQueueSize) {

                    // a thread queue exists and the size is big enough
                    waitingThreads.insert(thread);
                } else {

                    // thread waiting queue is too big, send default response
                    thread.scheduleEndEvent();

                    // statistics
                    double last = 0;
                    List<Double> values = model.threadQueueStatistics.get(thread.getId()).get(thread.getSid()).getDataValues();
                    if(values != null)
                        last = values.get(values.size() - 1);
                    model.threadQueueStatistics.get(thread.getId()).get(thread.getSid()).update(last + 1);
                }
            } else {

                // thread pool is too big, send default response
                thread.scheduleEndEvent();

                // statistics
                double last = 0;
                List<Double> values = model.threadPoolStatistics.get(thread.getId()).get(thread.getSid()).getDataValues();
                if(values != null)
                    last = values.get(values.size() - 1);
                model.threadPoolStatistics.get(thread.getId()).get(thread.getSid()).update(last + 1);
            }
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
        if (activeThreads.size() > 0) {
            smallestThread = Double.POSITIVE_INFINITY;
            for (Thread t : activeThreads) {
                if (t.getDemand() < smallestThread) {
                    smallestThread = t.getDemand();
                }
            }
        }

        cycleTime = (activeThreads.size() * smallestThread) / capacity;

        // schedule to time when smallest thread is done
        if(!activeThreads.isEmpty()) {
            if(isScheduled()) {
                reSchedule(new TimeSpan(cycleTime, model.getTimeUnit()));
            } else {
                schedule(new MessageObject(model, "",false), new TimeSpan(cycleTime, model.getTimeUnit()));
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

    public void removeExisitngThread(Thread thread) {
        existingThreads.remove(thread);
    }

    public Queue<Thread> getActiveThreads() {
        return activeThreads;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getUsage() {
        double timeSinceLastAdd = model.presentTime().getTimeAsDouble() - lastThreadEntry;
        double availPower = capacity * timeSinceLastAdd;
        if (activeThreads.size() > 0)
            return 1.0;
        else
            return 0.0;
//        if(doneWork > 0 && availPower > 0)
//            return (doneWork / availPower);
//        else
//            return 0;
    }
}
