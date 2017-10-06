package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

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
            model.serviceTimeseries.get(i).update(ms.getInstances() - model.idleQueues.get(i).size());
        }
        schedule(new TimeSpan(1, model.getTimeUnit()));
    }
}