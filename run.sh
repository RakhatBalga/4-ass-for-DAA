#!/bin/bash
# ==========================================
# Assignment 4 - Smart City Graph Runner
# For macOS / Linux
# ==========================================

# Stop on error
set -e

# Print what is happening
echo "=== Building project with Maven ==="
mvn clean compile

# Input and output paths
INPUT="data/tasks (1) (1).json"
OUTPUT="results"

echo "=== Running Main.java ==="
mvn exec:java -Dexec.mainClass="graph.Main" -Dexec.args="$INPUT $OUTPUT"

echo "✅ All CSV results written to $OUTPUT/"

