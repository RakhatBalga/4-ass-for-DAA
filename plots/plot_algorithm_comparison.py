import matplotlib.pyplot as plt
import numpy as np

# Data
datasets = ['tasks_large_1', 'tasks_large_2', 'tasks_large_3', 'tasks_medium_1', 'tasks_medium_2', 'tasks_medium_3', 'tasks_small_1', 'tasks_small_2', 'tasks_small_3', ]
scc_time = np.array([0.223459, 0.141833, 0.184167, 0.023875, 0.031042, 0.038167, 0.013625, 0.017666, 0.017417, ])
topo_time = np.array([0.05325, 0.01675, 0.133834, 0.019375, 0.0075, 0.024292, 0.009583, 0.010375, 0.011875, ])
sp_time = np.array([0.038125, 0.019875999999999998, 0.0745, 0.0115, 0.008042, 0.024625, 0.010792, 0.008, 0.019209, ])

# Plot
fig, ax = plt.subplots(figsize=(12, 6))
x = np.arange(len(datasets))
width = 0.6

p1 = ax.bar(x, scc_time, width, label='SCC', color='#2E86AB')
p2 = ax.bar(x, topo_time, width, bottom=scc_time, label='Topo Sort', color='#A23B72')
p3 = ax.bar(x, sp_time, width, bottom=scc_time+topo_time, label='DAG SP/LP', color='#F18F01')

ax.set_ylabel('Time (ms)', fontsize=12)
ax.set_title('Algorithm Execution Time Breakdown by Dataset', fontsize=14, fontweight='bold')
ax.set_xticks(x)
ax.set_xticklabels(datasets, rotation=45, ha='right')
ax.legend()
ax.grid(axis='y', alpha=0.3)
plt.tight_layout()
plt.savefig('plots/algorithm_comparison.png', dpi=300, bbox_inches='tight')
print('✓ Saved: plots/algorithm_comparison.png')
plt.close()
