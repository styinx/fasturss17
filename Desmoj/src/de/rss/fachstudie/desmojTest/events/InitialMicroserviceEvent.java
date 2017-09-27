package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class InitialMicroserviceEvent extends ExternalEvent {
    private DesmojTest model;
    private double time;
    private ContDistExponential timeToCreate;
    private int microservice = 0;

    /**
     * Triggers the first event.
     * Has to be called in doInitalSchedules.
     * @param owner
     * @param name
     * @param showInTrace
     * @param time          Time period to create first event
     */
    public InitialMicroserviceEvent(Model owner, String name, boolean showInTrace, double time, int msId) {
        super(owner, name, showInTrace);

        model = (DesmojTest) owner;
        timeToCreate = new ContDistExponential(model, name, time, true, false);
        this.microservice = msId;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {
        DesmojTest model = (DesmojTest) getModel();
        MessageObject initialMessageObject = new MessageObject(model, "MessageObject", model.getShowStartEvent());

        StartMicroserviceEvent startEvent = new StartMicroserviceEvent(model, "<b><u>Inital Event:</u></b> " + model.allMicroservices.get(microservice).getName(), model.getShowStartEvent(), microservice);
        startEvent.schedule(initialMessageObject, new TimeSpan(0, model.getTimeUnit()));

        schedule(new TimeSpan(timeToCreate.sample(), model.getTimeUnit()));
    }
}
