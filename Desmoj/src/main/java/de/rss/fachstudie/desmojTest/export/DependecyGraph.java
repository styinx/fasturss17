package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.entities.Microservice;
import de.rss.fachstudie.desmojTest.entities.Operation;
import de.rss.fachstudie.desmojTest.models.MainModelClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

/**
 * The <code>DependencyGraph</code> class is used in order to create the graph that displays the dependencies between
 * all of the systems microservice instances.
 */
public class DependecyGraph {
    private MainModelClass model;
    private List<Integer> nodes;
    private HashMap<Integer, Microservice> microservices;

    /**
     * Instantiates <code>DependencyGraph</code>.
     *
     * @param model         MainModelClass: The model which owns this DependencyGraph
     * @param microservices HashMap<Integer, Microservice>
     * @param id            int: The ID of this DependencyGraph
     */
    public DependecyGraph(MainModelClass model, HashMap<Integer, Microservice> microservices, int id) {
        this.model = model;
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

    /**
     * Create the javascript code for the <code>DependencyGraph</code>.
     *
     * @return String: js code for the graph
     */
    public String printGraph() {
        String nodes = printNodes();
        String links = printLinks();
        String html = "var graphMinimalistic = '" + model.getReport() + "';\n" + "var graph = {nodes:[";
        if(nodes.length() > 2)
            html += nodes.substring(0, nodes.length() - 2) + "], ";
        else
            html += "], ";
        if(links.length() > 2)
            html += "links:[" + links.substring(0, links.length() - 2) + "]};";
        else
            html += "]};";
        return html;
    }

    /**
     * Create the javascript code for the nodes of the <code>DependencyGraph</code>.
     *
     * @return String: js code for the nodes of the graph
     */
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

                int instanceLimit = microservices.get(id).getInstances();
                if(model.getReport().equals("minimalistic")) {
                    instanceLimit = (microservices.get(id).getInstances() < 10) ? microservices.get(id).getInstances() : 10;
                }
                for(int i = 0; i < instanceLimit; ++i) {
                    json += "{ name: '" + microservices.get(id).getName() +
                            "', id: " + (id + microservices.get(id).getInstances() + microservices.keySet().size() * i) +
                            ", labels : [" + labels.substring(0, labels.length() - 2) + "]" +
                            ", group: " + id + "}, ";
                }
            }
        }
        return json;
    }

    /**
     * Create the javascript code for the links between the nodes in the <code>DependencyGraph</code>.
     *
     * @return String: js code for links in the graphs
     */
    private String printLinks() {
        String json = "";
        nodes = new ArrayList<>();
        for(Integer id : microservices.keySet()) {

            int instanceLimit = microservices.get(id).getInstances();
            if(model.getReport().equals("minimalistic")) {
                instanceLimit = (microservices.get(id).getInstances() < 10) ? microservices.get(id).getInstances() : 10;
            }

            if(!nodes.contains(id)) {
                nodes.add(id);
                String labels = "";
                for (Operation op : microservices.get(id).getOperations()) {
                    labels += "'" + op.getName() + "', ";
                    for(SortedMap<String, String> depService : op.getDependencies()) {
                        int depId = getIdByName(depService.get("service"));
                        json += "{ source: " + id
                                + ", target : " + depId
                                + ", value : " + instanceLimit + "}, ";
                    }
                }
            }
        }
        return json;
    }
}