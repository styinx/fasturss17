# fasturss17

**Table of contents:**

- [Simulator](#Sim)
	- [Description](#Sim-Des)
	- [Usage](#Sim-Use)
	- [Documentation](#Sim-Doc)

## <a name="Sim"></a>Simulator

### <a name="Sim-Des"></a>Description

The tool is used for ...

<pre language="javascript">
{
  "simulation":
  {
    "experiment" : "Desmoj_Microservice_Experiment",
    "model" : "Simple microservice model",
    "duration" : *simulation time in seconds [Integer]*,
    "datapoints" : 500,
    "seed" : 1234
  },
  "microservices" :
  [
    {
      "name" : *name of the microservice [String]*,
      "instances" : *number of instances [Integer]*,
      "patterns" : 
      [
      	{*pattern name [String]* : *pattern value [String|Integer]*}
      ],
      "capacity" : *CPU capacity in MHz [Integer]*,
      "operations" :
      [
        {
          "name" : *name of the operation [String]*,
          "patterns" : 
          [
          	{*pattern name [String]* : *pattern value [String|Integer]*}
          ],
          "demand" : *CPU demand in MHz [Integer]*,
          "dependencies" :
          [
            {
              "operation" : *name of the depending operation [String]*,
              "service" : *name of the service the operation is part of [String]*,
              "probability" : *probability of the operation to get executed [Double]*
            }
          ]
        }
      ]
    }
  ],
  "generators" :
  [
    {
      "time" : *time interval a task is started [Integer]*,
      "microservice" : *the service the task starts [String]*,
      "operation" : *the operation of the task [String]*
    }
  ],
  "chaosmonkeys" :
  [
	{
    	"time" : *time instant the monkey gets executed [Integer]*,
        "microservice" : *name of the service that gets killed [String]*,
        "instances" : *number of instances that get killed [Integer]* 
    }
  ]
}
</pre>

#### Advanced Description

- microservices :
	- patterns : 
	  [
        {"Thread Pool" : 100}, // Instances can only create 100 threads at same time
        {"Thread Queue" : 100}, // Instances can put threads into a queue if the thread pool is consumed
      ]
   - operations :
       - patterns :
         [
           {"Circuit Breaker"} // The task queue is limited to the number of instances a service has
         ]


### <a name="Sim-Use"></a>Usage

What do i need for use ...

The most important thing to have is an existing microservice architecture. In order to use the simulator you will need to encode the architecture into the simulators own json format. The following code snippet will provide a minimal example to show a very basic architecture.

```javascript

```

### <a name="Sim-Doc"></a>Documentation

What does the tool ...

# Test
<pre language="java">
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
<pre language="java" style="max-height: 100px; overflow: hidden;">
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

