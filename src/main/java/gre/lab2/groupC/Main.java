package gre.lab2.groupC;

import gre.lab2.graph.*;

import java.io.IOException;

/**
 * Entry point of the program that runs the Bellman-Ford-Yens algorithm on a set of graph data
 * files. It processes each file to determine the presence of any negative weight cycles in the
 * graphs, or otherwise calculates and displays the shortest paths from a specified source vertex.
 *
 * @author Jarod Streckeisen, Timothée Van Hove
 */
public final class Main {
    static final String[] files = {
            "reseau1.txt", "reseau2.txt", "reseau3.txt", "reseau4.txt",
            "reseau5.txt", "reseau6.txt", "reseau7.txt", "reseau8.txt",
            "reseau9.txt", "reseau10.txt"
    };

    /**
     * Main method that processes each data file using the Bellman-Ford-Yens algorithm.
     *
     * @param args Command line arguments, not used in this implementation.
     */
    public static void main(String[] args) {
        for (String fileName : files) {
            processFile(fileName);
        }
    }

    /**
     * Processes a file to read a graph and compute the shortest path or detect a negative cycle.
     * The method computes the shortest path or negative cycle starting from vertex 0.
     *
     * @param fileName The name of the file to be processed.
     */
    private static void processFile(String fileName) {
        System.out.println("\nProcessing file: " + fileName);
        WeightedDigraph graph;
        try {
            graph = WeightedDigraphReader.fromFile("data/" + fileName);
        } catch (IOException e) {
            System.err.println("Error reading graph from file: " + e.getMessage());
            return;
        }

        BFYResult result = new BellmanFordYensAlgorithm().compute(graph, 0);
        displayResult(graph, result);
    }

    /**
     * Displays the result of the Bellman-Ford-Yens algorithm computation.
     *
     * @param graph  The graph on which the algorithm was run.
     * @param result The result of the Bellman-Ford-Yens algorithm computation.
     */
    private static void displayResult(WeightedDigraph graph, BFYResult result) {
        if (result.isNegativeCycle()) {
            System.out.println("Negative cycle detected!");
            System.out.println(replaceIntMaxValue(result.getNegativeCycle().toString()));
        } else {
            System.out.print("No negative cycle detected. ");
            if (graph.getNVertices() < 25) {
                System.out.println("\n" + replaceIntMaxValue(result.getShortestPathTree().toString()));
            } else {
                System.out.println("> 25 vertices, not displaying details");
            }
        }
    }

    /**
     * Replaces instances of Integer.MAX_VALUE in a string with the infinity symbol (∞) for better
     * readability.
     *
     * @param s The string containing output data which may include Integer.MAX_VALUE.
     * @return The string with all instances of Integer.MAX_VALUE replaced by '∞'.
     */
    private static String replaceIntMaxValue(String s) {
        return s.replace(String.valueOf(Integer.MAX_VALUE), "∞");
    }
}
