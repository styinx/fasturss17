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
 *
 * model:       reference to the experiment model
 * id:          internal unique number to identify a service
 * name:        the given name of the service, defined by the input
 * CPU:         the computing power a microservice has available
 * instances:   number of instances a service can create
 * operations:  an array of dependent operations
 */
public class MicroserviceEntity extends SimProcess{
    private DesmojTest model;
    private int id;
    private String name = "";
    private int CPU = 0;
    private int instances = 0;
    private Operation[] operations;

    public MicroserviceEntity (Model owner, String name, boolean showInTrace){
        super(owner, name , showInTrace);

        this.model = (DesmojTest) owner;
    }

    @Override
    public void lifeCycle() throws SuspendExecution {
//        hold(new TimeSpan(10, model.getTimeUnit()));
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
}
