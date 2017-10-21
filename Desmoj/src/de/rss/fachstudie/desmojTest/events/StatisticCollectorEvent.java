package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

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
        for(int i = 0; i < model.allMicroservices.size(); ++i) {
            MicroserviceEntity ms = model.allMicroservices.get(i);
            for(int j = 0; j < ms.getInstances(); ++j) {
                model.cpuStatistics.get(i).get(j).update(model.serviceCPU.get(i).get(j));
                model.threadStatistics.get(i).get(j).update(ms.getThreads().size());
            }
        }
        schedule(new TimeSpan(model.getSimulationTime() / model.getDatapoints(), model.getTimeUnit()));
    }
}
