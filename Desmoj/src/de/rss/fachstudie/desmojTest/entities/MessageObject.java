package de.rss.fachstudie.desmojTest.entities;

import de.rss.fachstudie.desmojTest.events.StopMicroserviceEvent;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

import java.util.Stack;

public class MessageObject extends Entity {
    private String name;
    private Stack<StopMicroserviceEvent> dependency;

    public MessageObject(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
        dependency = new Stack<>();
    }

    public Stack<StopMicroserviceEvent> getDependency() {
        return dependency;
    }

    public void addDependency(StopMicroserviceEvent dependency) {
        this.dependency.push(dependency);
    }

    public StopMicroserviceEvent removeDependency() {
        return this.dependency.pop();
    }

    public void setDependency(Stack<StopMicroserviceEvent> dependency) {
        this.dependency = dependency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
