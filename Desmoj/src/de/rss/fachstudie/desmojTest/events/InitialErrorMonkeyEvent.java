package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;
import sun.security.krb5.internal.crypto.Des;

public class InitialErrorMonkeyEvent extends ExternalEvent {
    private DesmojTest model;
    private double time;
    private int microservice;
    private int instances;

    public InitialErrorMonkeyEvent(Model owner, String name, boolean showInTrace, double time, int msId, int instances) {
        super(owner, name, showInTrace);

        this.model = (DesmojTest) owner;
        this.time = time;
        this.microservice = msId;
        this.instances = instances;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {
        ErrorMonkeyEvent monkeyEvent = new ErrorMonkeyEvent(model, "<b><u>Monkey Event:</u></b> " + model.allMicroservices.get(microservice).getName(), model.getShowMonkeyEvent(), microservice, instances);
        monkeyEvent.schedule(new TimeSpan(time, model.getTimeUnit()));
    }
}
