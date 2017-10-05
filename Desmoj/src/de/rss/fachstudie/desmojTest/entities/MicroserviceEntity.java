package de.rss.fachstudie.desmojTest.entities;

import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

import java.util.List;

/**
 * A MicroserviceEntity represents a collection of services.
 * Each instance is able to call operations to another service instance.
 * id: internal unique number to identify a service
 * name: the given name of the service in the input
 * CPU: the computing CPU a microservice has available
 * instances: number of instances a service can create
 * operations: ...
 */
public class MicroserviceEntity extends Entity{
    private int id;
    private String name = "";
    private int CPU = 0;
    private int instances = 0;
    private Operation[] operations;
    private double throughput = 0;
    private String[] dependencies;

    public MicroserviceEntity (Model owner, String name, boolean showInTrace){
        super(owner, name , showInTrace);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCPU() {
        return CPU;
    }

    public void setCPU(int CPU) {
        this.CPU = CPU;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int numberOfInstances) {
        this.instances = numberOfInstances;
    }

    public Operation[] getOperations() {
        return operations;
    }

    public void setOperations(Operation[] operations) {
        this.operations = operations;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(String[] dependencies) {
        this.dependencies = dependencies;
    }

    public double getThroughput() {
        return throughput;
    }

    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }
}
