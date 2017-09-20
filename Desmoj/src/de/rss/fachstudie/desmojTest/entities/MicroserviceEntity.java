package de.rss.fachstudie.desmojTest.entities;

import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

public class MicroserviceEntity extends Entity {
    private int id;
    private int throughput;
    private String name;
    private int numberOfInstances = 5;
    private String nextMicroservice = null;
    private String[] dependencies = null;

    public MicroserviceEntity (Model owner, String name, boolean showInTrace){
        super(owner, name , showInTrace);
    }

    public String getNextMicroservice() {
        return nextMicroservice;
    }

    public void setNextMicroservice(String nextMicroservice) {
        this.nextMicroservice = nextMicroservice;
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

    public int getThroughput() {
        return throughput;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setThroughput(int throughput) {
        this.throughput = throughput;
    }

    public int getNumberOfInstances() {
        return numberOfInstances;
    }

    public void setNumberOfInstances(int numberOfInstances) {
        this.numberOfInstances = numberOfInstances;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(String[] dependencies) {
        this.dependencies = dependencies;
    }
}
