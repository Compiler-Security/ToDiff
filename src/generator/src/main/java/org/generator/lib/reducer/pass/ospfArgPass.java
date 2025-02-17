package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.item.opg.OpArgG;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.rip.RIP;
import org.generator.lib.item.conf.node.rip.RIPIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.frontend.lexical.OpType;
import org.generator.util.collections.Pair;
import org.generator.util.net.ID;
import org.generator.util.net.IPRange;

import java.util.*;

public class ospfArgPass {

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
                    else opg_new.setTyp(OpArgG.OpGType.OSPF);
                }
            h.get(ctx_op).addOp(op);
        }
        return new Pair<>(h.values().stream().filter(x -> x.getTyp() == OpArgG.OpGType.Intf).toList(), h.values().stream().filter(x -> x.getTyp() == OpArgG.OpGType.OSPF).findFirst().orElse(null));
    }

    private static boolean hasIpOspfArea(OpAG opg) {
        boolean ip_ospf_area = false;
        for (var opa : opg.getOps()) {
            if (opa.op.Type() == OpType.IpOspfArea) {
                ip_ospf_area = true;
                break;
            }
        }
        return ip_ospf_area;
    }

    public static void solve(OpAG opg, ConfGraph topo, String r_name) {
        var opgs = splitOpAG(opg);
        var intf_opgs = opgs.first();
        var ospf_opg = opgs.second();

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

        //network XXX.XXX.XXX.XXX area XXX
        //ip ospf area XXX
        //set area & add ospfintf
        boolean is_ip_ospf_area = hasIpOspfArea(opg);
        if (is_ip_ospf_area) {
            for(var intf_opg: intf_opgs){
                //only the first ip opsf area work
                for(var op: intf_opg.popOpsOfType(OpType.IpOspfArea)){
                    var intf_name = intf_opg.getCtxOp().getNAME();
                    var intf = topo.getIntf(intf_name);
                    //add ospf Intf
                    if (!intf.isPersudo() && intf.getIp() != null){
                        var res = topo.<RIPIntf>getOrCreateNode(NodeGen.getOSPFIntfName(intf_name), NodeType.RIPIntf);
                        assert  !res.second();
                        res.first().setArea(op.getID());
                        topo.addOSPFIntfRelation(res.first().getName(), intf_name);
                    }
                    break;
                }
            }
        }else if (ospf_opg != null){
            HashMap<IPRange, ID> netToArea = new HashMap<>();
            for (var op: ospf_opg.popOpsOfType(OpType.NETAREAID)){
                if (!netToArea.containsKey(op.getIPRANGE())){
                    netToArea.put(op.getIPRANGE(), op.getID());
                }
            }
            for (var intf: topo.getIntfsOfRouter(r_name)){
                if (intf.getIp() == null) continue;
                int mask_len = -1;
                Map.Entry<IPRange, ID> mxEntry = null;
                //find the most small subnetwork
                for (var entry: netToArea.entrySet()){
                    if (entry.getKey().containsIp(intf.getIp())){
                        if (entry.getKey().getMaskOfIp() > mask_len) {
                            mask_len = entry.getKey().getMaskOfIp();
                            mxEntry = entry;
                        }
                    }
                }
                if (mxEntry != null){
                    //add ospf Intf
                    var intf_name = intf.getName();
                    if (!intf.isPersudo()){
                        var res = topo.<RIPIntf>getOrCreateNode(NodeGen.getOSPFIntfName(intf_name), NodeType.RIPIntf);
                        assert  !res.second();
                        res.first().setArea(mxEntry.getValue());
                        topo.addOSPFIntfRelation(res.first().getName(), intf_name);
                    }
                }
            }
        }


        //router ospf
        //add ospf && ospf daemon
        if (ospf_opg != null){
            var ospf_name = NodeGen.getOSPFName(r_name);
            var ospf = topo.<RIP>getOrCreateNode(ospf_name, NodeType.RIP);
            assert !ospf.second();
            topo.addOSPFRelation(ospf_name, r_name);
            var ospf_daemon_name = NodeGen.getOSPFDaemonName(ospf_name);
            var ospf_daemon = topo.<OSPFDaemon>getOrCreateNode(ospf_daemon_name, NodeType.OSPFDaemon);
            assert !ospf.second();
            topo.addOSPFDaemonRelation(ospf_name, ospf_daemon_name);

            //
        }

        //ospf intf commands
        //execute other intfs ops
        for (var intf_opg: intf_opgs){
            var exec = new ospfIntfExecPass();
            var cur_intf_name = intf_opg.getCtxOp().getNAME();
            var cur_ospf_intf_name = NodeGen.getOSPFIntfName(cur_intf_name);
            if (topo.containsNode(cur_ospf_intf_name)){
                exec.setCur_ospf_intf(topo.getOSPFIntf(cur_ospf_intf_name));
            }
            exec.execOps(intf_opg, topo);
        }

        //ospf other commands
        //execute other ospf ops
        if (ospf_opg != null){
            var exec = new ospfDaemonExecPass();
            exec.setCur_router(topo.getNodeNotNull(r_name));
            var ospf_name = NodeGen.getOSPFName(r_name);
            if (topo.containsNode(ospf_name)) {
                exec.setCur_ospf(topo.getNodeNotNull(ospf_name));
                var ospf_daemon_name = NodeGen.getOSPFDaemonName(ospf_name);
                exec.setCur_ospf_daemon(topo.getNodeNotNull(ospf_daemon_name));
            }
            exec.execOps(ospf_opg, topo);
        }

        //remove OSPF interface if OSPF daemon not running
        if (!topo.containsOSPFOfRouter(r_name)){
            for(var ospfintf : topo.getOSPFIntfOfRouter(r_name)){
                topo.delNode(ospfintf);
            }
        }
    }

}
