package de.rss.fachstudie.desmojTest.events;

import de.rss.fachstudie.desmojTest.entities.Shipping;
import de.rss.fachstudie.desmojTest.entities.ShippingRequest;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.EventOf2Entities;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.concurrent.TimeUnit;

public class ShippingEndEvent extends EventOf2Entities<Shipping, ShippingRequest> {

    private DesmojTest model;

    public ShippingEndEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        model = (DesmojTest) owner;
    }

    @Override
    public void eventRoutine(Shipping shipping, ShippingRequest shippingRequest) {
        sendTraceNote(shippingRequest + " has been completed");

        if (!model.shippingRequestQueue.isEmpty()) {
            ShippingRequest nextShippingRequest = model.shippingRequestQueue.first();
            model.shippingRequestQueue.remove(nextShippingRequest);

            ShippingEndEvent event = new ShippingEndEvent(model, "ShippingEndEvent", true);
            event.schedule(shipping, nextShippingRequest, new TimeSpan(model.getShippingTime(), TimeUnit.SECONDS));
        } else {
            model.idleShippingQueue.insert(shipping);
        }
    }
}
