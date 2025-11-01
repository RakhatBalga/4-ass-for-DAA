package graph.dagsp;

import graph.util.*;
import graph.topo.KahnTopoSort;
import java.util.*;

/**
 * Computes the longest path in a DAG (critical path).
 * Uses dynamic programming over topological ordering.
 * Time complexity: O(V + E)
 */
public class DAGLongestPath {
    private final Graph graph;
    private final Metrics metrics;
    private int[] dist;
    private int[] parent;

    public DAGLongestPath(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    /**
     * Computes the longest path in the DAG
     * @return true if successful, false if graph is not a DAG
     */
    public boolean computeLongestPath() {
        int n = graph.getN();

        dist = new int[n];
        parent = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);
        Arrays.fill(parent, -1);

        // Get topological order
        KahnTopoSort topoSort = new KahnTopoSort(graph);
        List<Integer> topoOrder = topoSort.topologicalSort();

        if (topoOrder == null) {
            return false; // Not a DAG
        }

        // Initialize all vertices with in-degree 0
        int[] inDegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (Edge e : graph.getAdj(u)) {
                inDegree[e.getV()]++;
            }
        }

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        metrics.startTimer();

        // Process vertices in topological order
        for (int u : topoOrder) {
            if (dist[u] != Integer.MIN_VALUE) {
                // Update distances for all outgoing edges
                for (Edge e : graph.getAdj(u)) {
                    int v = e.getV();
                    int weight = getEdgeWeight(e, u);

                    metrics.increment("relaxations");

                    if (dist[u] + weight > dist[v]) {
                        dist[v] = dist[u] + weight;
                        parent[v] = u;
                        metrics.increment("successful_relaxations");
                    }
                }
            }
        }

        metrics.stopTimer();
        return true;
    }

    /**
     * Gets the weight for an edge based on the weight model
     */
    private int getEdgeWeight(Edge e, int fromVertex) {
        if ("node".equals(graph.getWeightModel())) {
            return graph.getNodeWeight(e.getV());
        }
        return e.getW();
    }

    /**
     * Returns the longest distance to a vertex
     */
    public int getDistance(int vertex) {
        return dist[vertex];
    }

    /**
     * Returns all distances
     */
    public int[] getDistances() {
        return dist;
    }

    /**
     * Finds the critical path (longest path in the entire DAG)
     * @return the critical path and its length
     */
    public CriticalPathResult getCriticalPath() {
        // Find the vertex with maximum distance
        int maxDist = Integer.MIN_VALUE;
        int endVertex = -1;

        for (int i = 0; i < dist.length; i++) {
            if (dist[i] > maxDist) {
                maxDist = dist[i];
                endVertex = i;
            }
        }

        if (endVertex == -1) {
            return new CriticalPathResult(new ArrayList<>(), 0);
        }

        // Reconstruct the path
        List<Integer> path = reconstructPath(endVertex);

        return new CriticalPathResult(path, maxDist);
    }

    /**
     * Reconstructs the path to a given vertex
     */
    public List<Integer> reconstructPath(int dest) {
        if (dist[dest] == Integer.MIN_VALUE) {
            return null; // No path exists
        }

        List<Integer> path = new ArrayList<>();
        int current = dest;

        while (current != -1) {
            path.add(current);
            current = parent[current];
        }

        Collections.reverse(path);
        return path;
    }

    /**
     * Returns metrics from the last execution
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Result container for critical path
     */
    public static class CriticalPathResult {
        private final List<Integer> path;
        private final int length;

        public CriticalPathResult(List<Integer> path, int length) {
            this.path = path;
            this.length = length;
        }

        public List<Integer> getPath() {
            return path;
        }

        public int getLength() {
            return length;
        }

        @Override
        public String toString() {
            return "Critical Path: " + path + ", Length: " + length;
        }
    }
}