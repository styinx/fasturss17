package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.Predecessor;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import de.rss.fachstudie.desmojTest.resources.Thread;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;

public class FinishEvent extends ExternalEvent {
    private MainModelClass model;

    public FinishEvent(Model owner, String s, boolean b) {
        super(owner, s, b);

        model = (MainModelClass) owner;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {
        // Finish all threads in the task queue and save the response time
        for (int id = 0; id < model.taskQueues.size(); ++id) {
            for (MessageObject task : model.taskQueues.get(id)) {
                while (task.getDependency().size() > 0) {
                    Predecessor predecessor = task.removeDependency();
                    Thread thread = predecessor.getThread();
                    model.threadStatistics.get(id).get(thread.getSid()).update(model.presentTime().getTimeAsDouble() - thread.getCreationTime());
                }
            }
        }
    }
}
