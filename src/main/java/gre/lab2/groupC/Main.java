package gre.lab2.groupC;

import gre.lab2.graph.*;
import java.io.IOException;

public final class Main {
  static String[] files ={
          "reseau1.txt",
          "reseau2.txt",
          "reseau3.txt",
          "reseau4.txt",
          "reseau5.txt",
          "reseau6.txt",
          "reseau7.txt",
          "reseau8.txt",
          "reseau9.txt",
          "reseau10.txt"};

  public static void main(String[] args) {
    WeightedDigraph graph;

    for(String f : files) {
      System.out.println("\nProcessing file: " + f);
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
        System.out.println("Negative cycle detected!");
        System.out.println(result.getNegativeCycle());
      } else {
        System.out.print("No negative cycle detected. ");
        if (graph.getNVertices() < 25) {
          System.out.println("\n" + result.getShortestPathTree());
        } else {
          System.out.println("> 25 vertices, not displaying details");
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
