package org.example.graph.model.report;

public class GraphReport {
    private String path;
    private double cost;

    private long elapsedTime;

    public GraphReport(String path, double cost, long elapsedTime) {
        this.path = path;
        this.cost = cost;
        this.elapsedTime = elapsedTime;
    }

    @Override
    public String toString(){
        return "Path: " + this.path + " with cost: " + this.cost + " with elapsed time: " + elapsedTime + "ms";
    }
}
