package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.models.MSSimulator;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.concurrent.TimeUnit;

public class InitialMicroserviceEvent extends ExternalEvent {
    private MSSimulator model;
    ContDistExponential timeToCreate;

    /**
     * Triggers the first event.
     * Has to be called in doInitalSchedules.
     * @param owner
     * @param name
     * @param showInTrace
     * @param time          Time period to create first event
     */
    public InitialMicroserviceEvent(Model owner, String name, boolean showInTrace, double time) {
        super(owner, name, showInTrace);

        model = (MSSimulator) owner;
        timeToCreate = new ContDistExponential(model, name, time, true, false);
    }

    @Override
    public void eventRoutine() throws SuspendExecution {
        MSSimulator model = (MSSimulator) getModel();
        MessageObject initialMessageObject = new MessageObject(model, "Message" , true);

        StartMicroserviceEvent startEvent = new StartMicroserviceEvent(model, "Inital Event:" + model.allMicroservices.get(0).getName(), true, 0);
        startEvent.schedule(initialMessageObject, new TimeSpan(0, TimeUnit.SECONDS));

        schedule(new TimeSpan(timeToCreate.sample()));
    }
}
