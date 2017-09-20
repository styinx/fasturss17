package de.rss.fachstudie.desmojTest.entities;

import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

public class MessageObject extends Entity {
    private String name;
    private int id;

    public MessageObject(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
