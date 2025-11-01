import matplotlib.pyplot as plt
import numpy as np

# Data
vertices = [25, 35, 50, 12, 15, 18, 6, 8, 10, ]
scc_time = [0.223459, 0.141833, 0.184167, 0.023875, 0.031042, 0.038167, 0.013625, 0.017666, 0.017417, ]
topo_time = [0.05325, 0.01675, 0.133834, 0.019375, 0.0075, 0.024292, 0.009583, 0.010375, 0.011875, ]
shortest_time = [0.020708, 0.007917, 0.009917, 0.00525, 0.004042, 0.004375, 0.004792, 0.002041, 0.005709, ]
longest_time = [0.017417, 0.011959, 0.064583, 0.00625, 0.004, 0.02025, 0.006, 0.005959, 0.0135, ]

# Plot
plt.figure(figsize=(10, 6))
plt.scatter(vertices, scc_time, label='SCC (Tarjan)', marker='o', s=100, alpha=0.7)
plt.scatter(vertices, topo_time, label='Topo Sort (Kahn)', marker='s', s=100, alpha=0.7)
plt.scatter(vertices, shortest_time, label='Shortest Path', marker='^', s=100, alpha=0.7)
plt.scatter(vertices, longest_time, label='Longest Path', marker='v', s=100, alpha=0.7)
plt.xlabel('Number of Vertices', fontsize=12)
plt.ylabel('Execution Time (ms)', fontsize=12)
plt.title('Algorithm Performance vs Graph Size', fontsize=14, fontweight='bold')
plt.legend(loc='upper left')
plt.grid(True, alpha=0.3)
plt.tight_layout()
plt.savefig('plots/time_vs_size.png', dpi=300, bbox_inches='tight')
print('✓ Saved: plots/time_vs_size.png')
plt.close()
