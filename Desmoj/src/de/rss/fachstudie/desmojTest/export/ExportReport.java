package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.models.MSSimulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExportReport {
    DependecyGraph graph;
    DataChart chart;
    public ExportReport(MSSimulator model) {
        graph = new DependecyGraph(model.allMicroservices, 0);
        chart = new DataChart("dummy");

        try {
            Files.write(Paths.get("./Desmoj_Microservice_Experiment_stats.html"), outsourceFunctionJustForTest().getBytes());
        } catch (IOException ex) {
            System.out.println("");
        }
    }

    public String outsourceFunctionJustForTest() {
        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset='utf-8'>\n" +
                "    <title>Force Layout Example 1</title>\n" +
                "    <style>\n" +
                "\t\thtml{margin: 0px; padding: 0px;}" +
                "\t\thtml{padding: 0px; padding: 0px;}" +
                "\t\tsvg {\n" +
                "\t\t\tborder: 1px solid black;\n" +
                "\t\t\tbackground: lightgray;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t.node {\n" +
                "\t\t\tfill: #ccc;\n" +
                "\t\t\tstroke: #ddd;\n" +
                "\t\t\tstroke-width: 1px;\n" +
                "\t\t}\n" +
                "\t\t\n" +
                "\t\t.node:hover {\n" +
                "\t\t\tstroke: #fff;\n" +
                "\t\t\tstroke-width: 2px;\n" +
                "\t\t}\n" +
                "\t\t\n" +
                "\t\td3-tip {\n" +
                "\t\t\tcolor: black;\n" +
                "\t\t\tfont-size: 10px;\n" +
                "\t\t\tfont-family: Verdana;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t.link {\n" +
                "\t\t\tstroke: #777;\n" +
                "\t\t\tstroke-width: 2px;\n" +
                "\t\t}\n" +
                "\n" +
                "    </style>\n" +
                "\t<script src='http://d3js.org/d3.v3.min.js'></script>\n" +
                "\t<script type='text/javascript' src=\"http://labratrevenge.com/d3-tip/javascripts/d3.tip.v0.6.3.js\"> </script>\n" +
                "</head>\n" +
                "<body>\n" +
                "\t<script>\n" +
                "\t\t// Setup\n" +
                "\t\tvar color = d3.scale.category20();\n" +
                "\t\tvar width = window.innerWidth;\n" +
                "\t\tvar height = 400;\n" +
                "\t\tvar force = -120;\n" +
                "\t\tvar distance = 50;\n" +
                "\t\tvar svg = d3.select(\"body\").append(\"svg\").attr(\"width\", '100%').attr(\"height\", height);\n" +
                "\t\tvar force = d3.layout.force().charge(force).linkDistance(distance).size([width, height]);\n" +
                "\n" +
                "\t\t// Graph\n" +
                "\t\tvar graph = " + graph.printGraph() + "\n" +
                "\t\t\t\t\t\n" +
                "\t\tforce.nodes(graph.nodes).links(graph.links).start();\n" +
                "\t\t\n" +
                "\t\t// Links\n" +
                "\t\tvar link = svg.selectAll(\".link\")\n" +
                "\t\t\t.data(graph.links)\n" +
                "\t\t\t.enter().append(\"line\")\n" +
                "\t\t\t.attr(\"class\", \"link\")\n" +
                "\t\t\t.style(\"stroke-width\", function (d) {\n" +
                "\t\t\treturn Math.sqrt(d.value);\n" +
                "\t\t});\n" +
                "\t\t\n" +
                "\t\t// Tooltip\n" +
                "\t\tvar tip = d3.tip()\n" +
                "\t\t\t.attr('class', 'd3-tip')\n" +
                "\t\t\t.offset([-10, 0])\n" +
                "\t\t\t.html(function (d) {return  d.name + \"\";})\n" +
                "\t\tsvg.call(tip);\n" +
                "\t\t\n" +
                "\t\t// Nodes\n" +
                "\t\tvar node = svg.selectAll(\".node\")\n" +
                "\t\t\t.data(graph.nodes)\n" +
                "\t\t\t.enter()\n" +
                "\t\t\t.append(\"g\")\n" +
                "\t\t\t.attr(\"class\", \"node\")\n" +
                "\t\t\t.call(force.drag)\n" +
                "\t\t\t.append(\"circle\")\n" +
                "\t\t\t.attr(\"r\", 8)\n" +
                "\t\t\t.style(\"fill\", function (d) {return color(d.group); })\n" +
                "\t\t\t.on('mouseover', tip.show)\n" +
                "\t\t\t.on('mouseout', tip.hide);\n" +
                "\n" +
                "\t\t// Update\n" +
                "\t\tforce.on(\"tick\", function () \n" +
                "\t\t{\n" +
                "\t\t\tlink.attr(\"x1\", function (d) {return d.source.x;})\n" +
                "\t\t\t\t.attr(\"y1\", function (d) {return d.source.y;})\n" +
                "\t\t\t\t.attr(\"x2\", function (d) {return d.target.x;})\n" +
                "\t\t\t\t.attr(\"y2\", function (d) {return d.target.y;});\n" +
                "\t\t\t\t\n" +
                "\t\t\td3.selectAll(\"circle\")\n" +
                "\t\t\t\t.attr(\"cx\", function (d) {return d.x;})\n" +
                "\t\t\t\t.attr(\"cy\", function (d) {return d.y;});\n" +
                "\t\t\t\t\n" +
                "\t\t\td3.selectAll(\"text\")\n" +
                "\t\t\t\t.attr(\"x\", function (d) {return d.x;})\n" +
                "\t\t\t\t.attr(\"y\", function (d) {return d.y;});\n" +
                "\t\t});\n" +
                "\n" +
                "\t</script>\n" +
                //TODO 2svg elements??? chart.printChart() +
                "</body>\n" +
                "</html>";
        return html;
    }
}
