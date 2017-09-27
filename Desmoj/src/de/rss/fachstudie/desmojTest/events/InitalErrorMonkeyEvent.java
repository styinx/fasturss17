package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;
import sun.security.krb5.internal.crypto.Des;

public class InitalErrorMonkeyEvent extends ExternalEvent {
    private DesmojTest model;
    private double time;
    private int msId = 0;
    private int instances = 0;

    public InitalErrorMonkeyEvent(Model owner, String name, boolean showInTrace, double time, int msId, int instances) {
        super(owner, name, showInTrace);

        this.model = (DesmojTest) owner;
        this.time = time;
        this.msId = msId;
        this.instances = instances;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {
        ErrorMonkeyEvent monkeyEvent = new ErrorMonkeyEvent(model, "<b><u>Monkey Event:</u></b> " + model.allMicroservices.get(msId).getName(), model.getShowMonkeyEvent(), msId, instances);
        monkeyEvent.schedule(new TimeSpan(time, model.getTimeUnit()));
    }
}
