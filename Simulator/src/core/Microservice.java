package core;

import java.util.List;

import org.json.*;

public class Microservice {
	private String          service_name = "";
    private Integer         instances = 0;
    private String []       dependencies = {};
    private static Integer  instance_counter = 0;

    Microservice(JSONObject microservice)
    {
        try {
            this.service_name = microservice.getString("name");
            this.instances = microservice.getInt("instances");
            for(int i = 0; i < microservice.getJSONArray("dependencies").length(); ++i) {
                this.dependencies[i] = microservice.getJSONArray("dependencies").get(i).toString();
            }
            Microservice.instance_counter++;
        } catch(Exception e) {
            System.out.println("Error while assign json");
        }
    }
}
