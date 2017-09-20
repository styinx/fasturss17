package de.rss.fachstudie.desmojTest.entities;

import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

public class MicroserviceEntity extends Entity {
    private int id;
    private int throughput;
    private String name;
    private int numberOfInstances = 5;
    private String nextMicroservice = "" ; // sting of next microservice map to integer


    public int getIdFromNextMicroserviceByName(){


        return 0;
    }

    public MicroserviceEntity (Model owner, String name, boolean showInTrace){
        super(owner, name , showInTrace);
        this.name = name;
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
}
