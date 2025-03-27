package org.generator.lib.reducer.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.item.IR.Op;
import org.generator.lib.item.IR.OpPhy;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.babel.BABEL;
import org.generator.lib.item.conf.node.ospf.OSPF;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.isis.ISIS;
import org.generator.lib.item.conf.node.openfabric.FABRIC;
import org.generator.lib.item.conf.node.rip.RIP;
import org.generator.util.exception.Unimplemented;
import org.generator.util.exec.ExecStat;

public class phyExecArgPass extends  baseExecPass{
    ExecStat execOp(Op op, ConfGraph topo) {
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
            case LINKADD -> {
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
                if (!topo.containsEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK) || !topo.containsEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK))
                    return ExecStat.MISS;

                //delete edges in both directions
                topo.delEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK);
                topo.delEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK);
                return ExecStat.SUCC;
            }
            //MULTI:
            case NODESETOSPFUP -> {
                var r_name = op.getNAME();
                var ospf_name = NodeGen.getOSPFName(r_name);

                //check miss condition
                if (!topo.containsNode(r_name) || topo.containsNode(ospf_name)) return ExecStat.MISS;

                //new ospf
                OSPF ospf = NodeGen.new_OSPF(ospf_name);
                ospf.setStatus(OSPF.OSPF_STATUS.UP);
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
                ospf.setStatus(OSPF.OSPF_STATUS.UP);

                return ExecStat.SUCC;
            }
            case NODESETOSPFSHUTDOWN -> {
                var r_name = op.getNAME();
                var ospf_name = NodeGen.getOSPFName(r_name);
                //check condition
                if (!topo.containsNode(ospf_name)) return ExecStat.MISS;


                //delete ospf
                var ospf = (OSPF) topo.getNode(ospf_name).get();
                topo.delNode(ospf);

                return ExecStat.SUCC;
            }
            case NODESETRIPUP -> {
                var r_name = op.getNAME();
                var rip_name = NodeGen.getRIPName(r_name);

                //check miss condition
                if (!topo.containsNode(r_name) || topo.containsNode(rip_name)) return ExecStat.MISS;

                //new ospf
                RIP rip = NodeGen.new_RIP(rip_name);
                rip.setStatus(RIP.RIP_STATUS.UP);
                topo.addNode(rip);

                // new relation edge
                return topo.addRIPRelation(rip_name, r_name);
            }
            case NODESETRIPRE -> {
                var r_name = op.getNAME();
                var rip_name = NodeGen.getRIPName(r_name);
                //check condition
                if (!topo.containsNode(rip_name)) return ExecStat.MISS;

                //change ospf status
                var rip = (RIP) topo.getNode(rip_name).get();
                rip.setStatus(RIP.RIP_STATUS.UP);

                return ExecStat.SUCC;
            }
            case NODESETRIPSHUTDOWN -> {
                var r_name = op.getNAME();
                var rip_name = NodeGen.getRIPName(r_name);
                //check condition
                if (!topo.containsNode(rip_name)) return ExecStat.MISS;


                //delete ospf
                var rip = (RIP) topo.getNode(rip_name).get();
                topo.delNode(rip);

                return ExecStat.SUCC;
            }

            //------------ISIS----------------
            case NODESETISISUP -> {
                var r_name = op.getNAME();
                var isis_name = NodeGen.getISISName(r_name);

                //check miss condition
                if (!topo.containsNode(r_name) || topo.containsNode(isis_name)) return ExecStat.MISS;

                //new isis
                ISIS isis = NodeGen.new_ISIS(isis_name);
                isis.setStatus(ISIS.ISIS_STATUS.UP);
                topo.addNode(isis);

                // new relation edge
                return topo.addISISRelation(isis_name, r_name);
            }
            case NODESETISISRE -> {
                var r_name = op.getNAME();
                var isis_name = NodeGen.getISISName(r_name);
                //check condition
                if (!topo.containsNode(isis_name)) return ExecStat.MISS;

                //change isis status
                var isis = (ISIS) topo.getNode(isis_name).get();
                isis.setStatus(ISIS.ISIS_STATUS.UP);

                return ExecStat.SUCC;
            }
            case NODESETISISSHUTDOWN -> {
                var r_name = op.getNAME();
                var isis_name = NodeGen.getISISName(r_name);
                //check condition
                if (!topo.containsNode(isis_name)) return ExecStat.MISS;


                //delete isis
                var isis = (ISIS) topo.getNode(isis_name).get();
                topo.delNode(isis);

                return ExecStat.SUCC;
            }
            case NODESETBABELUP -> {
                var r_name = op.getNAME();
                var babel_name = NodeGen.getBABELName(r_name);

                //check miss condition
                if (!topo.containsNode(r_name) || topo.containsNode(babel_name)) return ExecStat.MISS;

                //new ospf
                BABEL babel = NodeGen.new_BABEL(babel_name);
                topo.addNode(babel);

                // new relation edge
                return topo.addBABELRelation(babel_name, r_name);
            }
            case NODESETBABELRE -> {
                var r_name = op.getNAME();
                var babel_name = NodeGen.getBABELName(r_name);
                //check condition
                if (!topo.containsNode(babel_name)) return ExecStat.MISS;

                //change ospf status
                var babel = (BABEL) topo.getNode(babel_name).get();

                return ExecStat.SUCC;
            }
            case NODESETBABELSHUTDOWN -> {
                var r_name = op.getNAME();
                var babel_name = NodeGen.getBABELName(r_name);
                //check condition
                if (!topo.containsNode(babel_name)) return ExecStat.MISS;


                //delete ospf
                var babel = (BABEL) topo.getNode(babel_name).get();
                topo.delNode(babel);

                return ExecStat.SUCC;
            }

            //------------OpenFabric----------------
            case NODESETFABRICUP -> {
                var r_name = op.getNAME();
                var opennfabric_name = NodeGen.getOpenFabricName(r_name);

                //check miss condition
                if (!topo.containsNode(r_name) || topo.containsNode(opennfabric_name)) return ExecStat.MISS;

                //new opennfabric
                FABRIC opennfabric = NodeGen.new_OpenFabric(opennfabric_name);
                opennfabric.setStatus(FABRIC.FABRIC_STATUS.UP);
                topo.addNode(opennfabric);

                // new relation edge
                return topo.addOpenFabricRelation(opennfabric_name, r_name);
            }
            case NODESETFABRICRE -> {
                var r_name = op.getNAME();
                var opennfabric_name = NodeGen.getOpenFabricName(r_name);
                //check condition
                if (!topo.containsNode(opennfabric_name)) return ExecStat.MISS;

                //change opennfabric status
                var opennfabric = (FABRIC) topo.getNode(opennfabric_name).get();
                opennfabric.setStatus(FABRIC.FABRIC_STATUS.UP);

                return ExecStat.SUCC;
            }
            case NODESETFABRICSHUTDOWN -> {
                var r_name = op.getNAME();
                var opennfabric_name = NodeGen.getOpenFabricName(r_name);
                //check condition
                if (!topo.containsNode(opennfabric_name)) return ExecStat.MISS;


                //delete opennfabric
                var opennfabric = (FABRIC) topo.getNode(opennfabric_name).get();
                topo.delNode(opennfabric);

                return ExecStat.SUCC;
            }

            default -> {
                assert false : "type error";
            }
        }
        return ExecStat.MISS;
    }
}