# Simulation-based Resilience Prediction of Microservice Architectures

This simulator was created as part of the Fachstudie __Simulation-based Resilience Prediction of Microservice Architectures__ at the Reliable Software Systems Research Group of the Institute of Software Technology at the University of Stuttgart.

It allows the simulation of microservice architectures in regard to resilience and is based on the [DesmoJ](http://desmoj.sourceforge.net) framework for discrete event modelling and simulation. 

**Table of contents:**
- [Installation](#Installation)
- [Input Model](#Input)
- [Usage](#Sim-Use)
- [Documentation](#Sim-Doc)

## <a name="Installation"></a>Installation

In order to run the simulator you have to download the DesmoJ binary from [sourceforge](http://desmoj.sourceforge.net/download.html) and then include it into the project.

## <a name="Input"></a>Input Model
This is an example input for the simulator. **TODO: write more about the input model**


```json
{
  "simulation":
  {
    "experiment" : "Desmoj_Microservice_Experiment",
    "model" : "Simple microservice model",
    "duration" : 50,
    "datapoints" : 50,
    "seed" : 2298
  },
  "microservices" :
  [
    {
      "name" : "Gateway",
      "instances" : 10,
      "CPU" : 3500,
      "operations" :
      [
        {
          "name" : "register",
          "patterns" : ["Circuit Breaker"],
          "duration" : 4,
          "CPU" : 300,
          "dependencies" :
          [
            {
              "operation" : "save",
              "service" : "Authentication",
              "probability" : 0.7
            }
          ]
        },
        {
          "name" : "update",
          "patterns" : ["Circuit Breaker"],
          "duration" : 1,
          "CPU" : 800,
          "dependencies" : 
          [
            {
              "operation" : "save",
              "service" : "Order",
              "probability" : 0.8
            }
          ]
        }
      ]
    },
    {
      "name" : "Authentication",
      "instances" : 5,
      "CPU" : 3500,
      "operations" :
      [
        {
          "name" : "save",
          "patterns" : [],
          "duration" : 2,
          "CPU" : 600,
          "dependencies" :
          [
            {
              "operation" : "update",
              "service" : "Backup",
              "probability" : 0.2
            }
          ]
        }
      ]
    },
    {
      "name" : "Order",
      "instances" : 5,
      "CPU" : 3500,
      "operations" :
      [
        {
          "name" : "save",
          "patterns" : [],
          "duration" : 2,
          "CPU" : 300,
          "dependencies" :
          [
            {
              "operation" : "update",
              "service" : "Backup",
              "probability" : 0.1
            }
          ]
        },
        {
          "name" : "update",
          "patterns" : [],
          "duration" : 2,
          "CPU" : 500,
          "dependencies" :
          [
            {
              "operation" : "register",
              "service" : "Shipping",
              "probability" : 0.4
            }
          ]
        },
        {
          "name" : "register",
          "patterns" : [],
          "duration" : 2,
          "CPU" : 900,
          "dependencies" :
          [
            {
              "operation" : "update",
              "service" : "Database",
              "probability" : 0.8
            }
          ]
        }
      ]
    },
    {
      "name" : "Backup",
      "instances" : 5,
      "CPU" : 3500,
      "operations" :
      [
        {
          "name" : "update",
          "patterns" : [],
          "duration" : 2,
          "CPU" : 700,
          "dependencies" :
          [
            {
              "operation" : "update",
              "service" : "Database",
              "probability" : 0.5
            }
          ]
        }
      ]
    },
    {
      "name" : "Shipping",
      "instances" : 5,
      "CPU" : 3500,
      "operations" :
      [
        {
          "name" : "register",
          "patterns" : [],
          "duration" : 2,
          "CPU" : 1200,
          "dependencies" :
          [
            {
              "operation" : "update",
              "service" : "Database",
              "probability" : 0.3
            }
          ]
        }
      ]
    },
    {
      "name" : "Database",
      "instances" : 5,
      "CPU" : 3500,
      "operations" :
      [
        {
          "name" : "update",
          "patterns" : [],
          "duration" : 2,
          "CPU" : 500,
          "dependencies" :
          [
          
          ]
        }
      ]
    }
  ],
  "generators" :
  [
    {
      "time" : 1,
      "microservice" : "Gateway",
      "operation" : "update"
    },
    {
      "time" : 50,
      "microservice" : "Backup",
      "operation" : "update"
    }
  ],
  "chaosmonkeys" :
  [
    {
      "time" : 50,
      "microservice" : "Database",
      "instances" : 1
    },
    {
      "time" : 50,
      "microservice" : "Gateway",
      "instances" : 1
    }
  ]
}
```
### Advanced Description

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
      

## <a name="Sim-Use"></a>Usage

What do i need for use ...

The most important thing to have is an existing microservice architecture. In order to use the simulator you will need to encode the architecture into the simulators own json format. 

## <a name="Sim-Doc"></a>Documentation

What does the tool ...
