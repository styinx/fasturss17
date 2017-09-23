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

"microservices" :					// definition of all microservices
[
	{
		"name" : "frontend",		// name
		"instances" : 4,			// instance counter
		"power" : 3.5,
		"operations" : 
		[
			{
				"name" : "doStuff",
				"dependencies" : 
				[
					{
						"service" : "Database", 
						"operation" : "save",
						"duration" : 4,
						"propability" : 0.8
					}
				],							
			}
		]
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
],
"message_object" :					// transmission object
[
	{
		"instances" : 4,
		"path" : 
		[
			"frontend", 
			"database"
		] 	
	},
],
"failures":							// error modes
[
	{
		"type" : "errormonkey"
		"service" : "frontend",		// shutdown target
		"instances" : 3,			// number of instances that should be killed
		"time" : [1, 2, 3]			// time period, at which the instances should be killed
	}
],
"triggers" :						// special runtime events
[

]
```

### <a name="Sim-Doc"></a>Documentation

What does the tool ...
