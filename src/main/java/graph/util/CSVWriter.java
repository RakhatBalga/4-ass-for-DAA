package graph.util;

import java.io.*;
import java.util.*;

/**
 * Universal CSV writer for exporting results
 */
public class CSVWriter {

    /**
     * Writes data to a CSV file
     * @param filePath output file path
     * @param headers column headers
     * @param rows data rows
     */
    public static void writeCSV(String filePath, List<String> headers, List<List<Object>> rows)
            throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write headers
            writer.println(String.join(",", headers));

            // Write rows
            for (List<Object> row : rows) {
                List<String> stringRow = new ArrayList<>();
                for (Object cell : row) {
                    stringRow.add(escapeCSV(String.valueOf(cell)));
                }
                writer.println(String.join(",", stringRow));
            }
        }
    }

    /**
     * Writes a map of data to CSV (useful for metrics)
     */
    public static void writeMapToCSV(String filePath, Map<String, Object> data)
            throws IOException {
        List<String> headers = Arrays.asList("Key", "Value");
        List<List<Object>> rows = new ArrayList<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            rows.add(Arrays.asList(entry.getKey(), entry.getValue()));
        }

        writeCSV(filePath, headers, rows);
    }

    /**
     * Appends a row to an existing CSV file
     */
    public static void appendToCSV(String filePath, List<Object> row) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            List<String> stringRow = new ArrayList<>();
            for (Object cell : row) {
                stringRow.add(escapeCSV(String.valueOf(cell)));
            }
            writer.println(String.join(",", stringRow));
        }
    }

    /**
     * Escapes CSV special characters
     */
    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        // Escape quotes and wrap in quotes if contains comma, quote, or newline
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    /**
     * Creates the results directory if it doesn't exist
     */
    public static void ensureResultsDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}