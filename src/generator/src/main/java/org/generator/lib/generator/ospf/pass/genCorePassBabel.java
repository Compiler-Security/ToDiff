package org.generator.lib.generator.ospf.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.babel.BABEL;
import org.generator.lib.item.conf.node.babel.BABELIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.rip.RIPIntf;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.util.net.IPRange;
import org.generator.util.ran.ranHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class genCorePassBabel extends genCorePass {
    String r_name;
    ConfGraph confg;

    private OpCtxG handleNetwork(){
        var opCtxg = OpCtxG.Of();
        addOp(opCtxg, OpType.RBABEL);
        //FIXME we should merge multiple interface to one
        for(var babel_intf: confg.getBABELIntfOfRouter(r_name)){
            Intf intf = (Intf) confg.getDstsByType(babel_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
            if (!confg.containsNode(NodeGen.getBABELIntfName(intf.getName()))){
                continue;
            }
            var op = addOp(opCtxg, OpType.BNETWORKINTF);
            op.setNAME(intf.getName());
        }
        return opCtxg;
    }

    OpCtxG handleRouter(){
        var opCtxg = OpCtxG.Of();
        BABEL babel = confg.getBABELOfRouter(r_name);
        if (babel == null) return opCtxg;
        addOp(opCtxg, OpType.RBABEL);
        {
            var op = addOp(opCtxg, OpType.BRESENDDELAY);
            op.setNUM(babel.getResendDelay());
        }
        {
            var op = addOp(opCtxg, OpType.BSOMMOTHING);
            op.setNUM(babel.getSmoothing());
        }
        return opCtxg;
    }
    OpCtxG handleIntf(BABELIntf babel_intf){
        var opCtxG = OpCtxG.Of();
        Intf intf = (Intf) confg.getDstsByType(babel_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
        {
            var op = addOp(opCtxG, OpType.IntfName);
            op.setNAME(intf.getName());
        }
        {
            var op = addOp(opCtxG, OpType.BWIRE);
            if (babel_intf.isWired()){
                op.setNAME("wired");
            }else{
                op.setNAME("wireless");
            }
        }
        {
            if (!babel_intf.isSplitHorizon()) {
                var op = addOp(opCtxG, OpType.BSPLITHORIZON);
            }
        }
        {
            var op = addOp(opCtxG, OpType.BHELLOINTERVAL);
            op.setNUM(babel_intf.getHelloInterval());
        }
        {
            var op = addOp(opCtxG, OpType.BUPDATEINTERVAL);
            op.setNUM(babel_intf.getUpdateInterval());
        }
        {
            var op = addOp(opCtxG, OpType.BCHANELNOINTEFERING);
            if (babel_intf.isNointerfering()){
                op.setNAME("noninterfering");
            }else{
                op.setNAME("interfering");
            }
        }
        {
            var op = addOp(opCtxG, OpType.BRXCOST);
            op.setNUM(babel_intf.getRxcost());
        }
        {
            var op = addOp(opCtxG, OpType.BRTTDECAY);
            op.setNUM(babel_intf.getRttDecay());
        }
        {
            var op = addOp(opCtxG, OpType.BRTTMIN);
            op.setNUM(babel_intf.getRttMin());
        }
        {
            var op = addOp(opCtxG, OpType.BRTTMAX);
            op.setNUM(babel_intf.getRttMax());
        }
        {
            var op = addOp(opCtxG, OpType.BPENALTY);
            op.setNUM(babel_intf.getPenalty());
        }
        {
            if (babel_intf.isTimeStamps()){
                var op = addOp(opCtxG, OpType.BENABLETIMESTAMP);
            }
        }
        return opCtxG;
    }

    private List<OpCtxG> handleIntfs(){
        List<OpCtxG> opgs = new ArrayList<>();
        for(var intf: confg.getIntfsOfRouter(r_name)){
            if (intf.getIp() == null) continue;
            var opCtxG = OpCtxG.Of();
            {
                var op = addOp(opCtxG, OpType.IntfName);
                op.setNAME(intf.getName());
            }
            {
                var op = addOp(opCtxG, OpType.IPAddr);
                op.setIP(intf.getIp());
            }
            opgs.add(opCtxG);
        }
        for(var babel_intf: confg.getBABELIntfOfRouter(r_name)){
            opgs.add(handleIntf(babel_intf));
        }
        return opgs;
    }
    @Override
    public List<OpCtxG> solve(ConfGraph confg, boolean ismissinglevel) {
        this.r_name = confg.getR_name();
        this.confg = confg;
        List<OpCtxG> opgs = new ArrayList<>();

        //intfs
        opgs.addAll(handleIntfs());

        opgs.add(handleRouter());
        //network
        opgs.add(handleNetwork());
        return opgs;
    }
}
