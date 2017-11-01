package de.rss.fachstudie.desmojTest.entities;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.modellingFeatures.CustomRes;
import de.rss.fachstudie.desmojTest.modellingFeatures.CustomResource;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.exception.DelayedInterruptException;
import desmoj.core.exception.InterruptException;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.Schedulable;
import desmoj.core.simulator.TimeInstant;

import java.util.Vector;

/**
 * A MicroserviceThread describes a part of a microservice instance.
 * This thread can performs work in form of operations.
 *
 * id:  the service id it belongs to
 * tid: the thread id (map to the number of existing threads in the service)
 */
public class MicroserviceThread extends Entity {
    MainModelClass model;
    private int id;
    private int tid;
    private TimeInstant creationTime;
    private boolean isBlocked;
    private Vector<CustomResource> usedResources;

    public MicroserviceThread(Model owner, String name, boolean b) {
        super(owner, name, b);

        model = (MainModelClass) owner;
        creationTime = model.presentTime();
        usedResources = new Vector<CustomResource>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public TimeInstant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(TimeInstant creation) {
        this.creationTime = creation;
    }

    public Vector<CustomResource> getUsedResources() {
        return usedResources;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
    }

    /**
     * Schedules this MicroserviceThread to be activated
     * directly after the given schedulable, which itself
     * must already be scheduled
     *
     * @param after
     */
    public void activateAfter(Schedulable after) {

        if (after == null) {
            return;
        }
        if (isBlocked()) {
            return;
        }

        // TODO: debugging

        // TODO: schedule this thread
    }

    public void passivate() throws DelayedInterruptException, InterruptException, SuspendExecution {
        // TODO: implement
        /*
        Strand.parkAndUnpark(this.getModel().getExperiment().getSchedulerStrand());

        if (getModel().getExperiment().isAborted()) {
            throw (new SimFinishedException(getModel(), getName(), presentTime()));
        }
        */
    }

    /**
     * Maked the MicroserviceThread obtain an array of resources and store them for further usage.
     *
     * @param obtainedResources
     */
    public void obtainResources(CustomResource[] obtainedResources) {
        if (obtainedResources.length <= 0) {
            //parameter contains nothing
            return;
        }

        // put all the obtained resources in the vector of used resources
        for (CustomResource obtainedResource : obtainedResources) {
            usedResources.addElement(obtainedResource);
        }

        // TODO: debugging
    }

    public CustomResource[] returnResources(CustomRes resPool, int n) {
        if (n <= 0) {
            // return nothing
            return null;
        }
        if (usedResources.isEmpty()) {
            // return nothing
            return null;
        }

        // make the arra yo store the resources which will be returned
        CustomResource[] returningRes = new CustomResource[n];

        // Counter for the index of the array
        int j = 0;

        // Collect all the resources from the Vector of usedResources
        for (int i = 0; i < usedResources.size(); i++) {
            if ((usedResources.elementAt(i)).getRes() == resPool) {
                // put res in array
                returningRes[j] = usedResources.elementAt(i);
                j++;
            }
            if (j == n) {
                break;
            }
        }

        // Remove the returning resource form the vector of usedResources
        for (int m = 0; m < j; m++) {
            usedResources.removeElement(returningRes[m]);
        }

        if (j < n) {
            // TODO: array is not full, send warning
        }

        // TODO: debugging
        return returningRes;
    }
}
