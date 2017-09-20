package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;

public class MicroserviceEvent extends Event<MessageObject> {

    private DesmojTest model;
    private ContDistUniform timeUntilFinished;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    MicroserviceEvent(Model owner, String name, Boolean showInTrace){
        super(owner, name, showInTrace);

        model = (DesmojTest) owner;

        timeUntilFinished = new ContDistUniform(model , name, 4,4, true, false);

    }

    @Override
    public void eventRoutine(MessageObject messageObject) throws SuspendExecution {
        model.taskQueue.insert(messageObject);

        if(!model.idleQueues.get(id).isEmpty()){
            MicroserviceEntity msEntity = model.idleQueues.get(id).first();
            model.idleQueues.get(id).remove(msEntity);
            model.taskQueue.remove(messageObject);

            MicroserviceEvent msEvent = new MicroserviceEvent(model, msEntity.getNextMicroservice() , true);

            msEvent.schedule(msEntity.getNextMicroservice(), messageObject);
        }




    }
}
