package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.entities.Operation;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.concurrent.TimeUnit;

/**
 * This event class gets a working object and schedules a timespan during a microservice is busy.
 */
public class StartMicroserviceEvent extends Event<MessageObject> {
    private DesmojTest model;
    private ContDistUniform timeUntilFinished;
    private int id;
    private String operation;

    public StartMicroserviceEvent(Model owner, String name, boolean showInTrace, int id, String operation){
        super(owner, name, showInTrace);

        this.id = id;
        this.operation = operation;
        model = (DesmojTest) owner;
        double msThroughput = model.allMicroservices.get(id).getThroughput();
        timeUntilFinished = new ContDistUniform(model, name, msThroughput, msThroughput, true, false);
    }

    @Override
    public void eventRoutine(MessageObject messageObject) throws SuspendExecution {
        model.taskQueues.get(id).insert(messageObject);

        if(!model.idleQueues.get(id).isEmpty()){
            MicroserviceEntity msEntity = model.idleQueues.get(id).first();
            model.idleQueues.get(id).remove(msEntity);
            model.taskQueues.get(id).remove(messageObject);

            StopMicroserviceEvent msEndEvent = new StopMicroserviceEvent(model,
                    "Stop Event: " + msEntity.getName() + " (" + operation + ")",
                    model.getShowStopEvent(), id, operation);
            msEndEvent.schedule(msEntity, messageObject, new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));

        }
    }
}
