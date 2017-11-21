package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

/**
 * An <code>InitialEvent</code> is an <code>ExternalEvent</code> which is the first event in the simulation
 * that is called in <code>doInitalSchedule</code> in the <code>Model</code>.
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
     *
     * @param owner Model: The model that owns this event
     * @param name String: The name of this event
     * @param showInTrace boolean: Whether this event is shown in the trace or not
     * @param time double: The timepoint at which the first event will be called
     * @param msId int: The ID of the microservice for which this event will create a request
     * @param op: String: The name of the operation that this event will schedule
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

    /**
     * The <code>eventRoutine</code> of the <code>InitialEvent</code>.
     * Schedules a start event to simulate an operation of a specified microservice
     *
     * @throws SuspendExecution
     */
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
