package graph.scc;

import graph.util.*;
import java.util.*;

/**
 * Implements Tarjan's algorithm for finding Strongly Connected Components.
 * Time complexity: O(V + E)
 * Space complexity: O(V)
 */
public class TarjanSCC {
    private final Graph graph;
    private final Metrics metrics;

    private int[] disc;     // discovery time
    private int[] low;      // lowest reachable vertex
    private boolean[] onStack;
    private Stack<Integer> stack;
    private List<List<Integer>> sccs;
    private int time;

    public TarjanSCC(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    /**
     * Finds all strongly connected components
     * @return list of SCCs, each SCC is a list of vertices
     */
    public List<List<Integer>> findSCCs() {
        int n = graph.getN();
        disc = new int[n];
        low = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        sccs = new ArrayList<>();
        time = 0;

        Arrays.fill(disc, -1);

        metrics.startTimer();

        // Run DFS from all unvisited vertices
        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i);
            }
        }

        metrics.stopTimer();

        return sccs;
    }

    /**
     * DFS traversal for Tarjan's algorithm
     */
    private void dfs(int u) {
        metrics.increment("dfs_visits");

        disc[u] = low[u] = time++;
        stack.push(u);
        onStack[u] = true;

        // Visit all adjacent vertices
        for (Edge e : graph.getAdj(u)) {
            int v = e.getV();
            metrics.increment("edges_explored");

            if (disc[v] == -1) {
                // Tree edge
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                // Back edge to vertex in current SCC
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        // If u is a root node, pop the stack and create SCC
        if (low[u] == disc[u]) {
            List<Integer> scc = new ArrayList<>();
            int v;
            do {
                v = stack.pop();
                onStack[v] = false;
                scc.add(v);
                metrics.increment("stack_pops");
            } while (v != u);

            sccs.add(scc);
        }
    }

    /**
     * Returns metrics from the last execution
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Returns sizes of all SCCs
     */
    public List<Integer> getSCCSizes() {
        List<Integer> sizes = new ArrayList<>();
        for (List<Integer> scc : sccs) {
            sizes.add(scc.size());
        }
        return sizes;
    }

    /**
     * Returns the number of SCCs
     */
    public int getSCCCount() {
        return sccs.size();
    }

    /**
     * Returns a mapping from vertex to its SCC index
     */
    public int[] getVertexToSCCMapping() {
        int n = graph.getN();
        int[] mapping = new int[n];

        for (int sccIdx = 0; sccIdx < sccs.size(); sccIdx++) {
            for (int vertex : sccs.get(sccIdx)) {
                mapping[vertex] = sccIdx;
            }
        }

        return mapping;
    }
}