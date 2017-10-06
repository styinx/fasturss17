package de.rss.fachstudie.desmojTest.entities;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

import java.util.HashMap;
import java.util.List;

/**
 * An operation connects two microservice instances. During a specified time interval the service
 * performs operations and uses a part of the microservice power.
 *
 */
public class Operation extends SimProcess{
    private DesmojTest model;
    private String name = "";
    private String service = "";
    private String pattern = "";
    private double duration = 0;
    private int CPU = 0;
    private double propability = 0;
    private HashMap<String, String>[] dependencies;

    public Operation(Model model, String s, boolean b, boolean b1) {
        super(model, s, b, b1);

        this.model = (DesmojTest) model;
    }

    @Override
    public void lifeCycle() throws SuspendExecution {
//        model.serviceCPU.get(0).provide(500);
//        hold(new TimeSpan(100, model.getTimeUnit()));
//        model.serviceCPU.get(0).takeBack(500);
    }

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

    public int getCPU() {
        return CPU;
    }

    public void setCPU(int CPU) {
        this.CPU = CPU;
    }

    public double getPropability() {
        return propability;
    }

    public void setPropability(double propability) {
        this.propability = propability;
    }
}
