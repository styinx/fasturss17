package de.rss.fachstudie.desmojTest.resources;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.*;

import java.util.ArrayList;
import java.util.List;

public class CPU extends Event {
    private MainModelClass model;
    private int capacity = 1000;
    private int robinTime = 10;
    private int cycleTime = 0;
    private double lastThreadEntry;
    private List<CPUThread> queue;

    public CPU (Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        model = (MainModelClass) owner;
        queue = new ArrayList<>();

        lastThreadEntry = 0;
    }

    public void eventRoutine(Entity entity) throws SuspendExecution {
        for(CPUThread thread : queue) {
            thread.subtractDemand(cycleTime);
            if(thread.getDemand() == 0) {
                queue.remove(thread);
                thread.schedule();
            }
        }

        calculateMin();
    }

    public void addThread(CPUThread thread) {
        if(lastThreadEntry != 0) {
            int robins = (int)Math.round((model.presentTime().getTimeAsDouble() - lastThreadEntry) / robinTime);
            for(int i = 0; i < robins; i++) {
                queue.get(i % queue.size()).subtractDemand(robinTime);
            }
        } else {
            lastThreadEntry = this.model.presentTime().getTimeAsDouble();
        }
        queue.add(thread);

        calculateMin();
    }

    private void calculateMin() {
        CPUThread minimum = new CPUThread(Double.POSITIVE_INFINITY);
        for(CPUThread t : queue) {
            if(t.getDemand() < minimum.getDemand()) {
                minimum = t;
            }
        }

        cycleTime = queue.size() * (minimum.getDemand() / robinTime);

        if(!queue.isEmpty()) {
            // TODO reschedule or schedule
            if(isScheduled()) {
                reSchedule(new TimeSpan(cycleTime, model.getTimeUnit()));
            } else {
                // TODO      \/
                schedule(null, new TimeSpan(cycleTime, model.getTimeUnit()));
            }
        }
    }
}
