package org.generator.lib.reducer.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.item.IR.Op;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.babel.BABEL;
import org.generator.lib.item.conf.node.babel.BABELIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.rip.RIP;
import org.generator.lib.item.conf.node.rip.RIPIntf;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.item.opg.OpArgG;
import org.generator.util.collections.Pair;

import java.util.HashMap;
import java.util.List;

public class babelArgPass {
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
                else opg_new.setTyp(OpArgG.OpGType.BABEL);
            }
            h.get(ctx_op).addOp(op);
        }
        return new Pair<>(h.values().stream().filter(x -> x.getTyp() == OpArgG.OpGType.Intf).toList(), h.values().stream().filter(x -> x.getTyp() == OpArgG.OpGType.BABEL).findFirst().orElse(null));
    }

    public static void solve(OpAG opg, ConfGraph topo, String r_name) {
        var opgs = splitOpAG(opg);
        var intf_opgs = opgs.first();
        var babel_opg = opgs.second();

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

        //router babel
        //network ...
        if (babel_opg != null){
            var babel_name = NodeGen.getBABELName(r_name);
            var babel = topo.<BABEL>getOrCreateNode(babel_name, NodeType.BABEL);
            assert !babel.second();
            topo.addBABELRelation(babel_name, r_name);
            for(var op: babel_opg.popOpsOfType(OpType.BNETWORKINTF)){
                var intf_name = op.getNAME();
                if (topo.containsNode(intf_name)){
                    var res = topo.<BABELIntf>getOrCreateNode(NodeGen.getBABELIntfName(intf_name), NodeType.BABELIntf);
                    if (!res.second()) {
                        topo.addBABELIntfRelation(res.first().getName(), intf_name);
                    }
                }
            }
        }

        //babel intf commands
        //execute other intfs ops
        for (var intf_opg: intf_opgs){
            var exec = new babelIntfExecPass();
            var cur_intf_name = intf_opg.getCtxOp().getNAME();
            var cur_babel_intf_name = NodeGen.getBABELIntfName(cur_intf_name);
            if (topo.containsNode(cur_babel_intf_name)){
                exec.setCur_babel_intf(topo.getBABELIntf(cur_babel_intf_name));
            }
            exec.execOps(intf_opg, topo);
        }

        //rip other commands
        //execute other ospf ops
        if (babel_opg != null){
            var exec = new babelDaemonExecPass();
            exec.setCur_router(topo.getNodeNotNull(r_name));
            var babel_name = NodeGen.getBABELName(r_name);
            if (topo.containsNode(babel_name)) {
                exec.setCur_babel(topo.getNodeNotNull(babel_name));
            }
            exec.execOps(babel_opg, topo);
        }


        //remove BABEL interface if BABEL daemon not running
        if (!topo.containsBABELOfRouter(r_name)){
            for(var babelintf : topo.getBABELIntfOfRouter(r_name)){
                topo.delNode(babelintf);
            }
        }
    }
}
