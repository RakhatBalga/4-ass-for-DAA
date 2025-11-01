# Assignment 4: Graph Algorithms (SCC, Topological Sort, DAG SP/LP)

## Project Overview

This project implements three fundamental graph algorithms as part of a "Smart City/Smart Campus Scheduling" scenario:

1. **Strongly Connected Components (SCC)** using Tarjan's algorithm
2. **Topological Sorting** using Kahn's algorithm
3. **Shortest and Longest Paths in DAGs** using dynamic programming

## Project Structure

```
Assignment4/
│
├── README.md                       # This file
├── pom.xml                         # Maven build configuration
│
├── /data/                          # Input graph datasets (JSON format)
│   ├── tasks (1) (1).json          # Provided example dataset
│   └── [additional datasets]       # Generated test datasets
│
├── /results/                       # Output CSV files (auto-generated)
│   ├── scc_results.csv
│   ├── topo_order.csv
│   ├── dag_shortest.csv
│   ├── dag_longest.csv
│   └── summary.csv                 # Summary metrics for all datasets
│
└── /src/
    ├── /main/java/graph/
    │   ├── Main.java                       # Application entry point
    │   │
    │   ├── /scc/
    │   │   ├── TarjanSCC.java              # Tarjan's SCC algorithm
    │   │   ├── CondensationGraph.java      # DAG of SCC components
    │   │   └── SCCExporter.java            # Export SCC results to CSV
    │   │
    │   ├── /topo/
    │   │   ├── KahnTopoSort.java           # Kahn's topological sort
    │   │   └── TopoExporter.java           # Export topo order to CSV
    │   │
    │   ├── /dagsp/
    │   │   ├── DAGShortestPath.java        # Single-source shortest paths
    │   │   ├── DAGLongestPath.java         # Longest path (critical path)
    │   │   └── DAGExporter.java            # Export SP/LP results to CSV
    │   │
    │   └── /util/
    │       ├── Graph.java                  # Core graph data structure
    │       ├── Edge.java                   # Edge representation
    │       ├── JsonLoader.java             # Load graphs from JSON
    │       ├── Metrics.java                # Performance instrumentation
    │       ├── CSVWriter.java              # Universal CSV writer
    │       └── SummaryCollector.java       # Aggregate all results
    │
    └── /test/java/graph/
        ├── SCCUnitTest.java                # SCC algorithm tests
        ├── TopoSortUnitTest.java           # Topological sort tests
        └── DAGSPUnitTest.java              # DAG SP/LP tests
```

## Requirements

- **Java**: JDK 11 or higher
- **Build Tool**: Maven 3.6+
- **Dependencies**:
    - Gson 2.10.1 (for JSON parsing)
    - JUnit 5.9.3 (for testing)

## Building the Project

### Clone and Build

```bash
# Clone the repository
git clone <repository-url>
cd Assignment4

# Build with Maven
mvn clean compile

# Run tests
mvn test

# Package into JAR
mvn package
```

This creates:
- `target/assignment4-1.0-SNAPSHOT.jar` - Regular JAR
- `target/assignment4-1.0-SNAPSHOT-with-dependencies.jar` - Fat JAR with all dependencies

## Running the Application

### Option 1: Using Maven

```bash
mvn exec:java -Dexec.mainClass="graph.Main"
```

### Option 2: Using the JAR

```bash
java -jar target/assignment4-1.0-SNAPSHOT-with-dependencies.jar
```

### Option 3: From IDE

Run the `graph.Main` class directly from your IDE (IntelliJ IDEA, Eclipse, etc.)

## Input Format

The program reads graph datasets from the `/data/` directory in JSON format:

```json
{
  "directed": true,
  "n": 8,
  "edges": [
    {"u": 0, "v": 1, "w": 3},
    {"u": 1, "v": 2, "w": 2}
  ],
  "source": 4,
  "weight_model": "edge"
}
```

**Fields:**
- `directed`: Whether the graph is directed (typically `true` for this assignment)
- `n`: Number of vertices
- `edges`: Array of edges with source (`u`), destination (`v`), and weight (`w`)
- `source`: Source vertex for shortest path computation (optional, default 0)
- `weight_model`: Either `"edge"` (use edge weights) or `"node"` (use node durations)

## Output

The program generates CSV files in the `/results/` directory:

### 1. `<dataset>_scc.csv`
Contains strongly connected components:
- Dataset name
- SCC index and size
- Vertices in each SCC
- Performance metrics (time, DFS visits, edges explored)

### 2. `<dataset>_topo.csv`
Contains topological ordering:
- Component order (order of SCCs)
- Task order (derived order of original vertices)
- Performance metrics (queue operations, in-degree updates)

### 3. `<dataset>_shortest.csv`
Contains shortest path results:
- Source and destination
- Distance and path
- Performance metrics (relaxations)

### 4. `<dataset>_longest.csv`
Contains longest path (critical path):
- Critical path vertices
- Path length
- Performance metrics

### 5. `summary.csv`
Aggregate metrics for all datasets:
- Dataset characteristics (vertices, edges, SCCs)
- Execution times for all algorithms
- Operation counts

## Algorithms Implemented

### 1. Tarjan's SCC Algorithm
- **Time Complexity**: O(V + E)
- **Space Complexity**: O(V)
- Uses depth-first search with discovery times and low-link values
- Identifies all strongly connected components in a single pass

### 2. Kahn's Topological Sort
- **Time Complexity**: O(V + E)
- **Space Complexity**: O(V)
- Uses in-degree tracking and queue-based processing
- Detects cycles (returns null if graph is not a DAG)

### 3. DAG Shortest Path
- **Time Complexity**: O(V + E)
- **Space Complexity**: O(V)
- Processes vertices in topological order
- Computes single-source shortest paths

### 4. DAG Longest Path
- **Time Complexity**: O(V + E)
- **Space Complexity**: O(V)
- Uses dynamic programming over topological order
- Finds the critical path (longest path in the entire DAG)

## Dataset Requirements

The assignment requires 9 datasets across three categories:

| Category | Nodes | Description | Count |
|----------|-------|-------------|-------|
| Small | 6-10 | Simple cases, 1-2 cycles or pure DAG | 3 |
| Medium | 10-20 | Mixed structures, several SCCs | 3 |
| Large | 20-50 | Performance and timing tests | 3 |

**Requirements:**
- Different density levels (sparse vs dense)
- Both cyclic and acyclic examples
- At least one graph with multiple SCCs

## Performance Instrumentation

The `Metrics` class tracks:
- **Execution time**: Measured using `System.nanoTime()`
- **Operation counters**:
    - SCC: DFS visits, edges explored, stack pops
    - Topological Sort: Queue pushes/pops, in-degree updates
    - DAG SP/LP: Edge relaxations, successful relaxations

## Testing

Run the test suite with:

```bash
mvn test
```

**Test Coverage:**
- `SCCUnitTest.java`: Tests for SCC detection, condensation graph
- `TopoSortUnitTest.java`: Tests for topological sorting, cycle detection
- `DAGSPUnitTest.java`: Tests for shortest/longest paths, path reconstruction

**Test Cases Include:**
- Single vertex graphs
- Linear DAGs
- Graphs with cycles
- Disconnected components
- Complex dependency structures
- Edge cases (empty graphs, self-loops)

## Example Usage

```java
// Load graph from JSON
JsonLoader.GraphData data = JsonLoader.loadGraph("data/tasks.json");
Graph graph = data.getGraph();

// Find SCCs
TarjanSCC tarjan = new TarjanSCC(graph);
List<List<Integer>> sccs = tarjan.findSCCs();

// Build condensation DAG
CondensationGraph condensation = new CondensationGraph(graph, sccs);
Graph dag = condensation.getCondensation();

// Topological sort
KahnTopoSort topo = new KahnTopoSort(dag);
List<Integer> order = topo.topologicalSort();

// Shortest paths
DAGShortestPath sp = new DAGShortestPath(dag);
sp.computeShortestPaths(0);
int distance = sp.getDistance(5);
List<Integer> path = sp.getPath(5);

// Longest path (critical path)
DAGLongestPath lp = new DAGLongestPath(dag);
lp.computeLongestPath();
DAGLongestPath.CriticalPathResult critical = lp.getCriticalPath();
```

## Analysis and Results

The program automatically generates:

1. **Per-dataset metrics**: Individual CSV files with detailed results
2. **Summary statistics**: Aggregated metrics across all datasets
3. **Console output**: Real-time progress and results

**Key Analysis Points:**
- Effect of graph density on algorithm performance
- Impact of SCC sizes on condensation efficiency
- Bottlenecks in each algorithm (DFS vs relaxation operations)
- Scalability with increasing graph size

## Code Quality

- **Packages**: Organized into logical modules (scc, topo, dagsp, util)
- **Documentation**: Javadoc comments on all public classes and methods
- **Code style**: Clear variable names, consistent formatting
- **Modularity**: Separation of concerns, reusable components
- **Error handling**: Graceful handling of invalid inputs![Screenshot 2025-11-02 at 20.15.18.png](Screenshot%202025-11-02%20at%2020.15.18.png)
![Screenshot 2025-10-26 at 18.26.43.png](Screenshot%202025-10-26%20at%2018.26.43.png)
![Screenshot 2025-10-26 at 18.26.43.png](Screenshot%202025-10-26%20at%2018.26.43.png)