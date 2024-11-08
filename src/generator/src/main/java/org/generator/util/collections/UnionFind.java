package org.generator.util.collections;

import java.util.ArrayList;
import java.util.List;

public class UnionFind {
    private int[] parent; // parent[i]表示元素i的父节点
    private int[] rank;   // rank[i]表示以i为根的集合所表示的树的高度

    int n;
    // 构造函数，n为集合中元素的数量
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        this.n = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i; // 初始时，每个元素的父节点是其自身
            rank[i] = 0;   // 初始时，每棵树的高度都是0
        }
    }

    // 查找元素x所在的集合的代表，同时进行路径压缩
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // 路径压缩
        }
        return parent[x];
    }

    // 合并元素x和元素y所在的集合
    public void union(int x, int y) {
        int xRoot = find(x);
        int yRoot = find(y);
        if (xRoot != yRoot) {
            if (rank[xRoot] < rank[yRoot]) {
                parent[xRoot] = yRoot;
            } else if (rank[xRoot] > rank[yRoot]) {
                parent[yRoot] = xRoot;
            } else {
                parent[yRoot] = xRoot;
                rank[xRoot]++; // 如果两棵树高度相同，则合并后的树高度+1
            }
        }
    }

    // 检查元素x和元素y是否属于同一集合
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    public List<Integer> getComponents(){
        List<Integer> res = new ArrayList<>();
        for(int i = 0; i < n; i++) if (find(i) == i) res.add(i);
        return res;
    }
}
