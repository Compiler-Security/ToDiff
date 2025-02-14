package org.generator.lib.reducer.pass;

import org.generator.lib.frontend.lexical.OpType_isis;
import org.generator.lib.item.IR.Op_ISIS;
import org.generator.lib.item.IR.OpPhy_ISIS;
import org.generator.lib.item.conf.graph.ConfGraph_ISIS;
import org.generator.lib.item.conf.node.NodeGen_ISIS;
import org.generator.lib.item.conf.node.isis.ISIS;
import org.generator.lib.item.conf.node.phy.Intf_ISIS;
import org.generator.lib.item.conf.edge.RelationEdge_ISIS;
import org.generator.lib.item.conf.node.NodeType_ISIS;
import org.generator.util.exception.Unimplemented;
import org.generator.util.exec.ExecStat;

public class phyExecArgPass_ISIS extends  baseExecPass_ISIS{
    ExecStat execOp(Op_ISIS op, ConfGraph_ISIS topo) {
        switch (op.Type()) {
            case NODEADD -> {
                switch (NodeGen_ISIS.getPhyNodeTypeByName(op.getNAME())) {
                    case NodeType_ISIS.Router -> {
                        return topo.addNode(NodeGen_ISIS.new_Router(op.getNAME()));
                    }
                    case NodeType_ISIS.Switch -> {
                        return topo.addNode(NodeGen_ISIS.new_Switch(op.getNAME()));
                    }
                    default -> {
                        new Unimplemented();
                    }
                }
            }
            case NODEDEL -> {
                var node = topo.getNode(op.getNAME());

                //check node present
                if (node.isEmpty()) {
                    return ExecStat.MISS;
                } else {
                    return topo.delNode(node.get());
                }
            }
            case NODESETISISUP -> {
                var r_name = op.getNAME();
                var ospf_name = NodeGen_ISIS.getISISName(r_name);

                //check miss condition
                if (!topo.containsNode(r_name) || topo.containsNode(ospf_name)) return ExecStat.MISS;

                //new ospf
                ISIS ospf = NodeGen_ISIS.new_ISIS(ospf_name);
                ospf.setStatus(ISIS.ISIS_STATUS.UP);
                topo.addNode(ospf);

                // new relation edge
                return topo.addISISRelation(ospf_name, r_name);
            }
            case NODESETISISRE -> {
                var r_name = op.getNAME();
                var ospf_name = NodeGen_ISIS.getISISName(r_name);
                //check condition
                if (!topo.containsNode(ospf_name)) return ExecStat.MISS;

                //change ospf status
                var ospf = (ISIS) topo.getNode(ospf_name).get();
                ospf.setStatus(ISIS.ISIS_STATUS.UP);

                return ExecStat.SUCC;
            }
            case NODESETISISSHUTDOWN -> {
                var r_name = op.getNAME();
                var ospf_name = NodeGen_ISIS.getISISName(r_name);
                //check condition
                if (!topo.containsNode(ospf_name)) return ExecStat.MISS;


                //delete ospf
                var ospf = (ISIS) topo.getNode(ospf_name).get();
                topo.delNode(ospf);

                return ExecStat.SUCC;
            }
            case INTFUP -> {
                var intf_name = op.getNAME();

                //check condition
                if (!topo.containsNode(intf_name)) return ExecStat.MISS;
                var intf = (Intf_ISIS) topo.getNode(intf_name).get();

                //set intf up
                intf.setUp(true);
                return ExecStat.SUCC;
            }
            case INTFDOWN -> {
                var intf_name = op.getNAME();

                //check condition
                if (!topo.containsNode(intf_name)) return ExecStat.MISS;
                var intf = (Intf_ISIS) topo.getNode(intf_name).get();

                //set intf down
                intf.setUp(false);
                return ExecStat.SUCC;
            }
            case LINKADD -> {
                var intf1_name = op.getNAME();
                var intf2_name = op.getNAME2();

                //link one end should always be a switch
                var node1_name = NodeGen_ISIS.getPhyNodeNameFromIntfName(intf1_name);
                var node2_name = NodeGen_ISIS.getPhyNodeNameFromIntfName(intf2_name);
                assert NodeGen_ISIS.getPhyNodeTypeByName(node2_name) == NodeType_ISIS.Switch : String.format("link operation %s not right", op);

                //check condition
                if (!topo.containsNode(node1_name) || !topo.containsNode(node2_name)) return ExecStat.MISS;
                if (topo.containsEdge(intf1_name, intf2_name, RelationEdge_ISIS.EdgeType.LINK) && topo.containsEdge(intf2_name, intf1_name, RelationEdge_ISIS.EdgeType.LINK))
                    return ExecStat.MISS;
                if (topo.containsNode(intf1_name) || topo.containsNode(intf2_name)) return ExecStat.MISS;

                Intf_ISIS intf1 = NodeGen_ISIS.new_Intf(intf1_name);
                Intf_ISIS intf2 = NodeGen_ISIS.new_Intf(intf2_name);
                topo.addNode(intf1);
                topo.addNode(intf2);
                topo.addEdge(node1_name, intf1_name, RelationEdge_ISIS.EdgeType.INTF);
                topo.addEdge(intf1_name, node1_name, RelationEdge_ISIS.EdgeType.PhyNODE);
                topo.addEdge(node2_name, intf2_name, RelationEdge_ISIS.EdgeType.INTF);
                topo.addEdge(intf2_name, node2_name, RelationEdge_ISIS.EdgeType.PhyNODE);

                //add edges in both directions
                topo.addEdge(intf1_name, intf2_name, RelationEdge_ISIS.EdgeType.LINK);
                topo.addEdge(intf2_name, intf1_name, RelationEdge_ISIS.EdgeType.LINK);
                return ExecStat.SUCC;
            }
            case LINKDOWN -> {
                //link down is equal to intf1 down && intf2 down
                var intf1_name = op.getNAME();
                var intf2_name = op.getNAME2();

                //check condition
                if (!topo.containsNode(intf1_name) || !topo.containsNode(intf2_name)) return ExecStat.MISS;


//                var op1 = new OpPhy(OpType.INTFDOWN);
//                op1.setNAME(op.getNAME());
//                var op2 = new OpPhy(OpType.INTFDOWN);
//                op2.setNAME(op.getNAME());
//                execOp(op1, topo);
//                execOp(op2, topo);
                return ExecStat.SUCC;
            }
            case LINKREMOVE -> {
                var intf1_name = op.getNAME();
                var intf2_name = op.getNAME2();

                //check condition
                if (!topo.containsEdge(intf1_name, intf2_name, RelationEdge_ISIS.EdgeType.LINK) || !topo.containsEdge(intf2_name, intf1_name, RelationEdge_ISIS.EdgeType.LINK))
                    return ExecStat.MISS;

                //delete edges in both directions
                topo.delEdge(intf1_name, intf2_name, RelationEdge_ISIS.EdgeType.LINK);
                topo.delEdge(intf2_name, intf1_name, RelationEdge_ISIS.EdgeType.LINK);
                return ExecStat.SUCC;
            }
            default -> {
                assert false : "type error";
            }
        }
        return ExecStat.MISS;
    }
}