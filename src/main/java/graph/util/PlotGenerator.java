package graph.util;

import java.io.*;
import java.util.*;

/**
 * Generates Python/Matplotlib scripts for creating analysis plots.
 * Reads summary.csv and generates visualization scripts.
 *
 * Usage: Run this after Main to generate plots from results.
 */
public class PlotGenerator {

    private static final String RESULTS_DIR = "results/";
    private static final String PLOTS_DIR = "plots/";

    public static void main(String[] args) throws IOException {
        System.out.println("=== Plot Generator ===\n");

        // Create plots directory
        new File(PLOTS_DIR).mkdirs();

        // Read summary data
        List<PlotData> data = readSummaryData();

        if (data.isEmpty()) {
            System.err.println("No data found in " + RESULTS_DIR + "summary.csv");
            System.err.println("Please run Main.java first to generate results.");
            return;
        }

        // Generate Python scripts for different plots
        generateTimeVsSizePlot(data);
        generateAlgorithmComparisonPlot(data);
        generateOperationsPlot(data);
        generateSCCDistributionPlot(data);

        // Generate master script to run all plots
        generateMasterScript();

        System.out.println("\n✓ Plot scripts generated in " + PLOTS_DIR);
        System.out.println("\nTo generate plots, run:");
        System.out.println("  python3 " + PLOTS_DIR + "generate_all_plots.py");
        System.out.println("\nOr individually:");
        System.out.println("  python3 " + PLOTS_DIR + "plot_time_vs_size.py");
        System.out.println("  python3 " + PLOTS_DIR + "plot_algorithm_comparison.py");
        System.out.println("  python3 " + PLOTS_DIR + "plot_operations.py");
        System.out.println("  python3 " + PLOTS_DIR + "plot_scc_distribution.py");
    }

    /**
     * Reads summary.csv data
     */
    private static List<PlotData> readSummaryData() throws IOException {
        List<PlotData> data = new ArrayList<>();
        String summaryFile = RESULTS_DIR + "summary.csv";

        File file = new File(summaryFile);
        if (!file.exists()) {
            return data;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine(); // Skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 10) {
                    PlotData pd = new PlotData();
                    pd.dataset = parts[0];
                    pd.vertices = Integer.parseInt(parts[1]);
                    pd.edges = Integer.parseInt(parts[2]);
                    pd.numSCCs = Integer.parseInt(parts[3]);
                    pd.sccTime = Double.parseDouble(parts[4]);
                    pd.topoTime = Double.parseDouble(parts[5]);
                    pd.shortestTime = Double.parseDouble(parts[6]);
                    pd.longestTime = Double.parseDouble(parts[7]);
                    pd.dfsVisits = Long.parseLong(parts[8]);
                    pd.relaxations = Long.parseLong(parts[9]);
                    data.add(pd);
                }
            }
        }

        return data;
    }

    /**
     * Plot 1: Execution time vs graph size
     */
    private static void generateTimeVsSizePlot(List<PlotData> data) throws IOException {
        StringBuilder script = new StringBuilder();
        script.append("import matplotlib.pyplot as plt\n");
        script.append("import numpy as np\n\n");

        script.append("# Data\n");
        script.append("vertices = [");
        for (PlotData pd : data) script.append(pd.vertices).append(", ");
        script.append("]\n");

        script.append("scc_time = [");
        for (PlotData pd : data) script.append(pd.sccTime).append(", ");
        script.append("]\n");

        script.append("topo_time = [");
        for (PlotData pd : data) script.append(pd.topoTime).append(", ");
        script.append("]\n");

        script.append("shortest_time = [");
        for (PlotData pd : data) script.append(pd.shortestTime).append(", ");
        script.append("]\n");

        script.append("longest_time = [");
        for (PlotData pd : data) script.append(pd.longestTime).append(", ");
        script.append("]\n\n");

        script.append("# Plot\n");
        script.append("plt.figure(figsize=(10, 6))\n");
        script.append("plt.scatter(vertices, scc_time, label='SCC (Tarjan)', marker='o', s=100, alpha=0.7)\n");
        script.append("plt.scatter(vertices, topo_time, label='Topo Sort (Kahn)', marker='s', s=100, alpha=0.7)\n");
        script.append("plt.scatter(vertices, shortest_time, label='Shortest Path', marker='^', s=100, alpha=0.7)\n");
        script.append("plt.scatter(vertices, longest_time, label='Longest Path', marker='v', s=100, alpha=0.7)\n");
        script.append("plt.xlabel('Number of Vertices', fontsize=12)\n");
        script.append("plt.ylabel('Execution Time (ms)', fontsize=12)\n");
        script.append("plt.title('Algorithm Performance vs Graph Size', fontsize=14, fontweight='bold')\n");
        script.append("plt.legend(loc='upper left')\n");
        script.append("plt.grid(True, alpha=0.3)\n");
        script.append("plt.tight_layout()\n");
        script.append("plt.savefig('plots/time_vs_size.png', dpi=300, bbox_inches='tight')\n");
        script.append("print('✓ Saved: plots/time_vs_size.png')\n");
        script.append("plt.close()\n");

        writeScript(PLOTS_DIR + "plot_time_vs_size.py", script.toString());
    }

    /**
     * Plot 2: Algorithm comparison (stacked bar chart)
     */
    private static void generateAlgorithmComparisonPlot(List<PlotData> data) throws IOException {
        StringBuilder script = new StringBuilder();
        script.append("import matplotlib.pyplot as plt\n");
        script.append("import numpy as np\n\n");

        script.append("# Data\n");
        script.append("datasets = [");
        for (PlotData pd : data) script.append("'").append(pd.dataset).append("', ");
        script.append("]\n");

        script.append("scc_time = np.array([");
        for (PlotData pd : data) script.append(pd.sccTime).append(", ");
        script.append("])\n");

        script.append("topo_time = np.array([");
        for (PlotData pd : data) script.append(pd.topoTime).append(", ");
        script.append("])\n");

        script.append("sp_time = np.array([");
        for (PlotData pd : data) script.append(pd.shortestTime + pd.longestTime).append(", ");
        script.append("])\n\n");

        script.append("# Plot\n");
        script.append("fig, ax = plt.subplots(figsize=(12, 6))\n");
        script.append("x = np.arange(len(datasets))\n");
        script.append("width = 0.6\n\n");

        script.append("p1 = ax.bar(x, scc_time, width, label='SCC', color='#2E86AB')\n");
        script.append("p2 = ax.bar(x, topo_time, width, bottom=scc_time, label='Topo Sort', color='#A23B72')\n");
        script.append("p3 = ax.bar(x, sp_time, width, bottom=scc_time+topo_time, label='DAG SP/LP', color='#F18F01')\n\n");

        script.append("ax.set_ylabel('Time (ms)', fontsize=12)\n");
        script.append("ax.set_title('Algorithm Execution Time Breakdown by Dataset', fontsize=14, fontweight='bold')\n");
        script.append("ax.set_xticks(x)\n");
        script.append("ax.set_xticklabels(datasets, rotation=45, ha='right')\n");
        script.append("ax.legend()\n");
        script.append("ax.grid(axis='y', alpha=0.3)\n");
        script.append("plt.tight_layout()\n");
        script.append("plt.savefig('plots/algorithm_comparison.png', dpi=300, bbox_inches='tight')\n");
        script.append("print('✓ Saved: plots/algorithm_comparison.png')\n");
        script.append("plt.close()\n");

        writeScript(PLOTS_DIR + "plot_algorithm_comparison.py", script.toString());
    }

    /**
     * Plot 3: Operations count analysis
     */
    private static void generateOperationsPlot(List<PlotData> data) throws IOException {
        StringBuilder script = new StringBuilder();
        script.append("import matplotlib.pyplot as plt\n\n");

        script.append("# Data\n");
        script.append("edges = [");
        for (PlotData pd : data) script.append(pd.edges).append(", ");
        script.append("]\n");

        script.append("dfs_visits = [");
        for (PlotData pd : data) script.append(pd.dfsVisits).append(", ");
        script.append("]\n");

        script.append("relaxations = [");
        for (PlotData pd : data) script.append(pd.relaxations).append(", ");
        script.append("]\n\n");

        script.append("# Plot\n");
        script.append("fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))\n\n");

        script.append("# Left plot: DFS visits\n");
        script.append("ax1.scatter(edges, dfs_visits, s=100, alpha=0.6, color='#2E86AB')\n");
        script.append("ax1.set_xlabel('Number of Edges', fontsize=12)\n");
        script.append("ax1.set_ylabel('DFS Visits', fontsize=12)\n");
        script.append("ax1.set_title('SCC Algorithm: DFS Operations', fontsize=13, fontweight='bold')\n");
        script.append("ax1.grid(True, alpha=0.3)\n\n");

        script.append("# Right plot: Relaxations\n");
        script.append("ax2.scatter(edges, relaxations, s=100, alpha=0.6, color='#F18F01')\n");
        script.append("ax2.set_xlabel('Number of Edges', fontsize=12)\n");
        script.append("ax2.set_ylabel('Edge Relaxations', fontsize=12)\n");
        script.append("ax2.set_title('DAG SP/LP: Relaxation Operations', fontsize=13, fontweight='bold')\n");
        script.append("ax2.grid(True, alpha=0.3)\n\n");

        script.append("plt.tight_layout()\n");
        script.append("plt.savefig('plots/operations.png', dpi=300, bbox_inches='tight')\n");
        script.append("print('✓ Saved: plots/operations.png')\n");
        script.append("plt.close()\n");

        writeScript(PLOTS_DIR + "plot_operations.py", script.toString());
    }

    /**
     * Plot 4: SCC distribution
     */
    private static void generateSCCDistributionPlot(List<PlotData> data) throws IOException {
        StringBuilder script = new StringBuilder();
        script.append("import matplotlib.pyplot as plt\n\n");

        script.append("# Data\n");
        script.append("datasets = [");
        for (PlotData pd : data) script.append("'").append(pd.dataset).append("', ");
        script.append("]\n");

        script.append("vertices = [");
        for (PlotData pd : data) script.append(pd.vertices).append(", ");
        script.append("]\n");

        script.append("num_sccs = [");
        for (PlotData pd : data) script.append(pd.numSCCs).append(", ");
        script.append("]\n\n");

        script.append("# Calculate average SCC size\n");
        script.append("avg_scc_size = [v/s if s > 0 else 0 for v, s in zip(vertices, num_sccs)]\n\n");

        script.append("# Plot\n");
        script.append("fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))\n\n");

        script.append("# Left: Number of SCCs\n");
        script.append("bars1 = ax1.bar(range(len(datasets)), num_sccs, color='#A23B72', alpha=0.7)\n");
        script.append("ax1.set_xlabel('Dataset', fontsize=12)\n");
        script.append("ax1.set_ylabel('Number of SCCs', fontsize=12)\n");
        script.append("ax1.set_title('SCC Count by Dataset', fontsize=13, fontweight='bold')\n");
        script.append("ax1.set_xticks(range(len(datasets)))\n");
        script.append("ax1.set_xticklabels(datasets, rotation=45, ha='right')\n");
        script.append("ax1.grid(axis='y', alpha=0.3)\n\n");

        script.append("# Right: Average SCC size\n");
        script.append("bars2 = ax2.bar(range(len(datasets)), avg_scc_size, color='#2E86AB', alpha=0.7)\n");
        script.append("ax2.set_xlabel('Dataset', fontsize=12)\n");
        script.append("ax2.set_ylabel('Average SCC Size', fontsize=12)\n");
        script.append("ax2.set_title('Average SCC Size by Dataset', fontsize=13, fontweight='bold')\n");
        script.append("ax2.set_xticks(range(len(datasets)))\n");
        script.append("ax2.set_xticklabels(datasets, rotation=45, ha='right')\n");
        script.append("ax2.grid(axis='y', alpha=0.3)\n\n");

        script.append("plt.tight_layout()\n");
        script.append("plt.savefig('plots/scc_distribution.png', dpi=300, bbox_inches='tight')\n");
        script.append("print('✓ Saved: plots/scc_distribution.png')\n");
        script.append("plt.close()\n");

        writeScript(PLOTS_DIR + "plot_scc_distribution.py", script.toString());
    }

    /**
     * Generate master script to run all plots
     */
    private static void generateMasterScript() throws IOException {
        StringBuilder script = new StringBuilder();
        script.append("#!/usr/bin/env python3\n");
        script.append("import subprocess\n");
        script.append("import sys\n");
        script.append("import os\n\n");

        script.append("print('=== Generating Analysis Plots ===')\n");
        script.append("print()\n\n");

        script.append("scripts = [\n");
        script.append("    'plot_time_vs_size.py',\n");
        script.append("    'plot_algorithm_comparison.py',\n");
        script.append("    'plot_operations.py',\n");
        script.append("    'plot_scc_distribution.py'\n");
        script.append("]\n\n");

        script.append("os.chdir('plots')\n\n");

        script.append("for script in scripts:\n");
        script.append("    print(f'Running {script}...')\n");
        script.append("    result = subprocess.run([sys.executable, script])\n");
        script.append("    if result.returncode != 0:\n");
        script.append("        print(f'Error running {script}')\n");
        script.append("        sys.exit(1)\n");
        script.append("    print()\n\n");

        script.append("print('✓ All plots generated successfully!')\n");
        script.append("print('Plots saved in plots/ directory')\n");

        writeScript(PLOTS_DIR + "generate_all_plots.py", script.toString());
    }

    /**
     * Writes a script to file
     */
    private static void writeScript(String filename, String content) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.print(content);
        }
        System.out.println("Generated: " + filename);
    }

    /**
     * Container for plot data
     */
    private static class PlotData {
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
    }
}