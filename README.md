# Name placeholder

**Table of contents:**

- [Simulator](#Sim)
	- [Description](#Sim-Des)
	- [JSON Format](#Sim-Form)
	- [Usage](#Sim-Use)
	- [Documentation](#Sim-Doc)

## <a name="Sim"></a>Simulator

### <a name="Sim-Des"></a>Description
---

The tool is used for ...

### <a name="Sim-Form"></a>JSON Format
---

<pre>
{
  "simulation":
  {
    "experiment" : <i>name of the experiment [String]</i>,
    "model" : <i>name of the model [String]</i>,
    "duration" : <i>simulation time in seconds [Double]</i>,
    "report" : <i>declares the type of report [String]</i>,
    "datapoints" : <i>number of points the report generates for each graph line [Integer]</i>,
    "seed" : <i>seed to generate random numbers [Integer]</i>
  },
  "microservices" :
  [
    {
      "name" : <i>name of the microservice [String]</i>,
      "instances" : <i>number of instances [Integer]</i>,
      "patterns" : 
      [
      	{<i>pattern name [String]</i> : <i>pattern value [String|Integer]</i>}
      ],
      "capacity" : <i>CPU capacity in MHz [Integer]</i>,
      "operations" :
      [
        {
          "name" : <i>name of the operation [String]</i>,
          "patterns" : 
          [
          	{<i>pattern name [String]</i> : <i>pattern value [String|Integer]</i>}
          ],
          "demand" : <i>CPU demand in MHz [Integer]</i>,
          "dependencies" :
          [
            {
              "operation" : <i>name of the depending operation [String]</i>,
              "service" : <i>name of the service the operation is part of [String]</i>,
              "probability" : <i>probability of the operation to get executed [Double]</i>
            }
          ]
        }
      ]
    }
  ],
  "generators" :
  [
    {
      "time" : <i>time interval a task is started [Double]</i>,
      "microservice" : <i>the service the task starts [String]</i>,
      "operation" : <i>the operation of the task [String]</i>
    }
  ],
  "chaosmonkeys" :
  [
    {
      "time" : <i>time instant the monkey gets executed [Double]</i>,
      "microservice" : <i>name of the service that gets killed [String]</i>,
      "instances" : <i>number of instances that get killed [Integer]</i>
    }
  ]
}
</pre>

#### Advanced Description

The ***simulation*** field hold general information for the simulation and the report.
- simulation :
  - report : 
    
    Defines the type of the report. Possible values : 
      - "" -> creates a default report,
      - "minimalistic" -> creates a cut down version of the report,
      - "none" -> no report is created
    
  - datapoints :
    
    Defines how much points are plotted on charts. Possible values:
      - 0 -> no charts are created
      - -1 -> creates default charts with one point per simulation time interval
      - [0-9]* -> number of points a chart will have

The ***microservices*** field holds the information about the microservice architecture.
- microservices :
	- patterns :
	  
	  Each different service can have none or multiple patterns. Possible values: 
	    - {"Thread Pool" : 100} 
	      - only 100 threads can exist at same time
        - {"Thread Queue" : 100}
          - if 100 threads exist, new threads are added to the queue with the size 100
      
   - operations :
    
     A service has one or multiple operations
       - patterns :
       
         Each operation can have none or multiple patterns. Possible values:
           - "Circuit Breaker"
           - the task queue is limited to the number of threads it can maximal take
         
- generators :
    
    Generators describe ... ***TODO***
      

### <a name="Sim-Use"></a>Usage
---

What do i need for use ...

The most important thing to have is an existing microservice architecture. In order to use the simulator you will need to encode the architecture into the simulators own json format. The following code snippet will provide a minimal example to show a very basic architecture.

``` javascript
{
  "simulation":
  {
    "experiment" : "Microservice Architecture Example",
    "model" : "Simple Web Shop",
    "duration" : 1000,
    "seed" : 1234
  },
  "microservices" :
  [
    {
      "name" : "Login",
      "instances" : 2,
      "patterns" : [],
      "capacity" : 1000,
      "operations" :
      [
        {
          "name" : "login",
          "patterns" : [],
          "demand" : 110,
          "dependencies" :
          [
            {
              "operation" : "",
              "service" : "",
              "probability" : 1.0
            }
          ]
        },
        {
          "name" : "update",
          "patterns" : [],
          "demand" : 10,
          "dependencies" : []
        }
      ]
    },
     {
      "name" : "Order",
      "instances" : 5,
      "patterns" : [{"Thread Pool" : 100}],
      "capacity" : 3000,
      "operations" :
      [
        {
          "name" : "checkout",
          "patterns" : [{"Circuit Breaker"}],
          "demand" : 80,
          "dependencies" :
          [
            {
              "operation" : "update",
              "service" : "Login",
              "probability" : 0.2
            }
          ]
        }
      ]
    }
  ],
  "generators" :
  [
    {
      "time" : 1,
      "microservice" : "Login",
      "operation" : "login"
    }
  ],
  "chaosmonkeys" :
  [
	{
    	"time" : 100,
        "microservice" : "Order",
        "instances" : 1
    }
  ]
}
```

### <a name="Sim-Doc"></a>Documentation
---

What does the tool ...

---
---
---
# Testarea
``` java
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Main class to start the experiment. This class will load the input file and create a model out of it.
 * doInitialSchedules Starts the inital event.
 * init Gets called at the start of the experiment and loads all relevant experiment resources.
 */
public class MainModelClass extends Model {
    private TimeUnit timeUnit       = TimeUnit.SECONDS;
    private double simulationTime   = 0;
    private int datapoints          = 0;
    private String resourcePath     = "Report/resources/";
    private boolean showInitEvent   = false;
}
```
<pre language="java" style="max-height: 30px; overflow: scroll;">
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Main class to start the experiment. This class will load the input file and create a model out of it.
 * doInitialSchedules Starts the inital event.
 * init Gets called at the start of the experiment and loads all relevant experiment resources.
 */
public class MainModelClass extends Model {
    private TimeUnit timeUnit       = TimeUnit.SECONDS;
    private double simulationTime   = 0;
    private int datapoints          = 0;
    private String resourcePath     = "Report/resources/";
    private boolean showInitEvent   = false;
}
</pre>

