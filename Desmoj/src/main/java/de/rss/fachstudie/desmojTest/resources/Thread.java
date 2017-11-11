package de.rss.fachstudie.desmojTest.resources;

import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.Microservice;
import de.rss.fachstudie.desmojTest.events.StopEvent;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

/**
 * A Thread describes a part of a microservice instance.
 * This thread can performs work in form of operations.
 *
 * id:  the service id it belongs to
 * tid: the thread id (map to the number of existing threads in the service)
 */
public class Thread extends Entity {
    MainModelClass model;
    private int id;
    private int sid;
    private int tid;
    private int demand;
    private StopEvent endEvent;
    private Microservice service;
    private MessageObject mobject;
    private double creationTime;
    private boolean isBlocked;

    public Thread(Model owner, String name, boolean b, int demand, StopEvent end, Microservice service, MessageObject mo) {
        super(owner, name, b);

        model = (MainModelClass) owner;
        this.id = service.getId();
        this.sid = service.getSid();
        this.tid = service.getThreads().size();
        this.demand = demand;
        this.endEvent = end;
        this.service = service;
        this.mobject = mo;
        creationTime = model.presentTime().getTimeAsDouble();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public double getCreationTime() {
        return creationTime;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
    }

    public int getDemand() {
        return this.demand;
    }

    public void subtractDemand(int value) {
        if(demand - value > 0)
            this.demand -= value;
        else
            demand = 0;
    }

    public void scheduleEndEvent() {
        endEvent.schedule(service, this, mobject);
    }

//    /**
//     * Schedules this Thread to be activated
//     * directly after the given schedulable, which itself
//     * must already be scheduled
//     *
//     * @param after
//     */
//    public void activateAfter(Schedulable after) {
//
//        if (after == null) {
//            return;
//        }
//        if (isBlocked()) {
//            return;
//        }
//
//        // TODO: debugging
//
//        // TODO: schedule this thread
//    }
//
//    public void passivate() throws DelayedInterruptException, InterruptException, SuspendExecution {
//        // TODO: implement
//        /*
//        Strand.parkAndUnpark(this.getModel().getExperiment().getSchedulerStrand());
//
//        if (getModel().getExperiment().isAborted()) {
//            throw (new SimFinishedException(getModel(), getName(), presentTime()));
//        }
//        */
//    }
//
//    /**
//     * Maked the Thread obtain an array of resources and store them for further usage.
//     *
//     * @param obtainedResources
//     */
//    public void obtainResources(CustomResource[] obtainedResources) {
//        if (obtainedResources.length <= 0) {
//            //parameter contains nothing
//            return;
//        }
//
//        // put all the obtained resources in the vector of used resources
//        for (CustomResource obtainedResource : obtainedResources) {
//            usedResources.addElement(obtainedResource);
//        }
//
//        // TODO: debugging
//    }
//
//    public CustomResource[] returnResources(CustomRes resPool, int n) {
//        if (n <= 0) {
//            // return nothing
//            return null;
//        }
//        if (usedResources.isEmpty()) {
//            // return nothing
//            return null;
//        }
//
//        // make the arra yo store the resources which will be returned
//        CustomResource[] returningRes = new CustomResource[n];
//
//        // Counter for the index of the array
//        int j = 0;
//
//        // Collect all the resources from the Vector of usedResources
//        for (int i = 0; i < usedResources.size(); i++) {
//            if ((usedResources.elementAt(i)).getRes() == resPool) {
//                // put res in array
//                returningRes[j] = usedResources.elementAt(i);
//                j++;
//            }
//            if (j == n) {
//                break;
//            }
//        }
//
//        // Remove the returning resource form the vector of usedResources
//        for (int m = 0; m < j; m++) {
//            usedResources.removeElement(returningRes[m]);
//        }
//
//        if (j < n) {
//            // TODO: array is not full, send warning
//        }
//
//        // TODO: debugging
//        return returningRes;
//    }
}
