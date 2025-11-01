import graph.dagsp.DAGShortestPath;
import graph.dagsp.DAGLongestPath;
import graph.dagsp.DAGLongestPath.CriticalPathResult;
import graph.util.Graph;
import graph.util.Metrics;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class DAGSPUnitTest {

    @Test
    public void testShortestPathInDAG() {
        // Create a simple DAG: 0 -> 1 (5), 0 -> 2 (3), 1 -> 3 (6), 2 -> 3 (2)
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 6);
        graph.addEdge(2, 3, 2);

        Metrics metrics = new Metrics();
        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);

        DAGShortestPath sp = new DAGShortestPath(graph, metrics);
        int[] distances = sp.shortestPathsFromSource(0, topoOrder);

        // Verify distances
        assertEquals(0, distances[0], "Distance to source should be 0");
        assertEquals(5, distances[1], "Distance to node 1 should be 5");
        assertEquals(3, distances[2], "Distance to node 2 should be 3");
        assertEquals(5, distances[3], "Distance to node 3 should be 5 (0->2->3)"); // 3 + 2 = 5

        // Test path reconstruction
        List<Integer> path = sp.reconstructPath(distances, 0, 3, topoOrder);
        assertEquals(Arrays.asList(0, 2, 3), path, "Shortest path should be 0->2->3");

        assertTrue(metrics.relaxations > 0, "Should have performed relaxations");
    }

    @Test
    public void testLongestPathInDAG() {
        // Create a DAG: 0 -> 1 (1), 0 -> 2 (2), 1 -> 3 (3), 2 -> 3 (1)
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 3);
        graph.addEdge(2, 3, 1);

        Metrics metrics = new Metrics();
        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);

        DAGLongestPath lp = new DAGLongestPath(graph, metrics);
        CriticalPathResult result = lp.findCriticalPath(topoOrder);

        // Verify critical path
        assertEquals(4, result.length, "Longest path length should be 4 (0->1->3: 1+3=4)");
        assertEquals(Arrays.asList(0, 1, 3), result.path, "Critical path should be 0->1->3");

        assertTrue(metrics.relaxations > 0, "Should have performed relaxations");
    }

    @Test
    public void testMultiplePaths() {
        // More complex DAG with multiple paths
        Graph graph = new Graph(6, true);
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 3);
        graph.addEdge(2, 3, 4);
        graph.addEdge(3, 4, 2);
        graph.addEdge(3, 5, 1);
        graph.addEdge(4, 5, 3);

        Metrics metrics = new Metrics();
        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3, 4, 5);

        // Test shortest path
        DAGShortestPath sp = new DAGShortestPath(graph, metrics);
        int[] shortestDistances = sp.shortestPathsFromSource(0, topoOrder);

        assertEquals(0, shortestDistances[0]);
        assertEquals(2, shortestDistances[1]);
        assertEquals(1, shortestDistances[2]);
        assertEquals(5, shortestDistances[3]); // 0->2->3 (1+4=5) or 0->1->3 (2+3=5) - both 5
        assertEquals(7, shortestDistances[4]); // 0->2->3->4 (1+4+2=7)
        assertEquals(6, shortestDistances[5]); // 0->2->3->5 (1+4+1=6)

        metrics.reset();

        // Test longest path
        DAGLongestPath lp = new DAGLongestPath(graph, metrics);
        CriticalPathResult critical = lp.findCriticalPath(topoOrder);

        // Longest path: 0->1->3->4->5 (2+3+2+3=10)
        assertEquals(10, critical.length);
        assertEquals(Arrays.asList(0, 1, 3, 4, 5), critical.path);
    }

    @Test
    public void testSingleNodeGraph() {
        Graph graph = new Graph(1, true);
        Metrics metrics = new Metrics();
        List<Integer> topoOrder = Arrays.asList(0);

        DAGShortestPath sp = new DAGShortestPath(graph, metrics);
        int[] distances = sp.shortestPathsFromSource(0, topoOrder);

        assertEquals(0, distances[0]);

        DAGLongestPath lp = new DAGLongestPath(graph, metrics);
        CriticalPathResult result = lp.findCriticalPath(topoOrder);

        assertEquals(0, result.length);
        assertEquals(Arrays.asList(0), result.path);
    }

    @Test
    public void testUnreachableNodes() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 2);
        graph.addEdge(2, 3, 1); // Nodes 2 and 3 are unreachable from 0

        Metrics metrics = new Metrics();
        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);

        DAGShortestPath sp = new DAGShortestPath(graph, metrics);
        int[] distances = sp.shortestPathsFromSource(0, topoOrder);

        assertEquals(0, distances[0]);
        assertEquals(2, distances[1]);
        assertEquals(Integer.MAX_VALUE, distances[2], "Unreachable node should have MAX_VALUE");
        assertEquals(Integer.MAX_VALUE, distances[3], "Unreachable node should have MAX_VALUE");

        // Test path reconstruction for unreachable node
        List<Integer> path = sp.reconstructPath(distances, 0, 3, topoOrder);
        assertTrue(path.isEmpty(), "Path to unreachable node should be empty");
    }

    @Test
    public void testNegativeWeights() {
        // DAGs can handle negative weights (unlike Dijkstra)
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, -2);
        graph.addEdge(0, 2, 5);
        graph.addEdge(1, 3, 3);
        graph.addEdge(2, 3, 1);

        Metrics metrics = new Metrics();
        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);

        DAGShortestPath sp = new DAGShortestPath(graph, metrics);
        int[] distances = sp.shortestPathsFromSource(0, topoOrder);

        assertEquals(0, distances[0]);
        assertEquals(-2, distances[1]);
        assertEquals(5, distances[2]);
        assertEquals(1, distances[3]); // 0->1->3: -2+3=1 is better than 0->2->3: 5+1=6
    }
}