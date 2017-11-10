package de.rss.fachstudie.desmojTest.export;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

public class Table {

    private TreeMap<String, List<Double>> values;
    private String header;

    public Table(String header, TreeMap<String, TreeMap<Integer, Double>> series) {
        values = new TreeMap<>();
        this.header = header;

        for(String key : series.keySet()) {
            TreeMap<Integer, Double> entry = series.get(key);
            double min = Double.POSITIVE_INFINITY, mean = 0, max = Double.NEGATIVE_INFINITY;
            for(Integer index : entry.keySet()) {
                if(entry.get(index) < min) {
                    min = entry.get(index);
                }
                if(entry.get(index) > max) {
                    max = entry.get(index);
                }
                if(index > 0) {
                    mean = (mean + entry.get(index)) / 2;
                }
            }
            values.put(key, new ArrayList<>(Arrays.asList(min, mean, max)));
        }
    }

    public String printTable() {
        String html = "";
        NumberFormat nf = new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH));

        String id = "table-" + header.replace(" ", "_");
        html += "<table class='stat-table tablesorter' id='" + id + "'>"
                + "<thead><tr><th><span onclick=\\\"hideTable(this, '" + id + "');\\\">&#x25BC;</span>" + header + "</th><th>Min</th><th>Mean</th><th>Max</th></thead>"
                + "<tbody>";

        for(String entry : values.keySet()) {
            List<Double> mmm = values.get(entry);
            String min = "", mean = "", max = "";

            if(mmm.get(0) == Double.POSITIVE_INFINITY)
                min = "-";
            else
                min = nf.format(mmm.get(0));

            if(mmm.get(2) == Double.NEGATIVE_INFINITY)
                max = "-";
            else
                max = nf.format(mmm.get(2));

            if(mmm.get(0) == Double.POSITIVE_INFINITY && mmm.get(2) == Double.NEGATIVE_INFINITY)
                mean = "-";
            else
                mean = nf.format(mmm.get(0));

            html += "<tr><td align='left'>" + entry + "</td><td>" + min + "</td><td>" + mean + "</td><td>" + max + "</td></tr>";
        }

        html += "</tbody>"
                + "</table>";

        return "document.getElementById('chart-container').innerHTML += \"" + html + "\"\n";
    }
}
