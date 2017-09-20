package de.rss.fachstudie.desmojTest.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class InputParser {
    private JsonElement element;

    public InputParser(String filename) {
        try {
            if(!filename.equals("")) {
                JsonParser parser = new JsonParser();
                element = parser.parse(new FileReader(filename));
            }
        } catch(FileNotFoundException ex) {
            System.out.println("Filer error.");
        }
    }

    public JsonElement getElement() {
        return element;
    }
}