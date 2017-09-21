package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.utils.Node;

import java.util.HashMap;

public class DependecyGraph {
    private Node root;
    private HashMap<Integer, MicroserviceEntity> microservices;
    public DependecyGraph(HashMap<Integer, MicroserviceEntity> microservices, int id) {
        this.microservices = microservices;
        this.root = new Node(microservices.get(id).getName());
        this.fillGraph(root, id);
    }

    public int getIdByName(String name){
        for(int i = 0; i < microservices.size() ; i ++){
            if(name.equals(microservices.get(i).getName())){
                return microservices.get(i).getId();
            }
        }
        return -1;
    }

    public Node getRoot() {
        return this.root;
    }

    private void fillGraph(Node parent, int id) {
        for(String ms : microservices.get(id).getDependencies()) {
            Node child = new Node(ms);
            int childId = getIdByName(ms);
            root.setChild(child);
            if(childId != -1)
                fillGraph(child, childId);
        }
    }

    //TODO make html output
    public void printGraph(Node root) {
        System.out.println(root.getValue());
        for(Node child : root.getChildren()) {
            printGraph(child);
        }
    }
}
