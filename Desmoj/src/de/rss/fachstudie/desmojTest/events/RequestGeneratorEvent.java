package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.Request;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.concurrent.TimeUnit;

public class RequestGeneratorEvent extends ExternalEvent {

    public RequestGeneratorEvent(Model owner,String name, boolean showInTrace) {
        super(owner, name, showInTrace);
    }

    @Override
    public void eventRoutine() throws SuspendExecution {

        DesmojTest model = (DesmojTest) getModel();

        Request request = new Request(model, "Request", true);
        RequestArrivalEvent requestArrival = new RequestArrivalEvent(model, "RequestArrivalEvent", true);

        requestArrival.schedule(request, new TimeSpan(0.0));

        schedule(new TimeSpan(model.getRequestArrivalTime(), TimeUnit.SECONDS));
    }
}
