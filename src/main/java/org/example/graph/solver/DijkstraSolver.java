package org.example.graph.solver;
import org.example.graph.model.graph.DirectedGraph;
import org.example.graph.model.graph.Edge;
import org.example.graph.model.graph.Node;
import org.example.graph.model.report.GraphReport;

import java.util.HashMap;

public class DijkstraSolver extends AbstractSolver {

    public DijkstraSolver(DirectedGraph graph) {
        super(graph);
    }


    @Override
    public GraphReport findShortestPath(Node start, Node end) {
        if (start == null || end == null){
            return new GraphReport("NONE", Double.POSITIVE_INFINITY, 0L);
        }
        HashMap<Node, Node> changedAt = new HashMap<>();
        changedAt.put(start, null);
        HashMap<Node, Double> shortestPathMap = new HashMap<>();
        long startTime = System.currentTimeMillis();
        for (Node node : graph.getNodes()) {
            if (node == start)
                shortestPathMap.put(start, 0.0);
            else shortestPathMap.put(node, Double.POSITIVE_INFINITY);
        }

        for (Edge edge : start.getEdges()) {
            shortestPathMap.put(edge.getDestination(), edge.getWeight());
            changedAt.put(edge.getDestination(), start);
        }

        start.setVisited();
        while (true) {
            Node currentNode = closestReachableUnvisited(shortestPathMap);
            if (currentNode == null) {
                graph.resetNodesVisited();
                return new GraphReport("NONE", Double.POSITIVE_INFINITY, System.currentTimeMillis() - startTime);
            }
            if (currentNode == end) {
                String path = getPathStr(end, end, changedAt);
                graph.resetNodesVisited();
                return new GraphReport(path, shortestPathMap.get(end), System.currentTimeMillis() - startTime);
            }
            currentNode.setVisited();
            for (Edge edge : currentNode.getEdges()) {
                if (edge.getDestination().isVisited())
                    continue;
                if (shortestPathMap.get(currentNode) + edge.getWeight()  < shortestPathMap.get(edge.getDestination())) {
                    shortestPathMap.put(edge.getDestination(), shortestPathMap.get(currentNode) + edge.getWeight());
                    changedAt.put(edge.getDestination(), currentNode);
                }
            }
        }
    }

    private Node closestReachableUnvisited(HashMap<Node, Double> shortestPathMap) {

        double shortestDistance = Double.POSITIVE_INFINITY;
        Node closestReachableNode = null;
        for (Node node : graph.getNodes()) {
            if (node.isVisited())
                continue;

            double currentDistance = shortestPathMap.get(node);
            if (currentDistance == Double.POSITIVE_INFINITY)
                continue;

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                closestReachableNode = node;
            }
        }
        return closestReachableNode;
    }

    private String getPathStr(Node child, Node end, HashMap<Node, Node> changedAt){
        StringBuilder path = new StringBuilder(end.getName());
        while (true) {
            Node parent = changedAt.get(child);
            if (parent == null) {
                break;
            }
            path.insert(0, parent.getName() + " ");
            child = parent;
        }
        return path.toString();
    }
}
