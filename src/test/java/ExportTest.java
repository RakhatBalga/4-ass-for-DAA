package graph;

import graph.scc.*;
import graph.topo.*;
import graph.dagsp.*;
import graph.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Unit tests for CSV export functionality.
 * Tests that all exporters correctly write data to CSV files.
 */
public class ExportTest {

    private static final String TEST_RESULTS_DIR = "test_results/";

    @BeforeAll
    public static void setupTestDirectory() {
        // Create test results directory
        new File(TEST_RESULTS_DIR).mkdirs();
    }

    @AfterAll
    public static void cleanupTestDirectory() throws IOException {
        // Clean up test results directory
        File dir = new File(TEST_RESULTS_DIR);
        if (dir.exists()) {
            Files.walk(dir.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    public void testCSVWriterBasic() throws IOException {
        String testFile = TEST_RESULTS_DIR + "test_basic.csv";

        List<String> headers = Arrays.asList("Name", "Age", "City");
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList("Alice", 25, "NYC"),
                Arrays.asList("Bob", 30, "LA")
        );

        CSVWriter.writeCSV(testFile, headers, rows);

        // Verify file exists
        assertTrue(new File(testFile).exists());

        // Read and verify content
        List<String> lines = Files.readAllLines(Paths.get(testFile));
        assertEquals(3, lines.size()); // Header + 2 rows
        assertEquals("Name,Age,City", lines.get(0));
        assertTrue(lines.get(1).contains("Alice"));
        assertTrue(lines.get(2).contains("Bob"));
    }

    @Test
    public void testCSVWriterWithSpecialCharacters() throws IOException {
        String testFile = TEST_RESULTS_DIR + "test_special.csv";

        List<String> headers = Arrays.asList("Text", "Value");
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList("Hello, World", 123),
                Arrays.asList("Quote\"Test", 456)
        );

        CSVWriter.writeCSV(testFile, headers, rows);

        assertTrue(new File(testFile).exists());

        // Read and verify escaping
        List<String> lines = Files.readAllLines(Paths.get(testFile));
        assertTrue(lines.get(1).contains("\"Hello, World\""));
        assertTrue(lines.get(2).contains("Quote"));
    }

    @Test
    public void testSCCExport() throws IOException {
        String testFile = TEST_RESULTS_DIR + "test_scc.csv";

        // Create a simple graph with SCCs
        Graph g = new Graph(4, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 1, 1); // Cycle: 1-2
        g.addEdge(2, 3, 1);

        TarjanSCC tarjan = new TarjanSCC(g);
        List<List<Integer>> sccs = tarjan.findSCCs();
        Metrics metrics = tarjan.getMetrics();

        // Export
        SCCExporter.exportSCCs(testFile, "test_graph", sccs, metrics);

        // Verify file exists and has content
        assertTrue(new File(testFile).exists());
        List<String> lines = Files.readAllLines(Paths.get(testFile));
        assertTrue(lines.size() > 1); // At least header + 1 row

        // Verify header
        assertTrue(lines.get(0).contains("Dataset"));
        assertTrue(lines.get(0).contains("SCC_Index"));
        assertTrue(lines.get(0).contains("SCC_Size"));

        // Verify data contains dataset name
        boolean foundDataset = false;
        for (String line : lines) {
            if (line.contains("test_graph")) {
                foundDataset = true;
                break;
            }
        }
        assertTrue(foundDataset, "Dataset name should appear in CSV");
    }

    @Test
    public void testTopoExport() throws IOException {
        String testFile = TEST_RESULTS_DIR + "test_topo.csv";

        // Create a simple DAG
        Graph g = new Graph(3, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);

        KahnTopoSort topo = new KahnTopoSort(g);
        List<Integer> order = topo.topologicalSort();
        Metrics metrics = topo.getMetrics();

        // Export
        TopoExporter.exportTopoOrder(testFile, "test_dag", order, order, metrics);

        // Verify
        assertTrue(new File(testFile).exists());
        List<String> lines = Files.readAllLines(Paths.get(testFile));
        assertTrue(lines.size() >= 2); // Header + data

        // Verify header
        assertTrue(lines.get(0).contains("Component_Order"));
        assertTrue(lines.get(0).contains("Task_Order"));
        assertTrue(lines.get(0).contains("Time_ms"));
    }

    @Test
    public void testDAGShortestPathExport() throws IOException {
        String testFile = TEST_RESULTS_DIR + "test_shortest.csv";

        // Create a simple DAG
        Graph g = new Graph(3, true, "edge");
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, 3);

        DAGShortestPath sp = new DAGShortestPath(g);
        sp.computeShortestPaths(0);
        int[] distances = sp.getDistances();
        List<Integer> path = sp.getPath(2);
        Metrics metrics = sp.getMetrics();

        // Export
        DAGExporter.exportShortestPaths(testFile, "test_dag", 0, distances, path, 2, metrics);

        // Verify
        assertTrue(new File(testFile).exists());
        List<String> lines = Files.readAllLines(Paths.get(testFile));
        assertTrue(lines.size() >= 2);

        // Verify header
        assertTrue(lines.get(0).contains("Source"));
        assertTrue(lines.get(0).contains("Distance"));
        assertTrue(lines.get(0).contains("Relaxations"));
    }

    @Test
    public void testDAGLongestPathExport() throws IOException {
        String testFile = TEST_RESULTS_DIR + "test_longest.csv";

        // Create a simple DAG
        Graph g = new Graph(3, true, "edge");
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, 3);

        DAGLongestPath lp = new DAGLongestPath(g);
        lp.computeLongestPath();
        DAGLongestPath.CriticalPathResult critical = lp.getCriticalPath();
        Metrics metrics = lp.getMetrics();

        // Export
        DAGExporter.exportLongestPath(testFile, "test_dag", critical, metrics);

        // Verify
        assertTrue(new File(testFile).exists());
        List<String> lines = Files.readAllLines(Paths.get(testFile));
        assertTrue(lines.size() >= 2);

        // Verify header and data
        assertTrue(lines.get(0).contains("Critical_Path"));
        assertTrue(lines.get(0).contains("Length"));
        assertTrue(lines.get(1).contains("test_dag"));
    }

    @Test
    public void testSummaryCollector() throws IOException {
        String testFile = TEST_RESULTS_DIR + "test_summary.csv";

        SummaryCollector summary = new SummaryCollector();

        // Add test data
        summary.addRow("dataset1", 10, 15, 3, 1.5, 0.8, 0.5, 0.6, 20, 30);
        summary.addRow("dataset2", 20, 30, 5, 2.5, 1.2, 0.9, 1.1, 40, 60);

        // Export
        summary.exportSummary(testFile);

        // Verify
        assertTrue(new File(testFile).exists());
        List<String> lines = Files.readAllLines(Paths.get(testFile));
        assertEquals(3, lines.size()); // Header + 2 rows

        // Verify header
        assertTrue(lines.get(0).contains("Dataset"));
        assertTrue(lines.get(0).contains("Vertices"));
        assertTrue(lines.get(0).contains("SCC_Time_ms"));

        // Verify data
        assertTrue(lines.get(1).contains("dataset1"));
        assertTrue(lines.get(2).contains("dataset2"));
    }

    @Test
    public void testCSVAppend() throws IOException {
        String testFile = TEST_RESULTS_DIR + "test_append.csv";

        // Write initial content
        List<String> headers = Arrays.asList("A", "B");
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList(1, 2)
        );
        CSVWriter.writeCSV(testFile, headers, rows);

        // Append a row
        CSVWriter.appendToCSV(testFile, Arrays.asList(3, 4));

        // Verify
        List<String> lines = Files.readAllLines(Paths.get(testFile));
        assertEquals(3, lines.size()); // Header + 2 rows
        assertTrue(lines.get(2).contains("3"));
    }

    @Test
    public void testEmptyDataExport() throws IOException {
        String testFile = TEST_RESULTS_DIR + "test_empty.csv";

        List<String> headers = Arrays.asList("Col1", "Col2");
        List<List<Object>> rows = new ArrayList<>();

        CSVWriter.writeCSV(testFile, headers, rows);

        // Should still create file with header
        assertTrue(new File(testFile).exists());
        List<String> lines = Files.readAllLines(Paths.get(testFile));
        assertEquals(1, lines.size()); // Only header
    }

    @Test
    public void testNullValueHandling() throws IOException {
        String testFile = TEST_RESULTS_DIR + "test_null.csv";

        List<String> headers = Arrays.asList("A", "B", "C");
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList(1, null, "text")
        );

        CSVWriter.writeCSV(testFile, headers, rows);

        assertTrue(new File(testFile).exists());
        List<String> lines = Files.readAllLines(Paths.get(testFile));

        // Null should be converted to empty string
        String dataLine = lines.get(1);
        assertTrue(dataLine.matches("1,,text"));
    }

    @Test
    public void testLargeDataExport() throws IOException {
        String testFile = TEST_RESULTS_DIR + "test_large.csv";

        List<String> headers = Arrays.asList("Index", "Value");
        List<List<Object>> rows = new ArrayList<>();

        // Add 1000 rows
        for (int i = 0; i < 1000; i++) {
            rows.add(Arrays.asList(i, "Value_" + i));
        }

        CSVWriter.writeCSV(testFile, headers, rows);

        // Verify all rows written
        List<String> lines = Files.readAllLines(Paths.get(testFile));
        assertEquals(1001, lines.size()); // Header + 1000 rows
    }

    @Test
    public void testResultsDirectoryCreation() {
        String newDir = TEST_RESULTS_DIR + "subdir/";
        CSVWriter.ensureResultsDirectory(newDir);

        assertTrue(new File(newDir).exists());
        assertTrue(new File(newDir).isDirectory());
    }
}