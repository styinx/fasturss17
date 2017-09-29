package de.rss.fachstudie.desmojTest.entities;

import java.util.HashMap;
import java.util.List;

/**
 * An operation connects two microservice instances. During a specified time interval the service
 * performs operations and uses a part of the microservice power.
 *
 */
public class Operation {
    private String name = "";
    private String service = "";
    private String pattern = "";
    private double duration = 0;
    private double propability = 0;
    private HashMap<String, String>[] dependencies;
    //private HashMap<String, String> dependencies;

    public Operation() {}

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

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public HashMap<String, String>[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(HashMap<String, String>[] operations) {
        this.dependencies = operations;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getPropability() {
        return propability;
    }

    public void setPropability(double propability) {
        this.propability = propability;
    }
}
