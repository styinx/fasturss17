package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

public class InitialChaosMonkeyEvent extends ExternalEvent {
    private DesmojTest model;
    private double time;
    private String microservice;
    private int msId = -1;
    private int instances;

    public InitialChaosMonkeyEvent(Model owner, String name, boolean showInTrace, double time, int msId, int instances) {
        super(owner, name, showInTrace);

        this.model = (DesmojTest) owner;
        this.time = time;
        this.msId = msId;
        this.instances = instances;
        this.microservice = model.allMicroservices.get(msId).getName();
    }

    public double getTime() {
        return this.time;
    }

    public String getMicroservice() {
        return this.microservice;
    }

    public int getInstances() {
        return this.instances;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {
        if(msId == -1) {
            msId = model.getIdByName(microservice);
        }
        ChaosMonkeyEvent monkeyEvent = new ChaosMonkeyEvent(model, "<b><u>Monkey Event:</u></b> " + microservice, model.getShowMonkeyEvent(), msId, instances);
        monkeyEvent.schedule(new TimeSpan(time, model.getTimeUnit()));
    }
}
