package graph.dagsp;

import graph.util.*;
import java.io.*;
import java.util.*;

/**
 * Exports DAG shortest/longest path results to CSV format
 */
public class DAGExporter {

    /**
     * Exports shortest path results to CSV
     */
    public static void exportShortestPaths(String filePath, String datasetName,
                                           int source, int[] distances,
                                           List<Integer> samplePath,
                                           int sampleDest,
                                           Metrics metrics) throws IOException {
        List<String> headers = Arrays.asList(
                "Dataset", "Source", "Destination", "Distance", "Path",
                "Time_ms", "Relaxations", "Successful_Relaxations"
        );

        List<List<Object>> rows = new ArrayList<>();

        // Add row for sample path
        rows.add(Arrays.asList(
                datasetName,
                source,
                sampleDest,
                distances[sampleDest] == Integer.MAX_VALUE ? "INF" : distances[sampleDest],
                samplePath != null ? samplePath.toString() : "No path",
                metrics.getElapsedTimeMs(),
                metrics.getCounter("relaxations"),
                metrics.getCounter("successful_relaxations")
        ));

        CSVWriter.writeCSV(filePath, headers, rows);
    }

    /**
     * Exports all distances from source
     */
    public static void exportAllDistances(String filePath, String datasetName,
                                          int source, int[] distances) throws IOException {
        List<String> headers = Arrays.asList("Dataset", "Source", "Destination", "Distance");

        List<List<Object>> rows = new ArrayList<>();
        for (int i = 0; i < distances.length; i++) {
            String dist = distances[i] == Integer.MAX_VALUE ? "INF" :
                    distances[i] == Integer.MIN_VALUE ? "-INF" :
                            String.valueOf(distances[i]);
            rows.add(Arrays.asList(datasetName, source, i, dist));
        }

        CSVWriter.writeCSV(filePath, headers, rows);
    }

    /**
     * Exports longest path (critical path) results
     */
    public static void exportLongestPath(String filePath, String datasetName,
                                         DAGLongestPath.CriticalPathResult criticalPath,
                                         Metrics metrics) throws IOException {
        List<String> headers = Arrays.asList(
                "Dataset", "Critical_Path", "Length",
                "Time_ms", "Relaxations", "Successful_Relaxations"
        );

        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(
                datasetName,
                criticalPath.getPath().toString(),
                criticalPath.getLength(),
                metrics.getElapsedTimeMs(),
                metrics.getCounter("relaxations"),
                metrics.getCounter("successful_relaxations")
        ));

        CSVWriter.writeCSV(filePath, headers, rows);
    }

    /**
     * Exports combined shortest and longest path summary
     */
    public static void exportDAGSummary(String filePath, String datasetName,
                                        int numVertices, int numEdges,
                                        int source, int shortestPathLength,
                                        int longestPathLength,
                                        Metrics shortestMetrics,
                                        Metrics longestMetrics) throws IOException {
        List<String> headers = Arrays.asList(
                "Dataset", "Vertices", "Edges", "Source",
                "Min_Path_Length", "Max_Path_Length",
                "Shortest_Time_ms", "Longest_Time_ms"
        );

        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(
                datasetName,
                numVertices,
                numEdges,
                source,
                shortestPathLength,
                longestPathLength,
                shortestMetrics.getElapsedTimeMs(),
                longestMetrics.getElapsedTimeMs()
        ));

        CSVWriter.writeCSV(filePath, headers, rows);
    }
}