package org.example.graph.model.graph;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedReader;

public class DirectedGraph {
    private final Set<Node> nodes;

    public DirectedGraph(){
        this.nodes = new HashSet<>();
    }

    public DirectedGraph(String filePath) throws IOException {
        this();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid line format: " + line);
                }

                String sourceName = parts[0];
                String destinationName = parts[1];
                double weight = Double.parseDouble(parts[2]);

                Node sourceNode = findNodeByName(sourceName);
                if (sourceNode == null) {
                    sourceNode = new Node(Integer.parseInt(sourceName), sourceName);
                    addNode(sourceNode);
                }

                Node destinationNode = findNodeByName(destinationName);
                if (destinationNode == null) {
                    destinationNode = new Node(Integer.parseInt(destinationName), destinationName);;
                    addNode(destinationNode);
                }
                addEdge(sourceNode, destinationNode, weight);
            }
        }
    }

    public void addNode(Node... n) {
        nodes.addAll(Arrays.asList(n));
    }

    public void addEdge(Node source, Node destination, double weight){
        nodes.add(source);
        nodes.add(destination);
        source.edges.add(new Edge(source, destination, weight));
    }


    public Set<Node> getNodes() {
        return nodes;
    }

    public Node findNodeByName(String name){
        for (var node: nodes){
            if (node.getName().equals(name)){
                return node;
            }
        }
        return null;
    }

    public void resetNodesVisited() {
        for (Node node : nodes) {
            node.unsetVisited();
        }
    }
}
