package graph.dagsp;

import graph.util.*;
import graph.topo.KahnTopoSort;
import java.util.*;

/**
 * Computes single-source shortest paths in a DAG using topological ordering.
 * Time complexity: O(V + E)
 */
public class DAGShortestPath {
    private final Graph graph;
    private final Metrics metrics;
    private int source;
    private int[] dist;
    private int[] parent;

    public DAGShortestPath(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    /**
     * Computes shortest paths from source to all reachable vertices
     * @param source the source vertex
     * @return true if successful, false if graph is not a DAG
     */
    public boolean computeShortestPaths(int source) {
        this.source = source;
        int n = graph.getN();

        dist = new int[n];
        parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        // Get topological order
        KahnTopoSort topoSort = new KahnTopoSort(graph);
        List<Integer> topoOrder = topoSort.topologicalSort();

        if (topoOrder == null) {
            return false; // Not a DAG
        }

        metrics.startTimer();

        // Process vertices in topological order
        for (int u : topoOrder) {
            if (dist[u] != Integer.MAX_VALUE) {
                // Relax all outgoing edges
                for (Edge e : graph.getAdj(u)) {
                    int v = e.getV();
                    int weight = getEdgeWeight(e, u);

                    metrics.increment("relaxations");

                    if (dist[u] + weight < dist[v]) {
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
     * Returns the shortest distance to a vertex
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
     * Reconstructs the shortest path from source to destination
     * @return list of vertices in the path, or null if no path exists
     */
    public List<Integer> getPath(int dest) {
        if (dist[dest] == Integer.MAX_VALUE) {
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
     * Returns the source vertex
     */
    public int getSource() {
        return source;
    }

    /**
     * Result container for shortest path computation
     */
    public static class ShortestPathResult {
        private final int source;
        private final int[] distances;
        private final Map<Integer, List<Integer>> paths;
        private final Metrics metrics;

        public ShortestPathResult(int source, int[] distances,
                                  Map<Integer, List<Integer>> paths, Metrics metrics) {
            this.source = source;
            this.distances = distances;
            this.paths = paths;
            this.metrics = metrics;
        }

        public int getSource() {
            return source;
        }

        public int[] getDistances() {
            return distances;
        }

        public List<Integer> getPath(int dest) {
            return paths.get(dest);
        }

        public Metrics getMetrics() {
            return metrics;
        }
    }
}