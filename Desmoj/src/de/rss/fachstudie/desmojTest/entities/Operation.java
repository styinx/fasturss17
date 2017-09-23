package de.rss.fachstudie.desmojTest.entities;

/**
 * An operation connects two microservice instances. During a specified time interval the service
 * performs operations and uses a part of the microservice power.
 *
 */
public class Operation {
    private String name = "";
    private String pattern = "";
    private String service = "";
    private String operation = "";
    private double duration = 0;
    private double propability = 0;

    public Operation() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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
