package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.Order;
import de.rss.fachstudie.desmojTest.entities.Request;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.concurrent.TimeUnit;

public class RequestArrivalEvent extends Event<Request> {

    private DesmojTest model;

    public RequestArrivalEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        model = (DesmojTest) owner;
    }

    @Override
    public void eventRoutine(Request request) throws SuspendExecution {
        model.requestQueue.insert(request);
        sendTraceNote("RequestQueueLength: " + model.requestQueue.length());

        if (!model.idleOrderQueue.isEmpty()) {
            Order order = model.idleOrderQueue.first();
            model.idleOrderQueue.remove(order);

            model.requestQueue.remove(request);

            OrderEndEvent orderEnd = new OrderEndEvent(model, "OrderEndEvent", true);

            orderEnd.schedule(order, request, new TimeSpan(model.getOrderTime(), TimeUnit.SECONDS));
        }
    }
}
