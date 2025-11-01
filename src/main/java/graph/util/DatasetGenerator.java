package graph.util;

import com.google.gson.*;
import java.io.*;
import java.util.*;

/**
 * Utility class for generating test datasets for the assignment.
 * Creates graphs of various sizes with different characteristics:
 * - Small (6-10 nodes)
 * - Medium (10-20 nodes)
 * - Large (20-50 nodes)
 * - Cyclic and acyclic variants
 * - Sparse and dense variants
 */
public class DatasetGenerator {

    private static final Random random = new Random(42); // Fixed seed for reproducibility

    public static void main(String[] args) throws IOException {
        String outputDir = "data/";
        new File(outputDir).mkdirs();

        System.out.println("=== Dataset Generator ===\n");
        System.out.println("Generating test datasets...\n");

        // Generate small datasets (6-10 nodes)
        generateDataset(outputDir + "tasks_small_1.json", 6, 8, true, 0.3, "Small DAG (sparse)");
        generateDataset(outputDir + "tasks_small_2.json", 8, 12, false, 0.4, "Small with cycle");
        generateDataset(outputDir + "tasks_small_3.json", 10, 25, true, 0.5, "Small DAG (dense)");

        // Generate medium datasets (10-20 nodes)
        generateDataset(outputDir + "tasks_medium_1.json", 12, 20, false, 0.3, "Medium with SCCs");
        generateDataset(outputDir + "tasks_medium_2.json", 15, 35, false, 0.35, "Medium mixed");
        generateDataset(outputDir + "tasks_medium_3.json", 18, 45, true, 0.3, "Medium DAG");

        // Generate large datasets (20-50 nodes)
        generateDataset(outputDir + "tasks_large_1.json", 25, 50, false, 0.2, "Large sparse with cycles");
        generateDataset(outputDir + "tasks_large_2.json", 35, 120, false, 0.25, "Large dense with SCCs");
        generateDataset(outputDir + "tasks_large_3.json", 50, 150, true, 0.15, "Large DAG (sparse)");

        System.out.println("\n✓ All 9 datasets generated successfully!");
        System.out.println("Datasets saved in: " + outputDir);
    }

    /**
     * Generates a single dataset
     */
    private static void generateDataset(String filename, int n, int targetEdges,
                                        boolean forceDAG, double cycleProbability,
                                        String description) throws IOException {
        System.out.println("Generating: " + filename);
        System.out.println("  " + description);
        System.out.println("  Nodes: " + n + ", Target edges: " + targetEdges);

        Set<String> edges = new HashSet<>();
        List<EdgeData> edgeList = new ArrayList<>();

        if (forceDAG) {
            // Generate DAG: only add edges from lower to higher indices
            int edgeCount = 0;
            while (edgeCount < targetEdges) {
                int u = random.nextInt(n - 1);
                int v = u + 1 + random.nextInt(n - u - 1);

                String edgeKey = u + "->" + v;
                if (!edges.contains(edgeKey)) {
                    edges.add(edgeKey);
                    int weight = 1 + random.nextInt(10);
                    edgeList.add(new EdgeData(u, v, weight));
                    edgeCount++;
                }
            }
        } else {
            // Generate graph with potential cycles
            // First, create a spanning tree to ensure connectivity
            List<Integer> visited = new ArrayList<>();
            visited.add(0);

            while (visited.size() < n) {
                int u = visited.get(random.nextInt(visited.size()));
                int v = random.nextInt(n);

                if (!visited.contains(v)) {
                    String edgeKey = u + "->" + v;
                    edges.add(edgeKey);
                    int weight = 1 + random.nextInt(10);
                    edgeList.add(new EdgeData(u, v, weight));
                    visited.add(v);
                }
            }

            // Add remaining edges (may create cycles)
            while (edgeList.size() < targetEdges) {
                int u = random.nextInt(n);
                int v = random.nextInt(n);

                if (u != v) {
                    String edgeKey = u + "->" + v;
                    if (!edges.contains(edgeKey)) {
                        // Decide whether to add a back edge (creates cycle)
                        boolean addEdge = true;
                        if (u > v && random.nextDouble() > cycleProbability) {
                            addEdge = false; // Skip back edge
                        }

                        if (addEdge) {
                            edges.add(edgeKey);
                            int weight = 1 + random.nextInt(10);
                            edgeList.add(new EdgeData(u, v, weight));
                        }
                    }
                }
            }
        }

        // Choose a random source vertex
        int source = random.nextInt(n);

        // Write to JSON
        writeGraphToJson(filename, n, edgeList, source, forceDAG, description);

        System.out.println("  Generated " + edgeList.size() + " edges");
        System.out.println("  Source: " + source);
        System.out.println();
    }

    /**
     * Writes graph data to JSON file
     */
    private static void writeGraphToJson(String filename, int n, List<EdgeData> edges,
                                         int source, boolean isDAG, String description)
            throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("description", description);
        json.addProperty("directed", true);
        json.addProperty("n", n);
        json.addProperty("source", source);
        json.addProperty("weight_model", "edge");
        json.addProperty("expected_dag", isDAG);

        JsonArray edgesArray = new JsonArray();
        for (EdgeData e : edges) {
            JsonObject edgeObj = new JsonObject();
            edgeObj.addProperty("u", e.u);
            edgeObj.addProperty("v", e.v);
            edgeObj.addProperty("w", e.w);
            edgesArray.add(edgeObj);
        }
        json.add("edges", edgesArray);

        // Write with pretty printing
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(json, writer);
        }
    }

    /**
     * Simple Edge class for generator
     */
    private static class EdgeData {
        final int u, v, w;

        EdgeData(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }
}