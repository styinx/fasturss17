package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

public class StatisticEvent extends ExternalEvent {
    private MainModelClass model;
    private double timeInterval = 0;

    public StatisticEvent(Model owner, String s, boolean b, double interval) {
        super(owner, s, b);

        model = (MainModelClass) owner;
        timeInterval = interval;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {
        for (int id = 0; id < model.serviceCPU.size(); ++id) {
            for (int instance = 0; instance < model.serviceCPU.get(id).size(); ++instance) {
                model.cpuStatistics.get(id).get(instance).update(model.serviceCPU.get(id).get(instance).getMeanUsage(model.getStatisitcChunks()));
                model.serviceCPU.get(id).get(instance).collectUsage();
            }
        }
        schedule(new TimeSpan(timeInterval, model.getTimeUnit()));
    }
}
