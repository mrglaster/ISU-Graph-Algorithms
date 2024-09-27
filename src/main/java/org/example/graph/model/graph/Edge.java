package org.example.graph.model.graph;

public class Edge {
    Node source;
    Node destination;
    double weight;

    public Edge(Node source, Node destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.format("(%s -> %s, %f)", source.name, destination.name, weight);
    }


    public int compareTo(Edge otherEdge) {
        if (this.weight > otherEdge.weight) {
            return 1;
        }
        else return -1;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getDestination() {
        return destination;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
