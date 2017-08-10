package de.rss.fachstudie.desmojTest.entities;

import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

public class ProcessingRequest extends Entity {
    public ProcessingRequest(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
    }
}
