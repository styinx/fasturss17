// Setup
var color = d3.scale.category20();
var width = window.innerWidth;
var height = window.innerHeight;
var power = -300;
var distance = 300;
var svg = d3.select("body").append("svg").attr("width", '100%').attr("height", height);
var force = d3.layout.force().charge(power).linkDistance(distance).size([width, height]);

if(!graph)
{
    graph = {nodes : [{name : "Graph is Empty (Some problems occurred)"}], links : []}
}

graph = renameLinks(graph);
graph = groupLinks(graph);
force.nodes(graph.nodes).links(graph.links).start();

// Tooltip
var tooltip = d3.select("body").append("div")
    .attr("class", "tooltip")
    .style("opacity", 0);

// Links
var link = svg.selectAll(".link")
	.data(graph.links)
	.enter().append("line")
	.attr("class", "link")
	.style("stroke-width", function (d) { return 1});
	//.style("stroke-width", function (d) { return Math.sqrt(d.value)});
	/*.on("mouseover", function(d,i) {
            tooltip.transition().duration(200).style("opacity", .9);
            tooltip.html("asdasdsd")
            .style("left", (d3.event.pageX - d.value.length*3) + "px")
            .style("top", (d3.event.pageY - 35) + "px");
    })
    .on("mouseout", function(d) {
        tooltip.transition().duration(500).style("opacity", 0);
    });*/

// Nodes
var node = svg.selectAll(".node")
	.data(graph.nodes)
	.enter()
	.append("g")
	.attr("class", "node")
	.call(force.drag)
	.append("circle")
	.attr("r", 8)
	.style("fill", function (d) {return color(d.group); })
	.on("mouseover", function(d,i) {
        tooltip.transition().duration(200).style("opacity", .9);
        tooltip.html(d.name)
        .style("left", (d3.event.pageX - d.name.length*3) + "px")
        .style("top", (d3.event.pageY - 35) + "px");
    })
    .on("mouseout", function(d) {
        tooltip.transition().duration(500).style("opacity", 0);
    });

// Update
force.on("tick", function ()
{
	link.attr("x1", function (d) {return d.source.x;})
		.attr("y1", function (d) {return d.source.y;})
		.attr("x2", function (d) {return d.target.x;})
		.attr("y2", function (d) {return d.target.y;});

	d3.selectAll("circle")
		.attr("cx", function (d) {return d.x;})
		.attr("cy", function (d) {return d.y;});

	d3.selectAll("text")
		.attr("x", function (d) {return d.x;})
		.attr("y", function (d) {return d.y;});
});

// Map links from indexes to ids
function renameLinks(graph)
{
    var links = [];
    graph.links.forEach(function(l)
    {
        var sourceNode = graph.nodes.filter(function(n) { return n.group === l.source; })[0];
        var targetNode = graph.nodes.filter(function(n) { return n.group === l.target; })[0];

        links.push({source: sourceNode.group, target: targetNode.group, value: l.value});
    });
    graph.links = links;
    return graph;
}

// Create links between groups
function groupLinks(graph)
{
    var links = [];
    var groups = {};
    graph.links.forEach(function(l)
    {
        groups[l.source] = l.target;
    });

    for(var g in groups)
    {
        var sourceNodes = graph.nodes.filter(function(n) { return n.group == g});
        var targetNodes = graph.nodes.filter(function(n) { /*return groups[g].forEach(function(gr) {return n.group == gr}) */return n.group == groups[g]});

        sourceNodes.forEach(function(s)
        {
            targetNodes.forEach(function(t)
            {
                links.push({source : s, target : t});
            });
        });
    }

    graph.links = links;
    return graph;
}

function toogleGroup()
{

}
