package org.generator.topo;

import org.generator.topo.edge.RelationEdge;
import org.generator.topo.node.TopoNode;
import org.generator.topo.node.phy.PhyNode;
import org.generator.util.exec.ExecStat;

import java.util.Optional;

public interface Topo {
    ExecStat addNode(TopoNode node);
    ExecStat delNode(TopoNode node);
    Optional<TopoNode> getNode(String name);

    ExecStat addEdge(String src_name, String dst_name, RelationEdge.EdgeType etyp);
    default boolean containsNode(String name){
        return getNode(name).isPresent();
    }

    Optional<RelationEdge> getEdge(String src_name, String dst_name, RelationEdge.EdgeType etyp);
    ExecStat delEdge(String src_name, String dst_name, RelationEdge.EdgeType etyp);
    default boolean containsEdge(String src_name, String dst_name, RelationEdge.EdgeType etyp){ return getEdge(src_name, dst_name, etyp).isPresent();}
}
