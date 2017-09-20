package de.rss.fachstudie.desmojTest.entities;

import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

import java.util.List;

public class MicroserviceEntity extends Entity {
    private int id;
    private int throughput;
    private String name;
    private int instances = 0;
    private String nextMicroservice = "";
    private String[] dependencies;


    public String[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(String[] dependencies) {
        this.dependencies = dependencies;
    }


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

    public int getInstances() {
        return instances;
    }

    public void setInstances(int numberOfInstances) {
        this.instances = numberOfInstances;
    }
}
