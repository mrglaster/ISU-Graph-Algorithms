package org.example.graph.solver;

import org.example.graph.model.graph.DirectedGraph;
import org.example.graph.model.graph.Node;
import org.example.graph.model.report.GraphReport;

abstract class AbstractSolver {

    protected DirectedGraph graph;

    public AbstractSolver(DirectedGraph graph){
        this.graph = graph;
    }
    abstract GraphReport findShortestPath(Node start, Node end);
}
