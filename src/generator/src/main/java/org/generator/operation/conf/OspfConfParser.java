package org.generator.operation.conf;

import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.generator.operation.opg.IntfOpgExec;
import org.generator.operation.opg.OpGroup;
import org.generator.operation.opg.OspfOpgExec;
import org.generator.operation.opg.SimpleOpGroup;
import org.generator.topo.graph.RelationGraph;
import org.generator.topo.node.NodeGen;
import org.generator.topo.node.NodeType;
import org.generator.topo.node.ospf.OSPF;
import org.generator.topo.node.ospf.OSPFDaemon;
import org.generator.topo.node.ospf.OSPFIntf;
import org.generator.topo.node.phy.Intf;
import org.generator.topo.node.phy.Router;
import org.generator.util.collections.Pair;
import org.generator.util.net.IPV4;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class OspfConfParser {

    private static void parseOpCtx(OpGroup opg) {
        Operation set_intf = null, set_ospf = null;
        for (var op : opg.getOps()) {
            if (op.Type() == OpType.ROSPF) {
                set_intf = null;
                set_ospf = op.isUnset() ? null : op;
                op.setCtxOp(new Operation(OpType.OSPFCONF));
            } else if (op.Type() == OpType.IntfName) {
                set_ospf = null;
                set_intf = op.isUnset() ? null : op;
                op.setCtxOp(new Operation(OpType.OSPFCONF));
            } else if (OpType.inOSPFINTF(op.Type())) {
                op.setCtxOp(set_intf);
            } else {
                op.setCtxOp(set_ospf);
            }
        }
    }

    private static void removeInvalidOp(SimpleOpGroup opg) {
        SimpleOpGroup totalOpg = new SimpleOpGroup();
        opg.getOps().stream().filter(x -> x.Type() != OpType.INVALID && x.getCtxOp() != null).forEach(totalOpg::addOp);
        opg.setOpgroup(totalOpg.getOps());
    }

    //    private static HashMap<Operation, SimpleOpGroup> mergeIntfGroup(SimpleOpGroup opg){
//        var h = new HashMap<Operation, SimpleOpGroup>();
//        Operation intfOp = null;
//        for (var op: opg.getOps()){
//            if (op.Type() == OpType.IntfName){
//                intfOp = op;
//                if (!h.containsKey(intfOp)){
//                    var sog = new SimpleOpGroup();
//                    sog.setCtxOp(intfOp);
//                    h.put(intfOp, sog);
//                }
//            }else if (op.Type() == OpType.ROSPF){
//                intfOp = null;
//            }else if (OpType.inOSPFINTF(op.Type())){
//                if (intfOp == null) continue;
//                h.get(intfOp).addOp(op);
//            }
//        }
//        return h;
//    }
//
//    private static @Nullable SimpleOpGroup mergeOspfGroup(SimpleOpGroup opg){
//        boolean has_ospf = false;
//        for (var op: opg.getOps()){
//            if (op.Type() == OpType.ROSPF){
//                has_ospf = !op.isUnset();
//            }
//        }
//        if (!has_ospf) return null;
//        boolean ospf_router = false;
//        SimpleOpGroup sg = new SimpleOpGroup();
//        sg.setCtxOp(new Operation(OpType.ROSPF));
//        for(var op: opg.getOps()){
//            if (op.Type() == OpType.ROSPF){
//                ospf_router = !op.isUnset();
//            }else if (op.Type() == OpType.IntfName){
//                ospf_router = false;
//            }else if (ospf_router && !OpType.inOSPFINTF(op.Type())){
//                sg.addOp(op);
//            }
//        }
//        return sg;
//    }
    private static boolean matchUnset(Operation unsetOp, Operation target) {
        //TODO consider unsetOp filed == target field && unsetOp.getCtxOp == target.getCtxOp
        return false;
    }

    private static void unsetOp(@NotNull SimpleOpGroup opg) {
        var op_list = opg.getOps();
        if (op_list.isEmpty()) return;
        boolean[] remain = new boolean[op_list.size()];
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

    private static Pair<List<SimpleOpGroup>, SimpleOpGroup> mergeOps(SimpleOpGroup opg) {
        var h = new HashMap<Operation, SimpleOpGroup>();
        for (var op : opg.getOps()) {
            if (op.Type() == OpType.IntfName || op.Type() == OpType.ROSPF) {
                if (!h.containsKey(op)) {
                    var opg_new = new SimpleOpGroup();
                    opg_new.setCtxOp(op);
                    h.put(op, opg_new);
                    if (op.Type() == OpType.IntfName) opg_new.setTyp(SimpleOpGroup.OpGType.Intf);
                    else opg_new.setTyp(SimpleOpGroup.OpGType.OSPF);
                }
            } else {
                h.get(op.getCtxOp()).addOp(op);
            }
        }
        return new Pair<>(h.values().stream().filter(x -> x.getTyp() == SimpleOpGroup.OpGType.Intf).toList(), h.values().stream().filter(x -> x.getTyp() == SimpleOpGroup.OpGType.OSPF).findFirst().orElse(null));
    }

    private static boolean IPNetworkInvalid(SimpleOpGroup opg) {
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
        return ip_ospf_area;
    }

    public static void parse(SimpleOpGroup opg, RelationGraph topo, String r_name) {
        //deduce conf, remove invalid operation, and unset accroding no operations
        parseOpCtx(opg);
        removeInvalidOp(opg);
        unsetOp(opg);
        parseOpCtx(opg);
        removeInvalidOp(opg);

        //remove invalid ip area  network area
        boolean is_ip_ospf_area = IPNetworkInvalid(opg);
        removeInvalidOp(opg);


        var opgs = mergeOps(opg);
        var intf_opgs = opgs.first();
        var ospf_opg = opgs.second();

        //set intf ip || add intf
        for (var intf_opg : intf_opgs) {
            assert intf_opg.getTyp() == SimpleOpGroup.OpGType.Intf;
            //add persudo intf accroding to int r1-eth0
            var intf_name = intf_opg.getCtxOp().getNAME();
            var res = topo.<Intf>getOrCreateNode(intf_name, NodeType.Intf);
            if (!res.second()) {
                res.first().setPersudo(true);
                topo.addIntfRelation(intf_name, r_name);
            }
            //parse ip address ...
            for (var op : intf_opg.popOpsOfType(OpType.IPAddr)) {
                topo.getIntf(intf_name).setIp(op.getIP());
            }
        }

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
            HashMap<IPV4, IPV4> netToArea = new HashMap<>();
            for (var op: ospf_opg.popOpsOfType(OpType.NETAREAID)){
                if (!netToArea.containsKey(op.getIP())){
                    netToArea.put(op.getIP(), op.getID());
                }
            }
            for (var intf: topo.getIntfsOfRouter(r_name)){
                //TODO match the most small net, and set area
            }
        }

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
        }

        //execute other intfs ops
        for (var intf_opg: intf_opgs){
            var exec = new IntfOpgExec();
            var cur_intf_name = intf_opg.getCtxOp().getNAME();
            var cur_ospf_intf_name = NodeGen.getOSPFIntfName(cur_intf_name);
            if (topo.containsNode(cur_ospf_intf_name)){
                exec.setCur_ospf_intf(topo.getOSPFIntf(cur_intf_name));
            }
            exec.execOps(intf_opg, topo);
        }
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
    }
}
