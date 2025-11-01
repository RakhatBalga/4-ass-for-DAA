import matplotlib.pyplot as plt

# Data
edges = [50, 120, 150, 20, 35, 45, 8, 12, 25, ]
dfs_visits = [25, 35, 50, 12, 15, 18, 6, 8, 10, ]
relaxations = [8, 8, 164, 18, 12, 49, 16, 13, 38, ]

# Plot
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))

# Left plot: DFS visits
ax1.scatter(edges, dfs_visits, s=100, alpha=0.6, color='#2E86AB')
ax1.set_xlabel('Number of Edges', fontsize=12)
ax1.set_ylabel('DFS Visits', fontsize=12)
ax1.set_title('SCC Algorithm: DFS Operations', fontsize=13, fontweight='bold')
ax1.grid(True, alpha=0.3)

# Right plot: Relaxations
ax2.scatter(edges, relaxations, s=100, alpha=0.6, color='#F18F01')
ax2.set_xlabel('Number of Edges', fontsize=12)
ax2.set_ylabel('Edge Relaxations', fontsize=12)
ax2.set_title('DAG SP/LP: Relaxation Operations', fontsize=13, fontweight='bold')
ax2.grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig('plots/operations.png', dpi=300, bbox_inches='tight')
print('✓ Saved: plots/operations.png')
plt.close()
