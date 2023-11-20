package org.generator.topo.graph;

import org.generator.topo.Topo;
import org.generator.topo.edge.RelationEdge;
import org.generator.topo.node.TopoNode;
import org.generator.topo.node.TopoNodeGen;
import org.generator.topo.node.TopoNodeType;
import org.generator.topo.node.ospf.OSPF;
import org.generator.topo.node.phy.Host;
import org.generator.util.collections.Pair;
import org.generator.util.exception.Unimplemented;
import org.generator.util.exec.ExecStat;
import org.jetbrains.annotations.NotNull;

public class TopoGen {
    private  static Pair<TopoNode, Boolean> addNode(TopoNode node, Topo topo){
        assert  topo.addNode(node) == ExecStat.SUCC: "Add node fail";
        return new Pair(node, false);
    }
    public static Pair<TopoNode, Boolean> getOrCrateNode(String node_name, TopoNodeType node_type, Topo topo){
        if (topo.containsNode(node_name)){
            return new Pair(topo.getNode(node_name).get(), true);
        }else{
            switch (node_type){
                case Router -> {
                    return addNode(TopoNodeGen.new_Router(node_name), topo);
                }
                case Switch -> {
                    return addNode(TopoNodeGen.new_Switch(node_name), topo);
                }
                case Intf -> {
                    return addNode(TopoNodeGen.new_Intf(node_name), topo);
                }
                case OSPF -> {
                    return addNode(TopoNodeGen.new_OSPF(node_name), topo);
                }
                case OSPFIntf -> {
                    return addNode(TopoNodeGen.new_OSPF_Intf(node_name), topo);
                }
                default -> {new Unimplemented();}
            }
        }
        return null;
    }

    public static void addOSPFIntfRelation(String ospf_intf_name, String intf_name, String ospf_name, Topo topo){
        topo.addEdge(ospf_intf_name, intf_name, RelationEdge.EdgeType.OSPFINTF);
        topo.addEdge(intf_name, ospf_intf_name, RelationEdge.EdgeType.INTF);
        topo.addEdge(ospf_name, ospf_intf_name, RelationEdge.EdgeType.OSPFINTF);
        topo.addEdge(ospf_intf_name, ospf_name, RelationEdge.EdgeType.OSPF);
    }

    public static void addOSPFAreaRelation(String area_name, String intf_name, @NotNull Topo topo){
        topo.addEdge(area_name, intf_name, RelationEdge.EdgeType.OSPFAREA);
        topo.addEdge(intf_name, area_name, RelationEdge.EdgeType.OSPFINTF);
    }
}
