package graph.topo;

import graph.util.*;
import java.io.*;
import java.util.*;

/**
 * Exports topological sort results to CSV
 */
public class TopoExporter {

    /**
     * Exports topological order to CSV
     */
    public static void exportTopoOrder(String filePath, String datasetName,
                                       List<Integer> componentOrder,
                                       List<Integer> taskOrder,
                                       Metrics metrics) throws IOException {
        List<String> headers = Arrays.asList(
                "Dataset", "Component_Order", "Task_Order",
                "Time_ms", "Queue_Pushes", "Queue_Pops", "InDegree_Updates"
        );

        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(
                datasetName,
                componentOrder.toString(),
                taskOrder.toString(),
                metrics.getElapsedTimeMs(),
                metrics.getCounter("queue_pushes"),
                metrics.getCounter("queue_pops"),
                metrics.getCounter("in_degree_updates")
        ));

        CSVWriter.writeCSV(filePath, headers, rows);
    }

    /**
     * Exports detailed topological order (one vertex per row)
     */
    public static void exportDetailedTopoOrder(String filePath, String datasetName,
                                               List<Integer> order,
                                               String orderType) throws IOException {
        List<String> headers = Arrays.asList("Dataset", "Order_Type", "Position", "Vertex");

        List<List<Object>> rows = new ArrayList<>();
        for (int i = 0; i < order.size(); i++) {
            rows.add(Arrays.asList(
                    datasetName,
                    orderType,
                    i,
                    order.get(i)
            ));
        }

        CSVWriter.writeCSV(filePath, headers, rows);
    }
}