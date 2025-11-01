package graph.util;

import java.util.*;

/**
 * Represents a directed graph with weighted edges.
 * Supports both adjacency list representation and operations needed for
 * SCC, topological sorting, and shortest/longest path algorithms.
 */
public class Graph {
    private final int n; // number of vertices
    private final List<List<Edge>> adj; // adjacency list
    private final boolean directed;
    private final String weightModel; // "edge" or "node"
    private int[] nodeWeights; // optional node weights

    /**
     * Creates a graph with n vertices
     */
    public Graph(int n, boolean directed, String weightModel) {
        this.n = n;
        this.directed = directed;
        this.weightModel = weightModel;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        if ("node".equals(weightModel)) {
            this.nodeWeights = new int[n];
        }
    }

    /**
     * Adds an edge from u to v with weight w
     */
    public void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(u, v, w));
        if (!directed) {
            adj.get(v).add(new Edge(v, u, w));
        }
    }

    /**
     * Sets the weight for a node (used when weightModel is "node")
     */
    public void setNodeWeight(int node, int weight) {
        if (nodeWeights != null) {
            nodeWeights[node] = weight;
        }
    }

    /**
     * Gets the weight for a node
     */
    public int getNodeWeight(int node) {
        return nodeWeights != null ? nodeWeights[node] : 0;
    }

    /**
     * Returns the adjacency list for vertex v
     */
    public List<Edge> getAdj(int v) {
        return adj.get(v);
    }

    /**
     * Returns the number of vertices
     */
    public int getN() {
        return n;
    }

    /**
     * Returns whether the graph is directed
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     * Returns the weight model
     */
    public String getWeightModel() {
        return weightModel;
    }

    /**
     * Creates the reverse (transpose) graph
     */
    public Graph getTranspose() {
        Graph transpose = new Graph(n, directed, weightModel);
        for (int u = 0; u < n; u++) {
            for (Edge e : adj.get(u)) {
                transpose.addEdge(e.getV(), e.getU(), e.getW());
            }
        }
        return transpose;
    }

    /**
     * Returns total number of edges
     */
    public int getEdgeCount() {
        int count = 0;
        for (int i = 0; i < n; i++) {
            count += adj.get(i).size();
        }
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph{n=").append(n)
                .append(", edges=").append(getEdgeCount())
                .append(", directed=").append(directed)
                .append(", weightModel=").append(weightModel)
                .append("}\n");
        for (int i = 0; i < n; i++) {
            sb.append(i).append(": ");
            for (Edge e : adj.get(i)) {
                sb.append("(").append(e.getV()).append(",w=").append(e.getW()).append(") ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}