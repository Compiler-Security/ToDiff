package org.generator.topo.graph;

import org.generator.topo.edge.RelationEdge;
import org.generator.topo.node.AbstractNode;
import org.generator.topo.node.NodeGen;
import org.generator.topo.node.NodeType;
import org.generator.topo.node.ospf.OSPFIntf;
import org.generator.util.collections.Pair;
import org.generator.util.exec.ExecStat;

public class RelationGraph extends AbstractRelationGraph {
    private Pair<AbstractNode, Boolean> createNode(AbstractNode node){
        var res = addNode(node);
        assert res == ExecStat.SUCC;
        return new Pair<>(node, false);
    }
    public Pair<AbstractNode, Boolean> getOrCrateNode(String node_name, NodeType node_type){
        if (containsNode(node_name)){
            return new Pair<>(getNode(node_name).get(), true);
        }else{
            return createNode(NodeGen.new_node(node_name, node_type));
        }
    }


    public OSPFIntf getOSPFIntf(String nodeName) {
        return (OSPFIntf) getNode(nodeName).get();
    }

    public void addOSPFRelation(String ospf_name, String phynode_name){
        addEdge(phynode_name, ospf_name, RelationEdge.EdgeType.OSPF);
        addEdge(ospf_name, phynode_name, RelationEdge.EdgeType.PhyNODE);
    }
    public void addOSPFIntfRelation(String ospf_intf_name, String intf_name, String ospf_name){
        addEdge(ospf_intf_name, intf_name, RelationEdge.EdgeType.INTF);
        addEdge(intf_name, ospf_intf_name, RelationEdge.EdgeType.OSPFINTF);
        addEdge(ospf_name, ospf_intf_name, RelationEdge.EdgeType.OSPFINTF);
        addEdge(ospf_intf_name, ospf_name, RelationEdge.EdgeType.OSPF);
    }

    public void addOSPFAreaRelation(String area_name, String intf_name){
        addEdge(area_name, intf_name, RelationEdge.EdgeType.INTF);
        addEdge(intf_name, area_name, RelationEdge.EdgeType.OSPFAREA);
    }



}
