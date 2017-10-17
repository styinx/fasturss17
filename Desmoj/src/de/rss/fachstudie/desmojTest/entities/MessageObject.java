package de.rss.fachstudie.desmojTest.entities;

import de.rss.fachstudie.desmojTest.events.StopMicroserviceEvent;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

import java.util.HashMap;
import java.util.Stack;

public class MessageObject extends Entity {
    private String name;
    private Stack<HashMap<MicroserviceEntity, StopMicroserviceEvent>> dependency;

    public MessageObject(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
        dependency = new Stack<>();
    }

    public Stack<HashMap<MicroserviceEntity, StopMicroserviceEvent>> getDependency() {
        return dependency;
    }

    public void addDependency(HashMap<MicroserviceEntity, StopMicroserviceEvent> dependency) {
        this.dependency.push(dependency);
    }

    public HashMap<MicroserviceEntity, StopMicroserviceEvent> removeDependency() {
        return this.dependency.pop();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
