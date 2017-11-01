package de.rss.fachstudie.desmojTest.modellingFeatures;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MicroserviceThread;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.advancedModellingFeatures.report.ResourceReporter;
import desmoj.core.report.Reporter;
import desmoj.core.simulator.*;

import java.util.Vector;

public class CustomRes extends QueueBased {

    private static int resNum = 0;
    private int id;
    private QueueList<MicroserviceThread> queue;
    private Vector<UsedResource> usedResources;
    private Vector<CustomResource> unusedResources;
    private CustomResourceDB resourceDB;
    private int limit;
    private int minimum;
    private int avail;

    private static class UsedResource {
        private MicroserviceThread thread;
        private Vector<CustomResource> occupiedResources;

        protected UsedResource(MicroserviceThread thread, Vector<CustomResource> occupiedResources) {
            this.thread = thread;
            this.occupiedResources = occupiedResources;
        }

        protected MicroserviceThread getMicroserviceThread() {
            return this.thread;
        }

        protected Vector<CustomResource> getOccupiedResources() {
            return this.occupiedResources;
        }
    }

    public CustomRes(MainModelClass owner, String name, int sortOrder, int capacity, boolean showInReport, boolean showInTrace) {
        super(owner, name, showInReport, showInTrace);

        this.id = resNum++;

        switch (sortOrder) {
            case QueueBased.FIFO:
                queue = new QueueListFifo<MicroserviceThread>();
                break;
            case QueueBased.LIFO:
                queue = new QueueListLifo<MicroserviceThread>();
                break;
            case QueueBased.RANDOM:
                queue = new QueueListRandom<MicroserviceThread>();
                break;
            default:
                queue = new QueueListFifo<MicroserviceThread>();
        }

        queue.setQueueBased(this);

        unusedResources = new Vector<CustomResource>();
        usedResources = new Vector<UsedResource>();
        resourceDB = owner.getResourceDB();

        this.limit = capacity;
        this.minimum = capacity;
        this.avail = capacity;

        if (capacity <= 0) {
            System.out.println("Attempting to create resource with limit <= 0, creating resource with limit 1 instead");

            limit = minimum = avail = 1;
        }

        for (int i = 0; i < capacity; i++) {
            CustomResource resource = new CustomResource(owner, name, this, true);
            unusedResources.addElement(resource);
        }
    }

    public CustomRes(MainModelClass owner, String name, int capacity, boolean showInReport, boolean showInTrace) {
        super(owner, name, showInReport, showInTrace);

        this.id = resNum++;

        queue = new QueueListFifo<MicroserviceThread>();
        queue.setQueueBased(this);

        unusedResources = new Vector<CustomResource>();
        usedResources = new Vector<UsedResource>();

        resourceDB = owner.getResourceDB();

        this.limit = capacity;
        this.minimum = capacity;
        this.avail = capacity;

        if (capacity <= 0) {
            System.out.println("Attempting to create resource with limit <= 0, creating resource with limit 1 instead");

            limit = minimum = avail = 1;
        }

        for (int i = 0; i < capacity; i++) {
            CustomResource resource = new CustomResource(owner, name, this, true);
            unusedResources.addElement(resource);
        }
    }

    /**
     * Activates the MicroserviceThread, given as a parameter of this method, as the next thread.
     * This thread should be a MicroserviceThread waiting in the queue for some resources.
     *
     * @param thread
     */
    protected void activateAsNext(MicroserviceThread thread) {
        if (thread != null) {

            if (thread.isScheduled()) {
                thread.skipTraceNote();
                thread.cancel();
            }

            // remember if the thread is blocked at the moment
            boolean wasBlocked = thread.isBlocked();

            // unblock the thread
            if (wasBlocked) {
                thread.setBlocked(false);
            }

            /*
            thread.skipTraceNote();
             */

            thread.activateAfter(current());

            // the thread status is still "Blocked"
            if (wasBlocked) {
                thread.setBlocked(true);
            }
        }
    }

    /**
     * Activates the first operatiopn waiting in the queue. That is a thread which was trying to acquire
     * resources, but there were not enough left in the Res. Or another thread was first in the queue
     * to be served. This method is called every time an thread returnes resources or when an thread
     * in the waiting-queue is satisfied.
     */
    protected void activateFirst() {
        MicroserviceThread first = queue.first();

        if (first != null) {

            if (first.isScheduled()) {
                first.skipTraceNote();
                first.cancel();
            }

            // remember if first is blocked at the moment
            boolean wasBlocked = first.isBlocked();

            // unblock the thread
            if (wasBlocked) {
                first.setBlocked(false);
            }

            first.skipTraceNote();
            first.activateAfter(current());

            // the status of first is still blocked
            if (wasBlocked) {
                first.setBlocked(true);
            }
        }
    }

    public Reporter createDefaultReporter() {
        return new ResourceReporter(this);
    }

    private CustomResource[] deliver(int n) {
        // TODO: get currentMicroserviceThread
        MicroserviceThread currentThread = new MicroserviceThread(getModel(), "tpm", false);

        // get resources from unused resource pool
        CustomResource[] resArray = new CustomResource[n];

        // fill the array of resources
        for (int i = 0; i < n; i++) {
            resArray[i] = unusedResources.firstElement();
            unusedResources.removeElement(unusedResources.firstElement());
        }

        updateProvidedRes(currentThread, resArray);

        // TODO: Debbuging

        return resArray;
    }

    protected int heldResources(MicroserviceThread thread) {
        int j = 0;

        for (int i = 0; i < usedResources.size(); i++) {
            UsedResource threadHoldRes = usedResources.elementAt(i);

            if (threadHoldRes.getMicroserviceThread() == thread) {
                j += threadHoldRes.getOccupiedResources().size();
            }
        }

        return j; // all the resources the thread holds atm
    }

    public boolean provide(int n) throws SuspendExecution {

        // TODO: get currentMicroserviceThread
        MicroserviceThread thread = new MicroserviceThread(getModel(), "tpm", false);

        if (thread == null) {
            return false;
        }

        if (n <= 0) {
            // trying to provide nothing or less
            return false;
        }

        // Total of resources acquired and already held by the current thread.
        int total = n + heldResources(thread);

        if (total > limit) {
            // trying to provide (in total) more than the capacity of the res
            return false;
        }

        if (queueLimit <= length()) {
            // Capacity limit of queue is reached
            return false;
        }

        // insert every thread in the queue for statistical reasons
        queue.insert(thread);

        if (n > avail || thread != queue.first()) {
            // waiting for resources
            resourceDB.noteResourceRequest(thread, this, n);

            do {
                thread.setBlocked(true);
                thread.skipTraceNote();
                thread.passivate();
            } while (n > avail || thread != queue.first());
        }

        // the thread has go the resources he wanted ...
        queue.remove(thread);
        thread.setBlocked(false);

        // Give the new first thread a chance
        activateFirst();

        // hand the resources over to the thread
        thread.obtainResources(deliver(n));

        updateStatistics(-n);

        // TODO: debugging

        return true;
    }

    public void takeBack(CustomResource[] returnedResources) {

        // TODO: get currentMicroserviceThread
        MicroserviceThread currentThread = new MicroserviceThread(getModel(), "tpm", false);

        if (currentThread == null) {
            return;
        }
        if (returnedResources.length <= 0) {
            // thread is releasing nothing
            return;
        }
        if (returnedResources.length > heldResources(currentThread)) {
            // trying to release more resources than the thread holds
            return;
        }

        // put the used resources back in the unused resources pool
        for (int i = 0; i < returnedResources.length; i++) {
            unusedResources.addElement(returnedResources[i]);
        }

        // Update which thread is holding which resources
        updateTakenBackRes(currentThread, returnedResources);

        updateStatistics(returnedResources.length);

        // update the resource database
        resourceDB.deleteResAllocation(this, currentThread, returnedResources.length);

        // TODO: debugging

        // give the new first thread in the queue a chance
        activateFirst();
    }

    public void takeBack(int n) {

        // TODO: get currentMicroserviceThread
        MicroserviceThread currentThread = new MicroserviceThread(getModel(), "tpm", false);

        if (currentThread == null) {
            return;
        }
        if (n <= 0) {
            // thread is releasing nothing
            return;
        }
        if (n > heldResources(currentThread)) {
            // thread is trying to release more resources than it's holding
            return;
        }

        // get the array of returned resources from the thread
        CustomResource[] returnedRes = currentThread.returnResources(this, n);

        // put the used resources back in the unused resources pool
        for (int i = 0; i < n; i++) {
            unusedResources.addElement(returnedRes[i]);
        }

        // update which thread is holding which resources
        updateTakenBackRes(currentThread, returnedRes);

        updateStatistics(n);

        // update the resource database
        resourceDB.deleteResAllocation(this, currentThread, n);

        // TODO: debugging

        activateFirst();
    }

    protected void updateProvidedRes(MicroserviceThread crntThread, CustomResource[] provRes) {
        boolean holdsResources = false;

        // search the whole vector
        for (int i = 0; i < usedResources.size(); i++) {
            // get hold of the usedresource pair
            UsedResource threadHoldRes = usedResources.elementAt(i);

            // is the thread already holding resources?
            if (threadHoldRes.getMicroserviceThread() == crntThread) {
                // update the held resources of the current Thread
                for (int j = 0; j < provRes.length; j++) {
                    threadHoldRes.getOccupiedResources().addElement(provRes[j]);
                }

                // tread already holds resources
                holdsResources = true;
            }
        }

        if (!holdsResources) {
            // the thread does not hold any resources
            Vector<CustomResource> occupiedRes = new Vector<CustomResource>();

            // copy al elements of the array to the vector
            for (int i = 0; i < provRes.length; i++) {
                occupiedRes.addElement(provRes[i]);
            }

            // Construct a new UsedResource object with the vector
            UsedResource ur = new UsedResource(crntThread, occupiedRes);
            usedResources.addElement(ur);
        }
    }

    protected void updateStatistics(int n) {
        TimeInstant now = presentTime();

        avail += n;

        if (avail < minimum) {
            minimum = avail;
        }
    }

    protected void updateTakenBackRes(MicroserviceThread crntThread, CustomResource[] returnedRes) {

        // search the whole vector
        for (int i = 0; i < usedResources.size(); i++) {
            // get hold of the usedResource pair
            UsedResource threadHoldRes = usedResources.elementAt(i);

            if (threadHoldRes.getMicroserviceThread() == crntThread) {
                // remove the resources form the vector of used resources
                for (int j = 0; j < returnedRes.length; j++) {
                    threadHoldRes.getOccupiedResources().removeElement(returnedRes[j]);
                }

                // are all resources form this thread taken back
                if (threadHoldRes.getOccupiedResources().isEmpty()) {
                    usedResources.removeElementAt(i);
                }
            }
        }
    }
}