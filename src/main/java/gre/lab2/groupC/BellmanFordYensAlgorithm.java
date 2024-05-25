package gre.lab2.groupC;

import gre.lab2.graph.BFYResult;
import gre.lab2.graph.IBellmanFordYensAlgorithm;
import gre.lab2.graph.WeightedDigraph;
import static gre.lab2.graph.BFYResult.UNREACHABLE;
import java.util.*;

/**
 * An implementation of the Bellman-Ford-Yens algorithm to find the shortest paths from a source
 * vertex to all other vertices in a weighted digraph, or to detect a negative cycle in the graph.
 */
public final class BellmanFordYensAlgorithm implements IBellmanFordYensAlgorithm {

    /**
     * A constant used to mark vertices that have been visited during cycle detection.
     */
    static final int VISITED = -2;

    /**
     * Computes the shortest path tree from a source vertex or detects a negative cycle in the graph.
     * If a negative cycle is detected during the computation, it returns the cycle instead of the shortest path tree.
     *
     * @param graph  The weighted digraph to be processed.
     * @param source The index of the source vertex from which paths are computed.
     * @return BFYResult which can be the shortest path tree or a negative cycle if one is found.
     * @author Jarod Streckeisen, Timothée Van Hove
     */
    @Override
    public BFYResult compute(WeightedDigraph graph, int source) {
        int numVertices = graph.getNVertices();
        int[] predecessors = new int[numVertices];
        int[] distances = new int[numVertices];

        // Initialize distances to the Integer max value and predecessors to -1
        Arrays.fill(predecessors, UNREACHABLE);
        Arrays.fill(distances, Integer.MAX_VALUE);

        // Track whether a vertex is currently in the queue
        boolean[] isInQueue = new boolean[numVertices];
        int[] ingoingVertexWeights = new int[numVertices];

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
                    vertices.add(UNREACHABLE);
                }
            } else {
                // Process every outgoing edge of the current vertex
                for (WeightedDigraph.Edge edge : graph.getOutgoingEdges(currentVertex)) {
                    int successor = edge.to(), weight = edge.weight();
                    int newDistance = distances[currentVertex] + weight;

                    // If the new path is shorter, update the distance and the predecessor
                    if (distances[successor] > newDistance) {
                        distances[successor] = newDistance;
                        predecessors[successor] = currentVertex;

                        // Add the edge weight in an array for quick access
                        ingoingVertexWeights[successor] = weight;

                        if (!isInQueue[successor]) {
                            vertices.add(successor);
                            isInQueue[successor] = true;
                        }
                    }
                }
                // At the nth iteration, we know there is a negative cycle
                if (iterationCount == numVertices) {
                    return getNegativeCycle(currentVertex, predecessors, ingoingVertexWeights);
                }
            }
        }
        return new BFYResult.ShortestPathTree(distances, predecessors);
    }

    /**
     * Detects and constructs a negative cycle starting from a given vertex.
     *
     * @param from The vertex from which cycle detection begins.
     * @param predecessors Array holding the predecessor of each vertex in the path construction.
     * @param ingoingVertexWeights Array holding weights of the incoming edges to each vertex.
     * @return BFYResult representing the negative cycle found in the graph.
     */
    static private BFYResult getNegativeCycle(int from, int[] predecessors, int[] ingoingVertexWeights) {
        int currentCycleVertex = from, cycleWeight = 0;
        List<Integer> negativeCycle = new LinkedList<>();

        while (true) {
            // Visit the predecessors until we detect a cycle
            if (predecessors[currentCycleVertex] == VISITED) {
                // Remove vertices and weights that are not part of the cycle
                while (negativeCycle.getLast() != currentCycleVertex) {
                    cycleWeight -= ingoingVertexWeights[negativeCycle.getLast()];
                    negativeCycle.removeLast();
                }
                return new BFYResult.NegativeCycle(negativeCycle, cycleWeight);
            }

            // Accumulate the weights
            cycleWeight += ingoingVertexWeights[currentCycleVertex];

            // Add the current vertex to the cycle from the beginning to respect the cycle order
            negativeCycle.addFirst(currentCycleVertex);
            int temp = currentCycleVertex;
            currentCycleVertex = predecessors[currentCycleVertex];

            // Re-use the predecessors array to mark the vertex as visited
            predecessors[temp] = VISITED;
        }
    }
}
