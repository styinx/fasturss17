package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.Operation;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

/**
 *
 */
public class InitialMicroserviceEvent extends ExternalEvent {
    private DesmojTest model;
    private double time;
    private ContDistExponential timeToCreate;
    private String microservice = "";
    private String operation = "";
    private int msId = -1;

    /**
     * Triggers the first event.
     * Has to be called in doInitalSchedules.
     * @param owner
     * @param name
     * @param showInTrace
     * @param time          Time period to create first event
     */
    public InitialMicroserviceEvent(Model owner, String name, boolean showInTrace, double time, int msId, String op) {
        super(owner, name, showInTrace);

        model = (DesmojTest) owner;
        timeToCreate = new ContDistExponential(model, name, time, model.getShowInitEvent(), true);
        this.msId = msId;
        this.microservice = model.allMicroservices.get(msId).getName();
        this.operation = op;
    }

    public double getTime() {
        return this.time;
    }

    public String getMicroservice() {
        return this.microservice;
    }

    public int getId() {
        return this.msId;
    }

    public String getOperation() {
        return operation;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {

        // Create a message object and begin event
        MessageObject initialMessageObject = new MessageObject(model, this.getClass().getName(), model.getShowStartEvent());
        StartMicroserviceEvent startEvent = new StartMicroserviceEvent(model,
                "Start Event: " + microservice + "(" + operation + ")",
                model.getShowStartEvent(), msId, operation);

        startEvent.schedule(initialMessageObject, new TimeSpan(0, model.getTimeUnit()));

        schedule(new TimeSpan(timeToCreate.sample(), model.getTimeUnit()));
    }
}
