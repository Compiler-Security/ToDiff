package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.item.opg.OpArgG;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.isis.ISIS;
import org.generator.lib.item.conf.node.isis.ISISDaemon;
import org.generator.lib.item.conf.node.isis.ISISIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.frontend.lexical.OpType;
import org.generator.util.collections.Pair;
import org.generator.util.net.ID;
import org.generator.util.net.IPRange;

import java.util.*;

public class isisArgPass {

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
                    else opg_new.setTyp(OpArgG.OpGType.ISIS);
                }
            h.get(ctx_op).addOp(op);
        }
        return new Pair<>(h.values().stream().filter(x -> x.getTyp() == OpArgG.OpGType.Intf).toList(), h.values().stream().filter(x -> x.getTyp() == OpArgG.OpGType.ISIS).findFirst().orElse(null));
    }

    // private static boolean hasIpisisArea(OpAG opg) {
    //     boolean ip_isis_area = false;
    //     for (var opa : opg.getOps()) {
    //         if (opa.op.Type() == OpType.IpisisArea) {
    //             ip_isis_area = true;
    //             break;
    //         }
    //     }
    //     return ip_isis_area;
    // }

    public static void solve(OpAG opg, ConfGraph topo, String r_name) {
        var opgs = splitOpAG(opg);
        var intf_opgs = opgs.first();
        var isis_opg = opgs.second();

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

        // add isis Intf
        for(var intf_opg: intf_opgs){
                var intf_name = intf_opg.getCtxOp().getNAME();
                var intf = topo.getIntf(intf_name);
                //add isis Intf
                //if (!intf.isPersudo() && intf.getIp() != null){
                if (!intf.isPersudo()){
                    var res = topo.<ISISIntf>getOrCreateNode(NodeGen.getISISIntfName(intf_name), NodeType.ISISIntf);
                    assert  !res.second();
                    topo.addISISIntfRelation(res.first().getName(), intf_name);
                }
            }




        //router isis
        //add isis && isis daemon
        if (isis_opg != null){
            var isis_name = NodeGen.getISISName(r_name);
            var isis = topo.<ISIS>getOrCreateNode(isis_name, NodeType.ISIS);
            assert !isis.second();
            topo.addISISRelation(isis_name, r_name);
            var isis_daemon_name = NodeGen.getISISDaemonName(isis_name);
            var isis_daemon = topo.<ISISDaemon>getOrCreateNode(isis_daemon_name, NodeType.ISISDaemon);
            assert !isis.second();
            topo.addISISDaemonRelation(isis_name, isis_daemon_name);

            //
        }

        //isis intf commands
        //execute other intfs ops
        for (var intf_opg: intf_opgs){
            var exec = new isisIntfExecPass();
            var cur_intf_name = intf_opg.getCtxOp().getNAME();
            var cur_isis_intf_name = NodeGen.getISISIntfName(cur_intf_name);
            if (topo.containsNode(cur_isis_intf_name)){
                exec.setCur_isis_intf(topo.getISISIntf(cur_isis_intf_name));
            }
            exec.execOps(intf_opg, topo);
        }

        //isis other commands
        //execute other isis ops
        if (isis_opg != null){
            var exec = new isisDaemonExecPass();
            exec.setCur_router(topo.getNodeNotNull(r_name));
            var isis_name = NodeGen.getISISName(r_name);
            if (topo.containsNode(isis_name)) {
                exec.setCur_isis(topo.getNodeNotNull(isis_name));
                var isis_daemon_name = NodeGen.getISISDaemonName(isis_name);
                exec.setCur_isis_daemon(topo.getNodeNotNull(isis_daemon_name));
            }
            exec.execOps(isis_opg, topo);
        }

        //remove isis interface if isis daemon not running
        if (!topo.containsISISOfRouter(r_name)){
            for(var isisintf : topo.getISISIntfOfRouter(r_name)){
                topo.delNode(isisintf);
            }
        }
    }

}
