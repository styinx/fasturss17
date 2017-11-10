package de.rss.fachstudie.desmojTest.resources;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.*;

import java.util.ArrayList;
import java.util.List;
import desmoj.core.simulator.Queue;

public class CPU extends Event {
    private MainModelClass model;
    private int capacity = 0;
    private int robinTime = 10;
    private double cycleTime = 0;
    private int doneWork = 0;
    private double lastThreadEntry;
    private Queue<Thread> activeThreads;
    private Queue<Thread> waitingThreads;
    private boolean hasThreadPool = false;
    private int threadPoolSize = 0;
    private boolean hasThreadQueue = false;
    private int threadQueueSize = 0;
    private double smallestThread = 0.0;

    public CPU (Model owner, String name, boolean showInTrace, int id, int capacity) {
        super(owner, name, showInTrace);

        model = (MainModelClass) owner;
        this.capacity = capacity;
        lastThreadEntry = 0;

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

    public int getActiveThreads(){
        return activeThreads.size();
    }

    public void eventRoutine(Entity entity) throws SuspendExecution {
        System.out.println("EVENTROUTINE");
        for(Thread thread : activeThreads) {
            thread.subtractDemand((int) smallestThread);
            doneWork += cycleTime;
            if(doneWork > capacity)
                doneWork -= capacity;
            if(thread.getDemand() == 0) {

                thread.scheduleEndEvent();
                System.out.println(model.presentTime().getTimeAsDouble());
                System.out.println(activeThreads.size() + " active thread size");
                activeThreads.remove(thread);
                System.out.println(activeThreads.size() + " active thread size");
            }
        }
        calculateMin();
    }

    public void addThread(Thread thread) {
        // update all threads that are currently in the active queue
        int robins = (int) Math.round((model.presentTime().getTimeAsDouble() - lastThreadEntry) / robinTime);
        for (int i = 0; i < robins; i++) {
            if (activeThreads.size() > 0) {
                System.out.println(activeThreads.size() + " ACTIVE THREADS");
                if (activeThreads.get(i % activeThreads.size()).getDemand() == 0) {
                    System.out.println(activeThreads.get(i % activeThreads.size()).getDemand() + " remaining demand should be 0");
                    System.out.println(model.presentTime().getTimeAsDouble() + " time");
                    activeThreads.get(i % activeThreads.size()).scheduleEndEvent();
                    activeThreads.remove(activeThreads.get(i % activeThreads.size()));
                } else {
                    System.out.println(activeThreads.get(i % activeThreads.size()).getDemand() + " remaining demand");
                    System.out.println(model.presentTime().getTimeAsDouble() +" time");
                    activeThreads.get(i % activeThreads.size()).subtractDemand(robinTime);
                    doneWork += robinTime;
                    if (doneWork > capacity)
                        doneWork -= capacity;
                }
            }
            System.out.println();
        }

        lastThreadEntry = this.model.presentTime().getTimeAsDouble();

        // check for patterns and add the current thread to the queue or
        // send a default response to the depending service
        if(!hasThreadPool || activeThreads.size() < threadPoolSize) {

            // cpu has no thread pool, or the size of the thread pool is big enough
            activeThreads.insert(thread);
        } else {

            // if thread queue pattern exists check the size of waiting queue
            if(hasThreadQueue) {
                if(waitingThreads.size() < threadQueueSize) {

                    // a thread queue exists and the size is big enough
                    waitingThreads.insert(thread);
                } else {

                    // thread waiting queue is too big,
                    thread.scheduleEndEvent();
                    // statistics
                    double last = 0;
                    List<Double> values = model.threadQueueStatistics.get(thread.getId()).get(thread.getSid()).getDataValues();
                    if(values != null)
                        last = values.get(values.size() - 1);
                    model.threadQueueStatistics.get(thread.getId()).get(thread.getSid()).update(last + 1);
                }
            } else {

                // thread pool is too big
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
        int freeSlots = threadPoolSize - activeThreads.size();
        for(int index = 0; index < freeSlots; ++index) {
            activeThreads.insert(waitingThreads.first());
            waitingThreads.removeFirst();
        }

        calculateMin();

    }

    private void calculateMin() {
        smallestThread = Double.POSITIVE_INFINITY;
        for(Thread t : activeThreads) {
            if(t.getDemand() < smallestThread) {
                smallestThread = t.getDemand();
            }
        }

        //TODO capacity lead to 100 or 0% cpu
        cycleTime = (activeThreads.size() * smallestThread)/ capacity ;

        System.out.println(cycleTime +" cycle");

        if(!activeThreads.isEmpty()) {
            if(isScheduled()) {
                reSchedule(new TimeSpan(cycleTime, model.getTimeUnit()));
            } else {
                System.out.println("Schedule to next time event " + cycleTime);
                schedule(new MessageObject(model, "",false), new TimeSpan(cycleTime, model.getTimeUnit()));
            }
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public double getUsage() {
        if(activeThreads.size() > 0){
            return 1.0;
        } else {
            return 0;
        }
    }
}
