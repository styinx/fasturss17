package de.rss.fachstudie.desmojTest.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.events.InitialChaosMonkeyEvent;
import de.rss.fachstudie.desmojTest.events.InitialMicroserviceEvent;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * The InputParser reads a valid json file and converts the contents into microservices and operations.
 */
public class InputParser {
    public static MicroserviceEntity[] microservices;
    public static InitialMicroserviceEvent[] generators;
    public static InitialChaosMonkeyEvent[] monkeys;
    public static HashMap<String, String> simulation;

    public InputParser(String filename) {
        try {
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(new JsonReader(new FileReader(filename)), JsonObject.class);
            simulation = gson.fromJson(root.get("simulation"), new TypeToken<HashMap<String, String>>(){}.getType());
            microservices = gson.fromJson(root.get("microservices"), MicroserviceEntity[].class);
            generators = gson.fromJson(root.get("generators"), InitialMicroserviceEvent[].class);
            monkeys = gson.fromJson(root.get("chaosmonkeys"), InitialChaosMonkeyEvent[].class);
        } catch(FileNotFoundException ex) {
            System.out.println("File " + filename + " not found");
        }
    }

    /**
     * Create Microservice Entities from a JSON input file.
     *
     * @param filename
     * @return MicroServiceEntity[]
     * TODO: unused
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