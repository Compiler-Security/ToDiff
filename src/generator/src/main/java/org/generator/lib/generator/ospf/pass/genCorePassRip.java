package org.generator.lib.generator.ospf.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.ospf.OSPF;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.rip.RIP;
import org.generator.lib.item.conf.node.rip.RIPIntf;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.util.net.IPRange;
import org.generator.util.ran.ranHelper;

import java.util.ArrayList;
import java.util.List;

public class genCorePassRip extends genCorePass{
    String r_name;
    ConfGraph confg;

    public OpCtxG handleDaemon(){
        var opCtxG = OpCtxG.Of();
        var rip_name = NodeGen.getRIPName(r_name);
        if (confg.containsNode(rip_name)){
            RIP rip = confg.getNodeNotNull(rip_name);
            addOp(opCtxG, OpType.RRIP);
            {
                var op = addOp(opCtxG, OpType.DISTANCE);
                op.setNUM(rip.getDistance());
            }
            {
                var op = addOp(opCtxG, OpType.DEFAULTMETRIC);
                op.setNUM(rip.getMetric());
            }
            {
                var op = addOp(opCtxG, OpType.TIMERSBASIC);
                op.setNUM(rip.getUpdate());
                op.setNUM2(rip.getTimeout());
                op.setNUM3(rip.getGarbage());
            }
        }
        return opCtxG;
    }

    OpCtxG handleIntf(RIPIntf rip_intf){
        var opCtxG = OpCtxG.Of();
        Intf intf = (Intf) confg.getDstsByType(rip_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
        {
            var op = addOp(opCtxG, OpType.IntfName);
            op.setNAME(intf.getName());
        }
        /*
        SPLIT POISON
        True  False X
        True True X
        False True poison
        False False HORIZON
         */
        if (rip_intf.isPoison() && !rip_intf.isSplitHorizon()){
            var op = addOp(opCtxG, OpType.IPSPLITPOISION);
        }
        if (!rip_intf.isSplitHorizon() && !rip_intf.isPoison()){
            var op = addOp(opCtxG, OpType.IPSPLITHORIZION);
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
        for(var ospf_intf: confg.getRIPIntfOfRouter(r_name)){
            opgs.add(handleIntf(ospf_intf));
        }
        return opgs;
    }

    private OpCtxG handleNetwork(){
        var opCtxg = OpCtxG.Of();
        addOp(opCtxg, OpType.RRIP);
        //FIXME we should merge multiple interface to one
        for(var rip_intf: confg.getRIPIntfOfRouter(r_name)){
            Intf intf = (Intf) confg.getDstsByType(rip_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
            if (ranHelper.randomInt(0, 1) == 0){
                var op = addOp(opCtxg, OpType.NETWORKI);
                op.setIPRANGE(IPRange.of(intf.getIp().toString()));
            }else{
                var op = addOp(opCtxg, OpType.NETWORKN);
                op.setNAME(intf.getName());
            }
        }
        return opCtxg;
    }

    private OpCtxG handlePassiveIntf(){
        boolean isAllPassive = true;
        var opCtxg = OpCtxG.Of();
        addOp(opCtxg, OpType.RRIP);
        for(var rip_intf: confg.getRIPIntfOfRouter(r_name)){
            isAllPassive  &= rip_intf.isPassive();
        }
        if (isAllPassive){
            addOp(opCtxg, OpType.PASSIVEINTFDEFAULT);
        }else{
            for(var rip_intf: confg.getRIPIntfOfRouter(r_name)) {
                if (rip_intf.isPassive()){
                    var op = addOp(opCtxg, OpType.PASSIVEINTFNAME);
                    Intf intf = (Intf) confg.getDstsByType(rip_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
                    op.setNAME(intf.getName());
                }
            }
        }
        return opCtxg;
    }

    private List<OpCtxG> handleVersion(){
        RIP.RIP_VTYPE vtype = null;
        boolean isVTypeSame = true;
        var opCtxgs = new ArrayList<OpCtxG>();
        for(var rip_intf: confg.getRIPIntfOfRouter(r_name)){
            if (vtype == null){
                vtype = rip_intf.getRecvVersion();
            }
            if (vtype != rip_intf.getSendVersion()) isVTypeSame = false;
            if (vtype != rip_intf.getRecvVersion()) isVTypeSame = false;
        }
        if (isVTypeSame){
            var opCtxg = OpCtxG.Of();
            addOp(opCtxg, OpType.RRIP);
            if (vtype == RIP.RIP_VTYPE.V1){
                var op = addOp(opCtxg, OpType.VERSION);
                op.setNUM(1);
            }
            if (vtype == RIP.RIP_VTYPE.V2){
                var op = addOp(opCtxg, OpType.VERSION);
                op.setNUM(2);
            }
            opCtxgs.add(opCtxg);
        }else{
            for(var rip_intf: confg.getRIPIntfOfRouter(r_name)) {
                var opCtxg = OpCtxG.Of();
                Intf intf = (Intf) confg.getDstsByType(rip_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
                var op = addOp(opCtxg, OpType.IntfName);
                op.setNAME(intf.getName());
                switch (rip_intf.getSendVersion()){
                    case V1 -> {addOp(opCtxg, OpType.IPSENDVERSION1);}
                    case V2 -> {addOp(opCtxg, OpType.IPSENDVERSION2);}
                    case V12 -> {addOp(opCtxg, OpType.IPSENDVERSION12);}
                }
                switch (rip_intf.getRecvVersion()){
                    case V1 -> {addOp(opCtxg, OpType.IPRECVVERSION1);}
                    case V2 -> {addOp(opCtxg, OpType.IPRECVVERSION2);}
                    case V12 -> addOp(opCtxg, OpType.IPRECVVERSION12);
                }
                opCtxgs.add(opCtxg);
            }
        }
        return opCtxgs;
    }
    @Override
    public List<OpCtxG> solve(ConfGraph confg){
        this.r_name = confg.getR_name();
        this.confg = confg;
        List<OpCtxG> opgs = new ArrayList<>();

        //daemon
        opgs.add(handleDaemon());

        //intfs
        opgs.addAll(handleIntfs());

        //network
        opgs.add(handleNetwork());

        //passive
        opgs.add(handlePassiveIntf());

        //version
        opgs.addAll(handleVersion());

        var res = mergeOpCtxgToEach(opgs);
        return res;
    }
}
