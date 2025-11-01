package graph.scc;

import graph.util.*;
import java.io.*;
import java.util.*;

/**
 * Exports SCC results to CSV format
 */
public class SCCExporter {

    /**
     * Exports SCC results to CSV
     */
    public static void exportSCCs(String filePath, String datasetName,
                                  List<List<Integer>> sccs, Metrics metrics)
            throws IOException {
        List<String> headers = Arrays.asList(
                "Dataset", "SCC_Index", "SCC_Size", "Vertices",
                "Time_ms", "DFS_Visits", "Edges_Explored", "Stack_Pops"
        );

        List<List<Object>> rows = new ArrayList<>();

        for (int i = 0; i < sccs.size(); i++) {
            List<Integer> scc = sccs.get(i);

            // Sort vertices for consistent output
            List<Integer> sortedSCC = new ArrayList<>(scc);
            Collections.sort(sortedSCC);

            rows.add(Arrays.asList(
                    datasetName,
                    i,
                    scc.size(),
                    sortedSCC.toString(),
                    i == 0 ? metrics.getElapsedTimeMs() : "", // Only show once
                    i == 0 ? metrics.getCounter("dfs_visits") : "",
                    i == 0 ? metrics.getCounter("edges_explored") : "",
                    i == 0 ? metrics.getCounter("stack_pops") : ""
            ));
        }

        CSVWriter.writeCSV(filePath, headers, rows);
    }

    /**
     * Exports a summary of SCC analysis
     */
    public static void exportSCCSummary(String filePath, String datasetName,
                                        int numVertices, int numEdges,
                                        List<List<Integer>> sccs, Metrics metrics)
            throws IOException {
        List<String> headers = Arrays.asList(
                "Dataset", "Vertices", "Edges", "Num_SCCs", "Largest_SCC",
                "Time_ms", "DFS_Visits", "Edges_Explored"
        );

        int largestSCC = 0;
        for (List<Integer> scc : sccs) {
            largestSCC = Math.max(largestSCC, scc.size());
        }

        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(
                datasetName,
                numVertices,
                numEdges,
                sccs.size(),
                largestSCC,
                metrics.getElapsedTimeMs(),
                metrics.getCounter("dfs_visits"),
                metrics.getCounter("edges_explored")
        ));

        CSVWriter.writeCSV(filePath, headers, rows);
    }
}