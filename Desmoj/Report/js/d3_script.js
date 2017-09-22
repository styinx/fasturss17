// Setup
var color = d3.scale.category20();
var width = window.innerWidth;
var height = 400;
var force = -120;
var distance = 50;
var svg = d3.select("body").append("svg").attr("width", '100%').attr("height", height);
var force = d3.layout.force().charge(force).linkDistance(distance).size([width, height]);

// Links
var link = svg.selectAll(".link")
	.data(graph.links)
	.enter().append("line")
	.attr("class", "link")
	.style("stroke-width", function (d) { Math.sqrt(d.value);});

// Tooltip
var tip = d3.tip()
	.attr('class', 'd3-tip')
	.offset([-10, 0])
	.html(function (d) {return  d.name + "";})
	svg.call(tip);

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
	.on('mouseover', tip.show)
	.on('mouseout', tip.hide);

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

//TODO check correct
function renameLinks(graph)
{
    for(var i in graph.links)
    {
        graph.links[i].source = graph.nodes[graph.links[i].source].id;
        graph.links[i].target = graph.nodes[graph.links[i].target].id;
    }
}