package graph.util;

import com.google.gson.*;
import java.io.FileReader;
import java.io.IOException;

/**
 * Loads graph data from JSON files.
 * Expected format:
 * {
 *   "directed": true/false,
 *   "n": <number of vertices>,
 *   "edges": [{"u": <from>, "v": <to>, "w": <weight>}, ...],
 *   "source": <source vertex> (optional),
 *   "weight_model": "edge" or "node"
 * }
 */
public class JsonLoader {

    /**
     * Loads a graph from a JSON file
     */
    public static GraphData loadGraph(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            boolean directed = json.get("directed").getAsBoolean();
            int n = json.get("n").getAsInt();
            String weightModel = json.has("weight_model") ?
                    json.get("weight_model").getAsString() : "edge";

            Graph graph = new Graph(n, directed, weightModel);

            JsonArray edges = json.getAsJsonArray("edges");
            for (JsonElement edgeElem : edges) {
                JsonObject edge = edgeElem.getAsJsonObject();
                int u = edge.get("u").getAsInt();
                int v = edge.get("v").getAsInt();
                int w = edge.get("w").getAsInt();
                graph.addEdge(u, v, w);
            }

            int source = json.has("source") ? json.get("source").getAsInt() : 0;

            return new GraphData(graph, source, filePath);
        } catch (Exception e) {
            throw new IOException("Failed to load graph from " + filePath, e);
        }
    }

    /**
     * Container for graph data loaded from JSON
     */
    public static class GraphData {
        private final Graph graph;
        private final int source;
        private final String fileName;

        public GraphData(Graph graph, int source, String fileName) {
            this.graph = graph;
            this.source = source;
            this.fileName = fileName;
        }

        public Graph getGraph() {
            return graph;
        }

        public int getSource() {
            return source;
        }

        public String getFileName() {
            return fileName;
        }
    }
}