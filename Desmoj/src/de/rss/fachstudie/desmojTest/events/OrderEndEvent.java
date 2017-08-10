package de.rss.fachstudie.desmojTest.events;

import de.rss.fachstudie.desmojTest.entities.Order;
import de.rss.fachstudie.desmojTest.entities.ProcessingRequest;
import de.rss.fachstudie.desmojTest.entities.Request;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.EventOf2Entities;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.concurrent.TimeUnit;

public class OrderEndEvent extends EventOf2Entities<Order, Request> {
    private DesmojTest model;

    public OrderEndEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        model = (DesmojTest) owner;
    }

    @Override
    public void eventRoutine(Order order, Request request) {
        sendTraceNote(request + " has been completed");

        ProcessingRequest processingRequest = new ProcessingRequest(model, "ProcessingRequest", true);
        ProcessingRequestArrivalEvent processingRequestArrival = new ProcessingRequestArrivalEvent(model, "ProcessingRequestArrivalEvent", true);

        processingRequestArrival.schedule(processingRequest, new TimeSpan(0.0));

        if (!model.requestQueue.isEmpty()) {
            Request nextRequest = model.requestQueue.first();
            model.requestQueue.remove(nextRequest);

            OrderEndEvent event = new OrderEndEvent(model, "OrderEndEvent", true);
            event.schedule(order, nextRequest, new TimeSpan(model.getOrderTime(), TimeUnit.SECONDS));
        } else {
            model.idleOrderQueue.insert(order);
        }
    }
}
