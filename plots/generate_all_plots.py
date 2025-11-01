#!/usr/bin/env python3
import subprocess
import sys
import os

print('=== Generating Analysis Plots ===')
print()

scripts = [
    'plot_time_vs_size.py',
    'plot_algorithm_comparison.py',
    'plot_operations.py',
    'plot_scc_distribution.py'
]

os.chdir('plots')

for script in scripts:
    print(f'Running {script}...')
    result = subprocess.run([sys.executable, script])
    if result.returncode != 0:
        print(f'Error running {script}')
        sys.exit(1)
    print()

print('✓ All plots generated successfully!')
print('Plots saved in plots/ directory')
