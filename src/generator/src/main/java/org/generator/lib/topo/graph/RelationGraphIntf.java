package org.generator.lib.topo.graph;

import org.generator.lib.topo.edge.RelationEdge;
import org.generator.lib.topo.node.AbstractNode;
import org.generator.util.exec.ExecStat;

import java.util.List;
import java.util.Optional;

public interface RelationGraphIntf {
    ExecStat addNode(AbstractNode node);
    ExecStat delNode(AbstractNode node);
    Optional<AbstractNode> getNode(String name);

    ExecStat addEdge(String src_name, String dst_name, RelationEdge.EdgeType etyp);
    default boolean containsNode(String name){
        return getNode(name).isPresent();
    }

    List<RelationEdge> getEdgesByType(String src_name, RelationEdge.EdgeType etyp);
    Optional<RelationEdge> getEdge(String src_name, String dst_name, RelationEdge.EdgeType etyp);
    ExecStat delEdge(String src_name, String dst_name, RelationEdge.EdgeType etyp);
    default boolean containsEdge(String src_name, String dst_name, RelationEdge.EdgeType etyp){ return getEdge(src_name, dst_name, etyp).isPresent();}

    String toDot(boolean verbose);
}
