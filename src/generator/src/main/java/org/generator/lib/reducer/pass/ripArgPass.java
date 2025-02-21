package org.generator.lib.reducer.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.item.IR.Op;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.ospf.OSPF;
import org.generator.lib.item.conf.node.ospf.OSPFDaemon;
import org.generator.lib.item.conf.node.ospf.OSPFIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.rip.RIP;
import org.generator.lib.item.conf.node.rip.RIPIntf;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.item.opg.OpArgG;
import org.generator.util.collections.Pair;
import org.generator.util.net.ID;
import org.generator.util.net.IPRange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ripArgPass {
    private static Pair<List<OpArgG>, OpArgG> splitOpAG(OpAG opAG) {
        var h = new HashMap<Op, OpArgG>();
        for (var opa : opAG.getOps()) {
            assert opa.getCtxOp() != null: "each active opa in opAG's normal form should have ctxOp";
            assert opa.getOp().Type().isSetOp() : "each op should be set Op";
            var ctx_op = opa.getCtxOp().getOp();
            var op = opa.getOp();
            if (!h.containsKey(ctx_op)) {
                var opg_new = new OpArgG();
                opg_new.setCtxOp(ctx_op);
                h.put(ctx_op, opg_new);
                if (ctx_op.Type() == OpType.IntfName) opg_new.setTyp(OpArgG.OpGType.Intf);
                else opg_new.setTyp(OpArgG.OpGType.RIP);
            }
            h.get(ctx_op).addOp(op);
        }
        return new Pair<>(h.values().stream().filter(x -> x.getTyp() == OpArgG.OpGType.Intf).toList(), h.values().stream().filter(x -> x.getTyp() == OpArgG.OpGType.RIP).findFirst().orElse(null));
    }

    public static void solve(OpAG opg, ConfGraph topo, String r_name) {
        var opgs = splitOpAG(opg);
        var intf_opgs = opgs.first();
        var rip_opg = opgs.second();

        //int name
        //ip address XXX.XXX.XXX.XXX
        //set intf ip || add intf
        for (var intf_opg : intf_opgs) {
            assert intf_opg.getTyp() == OpArgG.OpGType.Intf;
            //add persudo intf accroding to int r1-eth0
            var intf_name = intf_opg.getCtxOp().getNAME();
            var res = topo.<Intf>getOrCreateNode(intf_name, NodeType.Intf);
            if (!res.second()) {
                res.first().setPersudo(true);
                res.first().setUp(false);
                topo.addIntfRelation(intf_name, r_name);
            }
            //parse ip address ...
            for (var op : intf_opg.popOpsOfType(OpType.IPAddr)) {
                topo.getIntf(intf_name).setIp(op.getIP());
                topo.getIntf(intf_name).setUp(true);
            }
        }

        //router rip
        //network ...
        if (rip_opg != null){
            var rip_name = NodeGen.getRIPName(r_name);
            var rip = topo.<RIP>getOrCreateNode(rip_name, NodeType.RIP);
            assert !rip.second();
            topo.addRIPRelation(rip_name, r_name);
            for(var op: rip_opg.popOpsOfType(OpType.NETWORKN)){
                var intf_name = op.getNAME();
                if (topo.containsNode(intf_name)){
                    var res = topo.<RIPIntf>getOrCreateNode(NodeGen.getRIPIntfName(intf_name), NodeType.RIPIntf);
                    if (!res.second()) {
                        topo.addRIPIntfRelation(res.first().getName(), intf_name);
                    }
                }
            }
            for(var op: rip_opg.popOpsOfType(OpType.NETWORKI)){
                for(var intf: topo.getIntfsOfRouter(r_name)){
                    if (intf.getIp() == null) continue;
                    var intf_name = intf.getName();
                    //FIXME is this right?
                    if (op.getIPRANGE().containsId(intf.getIp().getAddressOfIp())){
                        var res = topo.<RIPIntf>getOrCreateNode(NodeGen.getRIPIntfName(intf_name), NodeType.RIPIntf);
                        if (!res.second()) {
                            topo.addRIPIntfRelation(res.first().getName(), intf_name);
                        }
                    }
                }
            }
        }

        //rip intf commands
        //execute other intfs ops
        for (var intf_opg: intf_opgs){
            var exec = new ripIntfExecPass();
            var cur_intf_name = intf_opg.getCtxOp().getNAME();
            var cur_rip_intf_name = NodeGen.getRIPIntfName(cur_intf_name);
            if (topo.containsNode(cur_rip_intf_name)){
                exec.setCur_rip_intf(topo.getRIPIntf(cur_rip_intf_name));
            }
            exec.execOps(intf_opg, topo);
        }

        //rip other commands
        //execute other ospf ops
        if (rip_opg != null){
            var exec = new ripDaemonExecPass();
            exec.setCur_router(topo.getNodeNotNull(r_name));
            var rip_name = NodeGen.getRIPName(r_name);
            if (topo.containsNode(rip_name)) {
                exec.setCur_rip(topo.getNodeNotNull(rip_name));
            }
            exec.execOps(rip_opg, topo);
        }

        //remove OSPF interface if OSPF daemon not running
        if (!topo.containsRIPOfRouter(r_name)){
            for(var ripintf : topo.getRIPIntfOfRouter(r_name)){
                topo.delNode(ripintf);
            }
        }
    }
}
