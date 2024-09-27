package org.example.graph.model.graph;

import java.util.LinkedList;

public class Node {
    int number;
    String name;
    private boolean visited;
    LinkedList<Edge> edges;

    public Node(int n, String name) {
        this.number = n;
        this.name = name;
        this.visited = false;
        this.edges = new LinkedList<>();
    }

    public boolean isVisited(){
        return visited;
    }

    public void setVisited() {
        visited = true;
    }

    void unsetVisited() {
        visited = false;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(LinkedList<Edge> edges) {
        this.edges = edges;
    }
}
