package de.rss.fachstudie.desmojTest.entities;

import de.rss.fachstudie.desmojTest.events.StopMicroserviceEvent;

/**
 * A class to save a triplet of previous operations
 */
public class Predecessor {
    private MicroserviceEntity entity;
    private MicroserviceThread thread;
    private StopMicroserviceEvent stopEvent;

    public Predecessor(MicroserviceEntity e, MicroserviceThread t, StopMicroserviceEvent s) {
        entity = e;
        thread = t;
        stopEvent = s;
    }

    public MicroserviceEntity getEntity() {
        return entity;
    }

    public MicroserviceThread getThread() {
        return thread;
    }

    public StopMicroserviceEvent getStopEvent() {
        return stopEvent;
    }
}
