package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.Shipping;
import de.rss.fachstudie.desmojTest.entities.ShippingRequest;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.concurrent.TimeUnit;

public class ShippingRequestArrivalEvent extends Event<ShippingRequest> {

    private DesmojTest model;

    public ShippingRequestArrivalEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        model = (DesmojTest) owner;
    }

    @Override
    public void eventRoutine(ShippingRequest shippingRequest) throws SuspendExecution {
        model.shippingRequestQueue.insert(shippingRequest);
        sendTraceNote("ShippingRequestQueueLength: " + model.shippingRequestQueue.length());

        if (!model.idleShippingQueue.isEmpty()) {
            Shipping shipping = model.idleShippingQueue.first();
            model.idleShippingQueue.remove(shipping);

            model.shippingRequestQueue.remove(shippingRequest);

            ShippingEndEvent shippingEnd = new ShippingEndEvent(model, "ShippingEndEvent", true);

            shippingEnd.schedule(shipping, shippingRequest, new TimeSpan(model.getShippingTime(), TimeUnit.SECONDS));
        }
    }
}
