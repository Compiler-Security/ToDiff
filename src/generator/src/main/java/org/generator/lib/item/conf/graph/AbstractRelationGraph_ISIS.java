package org.generator.lib.item.conf.graph;

import org.generator.lib.item.conf.edge.RelationEdge_ISIS;
import org.generator.lib.item.conf.node.AbstractNode_ISIS;
import org.generator.util.exec.ExecStat;
import org.generator.util.graph.AbstractGraph;

import java.util.*;

//TODO AbstractGraph is non-multi-edge graph
public abstract class AbstractRelationGraph_ISIS extends AbstractGraph<AbstractNode_ISIS, RelationEdge_ISIS> implements RelationGraphIntf_ISIS {
    public AbstractRelationGraph_ISIS(){
        name_to_nodes = new HashMap<>();
    }
    @Override
    public ExecStat addNode(AbstractNode_ISIS node) {
        if(name_to_nodes.containsKey(node.getName())) return ExecStat.MISS;
        if (super.hasNode(node))
            return ExecStat.MISS;
        super.addnode(node);
        name_to_nodes.put(node.getName(), node);
        return ExecStat.SUCC;
    }

    @Override
    public ExecStat delNode(AbstractNode_ISIS node) {
        if (!super.hasNode(node)) return ExecStat.MISS;
        super.delnode(node);
        name_to_nodes.remove(node.getName());
        return ExecStat.SUCC;
    }

    @Override
    public Optional<AbstractNode_ISIS> getNode(String name) {
        if (!name_to_nodes.containsKey(name)){
            return Optional.empty();
        }else{
            return Optional.of(name_to_nodes.get(name));
        }
    }


    @Override
    public ExecStat addEdge(String src_name, String dst_name, RelationEdge_ISIS.EdgeType etyp) {
        if (containsEdge(src_name, dst_name, etyp)) return ExecStat.MISS;
        var e = new RelationEdge_ISIS(name_to_nodes.get(src_name), name_to_nodes.get(dst_name), etyp);
        super.addEdge(e);
        return ExecStat.SUCC;
    }

    @Override
    public List<RelationEdge_ISIS> getEdgesByType(String src_name, RelationEdge_ISIS.EdgeType etyp) {
        if (getNode(src_name).isEmpty()){
            return new ArrayList<>();
        }
        var l = new ArrayList<RelationEdge_ISIS>();
        for (var edge: getOutEdgesOf(getNode(src_name).get())){
            if (edge.getType().equals(etyp)){
                l.add(edge);
            }
        }
        return l;
    }

    @Override
    public Optional<RelationEdge_ISIS> getEdge(String src_name, String dst_name, RelationEdge_ISIS.EdgeType etyp) {
        if (!name_to_nodes.containsKey(src_name) || !name_to_nodes.containsKey(dst_name)) return Optional.empty();
        for(var e: super.getOutEdgesOf(name_to_nodes.get(src_name))){
            if (e.getDst().getName().equals(dst_name) && e.getType().equals(etyp)){
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public ExecStat delEdge(String src_name, String dst_name, RelationEdge_ISIS.EdgeType etyp) {
        if (!containsEdge(src_name, dst_name, etyp)) return ExecStat.MISS;
        //TODO this is not elegant
        super.delEdge(name_to_nodes.get(src_name), name_to_nodes.get(dst_name));
        return ExecStat.SUCC;
    }

    @Override
    public String toString() {
        //StringBuilder buf = new StringBuilder();
        var nodes_str = String.format("nodes: %s", getNodes().toString());
        StringBuilder edge_str = new StringBuilder();
        for(var node: getNodes()){
            for (var e: getOutEdgesOf(node)){
                edge_str.append(String.format("%s\n", e.toString()));
            }
        }
        return nodes_str + "\n" + edge_str;
    }

    private Map<String, AbstractNode_ISIS> name_to_nodes;
}
