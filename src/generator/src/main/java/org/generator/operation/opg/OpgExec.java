package org.generator.operation.opg;

import org.generator.operation.op.OpGen;
import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.generator.topo.edge.RelationEdge;
import org.generator.topo.graph.RelationGraph;
import org.generator.topo.node.NodeGen;
import org.generator.topo.node.NodeType;
import org.generator.topo.node.ospf.OSPF;
import org.generator.topo.node.ospf.OSPFArea;
import org.generator.topo.node.ospf.OSPFIntf;
import org.generator.topo.node.phy.Intf;
import org.generator.topo.node.phy.Router;
import org.generator.util.exception.Unimplemented;
import org.generator.util.exec.ExecStat;
import org.generator.util.net.IPV4;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public class OpgExec {


    public OpgExec() {
        cur_intf = Optional.empty();
        cur_ospf = Optional.empty();
        cur_ospf_intf = Optional.empty();
        cur_router = Optional.empty();
    }

    private ExecStat execPhyOp(Operation op, RelationGraph topo) {
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
                execPhyOp(op1, topo);
                execPhyOp(op2, topo);
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

    private ExecStat execOSPFRouterWithTopo(@NotNull Operation op, @NotNull RelationGraph topo) {
        switch (op.Type()) {
            case ROSPF -> {
                if (cur_router.isEmpty()) {
                    return ExecStat.MISS;
                }
                var routerName = cur_router.get().getName();
                var ospfNodeName = NodeGen.getOSPFName(routerName);
                var res = topo.getOrCrateNode(ospfNodeName, NodeType.OSPF);
                cur_ospf = Optional.of((OSPF) res.first());
                if (res.second()) {
                    //FIXME if router ospf double, what should we do
                    var OSPFNode = (OSPF) res.first();
                    OSPFNode.setStatus(OSPF.OSPF_STATUS.UP);
                    return ExecStat.SUCC;
                } else {
                    return ExecStat.MISS;
                }
            }
//            case ROSPFNUM -> {}
//            case ROSPFVRF -> {}
            case RID -> {
                if (cur_ospf.isEmpty()) {
                    return ExecStat.MISS;
                }
                cur_ospf.get().setRouterId(op.getID());
                return ExecStat.SUCC;
            }
            case RABRTYPE -> {
                if (cur_ospf.isEmpty()) {
                    return ExecStat.MISS;
                }
                var typ = op.getNAME();
                for (var abr_type : OSPF.ABR_TYPE.values()) {
                    if (abr_type.match(typ)) {
                        cur_ospf.get().setAbrType(abr_type);
                        return ExecStat.SUCC;
                    }
                }
            }
            case NETAREAID -> {
                //FIXME we should to know how long the conf last
                if (cur_ospf.isEmpty() || cur_router.isEmpty()) {
                    return ExecStat.MISS;
                }
                var router = cur_router.get();
                var ospf = cur_ospf.get();
                var ip = op.getIP();
                var area = op.getID();
                for (var e : topo.getEdgesByType(router.getName(), RelationEdge.EdgeType.INTF)) {
                    var intf = (Intf) e.getDst();
                    if (ip.contains(intf.getIp())) {
                        var ospf_intf_name = NodeGen.getOSPFIntfName(intf.getName());
                        var res = topo.getOrCrateNode(ospf_intf_name, NodeType.OSPFIntf);
                        //set OSPFIntf
                        OSPFIntf ospfintf = (OSPFIntf) res.first();
                        if (!res.second()) {
                            topo.addOSPFIntfRelation(ospf_intf_name, intf.getName(), ospf.getName());
                        }
                        //set area
                        var res1 = topo.getOrCrateNode(NodeGen.getAreaName(area), NodeType.OSPFArea);
                        OSPFArea ospfarea = (OSPFArea) res1.first();
                        if (!res1.second()) {
                            topo.addOSPFAreaRelation(ospfarea.getName(), ospf_intf_name);
                        }
                    }
                }
                return ExecStat.SUCC;
            }
            case NETAREAIDNUM -> {
                //we change this to NETAREAID
                var num = op.getNUM();
                var op_new = new Operation(OpType.NETAREAIDNUM.template(), OpType.NETAREAIDNUM);
                op_new.setIP(IPV4.Of(num));
                return execOSPFRouterWithTopo(op_new, topo);
            }
        }
        return ExecStat.MISS;
    }

    private ExecStat execOSPFOp(@NotNull Operation op, RelationGraph topo) {
        if (OpType.inOSPFRouterWithTopo(op.Type())) {
            return execOSPFRouterWithTopo(op, topo);
        }
        return ExecStat.MISS;
    }

    public void ExecOpGroup(OpGroup opg, RelationGraph topo) {
        var target = opg.getTarget();
        if (target.isPresent()) {
            String target_st = target.get();
            if (topo.containsNode(target_st)){
                if (topo.getNode(target_st).get() instanceof Router r) {
                    cur_router = Optional.of(r);
                }
            }else{
                assert false:String.format("target %s not exist!", target_st);
            }

        }
        for (var op : opg.getOps()) {
            if (OpType.inPhy(op.Type())) {
                execPhyOp(op, topo);
            } else if (OpType.inOSPF(op.Type())) {
                execOSPFOp(op, topo);
            } else {
                new Unimplemented();
            }
        }
    }


    //Context
    @NotNull  Optional<OSPF> cur_ospf;

    @NotNull Optional<Intf> cur_intf;
    @NotNull Optional<OSPFIntf> cur_ospf_intf;

    @NotNull Optional<Router> cur_router;
}
