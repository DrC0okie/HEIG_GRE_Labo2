package gre.lab2.groupC;

import gre.lab2.graph.BFYResult;
import gre.lab2.graph.IBellmanFordYensAlgorithm;
import gre.lab2.graph.WeightedDigraph;
import java.util.*;

public final class BellmanFordYensAlgorithm implements IBellmanFordYensAlgorithm {

    @Override
    public BFYResult compute(WeightedDigraph graph, int from) {
        int sentinel = -1;
        int[] predecessors = new int[graph.getNVertices()];
        Arrays.fill(predecessors, sentinel);

        // Initialize the shortest path lengths to infinity
        int[] shortestPathLengths = new int[graph.getNVertices()];
        Arrays.fill(shortestPathLengths, Integer.MAX_VALUE);

        // Use an array to check if a vertex is in the queue
        boolean[] isInQueue = new boolean[graph.getNVertices()];

        // Initialize source length and iteration counter to 0
        shortestPathLengths[from] = 0;
        int iterationCount = 0;

        Deque<Integer> vertices = new ArrayDeque<>();

        // Add the source vertex and the sentinel to the queue
        vertices.add(from);
        vertices.add(sentinel);

        while (!vertices.isEmpty()) {

            // Retrieve the first vertex from the queue
            int currentVertex = vertices.removeFirst();

            //Only update isInQueue when it's not the sentinel
            if(currentVertex != sentinel){
                isInQueue[currentVertex] = false;
            }

            // End of iteration
            if (currentVertex == sentinel) {
                if (!vertices.isEmpty()) {
                    iterationCount++;

                    // Negative cycle detected
                    if (iterationCount == graph.getNVertices()) {
                        return getNegativeCycle(vertices, predecessors, graph);
                    }
                    vertices.add(sentinel);
                }
            } else {
                // Process every successor of the current vertex
                for (WeightedDigraph.Edge succ : graph.getOutgoingEdges(currentVertex)) {
                    // Improvement of the shortest path length => update predecessor
                    if (shortestPathLengths[succ.to()] > shortestPathLengths[currentVertex] + succ.weight()) {
                        shortestPathLengths[succ.to()] = shortestPathLengths[currentVertex] + succ.weight();
                        predecessors[succ.to()] = currentVertex;
                        if (!isInQueue[succ.to()]) {

                            // Add the successor to the queue
                            vertices.add(succ.to());
                            isInQueue[succ.to()] = true;
                        }
                    }
                }
            }
        }

        return new BFYResult.ShortestPathTree(shortestPathLengths, predecessors);
    }

    private BFYResult getNegativeCycle(Deque<Integer> vertices, int[] predecessors, WeightedDigraph graph) {
        List<Integer> negativeCycle = new LinkedList<>();

        // Get the last vertex of the Deque. It must be inside the negative cycle
        int currentCycleVertex = vertices.getLast();
        int cycleWeight = 0;

        // Add the last vertex in the list
        negativeCycle.add(currentCycleVertex);

        // Parse all predecessors of the current vertex until we find it again (cycle)
        while (true) {
            int predecessor = predecessors[currentCycleVertex];

            // Accumulate the weight of the edge between predecessor and currentCycleVertex
            for (WeightedDigraph.Edge edge : graph.getOutgoingEdges(predecessor)) {
                if (edge.to() == currentCycleVertex) {
                    cycleWeight += edge.weight();
                    break;
                }
            }

            // Move to the predecessor
            currentCycleVertex = predecessor;

            // Add the predecessor to the list
            negativeCycle.add(currentCycleVertex);

            // Check if we have completed the cycle
            if (currentCycleVertex == vertices.getLast()) {
                break;
            }
        }
        return new BFYResult.NegativeCycle(negativeCycle, cycleWeight);
    }
}
