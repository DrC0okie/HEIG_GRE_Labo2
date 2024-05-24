package gre.lab2.groupC;

import gre.lab2.graph.BFYResult;
import gre.lab2.graph.IBellmanFordYensAlgorithm;
import gre.lab2.graph.WeightedDigraph;

import static gre.lab2.graph.BFYResult.UNREACHABLE;

import java.util.*;

public final class BellmanFordYensAlgorithm implements IBellmanFordYensAlgorithm {

    static final int VISITED = -2;

    @Override
    public BFYResult compute(WeightedDigraph graph, int source) {
        int numVertices = graph.getNVertices();
        int[] predecessors = new int[numVertices], distances = new int[numVertices];

        // Initialize distances to infinity and predecessors to -1
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
                    int successor = edge.to();
                    int newDistance = distances[currentVertex] + edge.weight();

                    // If the new path is shorter, update the distance and predecessor
                    if (distances[successor] > newDistance) {

                        distances[successor] = newDistance;
                        predecessors[successor] = currentVertex;

                        ingoingVertexWeights[successor] = edge.weight();

                        if (!isInQueue[successor]) {
                            // Add the successor to the queue
                            vertices.add(successor);
                            isInQueue[successor] = true;
                        }
                    }
                }

                if(iterationCount == numVertices) {
                    return getNegativeCycle(currentVertex, predecessors, ingoingVertexWeights);
                }
            }
        }
        return new BFYResult.ShortestPathTree(distances, predecessors);
    }

    static private BFYResult getNegativeCycle(int from, int[] predecessors, int[]ingoingVertexWeights){
        int currentCycleVertex = from;
        List<Integer> negativeCycle = new LinkedList<>();
        int cycleWeight = 0;
        while(true){

            if (predecessors[currentCycleVertex] == VISITED) {
                while (negativeCycle.getLast() != currentCycleVertex) {
                    cycleWeight -= ingoingVertexWeights[negativeCycle.getLast()];
                    negativeCycle.removeLast();
                }
                return new BFYResult.NegativeCycle(negativeCycle, cycleWeight);
            }

            // Accumulate the weights
            cycleWeight += ingoingVertexWeights[currentCycleVertex];

            // Add the current vertex to the cycle
            negativeCycle.addFirst(currentCycleVertex);
            int temp = currentCycleVertex;
            currentCycleVertex = predecessors[currentCycleVertex];

            // Mark the vertex as visited
            predecessors[temp] = VISITED;
        }
    }
}
