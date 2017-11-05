package de.rss.fachstudie.desmojTest.modellingFeatures;

import de.rss.fachstudie.desmojTest.entities.Thread;
import desmoj.core.simulator.Model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class CustomResourceDB {

    private Model owner;
    private boolean debugMode;
    private Hashtable<CustomRes, Vector<AssignedResources>> assignmentTable;
    private Hashtable<Thread, RequestedResources> requestTable;
    private Hashtable<CustomRes, Integer> effCapacity;

    private static class AssignedResources {
        private Thread thread;
        private int seizedUnits;

        protected AssignedResources(Thread thread, int seizedRes) {
            this.thread = thread;
            this.seizedUnits = seizedRes;
        }

        protected Thread getThread() {
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
        requestTable = new Hashtable<Thread, RequestedResources>();
        effCapacity = new Hashtable<CustomRes, Integer>();

        debugOn();
    }

    private boolean checkThread(Thread thread) {
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

    /**
     * Deletes an entry in the resource data base.
     * (should be called when a Sim-process is done with it's requested number of resources from a
     * resource pool)
     * Check Desmoj documentation for more information.
     *
     * @param resPool
     * @param doneThread
     * @param quantity
     */
    public void deleteResAllocation(CustomRes resPool, Thread doneThread, int quantity) {

        // Check for null references and negative quantity
        if (!checkThread(doneThread)) {
            return;
        }
        if (!checkRes(resPool)) {
            return;
        }
        if (quantity <= 0) {
            return;
        }

        // Check if there is an entry for the given res pool in the assignment hashtable
        if (!assignmentTable.containsKey(resPool)) {
            // The res pool is not registered in the resourceDB
            return;
        } else {
            // Get hold of corresponding vector
            Vector<AssignedResources> resPoolVec = (Vector<AssignedResources>) assignmentTable.get(resPool);

            boolean foundInVec = false;

            // Search vector to find given thread
            for (int i = 0; i < resPoolVec.size(); i++) {
                AssignedResources assigRes = (AssignedResources) resPoolVec.elementAt(i);

                if (assigRes.getThread() == doneThread) {
                    foundInVec = true;

                    // Delete entry in the vector
                    resPoolVec.remove(assigRes);

                    if (!(assigRes.getSeizedUnits() == quantity)) {
                        if (quantity > assigRes.getSeizedUnits()) {
                            // TODO: send warning
                        } else {
                            // Less resources deleted than once allocated
                            assigRes.setSeizedUnits(assigRes.getSeizedUnits() - quantity);
                            resPoolVec.add(assigRes);
                        }
                    }
                }
            }

            if (!foundInVec) {
                // Given thread not found in vector
                // TODO: send warning
            }

            if (resPoolVec.isEmpty()) {
                assignmentTable.remove(resPool);
            } else {
                // still something in the vector
                assignmentTable.put(resPool, resPoolVec);
            }

            if (debugMode) {
                // TODO: print debug line
            }
        }
    }

    /**
     * Deletes an entry in the resource data base.
     * (should be called when a Sim-process receives it's requested number of resources from a resource
     * pool)
     * Check desmoj documentation for more information.
     *
     * @param gainThread
     * @param resPool
     * @param quantity
     */
    public void deleteResRequest(Thread gainThread, CustomRes resPool, int quantity) {

        // Check for null references and negative quantity
        if (!checkThread(gainThread)) {
            return;
        }
        if (!checkRes(resPool)) {
            return;
        }
        if (quantity <= 0) {
            return;
        }

        // Check if there is an entry for the given thread in the request table
        if (!requestTable.containsKey(gainThread)) {
            // The thread is not registered in the resourceDB
            return;
        }

        RequestedResources reqRes = (RequestedResources) requestTable.get(gainThread);

        if (reqRes.getResPool() != resPool) {
            // TODO: send warning
        }

        if (quantity > reqRes.getRequestedUnits()) {
            // more resources will be deleted than the thread has requested
            // TODO: send warning
            quantity = reqRes.getRequestedUnits();
        }

        if (reqRes.getRequestedUnits() == quantity) {
            requestTable.remove(gainThread);
        } else {
            // delete less resources than once requested
            reqRes.setRequestedUnits(reqRes.getRequestedUnits() - quantity);
            requestTable.put(gainThread, reqRes);
        }

        if (debugMode) {
            //TODO: print debug line
        }
    }

    /**
     * Makes an entry in the resource data base when a thread is allocating
     * a number of resources from a resource pool.
     *
     * @param resPool
     * @param allocatingThread
     * @param quantity
     */
    public void noteResourceAllocation(CustomRes resPool, Thread allocatingThread, int quantity) {
        // Check for null references and negative quantity
        if (!checkThread(allocatingThread)) {
            return;
        }
        if (!checkRes(resPool)) {
            return;
        }
        if (quantity <= 0) {
            return;
        }

        AssignedResources assigRes = new AssignedResources(allocatingThread, quantity);

        if (assignmentTable.containsKey(resPool)) {
            // entry for given res pool already exists in the assignment table

            boolean threadAlreadyAlloc = false;

            Vector<AssignedResources> assigResVec = (Vector<AssignedResources>) assignmentTable.get(resPool);

            // check if the thread is already allocating resources from this res pool
            for (int i = 0; i < assigResVec.size(); i++) {
                AssignedResources alreadyAssigRes = (AssignedResources) assigResVec.elementAt(i);

                if (alreadyAssigRes.getThread() == allocatingThread) {
                    // delete old entry
                    assigResVec.remove(alreadyAssigRes);

                    // add new number to old number
                    alreadyAssigRes.setSeizedUnits(alreadyAssigRes.getSeizedUnits() + quantity);

                    assigResVec.add(alreadyAssigRes);

                    threadAlreadyAlloc = true;
                }
            }

            if (!threadAlreadyAlloc) {
                assigResVec.add(assigRes);
            }

            assignmentTable.put(resPool, assigResVec);
        } else {
            // no entry for this res pool in the assignment table
            Vector<AssignedResources> resPoolVector = new Vector<AssignedResources>();
            resPoolVector.add(assigRes);
            assignmentTable.put(resPool, resPoolVector);
        }

        if (debugMode) {
            // TODO: make debug line
        }
    }

    /**
     * Makes an entry in the resource data base when a thread is requesting
     * a number of resources from a resource pool.
     *
     * @param requestingThread
     * @param resPool
     * @param quantity
     */
    public void noteResourceRequest(Thread requestingThread, CustomRes resPool, int quantity) {
        // Check for null references and negative quantity
        if (!checkThread(requestingThread)) {
            return;
        }
        if (!checkRes(resPool)) {
            return;
        }
        if (quantity <= 0) {
            return;
        }

        RequestedResources reqResources = new RequestedResources(resPool, quantity);

        if (requestTable.containsKey(requestingThread)) {
            // an entry for the given requesting thread already exists in the request table
            //TODO: send warning
            return;
        }

        requestTable.put(requestingThread, reqResources);

        if (debugMode) {
            //TODO: print debug line
        }
    }

    /**
     * Calculates the new effective available capacity of all resources the
     * given SimProcess holds units from. The given SimProcess will return the
     * resources he holds in the future, so the returning resource units will be
     * added to the effective available capacity of the resources.
     *
     * @param thread
     */
    private void reduce(Thread thread) {
        // get the Vector of all the resources this thread holds at the
        // moment
        Vector<CustomResource> usedResVec = thread.getUsedResources();

        // for every resource that the thread holds
        for (Enumeration<CustomResource> e = usedResVec.elements(); e.hasMoreElements(); ) {
            // get the resource
            CustomResource resource = e.nextElement();

            // get the resPool the resource belongs to
            CustomRes resPool = resource.getRes();

            // increment the effective available capacity of the res pool
            int effCap = ((Integer) effCapacity.get(resPool)).intValue();
            effCap++;

            effCapacity.put(resPool, Integer.valueOf(effCap));
        }
    }
}
