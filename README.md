# fasturss17

**Table of contents:**

- [Simulator](#Sim)
	- [Description](#Sim-Des)
	- [Usage](#Sim-Use)
	- [Documentation](#Sim-Doc)

## <a name="Sim"></a>Simulator

### <a name="Sim-Des"></a>Description

The tool is used for ...

### <a name="Sim-Use"></a>Usage

What do i need for use ...

```javascript
{
	"microservices" :									// definition of all microservices
	{
		"Frontend":										// microservice
		{
			"name" : "frontend",						// name
			"antipatterns" : ["Circuit Breaker"],		// resilience pattern
			"instances": 4,								// instance counter
			"dependencies": ["Database", "Server"],		// microservice dependency
			"throughput" : 3							// data transmission rate
		},
		"Database":
		{
			"name" : "database",
			"instances": 2,
			"dependencies": ["Frontend"],
			"throughput" : 3
		},
		"Server":
		{
			"name" : "server",
			"instances": 1,
			"dependencies": ["Frontend"],
			"throughput" : 3
		}
	},
	"message_object" :									// transmission object
	{
		"token" :										// message object
		{
			"instances" : 4,
			"path" : ["frontend" : 5, "database" : 3] 	// duration at a microservice
		},
		"token:
		{
			"server" : 5
		}
	},
	"failures":											// error modes
	{
		"chaosmonkey" :									// shutdown service
		{
			"service" : "frontend",						// shutdown target
			"instances" : 3,							// number of instances that should be killed
			"time" : [1, 2, 3]							// time period, at which the instances should be killed
		}
	},
	"triggers" :										// special runtime events
	{

	}
}
```

### <a name="Sim-Doc"></a>Documentation

What does the tool ...
