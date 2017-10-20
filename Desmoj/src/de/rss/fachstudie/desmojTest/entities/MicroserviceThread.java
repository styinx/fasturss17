package de.rss.fachstudie.desmojTest.entities;

import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

/**
 * A MicroserviceThread describes a part of a microservice instance.
 * This thread can performs work in form of operations.
 *
 * id:  the service id it belongs to
 * tid: the thread id (map to the number of existing threads in the service)
 */
public class MicroserviceThread extends Entity {
    DesmojTest model;
    private int id;
    private int tid;

    public MicroserviceThread(Model owner, String name, boolean b) {
        super(owner, name, b);

        model = (DesmojTest) owner;
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
}
