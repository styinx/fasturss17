package de.rss.fachstudie.desmojTest.utils;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private List<Node> children;
    private String value;

    public Node(String value) {
        children = new ArrayList<>();
        this.value = value;
    }

    public void setChild(Node child) {
        children.add(child);
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
