package de.rss.fachstudie.desmojTest.events;

import de.rss.fachstudie.desmojTest.entities.Microservice;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;
import desmoj.core.statistic.TimeSeries;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class StatisticEvent extends ExternalEvent {
    private MainModelClass model;

    public StatisticEvent(Model model, String s, boolean b) {
        super(model, s, b);

        this.model = (MainModelClass) model;
    }

    public void eventRoutine() {
        // Collect statisitcs from each service
        for(int id = 0; id < model.services.size(); ++id) {
            for(int instance = 0; instance < model.services.get(id).size(); ++instance) {
                Microservice entity = model.services.get(id).get(instance);

                // Threads
                model.threadStatistics.get(id).get(instance).update(entity.getThreads().size());
                // CPU
                model.cpuStatistics.get(id).get(instance).update((double)(entity.getCPU() - model.serviceCPU.get(id).get(instance))/entity.getCPU());
            }
            // Task Queue
            model.taskQueueStatistics.get(id).update(model.taskQueues.get(id).size());
        }
        schedule(new TimeSpan(model.getSimulationTime() / model.getDatapoints(), model.getTimeUnit()));
    }
}
