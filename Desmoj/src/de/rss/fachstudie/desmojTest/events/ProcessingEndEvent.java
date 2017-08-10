package de.rss.fachstudie.desmojTest.events;

import de.rss.fachstudie.desmojTest.entities.Processing;
import de.rss.fachstudie.desmojTest.entities.ProcessingRequest;
import de.rss.fachstudie.desmojTest.entities.ShippingRequest;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.EventOf2Entities;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.concurrent.TimeUnit;

public class ProcessingEndEvent extends EventOf2Entities<Processing, ProcessingRequest> {
    private DesmojTest model;

    public ProcessingEndEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        model = (DesmojTest) owner;
    }

    @Override
    public void eventRoutine(Processing processing, ProcessingRequest processingRequest) {
        sendTraceNote(processingRequest + " has been completed");

        ShippingRequest shippingRequest = new ShippingRequest(model, "ShippingRequest", true);
        ShippingRequestArrivalEvent shippingRequestArrival = new ShippingRequestArrivalEvent(model, "ShippingRequestArrivalEvent", true);

        shippingRequestArrival.schedule(shippingRequest, new TimeSpan(0.0));

        if (!model.processingRequestQueue.isEmpty()) {
            ProcessingRequest nextProcessingRequest = model.processingRequestQueue.first();
            model.processingRequestQueue.remove(nextProcessingRequest);

            ProcessingEndEvent event = new ProcessingEndEvent(model, "ProcessingEndEvent", true);
            event.schedule(processing, nextProcessingRequest, new TimeSpan(model.getProcessingTime(), TimeUnit.SECONDS));
        } else {
            model.idleProcessingQueue.insert(processing);
        }
    }
}
