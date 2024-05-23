package gre.lab2.groupC;

import gre.lab2.graph.BFYResult;
import gre.lab2.graph.IBellmanFordYensAlgorithm;
import gre.lab2.graph.WeightedDigraph;

import static gre.lab2.graph.BFYResult.UNREACHABLE;

import java.util.*;

public final class BellmanFordYensAlgorithm implements IBellmanFordYensAlgorithm {

    @Override
    public BFYResult compute(WeightedDigraph graph, int source) {
        int numVertices = graph.getNVertices();
        int[] predecessors = new int[numVertices], distances = new int[numVertices];

        // Initialize distances to infinity and predecessors to -1
        Arrays.fill(predecessors, UNREACHABLE);
        Arrays.fill(distances, Integer.MAX_VALUE);

        // Track whether a vertex is currently in the queue
        boolean[] isInQueue = new boolean[numVertices];

        distances[source] = 0;
        int iterationCount = 0;

        // Initialize and add the source vertex and the sentinel to the queue
        Deque<Integer> vertices = new ArrayDeque<>();
        vertices.add(source);
        vertices.add(UNREACHABLE);

        while (!vertices.isEmpty()) {

            // Retrieve the first vertex from the queue
            int currentVertex = vertices.poll();

            //Only update isInQueue when it's not the sentinel
            if (currentVertex != UNREACHABLE) {
                isInQueue[currentVertex] = false;
            }

            // End of iteration marker
            if (currentVertex == UNREACHABLE) {
                if (!vertices.isEmpty()) {
                    iterationCount++;
                    if (iterationCount == numVertices - 1) break;
                    vertices.add(UNREACHABLE);
                }
            } else {
                // Process every outgoing edge of the current vertex
                for (WeightedDigraph.Edge edge : graph.getOutgoingEdges(currentVertex)) {
                    int successor = edge.to();
                    int newDistance = distances[currentVertex] + edge.weight();

                    // If the new path is shorter, update the distance and predecessor
                    if (distances[successor] > newDistance) {
                        distances[successor] = newDistance;
                        predecessors[successor] = currentVertex;

                        if (!isInQueue[successor]) {
                            // Add the successor to the queue
                            vertices.add(successor);
                            isInQueue[successor] = true;
                        }
                    }
                }
            }
        }

//        // Check for negative-weight cycles
//        for (WeightedDigraph.Edge edge : graph.getOutgoingEdges()) {
//            int u = edge.from();
//            int v = edge.to();
//            int weight = edge.weight();
//            if (distances[u] + weight < distances[v]) {
//                // Negative cycle detected, find cycle
//                List<Integer> cycle = new ArrayList<>();
//                boolean[] visited = new boolean[numVertices];
//                do {
//                    visited[u] = true;
//                    u = predecessors[u];
//                } while (!visited[u]);
//
//                // Backtrack to get the full cycle
//                int start = u;
//                do {
//                    cycle.add(u);
//                    u = predecessors[u];
//                } while (u != start);
//                cycle.add(u); // Add start vertex to complete the cycle
//
//                Collections.reverse(cycle); // Optional: To list the cycle in the correct order
//                return new BFYResult.NegativeCycle(cycle, calculateCycleWeight(cycle, graph));
//            }
//        }


        return new BFYResult.ShortestPathTree(distances, predecessors);
    }

    static private BFYResult getNegativeCycle(Deque<Integer> vertices, int[] predecessors,
                                              WeightedDigraph graph) {
        int cycleWeight = 0;

        // Get the last vertex of Deque. TODO: Is it guaranteed to be part of the negative cycle?
        int currentVertex = vertices.getLast();
        List<Integer> negativeCycle = new LinkedList<>(List.of(currentVertex));

        // Traverse the predecessors to construct the cycle and accumulate the weights
        while (true) {
            int predecessor = predecessors[currentVertex];

            // Accumulate the weight of the edge between the predecessor and the current vertex
            for (WeightedDigraph.Edge edge : graph.getOutgoingEdges(predecessor)) {
                if (edge.to() == currentVertex) {
                    cycleWeight += edge.weight();
                    break;
                }
            }

            // Move to the predecessor and add it to the list
            currentVertex = predecessor;
            negativeCycle.add(currentVertex);

            // Check if the cycle is complete
            if (currentVertex == vertices.getLast()) {
                break;
            }
        }
        return new BFYResult.NegativeCycle(negativeCycle, cycleWeight);
    }
}
