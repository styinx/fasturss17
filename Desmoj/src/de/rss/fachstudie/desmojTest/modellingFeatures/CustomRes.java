package de.rss.fachstudie.desmojTest.modellingFeatures;

import de.rss.fachstudie.desmojTest.entities.Operation;
import desmoj.core.advancedModellingFeatures.report.ResourceReporter;
import desmoj.core.report.Reporter;
import desmoj.core.simulator.*;

import java.util.Vector;

public class CustomRes extends QueueBased {

    private static int resNum = 0;
    private int id;
    private QueueList<Operation> queue;
    private Vector<UsedResource> usedResources;
    private Vector<CustomResource> unusedResources;
    private ResourceDB resourceDB;
    private int limit;
    private int minimum;
    private int avail;

    private static class UsedResource {
        private Operation operation;
        private Vector<Resource> occupiedResources;

        protected UsedResource(Operation operation, Vector<Resource> occupiedResources) {
            this.operation = operation;
            this.occupiedResources = occupiedResources;
        }

        protected Operation getOperation() {
            return this.operation;
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
                queue = new QueueListFifo<Operation>();
                break;
            case QueueBased.LIFO:
                queue = new QueueListLifo<Operation>();
                break;
            case QueueBased.RANDOM:
                queue = new QueueListRandom<Operation>();
                break;
            default:
                queue = new QueueListFifo<Operation>();
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

        queue = new QueueListFifo<Operation>();
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
     * Activates the Operation, given as a parameter of this method, as the next operation.
     * This operation should be a Operation waiting in the queue for some resources.
     *
     * @param operation
     */
    protected void activateAsNext(Operation operation) {
        if (operation != null) {
            // TODO: implement magic
        }
    }

    /**
     * Activates the first operatiopn waiting in the queue. That is a operation which was trying to acquire
     * resources, but there were not enough left in the Res. Or another operation was first in the queue
     * to be served. This method is called every time an operation returnes resources or when an operation
     * in the waiting-queue is satisfied.
     */
    protected void activateFirst() {
        Operation first = queue.first();

        if (first != null) {
            // TODO: implement magic
        }
    }

    public Reporter createDefaultReporter() {
        return new ResourceReporter(this);
    }

    private Resource[] deliver(int n) {
        // TODO: continue implementing
        return null;
    }

}



























