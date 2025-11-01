package graph.util;

import java.io.*;
import java.util.*;

/**
 * Collects and exports summary results from all algorithms
 */
public class SummaryCollector {

    private final List<SummaryRow> rows = new ArrayList<>();

    /**
     * Adds a summary row for a dataset
     */
    public void addRow(String dataset, int vertices, int edges,
                       int numSCCs, double sccTime,
                       double topoTime, double shortestTime, double longestTime,
                       long dfsVisits, long relaxations) {
        rows.add(new SummaryRow(dataset, vertices, edges, numSCCs, sccTime,
                topoTime, shortestTime, longestTime,
                dfsVisits, relaxations));
    }

    /**
     * Exports summary to CSV
     */
    public void exportSummary(String filePath) throws IOException {
        List<String> headers = Arrays.asList(
                "Dataset", "Vertices", "Edges", "Num_SCCs",
                "SCC_Time_ms", "Topo_Time_ms", "Shortest_Time_ms", "Longest_Time_ms",
                "DFS_Visits", "Relaxations"
        );

        List<List<Object>> csvRows = new ArrayList<>();
        for (SummaryRow row : rows) {
            csvRows.add(Arrays.asList(
                    row.dataset, row.vertices, row.edges, row.numSCCs,
                    row.sccTime, row.topoTime, row.shortestTime, row.longestTime,
                    row.dfsVisits, row.relaxations
            ));
        }

        CSVWriter.writeCSV(filePath, headers, csvRows);
    }

    /**
     * Prints summary to console
     */
    public void printSummary() {
        System.out.println("\n=== SUMMARY OF ALL DATASETS ===\n");
        System.out.printf("%-20s %8s %8s %8s %10s %10s %10s %10s\n",
                "Dataset", "Vertices", "Edges", "SCCs",
                "SCC(ms)", "Topo(ms)", "Short(ms)", "Long(ms)");
        System.out.println("-".repeat(100));

        for (SummaryRow row : rows) {
            System.out.printf("%-20s %8d %8d %8d %10.3f %10.3f %10.3f %10.3f\n",
                    row.dataset, row.vertices, row.edges, row.numSCCs,
                    row.sccTime, row.topoTime, row.shortestTime, row.longestTime);
        }

        System.out.println("-".repeat(100));
    }

    /**
     * Internal class for storing summary data
     */
    private static class SummaryRow {
        String dataset;
        int vertices;
        int edges;
        int numSCCs;
        double sccTime;
        double topoTime;
        double shortestTime;
        double longestTime;
        long dfsVisits;
        long relaxations;

        SummaryRow(String dataset, int vertices, int edges, int numSCCs,
                   double sccTime, double topoTime, double shortestTime, double longestTime,
                   long dfsVisits, long relaxations) {
            this.dataset = dataset;
            this.vertices = vertices;
            this.edges = edges;
            this.numSCCs = numSCCs;
            this.sccTime = sccTime;
            this.topoTime = topoTime;
            this.shortestTime = shortestTime;
            this.longestTime = longestTime;
            this.dfsVisits = dfsVisits;
            this.relaxations = relaxations;
        }
    }
}