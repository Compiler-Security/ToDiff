package org.generator.lib.operation.conf;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.operation.opg.OpGroup;
import org.generator.lib.operation.opg.ParserOpGroup;
import org.generator.lib.item.topo.node.NodeGen;
import org.generator.lib.item.topo.node.ospf.OSPF;
import org.generator.lib.item.topo.node.ospf.OSPFIntf;
import org.generator.lib.item.topo.node.phy.Intf;
import org.generator.lib.operation.operation.Op;
import org.generator.lib.operation.opgexec.OspfIntfOpgExec;
import org.generator.lib.operation.opgexec.OspfOpgExec;
import org.generator.lib.item.topo.graph.RelationGraph;
import org.generator.lib.item.topo.node.NodeType;
import org.generator.lib.item.topo.node.ospf.OSPFDaemon;
import org.generator.util.collections.Pair;
import org.generator.util.net.IPBase;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OspfConfParser {

    private static void parseOpCtx(OpGroup opg) {
        Op set_intf = null, set_ospf = null;
        for (var op : opg.getOps()) {
            if (op.Type() == OpType.ROSPF) {
                set_intf = null;
                set_ospf = op.isUnset() ? null : op;
                op.setCtxOp(new Op(OpType.OSPFCONF));
            } else if (op.Type() == OpType.IntfName) {
                set_ospf = null;
                set_intf = op.isUnset() ? null : op;
                op.setCtxOp(new Op(OpType.OSPFCONF));
            } else if (op.Type() == OpType.IPAddr){
                op.setCtxOp(set_intf);
            }
            else if (op.Type().inOSPFINTF()) {
                op.setCtxOp(set_intf);
            } else if (op.Type().inOSPFDAEMON() || op.Type().inOSPFAREA() || op.Type().inOSPFRouterWithTopo()){
                op.setCtxOp(set_ospf);
            } else{
                op.setCtxOp(new Op(OpType.OSPFCONF));
            }
        }
    }

    private static void removeInvalidOp(ParserOpGroup opg) {
        ParserOpGroup totalOpg = new ParserOpGroup();
        opg.getOps().stream().filter(x -> x.Type() != OpType.INVALID && x.getCtxOp() != null).forEach(totalOpg::addOp);
        opg.setOpgroup(totalOpg.getOps());
    }

    private static boolean matchUnset(Op unsetOp, Op target) {
        assert unsetOp.isUnset() && !target.isUnset() : "match unset unset is not right";
        unsetOp = unsetOp.getMinimalUnsetOp();
        if (!unsetOp.getCtxOp().equals(target.getCtxOp())){
            return false;
        }
        if (unsetOp.Type() != target.Type()) return false;
        return unsetOp.equals(target.getMinimalUnsetOp());
    }

    private static void removePreOp(@NotNull ParserOpGroup opg){
        var op_list = opg.getOps();
        if (op_list.isEmpty()) return;
        boolean[] remain = new boolean[op_list.size()];
        Arrays.fill(remain, true);
        for (int i = op_list.size() - 1; i >= 0; i--) {
            if (remain[i]) {
                var op_cur = op_list.get(i);
                if (op_cur.Type() == OpType.ROSPF || op_cur.Type() == OpType.IntfName) continue;
                var op_unset = op_cur.cloneOfType(op_cur.Type());
                op_unset.setCtxOp(op_cur.getCtxOp());
                op_unset.setUnset(true);
                for (int j = i - 1; j >= 0; j--) {
                    var op = op_list.get(j);
                    if (remain[j] && !op.isUnset()) {
                        if (matchUnset(op_unset, op)) {
                            remain[j] = false;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < op_list.size(); i++) {
            if (!remain[i]) op_list.get(i).setCtxOp(null);
        }
    }
    private static void unsetOp(@NotNull ParserOpGroup opg) {
        var op_list = opg.getOps();
        if (op_list.isEmpty()) return;
        boolean[] remain = new boolean[op_list.size()];
        Arrays.fill(remain, true);
        for (int i = op_list.size() - 1; i >= 0; i--) {
            if (op_list.get(i).isUnset()) {
                var op_unset = op_list.get(i);
                remain[i] = false;
                for (int j = i - 1; j >= 0; j--) {
                    var op = op_list.get(j);
                    if (remain[j] && !op.isUnset()) {
                        if (matchUnset(op_unset, op)) {
                            remain[j] = false;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < op_list.size(); i++) {
            if (!remain[i]) op_list.get(i).setCtxOp(null);
        }
    }

    private static Pair<List<ParserOpGroup>, ParserOpGroup> mergeOps(ParserOpGroup opg) {
        var h = new HashMap<Op, ParserOpGroup>();
        for (var op : opg.getOps()) {
            if (op.Type() == OpType.IntfName || op.Type() == OpType.ROSPF) {
                if (!h.containsKey(op)) {
                    var opg_new = new ParserOpGroup();
                    opg_new.setCtxOp(op);
                    h.put(op, opg_new);
                    if (op.Type() == OpType.IntfName) opg_new.setTyp(ParserOpGroup.OpGType.Intf);
                    else opg_new.setTyp(ParserOpGroup.OpGType.OSPF);
                }
            } else {
                h.get(op.getCtxOp()).addOp(op);
            }
        }
        return new Pair<>(h.values().stream().filter(x -> x.getTyp() == ParserOpGroup.OpGType.Intf).toList(), h.values().stream().filter(x -> x.getTyp() == ParserOpGroup.OpGType.OSPF).findFirst().orElse(null));
    }

    private static boolean IPNetworkInvalid(ParserOpGroup opg) {
        boolean ip_ospf_area = false, network_area = false;
        for (var op : opg.getOps()) {
            if (op.Type() == OpType.IpOspfArea) {
                ip_ospf_area = true;
                break;
            }
            if (op.Type() == OpType.NETAREAID) {
                network_area = true;
                break;
            }
        }
        for (var op : opg.getOps()) {
            if (op.Type() == OpType.IpOspfArea && !ip_ospf_area) {
                op.setCtxOp(null);
            }
            if (op.Type() == OpType.NETAREAID && !network_area) {
                op.setCtxOp(null);
            }
        }
        if (ip_ospf_area){
            Set<Op> intfs = new HashSet<>();
            for(var op: opg.getOps()){
                if (op.Type() == OpType.IpOspfArea){
                    if (!intfs.contains(op.getCtxOp())){
                        intfs.add(op.getCtxOp());
                    }else {
                        op.setCtxOp(null);
                    }
                }
            }
        }else if (network_area){
            Set<IPBase> ips = new HashSet<>();
            for(var op: opg.getOps()){
                if (op.Type() == OpType.NETAREAID){
                    if (!ips.contains(op.getIP())){
                        ips.add(op.getIP());
                    }else{
                        op.setCtxOp(null);
                    }
                }
            }
        }
        return ip_ospf_area;
    }

    public static void parse(ParserOpGroup opg, RelationGraph topo, String r_name) {
        //deduce conf, remove invalid operation, and unset accroding no operations
        parseOpCtx(opg);
        //remove inst not in intf/router ospf
        removeInvalidOp(opg);
        //unset op, include intf, router ospf
        unsetOp(opg);
        //remove this op be unset
        removeInvalidOp(opg);
        parseOpCtx(opg);
        //remove inst not in intf/router ospf
        removeInvalidOp(opg);

        removePreOp(opg);
        removeInvalidOp(opg);

        //remove invalid ip area  network area
        boolean is_ip_ospf_area = IPNetworkInvalid(opg);
        removeInvalidOp(opg);

        var opgs = mergeOps(opg);
        var intf_opgs = opgs.first();
        var ospf_opg = opgs.second();

        //reconstruct opg
        opg.getOps().clear();
        for (var intf_opg: intf_opgs){
            opg.addOp(intf_opg.getCtxOp());
            opg.addOps(intf_opg.getOps());
        }
        if (ospf_opg != null){
            opg.addOp(ospf_opg.getCtxOp());
            opg.addOps(ospf_opg.getOps());
        }
        //int name
        //ip address XXX.XXX.XXX.XXX
        //set intf ip || add intf
        for (var intf_opg : intf_opgs) {
            assert intf_opg.getTyp() == ParserOpGroup.OpGType.Intf;
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
        if (is_ip_ospf_area) {
            for(var intf_opg: intf_opgs){
                //only the first ip opsf area work
                for(var op: intf_opg.popOpsOfType(OpType.IpOspfArea)){
                    var intf_name = intf_opg.getCtxOp().getNAME();
                    var intf = topo.getIntf(intf_name);
                    //add ospf Intf
                    if (!intf.isPersudo() && intf.getIp() != null){
                        var res = topo.<OSPFIntf>getOrCreateNode(NodeGen.getOSPFIntfName(intf_name), NodeType.OSPFIntf);
                        assert  !res.second();
                        res.first().setArea(op.getID());
                        topo.addOSPFIntfRelation(res.first().getName(), intf_name);
                    }
                    break;
                }
            }
        }else if (ospf_opg != null){
            HashMap<IPBase, IPBase> netToArea = new HashMap<>();
            for (var op: ospf_opg.popOpsOfType(OpType.NETAREAID)){
                if (!netToArea.containsKey(op.getIP())){
                    netToArea.put(op.getIP(), op.getID());
                }
            }
            for (var intf: topo.getIntfsOfRouter(r_name)){
                if (intf.getIp() == null) continue;
                int mask_len = -1;
                Map.Entry<IPBase, IPBase> mxEntry = null;
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
                        var res = topo.<OSPFIntf>getOrCreateNode(NodeGen.getOSPFIntfName(intf_name), NodeType.OSPFIntf);
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
            var ospf = topo.<OSPF>getOrCreateNode(ospf_name, NodeType.OSPF);
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
            var exec = new OspfIntfOpgExec();
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
            var exec = new OspfOpgExec();
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
