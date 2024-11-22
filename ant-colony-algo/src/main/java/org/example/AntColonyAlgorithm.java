package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// TODO Block Scheme
// Plot fix
public class AntColonyAlgorithm {

    // Path to the graph file
    private static final String defaultGraphPath = "/home/mrglaster/Desktop/graphs-study/ISU-Graph-Algorithms/ant-colony-algo/src/main/resources/graph_simple.txt";
    private static String graphPath = defaultGraphPath;

    // Number of cities in the graph
    private static int NUM_CITIES = 51;

    // Number of ants used in the algorithm
    private static final int NUM_ANTS = 100;

    // Maximum number of iterations for the algorithm
    private static final int MAX_ITERATIONS = 100000;

    // Evaporation rate of pheromones
    private static final double EVAPORATION_RATE = 0.2;

    // Parameters for the pheromone and distance influence
    private static final double ALPHA = 1.0;
    private static final double BETA = 2.0;

    // Pheromone deposit constant
    private static final double Q = 1.0;

    // Pheromone and distance matrices
    private static double[][] pheromone;
    private static double[][] distance;

    // Random number generator
    private static Random random;

    // List to store the Y-axis data for chart plotting
    List<Integer> chartYData = new ArrayList<>();

    // Default constructor
    public AntColonyAlgorithm(){
        random = new Random();
        pheromone = new double[NUM_CITIES][NUM_CITIES];
        distance = new double[NUM_CITIES][NUM_CITIES];
    }

    // Constructor with custom graph path and number of cities
    public AntColonyAlgorithm(String userGraphPath, int numCities){
        NUM_CITIES = numCities;
        random = new Random();
        pheromone = new double[NUM_CITIES][NUM_CITIES];
        distance = new double[NUM_CITIES][NUM_CITIES];
        graphPath = userGraphPath;
    }

    // Main method to solve the problem using the Ant Colony Algorithm
    public void solve(){
        initializeDistances();
        initializePheromones();
        int cntr = 0;
        List<Integer> bestPath;

        // Iterate through the maximum number of iterations
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            List<List<Integer>> antPaths = new ArrayList<>();

            // Each ant constructs a solution
            for (int ant = 0; ant < NUM_ANTS; ant++) {
                antPaths.add(constructSolution());
            }

            // Update pheromones based on the constructed solutions
            updatePheromones(antPaths);

            // Find the best path for the current iteration
            bestPath = constructSolution();
            if (bestPath != null){
                cntr += 1;
                System.out.println("Iteration #" + cntr);
                System.out.println("Best path length for iteration #" + cntr + " " + calculatePathLength(bestPath));
                chartYData.add((int)calculatePathLength(bestPath));
                System.out.println();
            }
        }

        // Find the best path after all iterations
        bestPath = constructSolution();
        if (bestPath != null) {
            System.out.println("Best Path: " + bestPath);
            System.out.println("Best Path Length: " + calculatePathLength(bestPath));
        } else {
            System.out.println("Unable to construct a path.");
        }

        // Plot the chart with the collected data
        //AntChartPlotter.plotChart(chartYData);
    }

    // Initialize the distance matrix from the graph file
    private static void initializeDistances() {
        for (int i = 0; i < NUM_CITIES; i++) {
            for (int j = 0; j < NUM_CITIES; j++) {
                if (i == j) {
                    distance[i][j] = 0;
                } else {
                    distance[i][j] = -1;
                }
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(graphPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                if (row.length == 3) {
                    int first = Integer.parseInt(row[0]);
                    int second = Integer.parseInt(row[1]);
                    int dist = Integer.parseInt(row[2]);
                    distance[first][second] = dist;
                } else {
                    System.out.println("Invalid line format: " + line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Initialize the pheromone matrix
    private static void initializePheromones() {
        for (int i = 0; i < NUM_CITIES; i++) {
            for (int j = 0; j < NUM_CITIES; j++) {
                pheromone[i][j] = 1.0;
            }
        }
    }

    // Construct a solution (path) for an ant
    private static List<Integer> constructSolution() {
        List<Integer> path = new ArrayList<>();
        List<Integer> unvisited = new ArrayList<>();
        for (int i = 0; i < NUM_CITIES; i++) {
            unvisited.add(i);
        }

        int currentCity = random.nextInt(NUM_CITIES);
        path.add(currentCity);
        unvisited.remove((Integer) currentCity);

        while (!unvisited.isEmpty()) {
            int nextCity = selectNextCity(currentCity, unvisited);
            if (nextCity == -1) {
                return null;
            }
            path.add(nextCity);
            unvisited.remove((Integer) nextCity);
            currentCity = nextCity;
        }

        return path;
    }

    // Select the next city to visit based on pheromone and distance
    private static int selectNextCity(int currentCity, List<Integer> unvisited) {
        double[] probabilities = new double[unvisited.size()];
        double sum = 0.0;

        for (int i = 0; i < unvisited.size(); i++) {
            int nextCity = unvisited.get(i);
            if (distance[currentCity][nextCity] == -1) {
                // If the path does not exist, the transition probability is 0
                probabilities[i] = 0;
            } else {
                probabilities[i] = Math.pow(pheromone[currentCity][nextCity], ALPHA) * Math.pow(1.0 / distance[currentCity][nextCity], BETA);
                sum += probabilities[i];
            }
        }

        if (sum == 0) {
            // If the sum of probabilities is 0, there are no available paths
            return -1;
        }

        double r = random.nextDouble() * sum;
        double cumulativeProbability = 0.0;
        for (int i = 0; i < unvisited.size(); i++) {
            cumulativeProbability += probabilities[i];
            if (r <= cumulativeProbability) {
                return unvisited.get(i);
            }
        }

        return unvisited.get(unvisited.size() - 1);
    }

    // Update the pheromone matrix based on the ant paths
    private static void updatePheromones(List<List<Integer>> antPaths) {
        for (int i = 0; i < NUM_CITIES; i++) {
            for (int j = 0; j < NUM_CITIES; j++) {
                pheromone[i][j] *= (1 - EVAPORATION_RATE);
            }
        }

        for (List<Integer> path : antPaths) {
            if (path != null) {
                double pathLength = calculatePathLength(path);
                double pheromoneDelta = Q / pathLength;

                for (int i = 0; i < path.size() - 1; i++) {
                    int from = path.get(i);
                    int to = path.get(i + 1);
                    pheromone[from][to] += pheromoneDelta;
                }
            }
        }
    }

    // Calculate the length of a given path
    private static double calculatePathLength(List<Integer> path) {
        double length = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            int from = path.get(i);
            int to = path.get(i + 1);
            length += distance[from][to];
        }
        return length;
    }
}