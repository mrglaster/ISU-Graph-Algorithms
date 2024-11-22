package org.example;

public class Main {
    public static void main(String[] args){
        String path = "/home/mrglaster/Desktop/graphs-study/ISU-Graph-Algorithms/ant-colony-algo/src/main/resources/graph_alt.txt";
        AntColonyAlgorithm algo = new AntColonyAlgorithm(path, 6);
        algo.solve();
    }
}
