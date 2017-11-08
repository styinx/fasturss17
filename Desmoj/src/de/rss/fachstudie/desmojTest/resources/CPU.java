package de.rss.fachstudie.desmojTest.resources;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.*;

import java.util.ArrayList;
import java.util.List;
import desmoj.core.simulator.Queue;

public class CPU extends Event {
    private MainModelClass model;
    private int capacity = 0;
    private int robinTime = 10;
    private int cycleTime = 0;
    private int doneWork = 0;
    private double lastThreadEntry;
    private Queue<Thread> activeThreads;
    private Queue<Thread> waitingThreads;
    private boolean hasThreadPool = false;
    private int threadPoolSize = 0;
    private boolean hasThreadQueue = false;
    private int threadQueueSize = 0;

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
        } else {
            waitingThreads = new Queue<>(owner, "", QueueBased.FIFO, 0, false, false);
        }
    }

    public void eventRoutine(Entity entity) throws SuspendExecution {
        for(Thread thread : activeThreads) {
            thread.subtractDemand(cycleTime);
            doneWork += cycleTime;
            if(doneWork > capacity)
                doneWork -= capacity;
            if(thread.getDemand() == 0) {
                thread.scheduleEndEvent();
                activeThreads.remove(thread);
            }
        }
        calculateMin();
    }

    public void addThread(Thread thread) {

        if(lastThreadEntry != 0) {
            int robins = (int) Math.round((model.presentTime().getTimeAsDouble() - lastThreadEntry) / robinTime);
            for (int i = 0; i < robins; i++) {
                if (activeThreads.size() > 0) {
                    if (activeThreads.get(i % activeThreads.size()).getDemand() == 0) {
                        activeThreads.get(i % activeThreads.size()).scheduleEndEvent();
                        activeThreads.remove(activeThreads.get(i % activeThreads.size()));
                    } else {
                        activeThreads.get(i % activeThreads.size()).subtractDemand(robinTime);
                        doneWork += robinTime;
                        if (doneWork > capacity)
                            doneWork -= capacity;
                    }
                }
            }
        } else {
            lastThreadEntry = this.model.presentTime().getTimeAsDouble();
        }

        // if thread pool patterns exists check size of active queue
        if(!hasThreadPool || activeThreads.size() < threadPoolSize) {

            // cpu has no thread pool, or the size of the thread pool is big enough
            activeThreads.insert(thread);
        } else {
            // if thread queue pattern exists check the size of waiting queue
            if(hasThreadQueue && waitingThreads.size() < threadQueueSize) {

                // a thread queue exists and the size is big enough
                activeThreads.insert(thread);
            } else {
                // thread waiting queue is too big, thread will not be added to cpu,
                // so the depending thread will be released
                thread.scheduleEndEvent();
            }
        }

        calculateMin();
    }

    private void calculateMin() {
        double smallestThread = Double.POSITIVE_INFINITY;
        for(Thread t : activeThreads) {
            if(t.getDemand() < smallestThread) {
                smallestThread = t.getDemand();
            }
        }

        cycleTime = activeThreads.size() * (int)(smallestThread / robinTime);

        if(!activeThreads.isEmpty()) {
            if(isScheduled()) {
                reSchedule(new TimeSpan(cycleTime, model.getTimeUnit()));
            } else {
                reSchedule(new TimeSpan(cycleTime, model.getTimeUnit()));
            }
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public double getUsage() {
        return ((float)doneWork / (float)capacity);
    }
}
