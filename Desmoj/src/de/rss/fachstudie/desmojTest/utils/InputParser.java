package de.rss.fachstudie.desmojTest.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.entities.Operation;
import de.rss.fachstudie.desmojTest.events.InitialErrorMonkeyEvent;
import de.rss.fachstudie.desmojTest.events.InitialMicroserviceEvent;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class InputParser {

    public static void read(String filename) {
        try {
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(new JsonReader(new FileReader(filename)), JsonObject.class);
            MicroserviceEntity[] microservices = gson.fromJson(root.get("microservices"), MicroserviceEntity[].class);
            InitialMicroserviceEvent[] generators = gson.fromJson(root.get("generators"), InitialMicroserviceEvent[].class);
            InitialErrorMonkeyEvent[] monkeys = gson.fromJson(root.get("errormonkeys"), InitialErrorMonkeyEvent[].class);
            /*for(MicroserviceEntity microservice : microservices) {
                for(Operation operation : microservice.getOperations()) {
                    for(Operation op : operation.getDependencies()) {
                        System.out.println(op.getName() + op.getService());
                    }
                }
            }*/
        } catch(FileNotFoundException ex) {
            System.out.println("File " + filename + " not found");
        }
    }

    /**
     * Create Microservice Entities from a JSON input file.
     *
     * @param filename
     * @return MicroServiceEntity[]
     */
    public static MicroserviceEntity[] createMicroserviceEntities(String filename) {
        try {
            if(!filename.equals("")) {
                Gson gson = new Gson();
                MicroserviceEntity[] entities = gson.fromJson(new JsonReader(new FileReader(filename)), MicroserviceEntity[].class);
                return entities;
            } else {
                System.out.println("Filename empty");
            }
            return null;
        } catch(FileNotFoundException ex) {
            System.out.println("File " + filename + " not found");
            return null;
        }
    }
}