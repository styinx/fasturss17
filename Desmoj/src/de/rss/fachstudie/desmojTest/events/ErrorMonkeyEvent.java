package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;

public class ErrorMonkeyEvent extends ExternalEvent {
    private DesmojTest model;
    private int instances = 3;
    private int msId = 0;

    public ErrorMonkeyEvent(Model owner, String name, boolean showInTrace, int msId) {
        super(owner, name, showInTrace);

        model = (DesmojTest) getModel();
        this.msId = msId;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {

        if(!model.idleQueues.get(msId).isEmpty()) {
            for(int i = 0; i < instances; ++i) {
                model.idleQueues.get(msId).removeFirst();
                System.out.println("Kill instance of " + model.allMicroservices.get(msId).getName());
            }
        } else {
            //TODO queue is empty, reschedule to next time a ms is in queue to kill
        }
    }
}
