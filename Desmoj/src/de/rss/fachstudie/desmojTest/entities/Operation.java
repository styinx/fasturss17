package de.rss.fachstudie.desmojTest.entities;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;

import java.util.SortedMap;

/**
 * An operation connects two microservice instances. During a specified time interval the service
 * performs operations and uses a portion of the microservice's computing power.
 *
 * model:           reference to the experiment model
 * name:            the given name of the operation, defined by the input
 * service:         name of the the owning microservice
 * pattern:         resilience pattern
 * duration:        time interval the operation needs to finish
 * CPU:             the needed computing power
 * probability:     the operation is only executed if a certain probability is reached
 * dependencies:    an array containing dependant operations of other services
 */
public class Operation extends Entity {
    private MainModelClass model;
    private String name = "";
    private String service = "";
    private String[] patterns = {};
    private double duration = 0;
    private int CPU = 0;
    private double probability = 0;
    private SortedMap<String, String>[] dependencies;

    public Operation(Model model, String s, boolean b) {
        super(model, s, b);

        this.model = (MainModelClass) model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String[] getPatterns() {
        return patterns;
    }

    public void setPatterns(String[] patterns) {
        this.patterns = patterns;
    }

    public SortedMap<String, String>[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(SortedMap<String, String>[] operations) {
        this.dependencies = operations;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getCPU() {
        return CPU;
    }

    public void setCPU(int CPU) {
        this.CPU = CPU;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
