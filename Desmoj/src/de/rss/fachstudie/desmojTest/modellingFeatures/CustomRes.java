package de.rss.fachstudie.desmojTest.modellingFeatures;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MicroserviceThread;
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
    private ResourceDB resourceDB;
    private int limit;
    private int minimum;
    private int avail;

    private static class UsedResource {
        private MicroserviceThread thread;
        private Vector<Resource> occupiedResources;

        protected UsedResource(MicroserviceThread thread, Vector<Resource> occupiedResources) {
            this.thread = thread;
            this.occupiedResources = occupiedResources;
        }

        protected MicroserviceThread getMicroserviceThread() {
            return this.thread;
        }

        protected Vector<Resource> getOccupiedResources() {
            return this.occupiedResources;
        }
    }

    public CustomRes(Model owner, String name, int sortOrder, int capacity, boolean showInReport, boolean showInTrace) {
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
        resourceDB = owner.getExperiment().getResourceDB();

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

    public CustomRes(Model owner, String name, int capacity, boolean showInReport, boolean showInTrace) {
        super(owner, name, showInReport, showInTrace);

        this.id = resNum++;

        queue = new QueueListFifo<MicroserviceThread>();
        queue.setQueueBased(this);

        unusedResources = new Vector<CustomResource>();
        usedResources = new Vector<UsedResource>();

        resourceDB = owner.getExperiment().getResourceDB();

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
            // TODO: implement magic
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
            // TODO: implement magic
        }
    }

    public Reporter createDefaultReporter() {
        return new ResourceReporter(this);
    }

    private Resource[] deliver(int n) {
        // TODO: implement
        return null;
    }

    protected int heldResources(MicroserviceThread thread) {
        // TODO: implement
        return -1;
    }

    public boolean provide(int n) throws SuspendExecution {
        // TODO: implement
        return false;
    }

    public void takeBack(CustomResource[] returnedResources) {
        // TODO: implement
    }

    public void takeBack(int n) {
        // TODO: implement
    }

    protected void updateProvidedRes(MicroserviceThread crntThread, CustomResource[] provRes) {
        // TODO: implement
    }

    protected void updateTakenBackRes(MicroserviceThread crntThread, Resource[] returnedRes) {
        // TODO: implement
    }

}



























