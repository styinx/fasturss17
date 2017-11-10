package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

/**
 *
 */
public class InitialEvent extends ExternalEvent {
    private MainModelClass model;
    private double time;
    private ContDistUniform timeToCreate;
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
    public InitialEvent(Model owner, String name, boolean showInTrace, double time, int msId, String op) {
        super(owner, name, showInTrace);

        model = (MainModelClass) owner;
        timeToCreate = new ContDistUniform(model, name, time, time, model.getShowInitEvent(), true);
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
        StartEvent startEvent = new StartEvent(model,
                "Start Event: " + microservice + "(" + operation + ")",
                model.getShowStartEvent(), msId, operation);

        startEvent.schedule(initialMessageObject, new TimeSpan(0, model.getTimeUnit()));

        schedule(new TimeSpan(timeToCreate.sample(), model.getTimeUnit()));
    }
}
