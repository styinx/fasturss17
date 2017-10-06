package de.rss.fachstudie.desmojTest.entities;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

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
public class MicroserviceEntity extends SimProcess{
    private DesmojTest model;
    private int id;
    private String name = "";
    private int CPU = 0;
    private int instances = 0;
    private Operation[] operations;
    private double startTime = 0;
    private double stopTime = 0;

    public MicroserviceEntity (Model owner, String name, boolean showInTrace){
        super(owner, name , showInTrace);

        this.model = (DesmojTest) owner;
    }

    @Override
    public void lifeCycle() throws SuspendExecution {
//        startTime = presentTime().getTimeAsDouble();
//
//        hold(new TimeSpan(10, model.getTimeUnit()));
//
//        stopTime = presentTime().getTimeAsDouble() - arrivalTime;
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

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getStopTime() {
        return stopTime;
    }

    public void setStopTime(double stopTime) {
        this.stopTime = stopTime;
    }
}
