package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;
import javafx.scene.paint.Stop;

import java.util.concurrent.TimeUnit;

public class StartMicroserviceEvent extends Event<MessageObject> {

    private DesmojTest model;
    private ContDistUniform timeUntilFinished;
    private int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //
    public StartMicroserviceEvent(Model owner, String name, Boolean showInTrace, Integer id){
        super(owner, name, showInTrace);
        setId(id);


        model = (DesmojTest) owner;

        timeUntilFinished = new ContDistUniform(model , name, 4,4, true, false);

    }

    @Override
    public void eventRoutine(MessageObject messageObject) throws SuspendExecution {
        model.taskQueues.get(id).insert(messageObject);

        if(!model.idleQueues.get(id).isEmpty()){
            MicroserviceEntity msEntity = model.idleQueues.get(id).first();
            model.idleQueues.get(id).remove(msEntity);
            model.taskQueues.get(id).remove(messageObject);

            
            StopMicroserviceEvent msEndEvent = new StopMicroserviceEvent(model, msEntity.getNextMicroservice() + "Stopp" , true, id);
            msEndEvent.schedule(msEntity, messageObject, new TimeSpan(timeUntilFinished.sample(), TimeUnit.SECONDS));

        }
    }
}
