package de.rss.fachstudie.desmojTest.entities;

import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.*;

import java.util.HashMap;

/**
 * A MicroserviceEntity represents a collection of services.
 * Each instance is able to call operations to another service instance.
 *
 * model:       reference to the experiment model
 * id:          internal unique number to identify a service
 * sid:         service id (maps to the number of existing instances)
 * name:        the given name of the service, defined by the input
 * CPU:         the computing power a microservice has available
 * instances:   number of instances a service can create
 * operations:  an array of dependent operations
 */
public class MicroserviceEntity extends Entity{
    private MainModelClass model;
    private int id;
    private int sid;
    private String name = "";
    private String[] patterns;
    private int CPU = 0;
    private int instances = 0;
    private Queue<MicroserviceThread> threads;
    private Operation[] operations;
    private HashMap<Integer, Double> responseTime;

    public MicroserviceEntity(Model owner, String name, boolean showInTrace){
        super(owner, name , showInTrace);

        this.model = (MainModelClass) owner;
        threads = new Queue<>(model, "Thread Queue " + name + " #" + sid, true, true);
        responseTime = new HashMap<>();
        for(int i = 0; i < model.getDatapoints(); ++i) {
            responseTime.put(i, 0.0);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getPatterns() {
        return patterns;
    }

    public void setPatterns(String[] patterns) {
        this.patterns = patterns;
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

    public Queue<MicroserviceThread> getThreads() {
        return threads;
    }

    public void setThreads(Queue<MicroserviceThread> threads) {
        this.threads = threads;
    }

    public Operation[] getOperations() {
        return operations;
    }

    public void setOperations(Operation[] operations) {
        this.operations = operations;
    }

    public void addResponseTime(Double startTime) {
        responseTime.put(startTime.intValue(), model.presentTime().getTimeAsDouble() - startTime);
    }

    public HashMap<Integer, Double> getResponseTime() {
        return responseTime;
    }
}
