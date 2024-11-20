package org.generator.lib.item.conf.graph;

import org.generator.lib.item.conf.edge.RelationEdge_ISIS;
import org.generator.lib.item.conf.node.AbstractNode_ISIS;
import org.generator.util.exec.ExecStat;

import java.util.List;
import java.util.Optional;

public interface RelationGraphIntf_ISIS {
    ExecStat addNode(AbstractNode_ISIS node);
    ExecStat delNode(AbstractNode_ISIS node);
    Optional<AbstractNode_ISIS> getNode(String name);

    ExecStat addEdge(String src_name, String dst_name, RelationEdge_ISIS.EdgeType etyp);
    default boolean containsNode(String name){
        return getNode(name).isPresent();
    }

    List<RelationEdge_ISIS> getEdgesByType(String src_name, RelationEdge_ISIS.EdgeType etyp);
    Optional<RelationEdge_ISIS> getEdge(String src_name, String dst_name, RelationEdge_ISIS.EdgeType etyp);
    ExecStat delEdge(String src_name, String dst_name, RelationEdge_ISIS.EdgeType etyp);
    default boolean containsEdge(String src_name, String dst_name, RelationEdge_ISIS.EdgeType etyp){ return getEdge(src_name, dst_name, etyp).isPresent();}

    String toDot(boolean verbose);
}
