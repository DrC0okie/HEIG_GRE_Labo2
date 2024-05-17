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
                        // TODO => detect negative cycle
                        return new BFYResult.NegativeCycle(new ArrayList<Integer>(2), 0);
                    }
                    vertices.add(sentinel);
                }
            } else {
                // Process every sucessor of the current vertex
                for (WeightedDigraph.Edge succ : graph.getOutgoingEdges(currentVertex)) {
                    // Improvement of the shortest path length
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

    private BFYResult detectNegativeCycle(WeightedDigraph graph, int[] shortestPathLengths, int[] predecessors) {
        int n = graph.getNVertices();

        for (int u = 0; u < n; u++) {
            for (WeightedDigraph.Edge edge : graph.getOutgoingEdges(u)) {
                if (shortestPathLengths[u] != Integer.MAX_VALUE &&
                        shortestPathLengths[edge.to()] > shortestPathLengths[u] + edge.weight()) {

                    // Cycle detected
                    List<Integer> cycle = new ArrayList<>();
                    boolean[] visited = new boolean[n];
                    int x = u;

                    // Find the start of the cycle
                    for (int i = 0; i < n; i++) {
                        x = predecessors[x];
                    }

                    // Trace the cycle
                    int start = x;
                    do {
                        cycle.add(start);
                        start = predecessors[start];
                    } while (start != x);

                    cycle.add(x);
                    Collections.reverse(cycle);

                    // Calculate cycle weight
                    int cycleWeight = 0;
                    for (int i = 0; i < cycle.size() - 1; i++) {
                        int from = cycle.get(i);
                        int to = cycle.get(i + 1);
                        for (WeightedDigraph.Edge e : graph.getOutgoingEdges(from)) {
                            if (e.to() == to) {
                                cycleWeight += e.weight();
                                break;
                            }
                        }
                    }

                    return new BFYResult.NegativeCycle(cycle, cycleWeight);
                }
            }
        }

        return new BFYResult.NegativeCycle(new ArrayList<>(), 0);
    }
}
