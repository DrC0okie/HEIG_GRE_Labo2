package gre.lab2.groupC;

import gre.lab2.graph.*;
import java.io.IOException;

public final class Main {
  static String[] files ={"reseau1.txt", "reseau2.txt", "reseau3.txt", "reseau4.txt"};

  public static void main(String[] args) {
    WeightedDigraph graph;

    for(String f : files) {
      System.out.println("File: " + f);
      // Read the graph from the file
      try {
        graph = WeightedDigraphReader.fromFile("data/" + f);
      } catch (IOException e) {
        System.err.println("Error reading graph from file: " + e.getMessage());
        return;
      }

      // Compute the result starting from vertex 0
      BFYResult result = new BellmanFordYensAlgorithm().compute(graph, 0);

      // Print the result
      if (result.isNegativeCycle()) {
        BFYResult.NegativeCycle negativeCycle = result.getNegativeCycle();
        System.out.println("Negative cycle detected!");
        System.out.println("Cycle length (total weight): " + negativeCycle.length());
        System.out.println("Cycle vertices: " + negativeCycle.vertices());
      } else {
        BFYResult.ShortestPathTree spt = result.getShortestPathTree();
        System.out.println("No negative cycle detected.");
        if (graph.getNVertices() < 25) {
          System.out.println("Shortest path tree:");
          System.out.println("Distances: " + arrayToString(spt.distances()));
          System.out.println("Predecessors: " + arrayToString(spt.predecessors()));
        }
      }
    }
  }

  private static String arrayToString(int[] array) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < array.length; i++) {
      sb.append(array[i]);
      if (i < array.length - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }
}
