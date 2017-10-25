package de.rss.fachstudie.desmojTest.modellingFeatures;

import de.rss.fachstudie.desmojTest.entities.MicroserviceThread;
import desmoj.core.simulator.Model;

import java.util.Hashtable;
import java.util.Vector;

public class CustomResourceDB {

    private Model owner;
    private boolean debugMode;
    private Hashtable<CustomRes, Vector<AssignedResources>> assignmentTable;
    private Hashtable<MicroserviceThread, RequestedResources> requestTable;
    private Hashtable<CustomRes, Integer> effCapacity;

    private static class AssignedResources {
        private MicroserviceThread thread;
        private int seizedUnits;

        protected AssignedResources(MicroserviceThread thread, int seizedRes) {
            this.thread = thread;
            this.seizedUnits = seizedRes;
        }

        protected MicroserviceThread getThread() {
            return this.thread;
        }

        protected int getSeizedUnits() {
            return this.seizedUnits;
        }

        protected void setSeizedUnits(int newQuantity) {
            this.seizedUnits = newQuantity;
        }
    }

    private static class RequestedResources {
        private CustomRes resPool;
        private int requestedUnits;

        protected RequestedResources(CustomRes resPool, int requestedRes) {
            this.resPool = resPool;
            this.requestedUnits = requestedRes;
        }

        protected CustomRes getResPool() {
            return resPool;
        }

        protected int getRequestedUnits() {
            return requestedUnits;
        }

        protected void setRequestedUnits(int requestedUnits) {
            this.requestedUnits = requestedUnits;
        }
    }

    public CustomResourceDB(Model owner) {
        this.owner = owner;

        assignmentTable = new Hashtable<CustomRes, Vector<AssignedResources>>();
        requestTable = new Hashtable<MicroserviceThread, RequestedResources>();
        effCapacity = new Hashtable<CustomRes, Integer>();

        debugOn();
    }

    private boolean checkThread(MicroserviceThread thread) {
        if (thread == null) {
            return false;
        }
        return true;
    }

    private boolean checkRes(CustomRes res) {
        if (res == null) {
            return false;
        }
        return true;
    }

    public void debugOn() {
        this.debugMode = true;
    }

    public void debugOff() {
        this.debugMode = false;
    }

    public void deleteResAllocation(CustomRes resPool, MicroserviceThread doneThread, int quantity) {
        // TODO: implement
    }

    public void deleteResRequest(MicroserviceThread gainThread, CustomRes resPool, int quantity) {
        // TODO: implement
    }

    public void noteResourceAllocation(CustomRes resPool, MicroserviceThread allocatingThread, int quantity) {
        // TODO: implement
    }

    public void noteResourceRequest(MicroserviceThread requestingThread, CustomRes resPool, int quantity) {
        // TODO: implement
    }

    private void reduce(MicroserviceThread thread) {
        // TODO: implement
    }
}
