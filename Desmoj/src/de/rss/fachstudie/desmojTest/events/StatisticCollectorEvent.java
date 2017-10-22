package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.TimeSpan;

import java.util.Map;

/**
 *
 */
public class StatisticCollectorEvent extends ExternalEvent {
    private DesmojTest model;

    public StatisticCollectorEvent(Model model, String s, boolean b) {
        super(model, s, b);

        this.model = (DesmojTest) model;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {
        // Collect statisitcs from each service
        for(int id = 0; id < model.idleQueues.size(); ++id) {
            for(int instance = 0; instance < model.idleQueues.get(id).size(); ++instance) {
                MicroserviceEntity entity = model.idleQueues.get(id).get(instance);
                model.cpuStatistics.get(id).get(instance).update(model.serviceCPU.get(id).get(instance));
                model.threadStatistics.get(id).get(instance).update(entity.getThreads().size());
            }
        }
        schedule(new TimeSpan(model.getSimulationTime() / model.getDatapoints(), model.getTimeUnit()));
    }
}
