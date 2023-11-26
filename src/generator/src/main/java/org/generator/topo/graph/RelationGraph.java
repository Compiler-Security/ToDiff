package org.generator.topo.graph;

import org.generator.topo.edge.RelationEdge;
import org.generator.topo.node.AbstractNode;
import org.generator.topo.node.NodeGen;
import org.generator.topo.node.NodeType;
import org.generator.topo.node.ospf.OSPFIntf;
import org.generator.util.collections.Pair;
import org.generator.util.exec.ExecStat;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkDOT;

import java.io.IOException;
import java.io.StringWriter;
import java.util.function.BiFunction;

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

    public ExecStat addOSPFRelation(String ospf_name, String phynode_name){
        var res1 = addEdge(phynode_name, ospf_name, RelationEdge.EdgeType.OSPF);
        var res2 =  addEdge(ospf_name, phynode_name, RelationEdge.EdgeType.PhyNODE);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }
    public ExecStat addOSPFIntfRelation(String ospf_intf_name, String intf_name, String ospf_name){
        var res1 = addEdge(ospf_intf_name, intf_name, RelationEdge.EdgeType.INTF);
        var res2 = addEdge(intf_name, ospf_intf_name, RelationEdge.EdgeType.OSPFINTF);
        var res3 = addEdge(ospf_name, ospf_intf_name, RelationEdge.EdgeType.OSPFINTF);
        var res4 = addEdge(ospf_intf_name, ospf_name, RelationEdge.EdgeType.OSPF);
        assert res1.join(res2).join(res3).join(res4) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }

    public ExecStat addOSPFAreaRelation(String area_name, String intf_name){
        var res1 = addEdge(area_name, intf_name, RelationEdge.EdgeType.INTF);
        var res2 = addEdge(intf_name, area_name, RelationEdge.EdgeType.OSPFAREA);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }


    @Override
    public String toDot(boolean verbose) {
        Graph graph = new SingleGraph("RelationGraph");
        BiFunction<String, String, String> node_label_f = (String node_name, String node_attri)-> {
            if (!verbose) return node_name;
            else{
                return String.format("%s\\n[%s]", node_name, node_attri);
            }
        };
        for (var node: getNodes()){
            graph.addNode(node.getName()).setAttribute("label", node_label_f.apply(node.getName(), node.getNodeAtrriStr()));
        }

        for(var src:getNodes()){
            for(var edge: getOutEdgesOf(src)){
                var gedge = graph.addEdge(String.format("%s->%s", edge.getSrc(), edge.getDst()), edge.getSrc().toString(), edge.getDst().toString(), true);
                if (verbose){
                    gedge.setAttribute("label", edge.getType().toString());
                    gedge.setAttribute("fontsize", 8);
                }
            }
        }

        FileSinkDOT fileSinkDOT = new FileSinkDOT(true);
        StringWriter stringWriter = new StringWriter();
        try {
            fileSinkDOT.writeAll(graph, stringWriter);
        }catch (IOException e){
            e.printStackTrace();
        }
        return stringWriter.toString();
    }
}
