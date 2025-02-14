package org.generator.lib.generator.phy.pass;

import org.generator.lib.frontend.lexical.OpType_isis;
import org.generator.lib.item.IR.OpCtx_ISIS;
import org.generator.lib.item.IR.OpPhy_ISIS;
import org.generator.lib.item.conf.edge.RelationEdge_ISIS;
import org.generator.lib.item.conf.graph.ConfGraph_ISIS;
import org.generator.lib.item.conf.node.AbstractNode_ISIS;
import org.generator.lib.item.conf.node.NodeType_ISIS;
import org.generator.lib.item.conf.node.phy.Intf_ISIS;
import org.generator.lib.item.opg.OpCtxG_ISIS;

public class genPhyCorePass_ISIS {
    public static OpCtxG_ISIS solve(ConfGraph_ISIS g){
        var opctxg = OpCtxG_ISIS.Of();
        var nodes = g.<AbstractNode_ISIS>getNodesByType(NodeType_ISIS.Router);
        nodes.addAll(g.<AbstractNode_ISIS>getNodesByType(NodeType_ISIS.Switch));
        for(var r: nodes){
            var op = new OpPhy_ISIS(OpType_isis.NODEADD);
            op.setNAME(r.getName());
            opctxg.addOp(OpCtx_ISIS.of(op));
            var op1 = new OpPhy_ISIS(OpType_isis.NODESETISISUP);
        }
        var intfs = g.<Intf_ISIS> getNodesByType(NodeType_ISIS.Intf);
        for(var intf: intfs){
            var target_intf = g.<Intf_ISIS>getDstsByType(intf.getName(), RelationEdge_ISIS.EdgeType.LINK).stream().findAny().get();
            if (intf.getName().compareTo(target_intf.getName()) < 0){
                var op = new OpPhy_ISIS(OpType_isis.LINKADD);
                op.setNAME(intf.getName());
                op.setNAME2(target_intf.getName());
                opctxg.addOp(OpCtx_ISIS.of(op));
                var op1 = new OpPhy_ISIS(OpType_isis.INTFUP);
                op1.setNAME(intf.getName());
                opctxg.addOp(OpCtx_ISIS.of(op1));
            }
        }
        for(var r: nodes){
            if (r.getNodeType() != NodeType_ISIS.Router) continue;
            var op = new OpPhy_ISIS(OpType_isis.NODESETISISUP);
            op.setNAME(r.getName());
            opctxg.addOp(OpCtx_ISIS.of(op));
        }
        return opctxg;
    }
}
