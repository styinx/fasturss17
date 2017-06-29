package core;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import org.json.*;

public class Simulator {
	private List<Microservice>  microservices = null;

    Simulator(String filename)
    {
        try {
            this.microservices = new ArrayList<Microservice>();
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            JSONObject obj = new JSONObject(content);
            JSONObject microservices = obj.getJSONObject("microservices");
            for (int i = 0; i < microservices.names().length(); ++i) {
                JSONObject microservice = new JSONObject(microservices.get(microservices.names().get(i).toString()).toString());
                this.microservices.add(new Microservice(microservice));
            }
        } catch(Exception e) {
            System.out.println("Error while reading json");
        }

    }
	
	public static void main(String args[])
	{
		Simulator sim = new Simulator("res/example.json");
	}
}
