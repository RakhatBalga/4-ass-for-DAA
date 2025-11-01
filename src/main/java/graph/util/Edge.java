package graph.util;

/**
 * Represents a directed edge from u to v with weight w.
 */
public class Edge {
    private final int u;
    private final int v;
    private final int w;

    public Edge(int u, int v, int w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public int getW() {
        return w;
    }

    @Override
    public String toString() {
        return "(" + u + " -> " + v + ", w=" + w + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Edge)) return false;
        Edge other = (Edge) obj;
        return u == other.u && v == other.v && w == other.w;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * u + v) + w;
    }
}