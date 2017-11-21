package de.rss.fachstudie.desmojTest.entities;

import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

import java.util.HashMap;

/**
 * A Microservice represents a collection of services.
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
public class Microservice extends Entity{
    private MainModelClass model;
    private boolean killed = false;
    private int id;
    private int sid;
    private String name = "";
    private HashMap<String, Integer>[] patterns;
    private int capacity = 0;
    private int instances = 0;
    private Operation[] operations;

    public Microservice(Model owner, String name, boolean showInTrace){
        super(owner, name , showInTrace);

        this.model = (MainModelClass) owner;
    }

    public boolean isKilled() {
        return killed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
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

    public HashMap<String, Integer>[] getPatterns() {
        return patterns;
    }

    public void setPatterns(HashMap<String, Integer>[] patterns) {
        this.patterns = patterns;
    }

    /**
     * Check if the <code>Microservice</code> implements the passed pattern.
     *
     * @param name String: The name of the pattern
     * @return boolean: True if the pattern is implemented
     * False if the pattern isn't implemented
     */
    public boolean hasPattern(String name) {
        if(patterns != null){
            for(HashMap<String, Integer> pattern : patterns) {
                if(pattern.containsKey(name)) {
                    return true;
                }
            }
        }

        return false;
    }


    public int getPattern(String name) {
        for(HashMap<String, Integer> pattern : patterns) {
            if(pattern.containsKey(name))
                return pattern.get(name);
        }
        return -1;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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

    public Operation getOperation(String name) {
        for(Operation o : operations) {
            if(o.getName().equals(name)) {
                return o;
            }
        }
        return null;
    }

    public void setOperations(Operation[] operations) {
        this.operations = operations;
    }
}
