package org.generator.operation.opg;

import org.generator.operation.op.OpGen;
import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.generator.topo.Topo;
import org.generator.topo.edge.RelationEdge;
import org.generator.topo.graph.TopoGen;
import org.generator.topo.node.TopoNodeGen;
import org.generator.topo.node.TopoNodeType;
import org.generator.topo.node.ospf.OSPF;
import org.generator.topo.node.ospf.OSPFArea;
import org.generator.topo.node.ospf.OSPFIntf;
import org.generator.topo.node.phy.Intf;
import org.generator.topo.node.phy.PhyNode;
import org.generator.topo.node.phy.Router;
import org.generator.util.exception.Unimplemented;
import org.generator.util.exec.ExecStat;
import org.generator.util.net.IPV4;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class OpgExec {


    public OpgExec(){
        cur_intf = Optional.empty();
        cur_ospf = Optional.empty();
        cur_ospf_intf = Optional.empty();
        cur_router = Optional.empty();
    }
    private ExecStat execPhyOp(Operation op, Topo topo){
        switch (op.Type()){
            case NODEADD -> {
                switch (TopoNodeGen.getPhyNodeTypeByName(op.getNAME())){
                    case PhyNode.NodeType.Router -> {return topo.addNode(TopoNodeGen.new_Router(op.getNAME()));}
                    case PhyNode.NodeType.Switch -> {return topo.addNode(TopoNodeGen.new_Switch(op.getNAME()));}
                    default -> {new Unimplemented();}
                }
            }
            case NODEDEL -> {
                var node = topo.getNode(op.getNAME());

                //check node present
                if (node.isEmpty()){
                    return ExecStat.MISS;
                }else{
                    return topo.delNode(node.get());
                }
            }
            case NODESETOSPFUP -> {
                var r_name = op.getNAME();
                var ospf_name = TopoNodeGen.getOSPFName(r_name);

                //check miss condition
                if (!topo.containsNode(r_name) || topo.containsNode(ospf_name)) return ExecStat.MISS;
                var router = topo.getNode(r_name).get();

                //new ospf
                OSPF ospf = TopoNodeGen.new_OSPF(ospf_name);
                ospf.setStatus(OSPF.OSPF_STATUS.UP);
                topo.addNode(ospf);

                // new relation edge
                topo.addEdge(r_name, ospf_name, RelationEdge.EdgeType.OSPF);
                return topo.addEdge(ospf_name, r_name, RelationEdge.EdgeType.PhyNODE);
            }
            case NODESETOSPFRE -> {
                var r_name = op.getNAME();
                var ospf_name = TopoNodeGen.getOSPFName(r_name);
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
                var node1_name = TopoNodeGen.getPhyNodeNameFromIntfName(intf1_name);
                var node2_name = TopoNodeGen.getPhyNodeNameFromIntfName(intf2_name);
                assert TopoNodeGen.getPhyNodeTypeByName(node2_name) == PhyNode.NodeType.Switch : String.format("link operation %s not right", op.toString());

                //check condition
                if (!topo.containsNode(node1_name) || !topo.containsNode(node2_name)) return ExecStat.MISS;
                if (topo.containsEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK) && topo.containsEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK)) return ExecStat.MISS;
                if (topo.containsNode(intf1_name) || topo.containsNode(intf2_name)) return ExecStat.MISS;

                Intf intf1 = TopoNodeGen.new_Intf(intf1_name);
                Intf intf2 = TopoNodeGen.new_Intf(intf2_name);
                topo.addNode(intf1);
                topo.addNode(intf2);
                topo.addEdge(node1_name, intf1_name, RelationEdge.EdgeType.INTF);
                topo.addEdge(intf1_name, node1_name, RelationEdge.EdgeType.PhyNODE);

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
                if (!topo.containsEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK) || !topo.containsEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK)) return ExecStat.MISS;

                //delete edges in both directions
                topo.delEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK);
                topo.delEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK);
                return ExecStat.SUCC;
            }
            default -> {assert false: "type error";}
        }
        return ExecStat.MISS;
    }

    private ExecStat execOSPFRouterWithTopo(@NotNull Operation op, @NotNull  Topo topo){
        switch (op.Type()){
            case ROSPF -> {
                if (cur_router.isEmpty()){
                    return ExecStat.MISS;
                }
                var routerName = cur_router.get().getName();
                var ospfNodeName = TopoNodeGen.getOSPFName(cur_router.get().getName());
                if (topo.containsNode(ospfNodeName)){
                    //FIXME if router ospf double, what should we do
                    cur_ospf = Optional.of((OSPF) topo.getNode(ospfNodeName).get());
                    return  ExecStat.MISS;
                }
                var OSPFNode = TopoNodeGen.new_OSPF(ospfNodeName);
                cur_ospf = Optional.of(OSPFNode);
                topo.addNode(OSPFNode);
                topo.addEdge(routerName, ospfNodeName, RelationEdge.EdgeType.OSPF);
                topo.addEdge(ospfNodeName, routerName, RelationEdge.EdgeType.PhyNODE);
                OSPFNode.setStatus(OSPF.OSPF_STATUS.UP);
            }
//            case ROSPFNUM -> {}
//            case ROSPFVRF -> {}
            case RID -> {
                if (cur_ospf.isEmpty()){
                    return ExecStat.MISS;
                }
                cur_ospf.get().setRouterId(op.getID());
                return ExecStat.SUCC;
            }
            case RABRTYPE -> {
                if (cur_ospf.isEmpty()){
                    return ExecStat.MISS;
                }
                var typ = op.getNAME();
                for (var abr_type : OSPF.ABR_TYPE.values()){
                    if (abr_type.match(typ)){
                        cur_ospf.get().setAbrType(abr_type);
                        return ExecStat.SUCC;
                    }
                }
            }
            case NETAREAID -> {
                //FIXME we should to know how long the conf last
                if (cur_ospf.isEmpty() || cur_router.isEmpty()){
                    return ExecStat.MISS;
                }
                var router = cur_router.get();
                var ospf = cur_ospf.get();
                var ip = op.getIP();
                var area = op.getID();
                for (var e: topo.getEdgesByType(router.getName(), RelationEdge.EdgeType.INTF)){
                    if (e.getDst() instanceof Intf intf){
                        if (ip.hasSubNet(intf.getIp())){
                            var ospf_intf_name = TopoNodeGen.getOSPFIntfName(intf.getName());
                            var res = TopoGen.getOrCrateNode(ospf_intf_name, TopoNodeType.OSPFIntf, topo);
                            OSPFIntf ospfintf = (OSPFIntf) res.first();
                            if (!res.second()){
                                TopoGen.addOSPFIntfRelation(ospf_intf_name, intf.getName(), ospf.getName(), topo);
                            }

                            var res1 = TopoGen.getOrCrateNode(TopoNodeGen.getAreaName(area), TopoNodeType.OSPFArea, topo);
                            OSPFArea ospfarea = (OSPFArea) res.first();
                            if (!res.second()){
                                TopoGen.addOSPFAreaRelation(ospfarea.getName(), ospf_intf_name, topo);
                            }
                            //TODO
                        }
                    }else{
                        assert false: "intf relation dst should be intf";
                    }
                }
            }
            case NETAREAIDNUM -> {
                var num = op.getNUM();
                var op_new = new Operation(OpType.NETAREAIDNUM.template(), OpType.NETAREAIDNUM);
                op_new.setIP(IPV4.Of(num));
                return execOSPFRouterWithTopo(op_new, topo);
            }
        }
        return ExecStat.MISS;
    }

    private ExecStat execOSPFOp(@NotNull Operation op, Topo topo){
        if (OpType.inOSPFRouterWithTopo(op.Type())){
            return execOSPFRouterWithTopo(op, topo);
        }
        return ExecStat.MISS;
    }

    public void ExecOpGroup(OpGroup opg, Topo topo, Optional<PhyNode> target){
        if (target.isPresent()){
            if (target.get() instanceof Router r){
                cur_router = Optional.of(r);
            }
        }
        for (var op: opg.getOps()){
            if (OpType.inPhy(op.Type())){
                execPhyOp(op, topo);
            }else if (OpType.inOSPF(op.Type())){
                execOSPFOp(op, topo);
            }else{
                new Unimplemented();
            }
        }
    }


    //Context
    Optional<OSPF> cur_ospf;

    Optional<Intf> cur_intf;
    Optional<OSPFIntf> cur_ospf_intf;

    Optional<Router> cur_router;
}
