package org.generator.lib.operation.opgexec;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.item.topo.node.NodeGen;
import org.generator.lib.item.topo.node.ospf.OSPF;
import org.generator.lib.item.topo.node.phy.Intf;
import org.generator.lib.operation.operation.OpGen;
import org.generator.lib.operation.operation.Op;
import org.generator.lib.item.topo.edge.RelationEdge;
import org.generator.lib.item.topo.graph.RelationGraph;
import org.generator.lib.item.topo.node.NodeType;
import org.generator.util.exception.Unimplemented;
import org.generator.util.exec.ExecStat;

public class PhyOpgExec extends OpgExec {
    ExecStat execOp(Op op, RelationGraph topo) {
        switch (op.Type()) {
            case NODEADD -> {
                switch (NodeGen.getPhyNodeTypeByName(op.getNAME())) {
                    case NodeType.Router -> {
                        return topo.addNode(NodeGen.new_Router(op.getNAME()));
                    }
                    case NodeType.Switch -> {
                        return topo.addNode(NodeGen.new_Switch(op.getNAME()));
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
            case NODESETOSPFUP -> {
                var r_name = op.getNAME();
                var ospf_name = NodeGen.getOSPFName(r_name);

                //check miss condition
                if (!topo.containsNode(r_name) || topo.containsNode(ospf_name)) return ExecStat.MISS;

                //new ospf
                OSPF ospf = NodeGen.new_OSPF(ospf_name);
                ospf.setStatus(OSPF.OSPF_STATUS.INIT);
                topo.addNode(ospf);

                // new relation edge
                return topo.addOSPFRelation(ospf_name, r_name);
            }
            case NODESETOSPFRE -> {
                var r_name = op.getNAME();
                var ospf_name = NodeGen.getOSPFName(r_name);
                //check condition
                if (!topo.containsNode(ospf_name)) return ExecStat.MISS;

                //change ospf status
                var ospf = (OSPF) topo.getNode(ospf_name).get();
                ospf.setStatus(OSPF.OSPF_STATUS.Restart);

                return ExecStat.SUCC;
            }
            case INTFUP -> {
                var intf_name = op.getNAME();

                //check condition
                if (!topo.containsNode(intf_name)) return ExecStat.MISS;
                var intf = (Intf) topo.getNode(intf_name).get();

                //set intf up
                intf.setUp(true);
                return ExecStat.SUCC;
            }
            case INTFDOWN -> {
                var intf_name = op.getNAME();

                //check condition
                if (!topo.containsNode(intf_name)) return ExecStat.MISS;
                var intf = (Intf) topo.getNode(intf_name).get();

                //set intf down
                intf.setUp(false);
                return ExecStat.SUCC;
            }
            case LINKUP -> {
                var intf1_name = op.getNAME();
                var intf2_name = op.getNAME2();

                //link one end should always be a switch
                var node1_name = NodeGen.getPhyNodeNameFromIntfName(intf1_name);
                var node2_name = NodeGen.getPhyNodeNameFromIntfName(intf2_name);
                assert NodeGen.getPhyNodeTypeByName(node2_name) == NodeType.Switch : String.format("link operation %s not right", op);

                //check condition
                if (!topo.containsNode(node1_name) || !topo.containsNode(node2_name)) return ExecStat.MISS;
                if (topo.containsEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK) && topo.containsEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK))
                    return ExecStat.MISS;
                if (topo.containsNode(intf1_name) || topo.containsNode(intf2_name)) return ExecStat.MISS;

                Intf intf1 = NodeGen.new_Intf(intf1_name);
                Intf intf2 = NodeGen.new_Intf(intf2_name);
                topo.addNode(intf1);
                topo.addNode(intf2);
                topo.addEdge(node1_name, intf1_name, RelationEdge.EdgeType.INTF);
                topo.addEdge(intf1_name, node1_name, RelationEdge.EdgeType.PhyNODE);
                topo.addEdge(node2_name, intf2_name, RelationEdge.EdgeType.INTF);
                topo.addEdge(intf2_name, node2_name, RelationEdge.EdgeType.PhyNODE);

                //add edges in both directions
                topo.addEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK);
                topo.addEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK);
                return ExecStat.SUCC;
            }
            case LINKDOWN -> {
                //link down is equal to intf1 down && intf2 down
                var intf1_name = op.getNAME();
                var intf2_name = op.getNAME2();

                //check condition
                if (!topo.containsNode(intf1_name) || !topo.containsNode(intf2_name)) return ExecStat.MISS;


                var op1 = OpGen.GenOperation(OpType.INTFDOWN);
                op1.setNAME(op.getNAME());
                var op2 = OpGen.GenOperation(OpType.INTFDOWN);
                op2.setNAME(op.getNAME());
                execOp(op1, topo);
                execOp(op2, topo);
                return ExecStat.SUCC;
            }
            case LINKREMOVE -> {
                var intf1_name = op.getNAME();
                var intf2_name = op.getNAME2();

                //check condition
                if (!topo.containsEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK) || !topo.containsEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK))
                    return ExecStat.MISS;

                //delete edges in both directions
                topo.delEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK);
                topo.delEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK);
                return ExecStat.SUCC;
            }
            default -> {
                assert false : "type error";
            }
        }
        return ExecStat.MISS;
    }
}
