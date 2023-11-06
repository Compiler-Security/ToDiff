package org.generator.operation.opg;

import org.generator.operation.op.OpGen;
import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.generator.topo.Topo;
import org.generator.topo.edge.RelationEdge;
import org.generator.topo.node.TopoNodeGen;
import org.generator.topo.node.ospf.OSPF;
import org.generator.topo.node.phy.Intf;
import org.generator.topo.node.phy.PhyNode;
import org.generator.util.exception.Unimplemented;
import org.generator.util.exec.ExecStat;

public class OpgExec {


    static private ExecStat execPhyOp(Operation op, Topo topo){
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
                var node2_name = TopoNodeGen.getPhyNodeNameFromIntfName(intf2_name);
                assert TopoNodeGen.getPhyNodeTypeByName(node2_name) == PhyNode.NodeType.Switch : String.format("link operation %s not right", op.toString());

                //check condition
                if (topo.containsEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK) && topo.containsEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK)) return ExecStat.MISS;


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
    static void ExecOpGroup(OpGroup opg, Topo topo){
        for (var op: opg.getOps()){
            if (OpType.inPhy(op.Type())){

            }else{
                new Unimplemented();
            }
        }
    }
}
