package org.example;
import org.example.graph.model.graph.DirectedGraph;
import org.example.graph.solver.DijkstraSolver;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        DirectedGraph graph = new DirectedGraph("./data/test_graph");
        DijkstraSolver solver = new DijkstraSolver(graph);
        System.out.println(solver.findShortestPath(graph.findNodeByName("1"), graph.findNodeByName("9")));
    }
}