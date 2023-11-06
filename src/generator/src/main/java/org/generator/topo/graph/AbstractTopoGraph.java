package org.generator.topo.graph;

import org.generator.topo.Topo;
import org.generator.topo.edge.RelationEdge;
import org.generator.topo.node.TopoNode;
import org.generator.util.exec.ExecStat;
import org.generator.util.graph.AbstractGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//FIXME AbstractGraph is non-multi-edge graph
public class AbstractTopoGraph extends AbstractGraph<TopoNode, RelationEdge> implements Topo {
    public AbstractTopoGraph(){
        name_to_nodes = new HashMap<>();
    }
    @Override
    public ExecStat addNode(TopoNode node) {
        if (super.hasNode(node)) return ExecStat.MISS;
        super.addnode(node);
        name_to_nodes.put(node.getName(), node);
        return ExecStat.SUCC;
    }

    @Override
    public ExecStat delNode(TopoNode node) {
        if (!super.hasNode(node)) return ExecStat.MISS;
        super.delnode(node);
        name_to_nodes.remove(node.getName());
        return ExecStat.SUCC;
    }

    @Override
    public Optional<TopoNode> getNode(String name) {
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

    private Map<String, TopoNode> name_to_nodes;
}
