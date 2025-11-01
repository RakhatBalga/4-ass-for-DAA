package graph;

import graph.scc.*;
import graph.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Unit tests for Strongly Connected Components (Tarjan's algorithm)
 */
public class SCCUnitTest {

    @Test
    public void testSingleVertex() {
        Graph g = new Graph(1, true, "edge");

        TarjanSCC tarjan = new TarjanSCC(g);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(1, sccs.size());
        assertEquals(1, sccs.get(0).size());
        assertTrue(sccs.get(0).contains(0));
    }

    @Test
    public void testLinearDAG() {
        // 0 -> 1 -> 2 -> 3 (no cycles)
        Graph g = new Graph(4, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 3, 1);

        TarjanSCC tarjan = new TarjanSCC(g);
        List<List<Integer>> sccs = tarjan.findSCCs();

        // Each vertex should be its own SCC
        assertEquals(4, sccs.size());
    }

    @Test
    public void testSimpleCycle() {
        // 0 -> 1 -> 2 -> 0 (one SCC)
        Graph g = new Graph(3, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        TarjanSCC tarjan = new TarjanSCC(g);
        List<List<Integer>> sccs = tarjan.findSCCs();

        // Should be one SCC with all vertices
        assertEquals(1, sccs.size());
        assertEquals(3, sccs.get(0).size());
    }

    @Test
    public void testMultipleSCCs() {
        // Two separate cycles: (0 <-> 1) and (2 <-> 3), plus edges 1->2
        Graph g = new Graph(4, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);  // Cycle 1
        g.addEdge(2, 3, 1);
        g.addEdge(3, 2, 1);  // Cycle 2
        g.addEdge(1, 2, 1);  // Connection between SCCs

        TarjanSCC tarjan = new TarjanSCC(g);
        List<List<Integer>> sccs = tarjan.findSCCs();

        // Should have 2 SCCs
        assertEquals(2, sccs.size());
    }

    @Test
    public void testProvidedExample() {
        // From tasks (1) (1).json
        Graph g = new Graph(8, true, "edge");
        g.addEdge(0, 1, 3);
        g.addEdge(1, 2, 2);
        g.addEdge(2, 3, 4);
        g.addEdge(3, 1, 1);  // Cycle: 1->2->3->1
        g.addEdge(4, 5, 2);
        g.addEdge(5, 6, 5);
        g.addEdge(6, 7, 1);

        TarjanSCC tarjan = new TarjanSCC(g);
        List<List<Integer>> sccs = tarjan.findSCCs();

        // Should have multiple SCCs, including one with {1, 2, 3}
        assertTrue(sccs.size() >= 5);

        // Check that there's an SCC containing the cycle
        boolean foundCycle = false;
        for (List<Integer> scc : sccs) {
            if (scc.size() == 3) {
                Set<Integer> sccSet = new HashSet<>(scc);
                if (sccSet.contains(1) && sccSet.contains(2) && sccSet.contains(3)) {
                    foundCycle = true;
                    break;
                }
            }
        }
        assertTrue(foundCycle, "Should find SCC with vertices {1, 2, 3}");
    }

    @Test
    public void testCondensationGraph() {
        // Graph with one cycle
        Graph g = new Graph(4, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 1, 1);  // Cycle: 1->2->1
        g.addEdge(2, 3, 1);

        TarjanSCC tarjan = new TarjanSCC(g);
        List<List<Integer>> sccs = tarjan.findSCCs();

        CondensationGraph condensation = new CondensationGraph(g, sccs);
        Graph dag = condensation.getCondensation();

        // Condensation should be a DAG
        assertTrue(condensation.isDAG());

        // Should have 3 components: {0}, {1,2}, {3}
        assertEquals(3, dag.getN());
    }

    @Test
    public void testDisconnectedGraph() {
        // Two separate components with no edges between them
        Graph g = new Graph(4, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(2, 3, 1);

        TarjanSCC tarjan = new TarjanSCC(g);
        List<List<Integer>> sccs = tarjan.findSCCs();

        // Should have 4 SCCs (each vertex is its own SCC)
        assertEquals(4, sccs.size());
    }

    @Test
    public void testSelfLoop() {
        // Vertex with self-loop
        Graph g = new Graph(2, true, "edge");
        g.addEdge(0, 0, 1);  // Self-loop
        g.addEdge(0, 1, 1);

        TarjanSCC tarjan = new TarjanSCC(g);
        List<List<Integer>> sccs = tarjan.findSCCs();

        // Self-loop makes vertex 0 an SCC by itself
        assertEquals(2, sccs.size());
    }
}