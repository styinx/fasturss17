package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;

public class FinishEvent extends ExternalEvent {
    private MainModelClass model;

    public FinishEvent(Model owner, String s, boolean b) {
        super(owner, s, b);

        model = (MainModelClass) owner;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {
        // Finish all threads in the task queue and save the response time
        for (int id = 0; id < model.serviceCPU.size(); ++id) {
            for (int instance = 0; instance < model.serviceCPU.get(id).size(); ++instance) {
                model.serviceCPU.get(id).get(instance).releaseUnfinishedThreads();
            }
        }
    }
}
