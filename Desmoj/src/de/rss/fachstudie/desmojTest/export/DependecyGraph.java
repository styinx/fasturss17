package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.entities.Operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

public class DependecyGraph {
    private List<Integer> nodes;
    private HashMap<Integer, MicroserviceEntity> microservices;
    public DependecyGraph(HashMap<Integer, MicroserviceEntity> microservices, int id) {
        this.microservices = microservices;
        this.nodes = new ArrayList<>();
    }

    public int getIdByName(String name){
        for(int i = 0; i < microservices.size() ; i ++){
            if(name.equals(microservices.get(i).getName())){
                return microservices.get(i).getId();
            }
        }
        return -1;
    }


    public String printGraph() {
        String nodes = printNodes();
        String links = printLinks();
        String html = "var graph = {nodes:[" + nodes.substring(0, nodes.length() - 2) + "],"
                + "links:[" + links.substring(0, links.length() - 2) + "]};";
        return html;
    }

    private String printNodes() {
        String json = "";
        nodes = new ArrayList<>();
        for(Integer id : microservices.keySet()) {
            if(!nodes.contains(id)) {
                String labels = "";
                for (Operation op : microservices.get(id).getOperations()) {
                    labels += "'" + op.getName() + "', ";
                }
                nodes.add(id);
                for(int i = 0; i < microservices.get(id).getInstances(); ++i) {
                    json += "{ name: '" + microservices.get(id).getName() +
                        "', id: " + (id + microservices.get(id).getInstances() + microservices.keySet().size() * i) +
                        ", labels : [" + labels.substring(0, labels.length() - 2) + "]" +
                        ", group: " + id + "}, ";
                }
            }
        }
        return json;
    }

    private String printLinks() {
        String json = "";
        nodes = new ArrayList<>();
        for(Integer id : microservices.keySet()) {
            if(!nodes.contains(id)) {
                nodes.add(id);
                String labels = "";
                for (Operation op : microservices.get(id).getOperations()) {
                    labels += "'" + op.getName() + "', ";
                    for(SortedMap<String, String> depService : op.getDependencies()) {
                        int depId = getIdByName(depService.get("service"));
                        json += "{ source: " + id
                                + ", target : " + depId
                                + ", value : " + microservices.get(id).getInstances() + "}, ";
                    }
                }
            }
        }
        return json;
    }
}