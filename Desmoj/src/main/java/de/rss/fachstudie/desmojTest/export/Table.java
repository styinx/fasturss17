package de.rss.fachstudie.desmojTest.export;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

public class Table {

    private TreeMap<String, List<Double>> values;
    private String header;

    public Table(String header, TreeMap<String, TreeMap<Double, Double>> series) {
        values = new TreeMap<>();
        this.header = header;

        for(String key : series.keySet()) {
            TreeMap<Double, Double> entry = series.get(key);
            double start = Double.NEGATIVE_INFINITY, end = Double.NEGATIVE_INFINITY;
            double min = Double.POSITIVE_INFINITY, mean = 0, max = Double.NEGATIVE_INFINITY;

            if (entry.size() > 0) {
                start = entry.firstEntry().getValue();
                end = entry.lastEntry().getValue();
            }

            for (Double index : entry.keySet()) {
                if(entry.get(index) < min) {
                    min = entry.get(index);
                }
                if(entry.get(index) > max) {
                    max = entry.get(index);
                }
                if(index > 0) {
                    mean += entry.get(index);
                }
            }
            values.put(key, new ArrayList<>(Arrays.asList(start, min, mean / entry.size(), max, end)));
        }
    }

    public String printTable() {
        String html = "";
        NumberFormat nf = new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH));

        String id = "table-" + header.replace(" ", "_");
        html += "<table class='stat-table tablesorter' id='" + id + "'>"
                + "<thead><tr><th><span onclick=\\\"toggleTable(this, '" + id + "');\\\">&#x25BA;</span>" + header + "</th>"
                + "<th>Start</th><th>Min</th><th>Mean</th><th>Max</th><th>End</th></thead>"
                + "<tbody class='hidden'>";

        for(String entry : values.keySet()) {
            List<Double> mmm = values.get(entry);
            String start = "-", min = "-", mean = "-", max = "-", end = "-";

            if (mmm.get(0) != Double.NEGATIVE_INFINITY)
                start = nf.format(mmm.get(0));

            if (mmm.get(1) != Double.POSITIVE_INFINITY)
                min = nf.format(mmm.get(1));

            if (mmm.get(3) != Double.NEGATIVE_INFINITY)
                max = nf.format(mmm.get(3));

            if (mmm.get(1) != Double.POSITIVE_INFINITY && mmm.get(3) != Double.NEGATIVE_INFINITY)
                mean = nf.format(mmm.get(2));

            if (mmm.get(4) != Double.NEGATIVE_INFINITY)
                end = nf.format(mmm.get(4));

            html += "<tr><td align='left'>" + entry + "</td><td>" + start + "</td><td>" + min + "</td>"
                    + "<td>" + mean + "</td><td>" + max + "</td><td>" + end + "</td></tr>";
        }

        html += "</tbody>"
                + "</table>";

        return "document.getElementById('chart-container').innerHTML += \"" + html + "\"\n";
    }
}