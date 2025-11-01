package graph.scc;

import graph.util.*;
import java.util.*;

/**
 * Builds the condensation graph (DAG) from the original graph and its SCCs.
 * Each SCC becomes a single vertex in the condensation graph.
 */
public class CondensationGraph {
    private final Graph original;
    private final List<List<Integer>> sccs;
    private final int[] vertexToSCC;
    private Graph condensation;

    public CondensationGraph(Graph original, List<List<Integer>> sccs) {
        this.original = original;
        this.sccs = sccs;
        this.vertexToSCC = new int[original.getN()];

        // Map each vertex to its SCC index
        for (int i = 0; i < sccs.size(); i++) {
            for (int v : sccs.get(i)) {
                vertexToSCC[v] = i;
            }
        }

        buildCondensation();
    }

    /**
     * Builds the condensation graph
     */
    private void buildCondensation() {
        int numSCCs = sccs.size();
        condensation = new Graph(numSCCs, true, original.getWeightModel());

        // Use a set to avoid duplicate edges between SCCs
        Set<String> addedEdges = new HashSet<>();

        // For each edge in the original graph
        for (int u = 0; u < original.getN(); u++) {
            int sccU = vertexToSCC[u];

            for (Edge e : original.getAdj(u)) {
                int v = e.getV();
                int sccV = vertexToSCC[v];

                // Add edge if it connects different SCCs
                if (sccU != sccV) {
                    String edgeKey = sccU + "->" + sccV;
                    if (!addedEdges.contains(edgeKey)) {
                        condensation.addEdge(sccU, sccV, e.getW());
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }

        // If using node weights, aggregate weights from component members
        if ("node".equals(original.getWeightModel())) {
            for (int i = 0; i < numSCCs; i++) {
                int totalWeight = 0;
                for (int v : sccs.get(i)) {
                    totalWeight += original.getNodeWeight(v);
                }
                condensation.setNodeWeight(i, totalWeight);
            }
        }
    }

    /**
     * Returns the condensation DAG
     */
    public Graph getCondensation() {
        return condensation;
    }

    /**
     * Returns the mapping from original vertices to SCC indices
     */
    public int[] getVertexToSCCMapping() {
        return vertexToSCC;
    }

    /**
     * Returns the SCC that contains a given vertex
     */
    public int getSCCIndex(int vertex) {
        return vertexToSCC[vertex];
    }

    /**
     * Returns all vertices in a given SCC
     */
    public List<Integer> getSCC(int sccIndex) {
        return sccs.get(sccIndex);
    }

    /**
     * Returns all SCCs
     */
    public List<List<Integer>> getAllSCCs() {
        return sccs;
    }

    /**
     * Checks if the condensation graph is a DAG (should always be true)
     */
    public boolean isDAG() {
        // Simple cycle detection using DFS
        int n = condensation.getN();
        int[] color = new int[n]; // 0: white, 1: gray, 2: black

        for (int i = 0; i < n; i++) {
            if (color[i] == 0) {
                if (hasCycleDFS(i, color)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasCycleDFS(int u, int[] color) {
        color[u] = 1; // gray

        for (Edge e : condensation.getAdj(u)) {
            int v = e.getV();
            if (color[v] == 1) {
                return true; // back edge found
            }
            if (color[v] == 0 && hasCycleDFS(v, color)) {
                return true;
            }
        }

        color[u] = 2; // black
        return false;
    }
}