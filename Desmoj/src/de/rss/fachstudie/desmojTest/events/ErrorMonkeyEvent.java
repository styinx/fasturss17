package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

public class ErrorMonkeyEvent extends ExternalEvent {
    private DesmojTest model;
    private int instances = 0;
    private int msId = 0;

    public ErrorMonkeyEvent(Model owner, String name, boolean showInTrace, int msId, int instances) {
        super(owner, name, showInTrace);

        model = (DesmojTest) getModel();
        this.msId = msId;
        this.instances = instances;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {

        for(int i = 0; i < instances; ++i) {
            if(model.idleQueues.get(msId).removeFirst() != null) {
                this.instances -= 1;
            }
        }

        if(this.instances > 0) {
            // TODO adjust schedule interval, depends on simulation performance
            schedule(new TimeSpan(1, model.getTimeUnit()));
        }
    }
}
