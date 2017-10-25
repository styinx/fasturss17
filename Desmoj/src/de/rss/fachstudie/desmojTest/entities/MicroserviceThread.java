package de.rss.fachstudie.desmojTest.entities;

import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;

/**
 * A MicroserviceThread describes a part of a microservice instance.
 * This thread can performs work in form of operations.
 *
 * id:  the service id it belongs to
 * tid: the thread id (map to the number of existing threads in the service)
 */
public class MicroserviceThread extends Entity {
    MainModelClass model;
    private int id;
    private int tid;
    private TimeInstant creationTime;

    public MicroserviceThread(Model owner, String name, boolean b) {
        super(owner, name, b);

        model = (MainModelClass) owner;
        creationTime = model.presentTime();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public TimeInstant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(TimeInstant creation) {
        this.creationTime = creation;
    }
}
