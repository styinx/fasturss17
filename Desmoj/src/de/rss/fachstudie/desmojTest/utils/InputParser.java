package de.rss.fachstudie.desmojTest.utils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class InputParser {

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