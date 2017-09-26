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
    private ContDistExponential timeToCreate;
    private boolean periodically = false;
    private String type = "Microservice";
    private int msId = 0;

    /**
     * Triggers the first event.
     * Has to be called in doInitalSchedules.
     * @param owner
     * @param name
     * @param showInTrace
     * @param time          Time period to create first event
     */
    public InitialMicroserviceEvent(Model owner, String name, boolean showInTrace, double time, String type, boolean periodically, int msId) {
        super(owner, name, showInTrace);

        model = (DesmojTest) owner;
        timeToCreate = new ContDistExponential(model, name, time, true, false);
        this.periodically = periodically;
        this.type = type;
        this.msId = msId;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {
        if(type.equals("Microservice")) {
            DesmojTest model = (DesmojTest) getModel();
            MessageObject initialMessageObject = new MessageObject(model, "MessageObject", true);

            StartMicroserviceEvent startEvent = new StartMicroserviceEvent(model, "<b><u>Inital Event:</u></b> " + model.allMicroservices.get(0).getName(), true, 0);
            startEvent.schedule(initialMessageObject, new TimeSpan(0, TimeUnit.SECONDS));
        } else if(type.equals("ErrorMonkey")) {
            ErrorMonkeyEvent monkeyEvent = new ErrorMonkeyEvent(model, "<b><u>ErrorMonkey Event:</u></b>", true, msId);
            monkeyEvent.schedule(new TimeSpan(timeToCreate.sample(), TimeUnit.SECONDS));
        }

        if(periodically) {
            schedule(new TimeSpan(timeToCreate.sample(), TimeUnit.SECONDS));
        }
    }
}
