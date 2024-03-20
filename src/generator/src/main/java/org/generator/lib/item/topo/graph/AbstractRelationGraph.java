package org.generator.lib.item.topo.graph;

import org.generator.lib.item.topo.edge.RelationEdge;
import org.generator.lib.item.topo.node.AbstractNode;
import org.generator.util.exec.ExecStat;
import org.generator.util.graph.AbstractGraph;

import java.util.*;

//FIXME AbstractGraph is non-multi-edge graph
public abstract class AbstractRelationGraph extends AbstractGraph<AbstractNode, RelationEdge> implements RelationGraphIntf {
    public AbstractRelationGraph(){
        name_to_nodes = new HashMap<>();
    }
    @Override
    public ExecStat addNode(AbstractNode node) {
        if(name_to_nodes.containsKey(node.getName())) return ExecStat.MISS;
        if (super.hasNode(node))
            return ExecStat.MISS;
        super.addnode(node);
        name_to_nodes.put(node.getName(), node);
        return ExecStat.SUCC;
    }

    @Override
    public ExecStat delNode(AbstractNode node) {
        if (!super.hasNode(node)) return ExecStat.MISS;
        super.delnode(node);
        name_to_nodes.remove(node.getName());
        return ExecStat.SUCC;
    }

    @Override
    public Optional<AbstractNode> getNode(String name) {
        if (!name_to_nodes.containsKey(name)){
            return Optional.empty();
        }else{
            return Optional.of(name_to_nodes.get(name));
        }
    }


    @Override
    public ExecStat addEdge(String src_name, String dst_name, RelationEdge.EdgeType etyp) {
        if (containsEdge(src_name, dst_name, etyp)) return ExecStat.MISS;
        var e = new RelationEdge(name_to_nodes.get(src_name), name_to_nodes.get(dst_name), etyp);
        super.addEdge(e);
        return ExecStat.SUCC;
    }

    @Override
    public List<RelationEdge> getEdgesByType(String src_name, RelationEdge.EdgeType etyp) {
        if (getNode(src_name).isEmpty()){
            return new ArrayList<>();
        }
        var l = new ArrayList<RelationEdge>();
        for (var edge: getOutEdgesOf(getNode(src_name).get())){
            if (edge.getType().equals(etyp)){
                l.add(edge);
            }
        }
        return l;
    }

    @Override
    public Optional<RelationEdge> getEdge(String src_name, String dst_name, RelationEdge.EdgeType etyp) {
        if (!name_to_nodes.containsKey(src_name) || !name_to_nodes.containsKey(dst_name)) return Optional.empty();
        for(var e: super.getOutEdgesOf(name_to_nodes.get(src_name))){
            if (e.getDst().getName().equals(dst_name) && e.getType().equals(etyp)){
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public ExecStat delEdge(String src_name, String dst_name, RelationEdge.EdgeType etyp) {
        if (!containsEdge(src_name, dst_name, etyp)) return ExecStat.MISS;
        //FIXME API not 100% good
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

    private Map<String, AbstractNode> name_to_nodes;
}
