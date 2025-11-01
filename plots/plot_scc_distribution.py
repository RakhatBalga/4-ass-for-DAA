import matplotlib.pyplot as plt

# Data
datasets = ['tasks_large_1', 'tasks_large_2', 'tasks_large_3', 'tasks_medium_1', 'tasks_medium_2', 'tasks_medium_3', 'tasks_small_1', 'tasks_small_2', 'tasks_small_3', ]
vertices = [25, 35, 50, 12, 15, 18, 6, 8, 10, ]
num_sccs = [5, 5, 50, 7, 5, 18, 6, 8, 10, ]

# Calculate average SCC size
avg_scc_size = [v/s if s > 0 else 0 for v, s in zip(vertices, num_sccs)]

# Plot
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))

# Left: Number of SCCs
bars1 = ax1.bar(range(len(datasets)), num_sccs, color='#A23B72', alpha=0.7)
ax1.set_xlabel('Dataset', fontsize=12)
ax1.set_ylabel('Number of SCCs', fontsize=12)
ax1.set_title('SCC Count by Dataset', fontsize=13, fontweight='bold')
ax1.set_xticks(range(len(datasets)))
ax1.set_xticklabels(datasets, rotation=45, ha='right')
ax1.grid(axis='y', alpha=0.3)

# Right: Average SCC size
bars2 = ax2.bar(range(len(datasets)), avg_scc_size, color='#2E86AB', alpha=0.7)
ax2.set_xlabel('Dataset', fontsize=12)
ax2.set_ylabel('Average SCC Size', fontsize=12)
ax2.set_title('Average SCC Size by Dataset', fontsize=13, fontweight='bold')
ax2.set_xticks(range(len(datasets)))
ax2.set_xticklabels(datasets, rotation=45, ha='right')
ax2.grid(axis='y', alpha=0.3)

plt.tight_layout()
plt.savefig('plots/scc_distribution.png', dpi=300, bbox_inches='tight')
print('✓ Saved: plots/scc_distribution.png')
plt.close()
