package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.Operation;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDist;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.dist.ContDistUniform;
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
    private String microservice = "";
    private int msId = -1;

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
        this.msId = msId;
        this.microservice = model.allMicroservices.get(msId).getName();
    }

    public double getTime() {
        return this.time;
    }

    public String getMicroservice() {
        return this.microservice;
    }

    public int getId() {
        return this.msId;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {
        if(msId == -1) {
            msId = model.getIdByName(microservice);
        }
        DesmojTest model = (DesmojTest) getModel();
        for(Operation operation : model.allMicroservices.get(msId).getOperations()) {

            // Create a random propability, if the operation propability is within this value it gets started
            ContDistUniform prop = new ContDistUniform(this.model, "",0.0, 1.0,false, false);
            if(prop.sample() <= operation.getProbability()) {
                MessageObject initialMessageObject = new MessageObject(model, "MessageObject", model.getShowStartEvent());
                StartMicroserviceEvent startEvent = new StartMicroserviceEvent(model,"", model.getShowInitEvent(),
                        msId, operation.getName());

                startEvent.schedule(initialMessageObject, new TimeSpan(0, model.getTimeUnit()));

            }
        }
        schedule(new TimeSpan(timeToCreate.sample(), model.getTimeUnit()));
    }
}
