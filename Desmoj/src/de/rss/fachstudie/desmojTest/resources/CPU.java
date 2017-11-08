package de.rss.fachstudie.desmojTest.resources;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import de.rss.fachstudie.desmojTest.resources.Thread;
import desmoj.core.simulator.*;

import java.util.ArrayList;
import java.util.List;

public class CPU extends Event {
    private MainModelClass model;
    private int capacity = 0;
    private int robinTime = 10;
    private int cycleTime = 0;
    private int doneWork = 0;
    private double lastThreadEntry;
    private List<Thread> queue;

    public CPU (Model owner, String name, boolean showInTrace, int capacity) {
        super(owner, name, showInTrace);

        model = (MainModelClass) owner;
        queue = new ArrayList<>();
        this.capacity = capacity;
        lastThreadEntry = 0;
    }

    public void eventRoutine(Entity entity) throws SuspendExecution {
        for(Thread thread : queue) {
            thread.subtractDemand(cycleTime);
            doneWork += cycleTime;
            if(doneWork > capacity)
                doneWork -= capacity;
            if(thread.getDemand() == 0) {
                queue.remove(thread);
                thread.scheduleEndEvent();
            }
        }
        calculateMin();
    }

    public void addThread(Thread thread) {
        if(lastThreadEntry != 0) {
            int robins = (int)Math.round((model.presentTime().getTimeAsDouble() - lastThreadEntry) / robinTime);
            for(int i = 0; i < robins; i++) {
                queue.get(i % queue.size()).subtractDemand(robinTime);
                doneWork += robinTime;
                if(doneWork > capacity)
                    doneWork -= capacity;
            }
        } else {
            lastThreadEntry = this.model.presentTime().getTimeAsDouble();
        }
        queue.add(thread);
        calculateMin();
    }

    private void calculateMin() {
        double smallestThread = Double.POSITIVE_INFINITY;
        for(Thread t : queue) {
            if(t.getDemand() < smallestThread) {
                smallestThread = t.getDemand();
            }
        }

        cycleTime = queue.size() * (int)(smallestThread / robinTime);

        if(!queue.isEmpty()) {
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
