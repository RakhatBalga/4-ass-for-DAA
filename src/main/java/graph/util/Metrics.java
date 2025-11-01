package graph.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks performance metrics for graph algorithms:
 * - Execution time
 * - Operation counters (DFS visits, edge relaxations, etc.)
 */
public class Metrics {
    private long startTime;
    private long endTime;
    private final Map<String, Long> counters;

    public Metrics() {
        this.counters = new HashMap<>();
    }

    /**
     * Starts timing
     */
    public void startTimer() {
        startTime = System.nanoTime();
    }

    /**
     * Stops timing
     */
    public void stopTimer() {
        endTime = System.nanoTime();
    }

    /**
     * Gets elapsed time in milliseconds
     */
    public double getElapsedTimeMs() {
        return (endTime - startTime) / 1_000_000.0;
    }

    /**
     * Gets elapsed time in nanoseconds
     */
    public long getElapsedTimeNs() {
        return endTime - startTime;
    }

    /**
     * Increments a counter by 1
     */
    public void increment(String counterName) {
        counters.put(counterName, counters.getOrDefault(counterName, 0L) + 1);
    }

    /**
     * Increments a counter by a specific amount
     */
    public void increment(String counterName, long amount) {
        counters.put(counterName, counters.getOrDefault(counterName, 0L) + amount);
    }

    /**
     * Gets the value of a counter
     */
    public long getCounter(String counterName) {
        return counters.getOrDefault(counterName, 0L);
    }

    /**
     * Resets all counters and timer
     */
    public void reset() {
        startTime = 0;
        endTime = 0;
        counters.clear();
    }

    /**
     * Returns all counter names
     */
    public Iterable<String> getCounterNames() {
        return counters.keySet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Elapsed time: ").append(String.format("%.3f ms", getElapsedTimeMs())).append("\n");
        sb.append("Counters:\n");
        for (Map.Entry<String, Long> entry : counters.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns metrics as a map for CSV export
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("time_ms", getElapsedTimeMs());
        map.put("time_ns", getElapsedTimeNs());
        map.putAll(counters);
        return map;
    }
}