package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

public class InitialMicroserviceEvent extends ExternalEvent {

    ContDistExponential timeToCreate;

    public InitialMicroserviceEvent(Model model, String s, boolean b) {
        super(model, s, b);
        timeToCreate = new ContDistExponential(model, s , 1, true, false);
    }

    @Override
    public void eventRoutine() throws SuspendExecution {
        DesmojTest model = (DesmojTest) getModel();
        MessageObject initialMessageObject = new MessageObject(model, "Message" , true);

        StartMicroserviceEvent startEvent = new StartMicroserviceEvent(model, model.allMicroservices.get(0).getName() + "StartInitialEvent", true, 0);
        startEvent.schedule(initialMessageObject, new TimeSpan(0));

        schedule(new TimeSpan(timeToCreate.sample()));
    }
}
