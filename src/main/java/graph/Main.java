package graph;

import graph.scc.*;
import graph.topo.*;
import graph.dagsp.*;
import graph.util.*;

import java.io.*;
import java.util.*;

/**
 * Main entry point for the graph algorithms assignment.
 * Processes all datasets and generates results.
 */
public class Main {

    private static final String DATA_DIR = "data/";
    private static final String RESULTS_DIR = "results/";

    public static void main(String[] args) {
        try {
            // Ensure results directory exists
            CSVWriter.ensureResultsDirectory(RESULTS_DIR);

            System.out.println("=== Graph Algorithms: SCC, Topological Sort, DAG SP/LP ===\n");

            // Get all JSON files from data directory
            List<String> dataFiles = getDataFiles();

            if (dataFiles.isEmpty()) {
                System.err.println("No JSON files found in " + DATA_DIR);
                System.err.println("Run DatasetGenerator first to create test datasets.");
                return;
            }

            SummaryCollector summary = new SummaryCollector();

            // Process each dataset
            for (String dataFile : dataFiles) {
                System.out.println("\n" + "=".repeat(80));
                System.out.println("Processing: " + dataFile);
                System.out.println("=".repeat(80));

                try {
                    processDataset(dataFile, summary);
                } catch (Exception e) {
                    System.err.println("Error processing " + dataFile + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Export and print summary
            summary.exportSummary(RESULTS_DIR + "summary.csv");
            summary.printSummary();

            System.out.println("\n✓ All results exported to " + RESULTS_DIR);
            System.out.println("\nNext steps:");
            System.out.println("  1. Check results/summary.csv for overall metrics");
            System.out.println("  2. Run PlotGenerator to create visualizations:");
            System.out.println("     mvn exec:java -Dexec.mainClass=\"graph.util.PlotGenerator\"");

        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Processes a single dataset through all algorithms
     */
    private static void processDataset(String dataFile, SummaryCollector summary) throws IOException {
        String baseName = new File(dataFile).getName().replace(".json", "");

        // Load graph
        JsonLoader.GraphData graphData = JsonLoader.loadGraph(DATA_DIR + dataFile);
        Graph graph = graphData.getGraph();
        int source = graphData.getSource();

        System.out.println("Graph: " + graph.getN() + " vertices, " +
                graph.getEdgeCount() + " edges");
        System.out.println("Weight model: " + graph.getWeightModel());
        System.out.println("Source vertex: " + source);

        // 1. Run SCC (Tarjan)
        System.out.println("\n--- Step 1: Finding Strongly Connected Components (Tarjan) ---");
        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();
        Metrics sccMetrics = tarjan.getMetrics();

        System.out.println("Found " + sccs.size() + " SCCs");
        System.out.println("SCC sizes: " + tarjan.getSCCSizes());
        System.out.printf("Time: %.3f ms\n", sccMetrics.getElapsedTimeMs());
        System.out.println("DFS visits: " + sccMetrics.getCounter("dfs_visits"));
        System.out.println("Edges explored: " + sccMetrics.getCounter("edges_explored"));

        // Export SCC results
        SCCExporter.exportSCCs(RESULTS_DIR + baseName + "_scc.csv",
                baseName, sccs, sccMetrics);

        // 2. Build Condensation Graph
        System.out.println("\n--- Step 2: Building Condensation Graph (DAG) ---");
        CondensationGraph condensation = new CondensationGraph(graph, sccs);
        Graph dag = condensation.getCondensation();

        System.out.println("Condensation DAG: " + dag.getN() + " components, " +
                dag.getEdgeCount() + " edges");
        System.out.println("Is DAG: " + condensation.isDAG());

        // 3. Topological Sort
        System.out.println("\n--- Step 3: Topological Sort (Kahn) ---");
        KahnTopoSort topoSort = new KahnTopoSort(dag);
        List<Integer> componentOrder = topoSort.topologicalSort();
        Metrics topoMetrics = topoSort.getMetrics();

        if (componentOrder == null) {
            System.out.println("ERROR: Condensation graph has a cycle (should not happen!)");
            return;
        }

        // Derive task order from component order
        List<Integer> taskOrder = KahnTopoSort.deriveTaskOrder(componentOrder, sccs);

        System.out.println("Component order: " + componentOrder);
        System.out.println("Task order (first 10): " +
                taskOrder.subList(0, Math.min(10, taskOrder.size())) + "...");
        System.out.printf("Time: %.3f ms\n", topoMetrics.getElapsedTimeMs());
        System.out.println("Queue operations: " + topoMetrics.getCounter("queue_pops"));

        // Export topological order
        TopoExporter.exportTopoOrder(RESULTS_DIR + baseName + "_topo.csv",
                baseName, componentOrder, taskOrder, topoMetrics);

        // 4. Shortest Paths in DAG
        System.out.println("\n--- Step 4: Shortest Paths in DAG ---");
        DAGShortestPath shortestPath = new DAGShortestPath(dag);

        // Use source component (map original source to SCC)
        int sourceComponent = condensation.getSCCIndex(source);

        boolean spSuccess = shortestPath.computeShortestPaths(sourceComponent);
        Metrics spMetrics = shortestPath.getMetrics();

        if (spSuccess) {
            int[] distances = shortestPath.getDistances();

            // Find a reachable destination for sample path
            int sampleDest = -1;
            for (int i = 0; i < distances.length; i++) {
                if (distances[i] != Integer.MAX_VALUE && i != sourceComponent) {
                    sampleDest = i;
                    break;
                }
            }

            List<Integer> samplePath = null;
            if (sampleDest != -1) {
                samplePath = shortestPath.getPath(sampleDest);
                System.out.println("Sample shortest path from " + sourceComponent +
                        " to " + sampleDest + ": " + samplePath);
                System.out.println("Distance: " + distances[sampleDest]);
            } else {
                System.out.println("No reachable destinations from source");
            }

            System.out.printf("Time: %.3f ms\n", spMetrics.getElapsedTimeMs());
            System.out.println("Relaxations: " + spMetrics.getCounter("relaxations"));

            // Export shortest paths
            DAGExporter.exportShortestPaths(RESULTS_DIR + baseName + "_shortest.csv",
                    baseName, sourceComponent, distances,
                    samplePath, sampleDest != -1 ? sampleDest : 0,
                    spMetrics);
        } else {
            System.out.println("ERROR: Failed to compute shortest paths (not a DAG)");
        }

        // 5. Longest Path (Critical Path)
        System.out.println("\n--- Step 5: Longest Path (Critical Path) ---");
        DAGLongestPath longestPath = new DAGLongestPath(dag);
        boolean lpSuccess = longestPath.computeLongestPath();
        Metrics lpMetrics = longestPath.getMetrics();

        if (lpSuccess) {
            DAGLongestPath.CriticalPathResult criticalPath = longestPath.getCriticalPath();

            System.out.println("Critical Path: " + criticalPath.getPath());
            System.out.println("Length: " + criticalPath.getLength());
            System.out.printf("Time: %.3f ms\n", lpMetrics.getElapsedTimeMs());
            System.out.println("Relaxations: " + lpMetrics.getCounter("relaxations"));

            // Export longest path
            DAGExporter.exportLongestPath(RESULTS_DIR + baseName + "_longest.csv",
                    baseName, criticalPath, lpMetrics);
        } else {
            System.out.println("ERROR: Failed to compute longest path (not a DAG)");
        }

        // Add to summary
        summary.addRow(
                baseName,
                graph.getN(),
                graph.getEdgeCount(),
                sccs.size(),
                sccMetrics.getElapsedTimeMs(),
                topoMetrics.getElapsedTimeMs(),
                spSuccess ? spMetrics.getElapsedTimeMs() : 0,
                lpSuccess ? lpMetrics.getElapsedTimeMs() : 0,
                sccMetrics.getCounter("dfs_visits"),
                spSuccess && lpSuccess ?
                        spMetrics.getCounter("relaxations") + lpMetrics.getCounter("relaxations") : 0
        );
    }

    /**
     * Gets all JSON files from the data directory
     */
    private static List<String> getDataFiles() {
        List<String> files = new ArrayList<>();
        File dataDir = new File(DATA_DIR);

        if (!dataDir.exists() || !dataDir.isDirectory()) {
            return files;
        }

        File[] jsonFiles = dataDir.listFiles((dir, name) -> name.endsWith(".json"));

        if (jsonFiles != null) {
            for (File file : jsonFiles) {
                files.add(file.getName());
            }
            Collections.sort(files);
        }

        return files;
    }
}