package org.generator.topo.graph;

import org.generator.topo.edge.RelationEdge;
import org.generator.topo.node.TopoNode;
import org.generator.topo.node.phy.Intf;
import org.generator.topo.node.phy.PhyNode;
import org.generator.topo.node.phy.Router;
import org.generator.topo.node.phy.Switch;
import org.generator.util.graph.AbstractGraph;
import org.generator.util.graph.Edge;

import java.util.Collection;
import java.util.Set;

public class RelationGraph{
    AbstractGraph<TopoNode, RelationEdge>rlg;
    public void addPhyNode(PhyNode node){
        assert !rlg.hasNode(node);
        rlg.addNode(node);
    }

    public boolean hasPhyNode(PhyNode node){
        return rlg.hasNode(node);
    }

    public void addIntfToPhyNode(Intf intf, PhyNode node){
        assert rlg.hasNode(node);
        assert !rlg.hasNode(intf);
        rlg.addEdge(new RelationEdge(intf, node));
        rlg.addEdge(new RelationEdge(node, intf));
    }

//    public Set<Intf> getIntfFromPhyNode(PhyNode node){
//        assert rlg.hasNode(node);
//        //return rlg.get
//    }
}
