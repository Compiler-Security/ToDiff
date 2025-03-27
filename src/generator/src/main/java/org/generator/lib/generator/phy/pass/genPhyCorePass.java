package org.generator.lib.generator.phy.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.IR.Op;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpPhy;
import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.opg.OpCtxG;

public class genPhyCorePass {
    public static OpCtxG solve(ConfGraph g){
        var opctxg = OpCtxG.Of();
        var nodes = g.<AbstractNode>getNodesByType(NodeType.Router);
        nodes.addAll(g.<AbstractNode>getNodesByType(NodeType.Switch));
        for(var r: nodes){
            var op = new OpPhy(OpType.NODEADD);
            op.setNAME(r.getName());
            opctxg.addOp(OpCtx.of(op));
        }
        var intfs = g.<Intf> getNodesByType(NodeType.Intf);
        for(var intf: intfs){
            var target_intf = g.<Intf>getDstsByType(intf.getName(), RelationEdge.EdgeType.LINK).stream().findAny().get();
            if (intf.getName().compareTo(target_intf.getName()) < 0){
                var op = new OpPhy(OpType.LINKADD);
                op.setNAME(intf.getName());
                op.setNAME2(target_intf.getName());
                opctxg.addOp(OpCtx.of(op));
                var op1 = new OpPhy(OpType.INTFUP);
                op1.setNAME(intf.getName());
                opctxg.addOp(OpCtx.of(op1));
            }
        }
        for(var r: nodes){
            if (r.getNodeType() != NodeType.Router) continue;
            OpPhy op = null;
            //MULTI:
            switch (generate.protocol){
                case OSPF -> {
                    if (g.containsNode(NodeGen.getOSPFName(r.getName()))){
                        op = new OpPhy(OpType.NODESETOSPFUP);
                    }
                }
                case RIP -> {
                    if (g.containsNode(NodeGen.getRIPName(r.getName()))){
                        op = new OpPhy(OpType.NODESETRIPUP);
                    }
                }
                case ISIS -> {
                    if (g.containsNode(NodeGen.getISISName(r.getName()))){
                        op = new OpPhy(OpType.NODESETISISUP);
                    }
                }
                case BABEL -> {
                    if (g.containsNode(NodeGen.getBABELName(r.getName()))){
                        op = new OpPhy(OpType.NODESETBABELUP);
                    }
                }
                case OpenFabric -> {
                    if (g.containsNode(NodeGen.getOpenFabricName(r.getName()))){
                        op = new OpPhy(OpType.NODESETFABRICUP);
                    }
                }
            }
            if (op != null) {
                op.setNAME(r.getName());
                opctxg.addOp(OpCtx.of(op));
            }
        }
        return opctxg;
    }
}
