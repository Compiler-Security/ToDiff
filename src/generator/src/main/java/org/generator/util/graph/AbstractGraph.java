package org.generator.util.graph;

import java.util.*;

public class AbstractGraph<N, E extends Edge<N>> implements Graph<N, E>{
    Map<N, Map<N, E>> preds;
    Map<N, Map<N, E>> nexts;

    public AbstractGraph(){
        preds = new HashMap<>();
        nexts = new HashMap<>();
    }

    @Override
    public boolean hasNode(N node) {
        return preds.containsKey(node);
    }

    @Override
    public boolean hasEdge(N source, N target) {
        var succes =  nexts.get(source);
        if (succes == null) return false;
        return succes.containsKey(target);
    }

    @Override
    public Set<N> getPredsOf(N node) {
        return preds.get(node).keySet();
    }

    @Override
    public Set<N> getSuccsOf(N node) {
        return nexts.get(node).keySet();
    }

    @Override
    public Collection<E> getInEdgesOf(N node) {
        return  preds.getOrDefault(node, new HashMap<>()).values();
    }

    @Override
    public Collection<E> getOutEdgesOf(N node) {
        return nexts.getOrDefault(node, new HashMap<>()).values();
    }

    @Override
    public Set<N> getNodes() {
        return preds.keySet();
    }

    @Override
    public boolean hasEdge(Edge<N> edge) {
        return hasEdge(edge.getSrc(), edge.getDst());
    }

    @Override
    public void addnode(N node) {
        assert !hasNode(node);
        preds.put(node, new HashMap<>());
        nexts.put(node, new HashMap<>());
    }

    @Override
    public void addEdge(E edge) {
        assert hasNode(edge.getSrc()) && hasNode(edge.getDst());
        nexts.get(edge.getSrc()).put(edge.getDst(), edge);
        preds.get(edge.getDst()).put(edge.getSrc(), edge);
    }

    @Override
    public void delnode(N node) {
        var nexs = getSuccsOf(node).stream().toList();
        for (var nex: nexs){
            delEdge(node, nex);
        }
        var pres = getPredsOf(node).stream().toList();
        for (var pre: pres){
            delEdge(pre, node);
        }
        preds.remove(node);
        nexts.remove(node);
    }

    @Override
    public void delEdge(N srcNode, N dstNode) {
        assert hasNode(srcNode) && hasNode(dstNode);
        preds.get(dstNode).remove(srcNode);
        nexts.get(srcNode).remove(dstNode);
    }
}
