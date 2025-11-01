package graph.topo;

import graph.util.*;
import java.util.*;

/**
 * Implements Kahn's algorithm for topological sorting of a DAG.
 * Uses in-degree tracking and queue-based processing.
 * Time complexity: O(V + E)
 */
public class KahnTopoSort {
    private final Graph graph;
    private final Metrics metrics;

    public KahnTopoSort(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    /**
     * Computes a topological ordering of the graph
     * @return topological order as list of vertices, or null if graph has a cycle
     */
    public List<Integer> topologicalSort() {
        int n = graph.getN();
        int[] inDegree = new int[n];

        // Calculate in-degrees
        for (int u = 0; u < n; u++) {
            for (Edge e : graph.getAdj(u)) {
                inDegree[e.getV()]++;
            }
        }

        // Queue for vertices with in-degree 0
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.increment("queue_pushes");
            }
        }

        List<Integer> topoOrder = new ArrayList<>();

        metrics.startTimer();

        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.increment("queue_pops");
            topoOrder.add(u);

            // Reduce in-degree for all neighbors
            for (Edge e : graph.getAdj(u)) {
                int v = e.getV();
                inDegree[v]--;
                metrics.increment("in_degree_updates");

                if (inDegree[v] == 0) {
                    queue.offer(v);
                    metrics.increment("queue_pushes");
                }
            }
        }

        metrics.stopTimer();

        // Check if all vertices are included (no cycle)
        if (topoOrder.size() != n) {
            return null; // Graph has a cycle
        }

        return topoOrder;
    }

    /**
     * Returns metrics from the last execution
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Computes a topological order and derives original task order from SCC mapping
     */
    public static class TopoResult {
        private final List<Integer> componentOrder;
        private final List<Integer> taskOrder;
        private final boolean isDAG;

        public TopoResult(List<Integer> componentOrder, List<Integer> taskOrder, boolean isDAG) {
            this.componentOrder = componentOrder;
            this.taskOrder = taskOrder;
            this.isDAG = isDAG;
        }

        public List<Integer> getComponentOrder() {
            return componentOrder;
        }

        public List<Integer> getTaskOrder() {
            return taskOrder;
        }

        public boolean isDAG() {
            return isDAG;
        }
    }

    /**
     * Derives original task order from SCC topological order
     */
    public static List<Integer> deriveTaskOrder(List<Integer> sccOrder, List<List<Integer>> sccs) {
        List<Integer> taskOrder = new ArrayList<>();

        for (int sccIdx : sccOrder) {
            // Add all tasks in this SCC
            List<Integer> sccTasks = new ArrayList<>(sccs.get(sccIdx));
            Collections.sort(sccTasks); // Consistent ordering within SCC
            taskOrder.addAll(sccTasks);
        }

        return taskOrder;
    }
}