package org.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AntColonyAlgorithm {

    // Path to the graph file
    private static final String defaultGraphPath = "/home/mrglaster/Desktop/graphs-study/ISU-Graph-Algorithms/ant-colony-algo/src/main/resources/graph_simple.txt";
    private static String graphPath = defaultGraphPath;

    // Number of cities in the graph
    private static int NUM_CITIES = 51;

    // Number of ants used in the algorithm
    private static final int NUM_ANTS = 1;

    private static final double BINOMIAL_P = 0.5;

    // Maximum number of iterations for the algorithm
    private static final int MAX_ITERATIONS = 1000;

    private static final boolean isBidirectional = false;

    // Evaporation rate of pheromones
    private static final double EVAPORATION_RATE = 0.01;

    // Parameters for the pheromone and distance influence
    private static final double ALPHA = 1.0;
    private static final double BETA = 1.0;

    // Pheromone deposit constant
    private static final double Q = 1.0;

    // Pheromone and distance matrices
    private static double[][] pheromone;
    private static double[][] distance;

    private static final int K_PLOT_ANT = 1;

    // Random number generator
    private static Random random;

    // List to store the Y-axis data for chart plotting
    List<Integer> chartBestOnIterationY = new ArrayList<>();

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


        List<Integer> specificAntResultsPaths = new ArrayList<>();


        // Iterate through the maximum number of iterations
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            List<List<Integer>> antBestPaths = new ArrayList<>();
            int iterationBestPathValue = 999999;
            List<Integer> iterationsBestPath = null;
            // Each ant constructs a solution
            for (int ant = 0; ant < NUM_ANTS; ant++) {
                List<Integer> currentBestPath = constructSolution();
                if (currentBestPath != null){
                    int pathValue = (int) calculatePathLength(currentBestPath);
                    if (ant == K_PLOT_ANT || NUM_ANTS == 1) {
                        specificAntResultsPaths.add(pathValue);
                    }
                    if (pathValue < iterationBestPathValue){
                        iterationBestPathValue = pathValue;
                        iterationsBestPath = currentBestPath;
                    }
                }
                antBestPaths.add(iterationsBestPath);
                updatePheromones(antBestPaths);
            }

            if (iterationsBestPath != null){
                System.out.println("Iteration #" + cntr);
                System.out.println("Best path length for iteration #" + cntr + " " + iterationBestPathValue);
                chartBestOnIterationY.add((int)calculatePathLength(iterationsBestPath));
                System.out.println();
            }
        }

        bestPath = constructSolution();
        if (bestPath != null) {
            System.out.println("Best Path: " + bestPath);
            System.out.println("Best Path Length: " + calculatePathLength(bestPath));

        } else {
            System.out.println("Unable to construct a path.");
        }

        AntChartPlotter.plotCharts(chartBestOnIterationY, specificAntResultsPaths, K_PLOT_ANT);
    }


    public static int getBinomial(int n, double p) {
        // Calculate the logarithm of the probability of failure (1 - p)
        double log_q = Math.log(1.0 - p);

        // Initialize the count of successes (x) to 0
        int x = 0;

        // Initialize the cumulative sum of logarithms
        double sum = 0;

        // Loop indefinitely until the stopping condition is met
        for(;;) {
            // Add the logarithm of a random value scaled by the remaining trials
            sum += Math.log(random.nextFloat()) / (n - x);

            // If the cumulative sum is less than log_q, return the current count of successes
            if(sum < log_q) {
                return x;
            }

            // Increment the count of successes
            x++;
        }
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
                    if (isBidirectional){
                        distance[second][first] = dist;
                    }
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

        //int currentCity = random.nextInt(NUM_CITIES);
        int currentCity = getBinomial(NUM_CITIES, BINOMIAL_P);
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
                    if (isBidirectional){
                        pheromone[to][from] += pheromoneDelta;
                    }
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
        double latest_path_part = distance[path.get(path.size() - 1)][path.get(0)];
        if (latest_path_part != -1){
            length += distance[path.get(path.size() - 1)][path.get(0)];
        }
        return length;
    }
}