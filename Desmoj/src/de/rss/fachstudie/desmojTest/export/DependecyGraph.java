package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.utils.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DependecyGraph {
    private Node root;
    private List<String> services;
    private HashMap<Integer, MicroserviceEntity> microservices;
    public DependecyGraph(HashMap<Integer, MicroserviceEntity> microservices, int id) {
        this.microservices = microservices;
        this.services = new ArrayList<String>();
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
            parent.setChild(child);
            if(childId != -1)
                fillGraph(child, childId);
        }
    }

    public String printGraph() {
        String nodes = printNodes("", root);
        String links = printLinks("", root);
        String html = "{nodes:[" + nodes.substring(0, nodes.length() - 1) + "],"
                + "links:[" + links.substring(0, links.length() - 1) + "]}";
        return html;
    }

    public String printNodes(String html, Node parent) {
        String json = html;
        if(!services.contains(parent.getValue())) {
            services.add(parent.getValue());
            json += "{ name : '" + parent.getValue() + "', id : '" + parent.getValue() + "'},";
        }

        for(Node child : parent.getChildren()) {
            json += printNodes("", child);
        }
        return json;
    }

    public String printLinks(String html, Node parent) {
        String json = html;

        for(Node child : parent.getChildren()) {
            json += "{ source : '" + parent.getValue() + "'"
                    + ", target : '" + getIdByName(child.getValue()) + "'},";
            json += printLinks("", child);
        }
        return json;
    }
}
