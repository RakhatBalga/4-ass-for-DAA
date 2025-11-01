package graph;

import graph.topo.*;
import graph.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Unit tests for Topological Sort (Kahn's algorithm)
 */
public class TopoSortUnitTest {

    @Test
    public void testSingleVertex() {
        Graph g = new Graph(1, true, "edge");

        KahnTopoSort topo = new KahnTopoSort(g);
        List<Integer> order = topo.topologicalSort();

        assertNotNull(order);
        assertEquals(1, order.size());
        assertEquals(0, order.get(0));
    }

    @Test
    public void testLinearDAG() {
        // 0 -> 1 -> 2 -> 3
        Graph g = new Graph(4, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 3, 1);

        KahnTopoSort topo = new KahnTopoSort(g);
        List<Integer> order = topo.topologicalSort();

        assertNotNull(order);
        assertEquals(4, order.size());

        // Verify ordering: 0 before 1, 1 before 2, 2 before 3
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(1) < order.indexOf(2));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    public void testDiamondDAG() {
        // 0 -> 1 -> 3
        //  \-> 2 ->/
        Graph g = new Graph(4, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 1);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 3, 1);

        KahnTopoSort topo = new KahnTopoSort(g);
        List<Integer> order = topo.topologicalSort();

        assertNotNull(order);
        assertEquals(4, order.size());

        // 0 must come first, 3 must come last
        assertEquals(0, order.get(0));
        assertEquals(3, order.get(3));

        // Both 1 and 2 must come before 3
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    public void testCycleDetection() {
        // Graph with cycle: 0 -> 1 -> 2 -> 0
        Graph g = new Graph(3, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        KahnTopoSort topo = new KahnTopoSort(g);
        List<Integer> order = topo.topologicalSort();

        // Should return null because of cycle
        assertNull(order);
    }

    @Test
    public void testDisconnectedDAG() {
        // Two separate chains: 0->1 and 2->3
        Graph g = new Graph(4, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(2, 3, 1);

        KahnTopoSort topo = new KahnTopoSort(g);
        List<Integer> order = topo.topologicalSort();

        assertNotNull(order);
        assertEquals(4, order.size());

        // 0 before 1, and 2 before 3
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    public void testComplexDAG() {
        // More complex DAG
        //     0
        //    /|\
        //   1 2 3
        //   |X| |
        //   4 5 6
        Graph g = new Graph(7, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 1);
        g.addEdge(0, 3, 1);
        g.addEdge(1, 4, 1);
        g.addEdge(1, 5, 1);
        g.addEdge(2, 4, 1);
        g.addEdge(2, 5, 1);
        g.addEdge(3, 6, 1);

        KahnTopoSort topo = new KahnTopoSort(g);
        List<Integer> order = topo.topologicalSort();

        assertNotNull(order);
        assertEquals(7, order.size());

        // 0 must be first
        assertEquals(0, order.get(0));

        // All dependencies must be satisfied
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(2));
        assertTrue(order.indexOf(0) < order.indexOf(3));
        assertTrue(order.indexOf(1) < order.indexOf(4));
        assertTrue(order.indexOf(2) < order.indexOf(5));
    }

    @Test
    public void testEmptyGraph() {
        Graph g = new Graph(0, true, "edge");

        KahnTopoSort topo = new KahnTopoSort(g);
        List<Integer> order = topo.topologicalSort();

        assertNotNull(order);
        assertEquals(0, order.size());
    }

    @Test
    public void testTaskOrderDerivation() {
        // Test deriving task order from SCC order
        List<List<Integer>> sccs = Arrays.asList(
                Arrays.asList(0),
                Arrays.asList(1, 2, 3),
                Arrays.asList(4),
                Arrays.asList(5)
        );

        List<Integer> sccOrder = Arrays.asList(0, 1, 2, 3);
        List<Integer> taskOrder = KahnTopoSort.deriveTaskOrder(sccOrder, sccs);

        // Should expand SCCs in order
        assertEquals(7, taskOrder.size());

        // First task should be 0
        assertEquals(0, taskOrder.get(0));

        // Tasks 1,2,3 should come next (in sorted order within SCC)
        assertTrue(taskOrder.indexOf(1) < taskOrder.indexOf(4));
        assertTrue(taskOrder.indexOf(2) < taskOrder.indexOf(4));
        assertTrue(taskOrder.indexOf(3) < taskOrder.indexOf(4));
    }
}