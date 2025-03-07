package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.item.opg.OpArgG;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.openfabric.FABRIC;
import org.generator.lib.item.conf.node.openfabric.FABRICDaemon;
import org.generator.lib.item.conf.node.openfabric.FABRICIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.frontend.lexical.OpType;
import org.generator.util.collections.Pair;
import org.generator.util.net.ID;
import org.generator.util.net.IPRange;

import java.util.*;

public class openfabricArgPass {

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
                    else opg_new.setTyp(OpArgG.OpGType.FABRIC);
                }
            h.get(ctx_op).addOp(op);
        }
        return new Pair<>(h.values().stream().filter(x -> x.getTyp() == OpArgG.OpGType.Intf).toList(), h.values().stream().filter(x -> x.getTyp() == OpArgG.OpGType.FABRIC).findFirst().orElse(null));
    }



    public static void solve(OpAG opg, ConfGraph topo, String r_name) {
        var opgs = splitOpAG(opg);
        var intf_opgs = opgs.first();
        var openfabric_opg = opgs.second();

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
            }
        }

        // add openfabric Intf
        for(var intf_opg: intf_opgs){
                var intf_name = intf_opg.getCtxOp().getNAME();
                var intf = topo.getIntf(intf_name);
                //add openfabric Intf
                //if (!intf.isPersudo() && intf.getIp() != null){
                if (!intf.isPersudo()){
                    var res = topo.<FABRICIntf>getOrCreateNode(NodeGen.getOpenFabricIntfName(intf_name), NodeType.FABRICIntf);
                    assert  !res.second();
                    topo.addOpenFabricIntfRelation(res.first().getName(), intf_name);
                }
            }




        //router openfabric
        //add openfabric && openfabric daemon
        if (openfabric_opg != null){
            var openfabric_name = NodeGen.getOpenFabricName(r_name);
            var openfabric = topo.<FABRIC>getOrCreateNode(openfabric_name, NodeType.FABRIC);
            assert !openfabric.second();
            topo.addOpenFabricRelation(openfabric_name, r_name);
            var openfabric_daemon_name = NodeGen.getOpenFabricDaemonName(openfabric_name);
            var openfabric_daemon = topo.<FABRICDaemon>getOrCreateNode(openfabric_daemon_name, NodeType.FABRICDaemon);
            assert !openfabric.second();
            topo.addOpenFabricDaemonRelation(openfabric_name, openfabric_daemon_name);

            //
        }

        //openfabric intf commands
        //execute other intfs ops
        for (var intf_opg: intf_opgs){
            var exec = new openfabricIntfExecPass();
            var cur_intf_name = intf_opg.getCtxOp().getNAME();
            var cur_openfabric_intf_name = NodeGen.getOpenFabricIntfName(cur_intf_name);
            if (topo.containsNode(cur_openfabric_intf_name)){
                exec.setCur_openfabric_intf(topo.getOpenFabricIntf(cur_openfabric_intf_name));
            }
            exec.execOps(intf_opg, topo);
        }

        //openfabric other commands
        //execute other openfabric ops
        if (openfabric_opg != null){
            var exec = new openfabricDaemonExecPass();
            exec.setCur_router(topo.getNodeNotNull(r_name));
            var openfabric_name = NodeGen.getOpenFabricName(r_name);
            if (topo.containsNode(openfabric_name)) {
                exec.setCur_openfabric(topo.getNodeNotNull(openfabric_name));
                var openfabric_daemon_name = NodeGen.getOpenFabricDaemonName(openfabric_name);
                exec.setCur_openfabric_daemon(topo.getNodeNotNull(openfabric_daemon_name));
            }
            exec.execOps(openfabric_opg, topo);
        }

        //remove openfabric interface if openfabric daemon not running
        if (!topo.containsOpenFabricOfRouter(r_name)){
            for(var openfabricintf : topo.getOpenFabricIntfOfRouter(r_name)){
                topo.delNode(openfabricintf);
            }
        }
    }

}
