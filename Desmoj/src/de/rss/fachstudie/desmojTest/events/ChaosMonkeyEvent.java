package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.Microservice;
import de.rss.fachstudie.desmojTest.entities.Thread;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.TimeSpan;

public class ChaosMonkeyEvent extends ExternalEvent {
    private MainModelClass model;
    private int instances = 0;
    private int msId = 0;
    private double nextReschedule = 1;

    public ChaosMonkeyEvent(Model owner, String name, boolean showInTrace, int msId, int instances) {
        super(owner, name, showInTrace);

        model = (MainModelClass) getModel();
        this.msId = msId;
        this.instances = instances;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {

        for(int i = 0; i < instances; ++i) {
            for(Microservice msEntity : model.services.get(msId))
            {
                if(!msEntity.isKilled()) {
                    msEntity.setKilled(true);
                    msEntity.setThreads(new Queue<Thread>(model, "", false, false));
                    model.serviceCPU.get(msEntity.getId()).put(msEntity.getSid(), msEntity.getCPU());
                    this.instances -= 1;
                    break;
                }
            }
        }

        if(this.instances > 0) {
            schedule(new TimeSpan(nextReschedule, model.getTimeUnit()));
        }
    }
}
