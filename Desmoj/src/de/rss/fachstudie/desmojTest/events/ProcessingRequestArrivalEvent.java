package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.Processing;
import de.rss.fachstudie.desmojTest.entities.ProcessingRequest;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.concurrent.TimeUnit;

public class ProcessingRequestArrivalEvent extends Event<ProcessingRequest> {
    private DesmojTest model;

    public ProcessingRequestArrivalEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        model = (DesmojTest) owner;
    }

    @Override
    public void eventRoutine(ProcessingRequest processingRequest) throws SuspendExecution {
        model.processingRequestQueue.insert(processingRequest);
        sendTraceNote("ProcessingRequestQueueLength: " + model.processingRequestQueue.length());

        if (!model.idleProcessingQueue.isEmpty()) {
            Processing processing = model.idleProcessingQueue.first();
            model.idleProcessingQueue.remove(processing);

            model.processingRequestQueue.remove(processingRequest);

            ProcessingEndEvent processingEnd = new ProcessingEndEvent(model, "ProcessingEndEvent", true);

            processingEnd.schedule(processing, processingRequest, new TimeSpan(model.getProcessingTime(), TimeUnit.SECONDS));
        }
    }
}
