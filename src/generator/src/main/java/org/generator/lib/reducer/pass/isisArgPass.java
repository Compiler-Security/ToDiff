package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op_ISIS;
import org.generator.lib.item.opg.OpAG_ISIS;
import org.generator.lib.item.opg.OpArgG_ISIS;
import org.generator.lib.item.conf.graph.ConfGraph_ISIS;
import org.generator.lib.item.conf.node.NodeGen_ISIS;
import org.generator.lib.item.conf.node.NodeType_ISIS;
import org.generator.lib.item.conf.node.isis.ISIS;
import org.generator.lib.item.conf.node.isis.ISISDaemon;
import org.generator.lib.item.conf.node.isis.ISISIntf;
import org.generator.lib.item.conf.node.phy.Intf_ISIS;
import org.generator.lib.frontend.lexical.OpType_isis;
import org.generator.util.collections.Pair;
import org.generator.util.net.ID;
import org.generator.util.net.IPRange;

import java.util.*;

public class isisArgPass {

    private static Pair<List<OpArgG_ISIS>, OpArgG_ISIS> splitOpAG(OpAG_ISIS opAG) {
        var h = new HashMap<Op_ISIS, OpArgG_ISIS>();
        for (var opa : opAG.getOps()) {
            assert opa.getCtxOp() != null: "each active opa in opAG's normal form should have ctxOp";
            assert opa.getOp().Type().isSetOp() : "each op should be set Op";
            var ctx_op = opa.getCtxOp().getOp();
            var op = opa.getOp();
            if (!h.containsKey(ctx_op)) {
                    var opg_new = new OpArgG_ISIS();
                    opg_new.setCtxOp(ctx_op);
                    h.put(ctx_op, opg_new);
                    if (ctx_op.Type() == OpType_isis.IntfName) opg_new.setTyp(OpArgG_ISIS.OpGType_ISIS.Intf);
                    else opg_new.setTyp(OpArgG_ISIS.OpGType_ISIS.ISIS);
                }
            h.get(ctx_op).addOp(op);
        }
        return new Pair<>(h.values().stream().filter(x -> x.getTyp() == OpArgG_ISIS.OpGType_ISIS.Intf).toList(), h.values().stream().filter(x -> x.getTyp() == OpArgG_ISIS.OpGType_ISIS.ISIS).findFirst().orElse(null));
    }

    // private static boolean hasIpOspfArea(OpAG_ISIS opg) {
    //     boolean ip_ospf_area = false;
    //     for (var opa : opg.getOps()) {
    //         if (opa.op.Type() == OpType_isis.IpOspfArea) {
    //             ip_ospf_area = true;
    //             break;
    //         }
    //     }
    //     return ip_ospf_area;
    // }

    // public static void solve(OpAG_ISIS opg, ConfGraph_ISIS topo, String r_name) {
    //     var opgs = splitOpAG(opg);
    //     var intf_opgs = opgs.first();
    //     var ospf_opg = opgs.second();

    //     //int name
    //     //ip address XXX.XXX.XXX.XXX
    //     //set intf ip || add intf
    //     for (var intf_opg : intf_opgs) {
    //         assert intf_opg.getTyp() == OpArgG_ISIS.OpGType_ISIS.Intf;
    //         //add persudo intf accroding to int r1-eth0
    //         var intf_name = intf_opg.getCtxOp().getNAME();
    //         var res = topo.<Intf_ISIS>getOrCreateNode(intf_name, NodeType_ISIS.Intf);
    //         if (!res.second()) {
    //             res.first().setPersudo(true);
    //             res.first().setUp(false);
    //             topo.addIntfRelation(intf_name, r_name);
    //         }
    //         //parse ip address ...
    //         for (var op : intf_opg.popOpsOfType(OpType_isis.IPAddr)) {
    //             topo.getIntf(intf_name).setIp(op.getIP());
    //         }
    //     }

    //     //network XXX.XXX.XXX.XXX area XXX
    //     //ip ospf area XXX
    //     //set area & add ospfintf
    //     boolean is_ip_ospf_area = hasIpOspfArea(opg);
    //     if (is_ip_ospf_area) {
    //         for(var intf_opg: intf_opgs){
    //             //only the first ip opsf area work
    //             for(var op: intf_opg.popOpsOfType(OpType.IpOspfArea)){
    //                 var intf_name = intf_opg.getCtxOp().getNAME();
    //                 var intf = topo.getIntf(intf_name);
    //                 //add ospf Intf
    //                 if (!intf.isPersudo() && intf.getIp() != null){
    //                     var res = topo.<ISISIntf>getOrCreateNode(NodeGen_ISIS.getISISIntfName(intf_name), NodeType_ISIS.ISISIntf);
    //                     assert  !res.second();
    //                     res.first().setArea(op.getID());
    //                     topo.addISISIntfRelation(res.first().getName(), intf_name);
    //                 }
    //                 break;
    //             }
    //         }
    //     }else if (ospf_opg != null){
    //         HashMap<IPRange, ID> netToArea = new HashMap<>();
    //         for (var op: ospf_opg.popOpsOfType(OpType_isis.NETAREAID)){
    //             if (!netToArea.containsKey(op.getIPRANGE())){
    //                 netToArea.put(op.getIPRANGE(), op.getID());
    //             }
    //         }
    //         for (var intf: topo.getIntfsOfRouter(r_name)){
    //             if (intf.getIp() == null) continue;
    //             int mask_len = -1;
    //             Map.Entry<IPRange, ID> mxEntry = null;
    //             //find the most small subnetwork
    //             for (var entry: netToArea.entrySet()){
    //                 if (entry.getKey().containsIp(intf.getIp())){
    //                     if (entry.getKey().getMaskOfIp() > mask_len) {
    //                         mask_len = entry.getKey().getMaskOfIp();
    //                         mxEntry = entry;
    //                     }
    //                 }
    //             }
    //             if (mxEntry != null){
    //                 //add ospf Intf
    //                 var intf_name = intf.getName();
    //                 if (!intf.isPersudo()){
    //                     var res = topo.<ISISIntf>getOrCreateNode(NodeGen_ISIS.getISISIntfName(intf_name), NodeType_ISIS.ISISIntf);
    //                     assert  !res.second();
    //                     res.first().setArea(mxEntry.getValue());
    //                     topo.addISISIntfRelation(res.first().getName(), intf_name);
    //                 }
    //             }
    //         }
    //     }


    //     //router ospf
    //     //add ospf && ospf daemon
    //     if (ospf_opg != null){
    //         var ospf_name = NodeGen_ISIS.getISISName(r_name);c
    //         var ospf = topo.<ISIS>getOrCreateNode(ospf_name, NodeType_ISIS.ISIS);
    //         assert !ospf.second();
    //         topo.addISISRelation(ospf_name, r_name);
    //         var ospf_daemon_name = NodeGen_ISIS.getISISDaemonName(ospf_name);
    //         var ospf_daemon = topo.<ISISDaemon>getOrCreateNode(ospf_daemon_name, NodeType_ISIS.ISISDaemon);
    //         assert !ospf.second();
    //         topo.addISISDaemonRelation(ospf_name, ospf_daemon_name);

    //         //
    //     }

    //     //ospf intf commands
    //     //execute other intfs ops
    //     for (var intf_opg: intf_opgs){
    //         var exec = new isisIntfExecPass();
    //         var cur_intf_name = intf_opg.getCtxOp().getNAME();
    //         var cur_ospf_intf_name = NodeGen_ISIS.getISISIntfName(cur_intf_name);
    //         if (topo.containsNode(cur_ospf_intf_name)){
    //             exec.setCur_isis_intf(topo.getISISIntf(cur_ospf_intf_name));
    //         }
    //         exec.execOps(intf_opg, topo);
    //     }

    //     //ospf other commands
    //     //execute other ospf ops
    //     if (ospf_opg != null){
    //         var exec = new isisDaemonExecPass();
    //         exec.setCur_router(topo.getNodeNotNull(r_name));
    //         var ospf_name = NodeGen.getOSPFName(r_name);
    //         if (topo.containsNode(ospf_name)) {
    //             exec.setCur_ospf(topo.getNodeNotNull(ospf_name));
    //             var ospf_daemon_name = NodeGen.getOSPFDaemonName(ospf_name);
    //             exec.setCur_ospf_daemon(topo.getNodeNotNull(ospf_daemon_name));
    //         }
    //         exec.execOps(ospf_opg, topo);
    //     }

    //     //remove OSPF interface if OSPF daemon not running
    //     if (!topo.containsOSPFOfRouter(r_name)){
    //         for(var ospfintf : topo.getOSPFIntfOfRouter(r_name)){
    //             topo.delNode(ospfintf);
    //         }
    //     }
    // }

}
