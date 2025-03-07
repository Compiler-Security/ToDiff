/**
 * This pass will gen Core OpGA from ConfGraph
 */
package org.generator.lib.generator.ospf.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.openfabric.FABRIC;
import org.generator.lib.item.conf.node.openfabric.FABRICIntf;
import org.generator.lib.item.conf.node.openfabric.FABRICDaemon;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.util.collections.Pair;
import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;
import org.generator.util.ran.ranHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class genCorePassOpenfabric extends genCorePass {

    String r_name;
    ConfGraph confg;
    public genCorePassOpenfabric(){}

    public OpCtxG handleDaemon(boolean ismissinglevel){
        var opCtxG = OpCtxG.Of();
        var openfabric_name = NodeGen.getOpenFabricName(r_name);
        if (confg.containsNode(openfabric_name)){
            FABRIC openfabric = confg.getNodeNotNull(NodeGen.getOpenFabricName(r_name));
            addOp(opCtxG, OpType.RFABRIC);
            {
                if(openfabric.getNET() != null){
                    var op = addOp(opCtxG, OpType.NET);
                    op.setNET(openfabric.getNET());
                }
            }

            //FIXME: it doesn't complete yet
        }

        var openfabric_daemon_name = NodeGen.getOpenFabricDaemonName(openfabric_name);
        if (confg.containsNode(openfabric_daemon_name)){
            FABRICDaemon daemon = confg.getNodeNotNull(openfabric_daemon_name);
            // {
            //     var op = addOp(opCtxG, OpType.METRICSTYLE);
            //     op.setNAME(daemon.getMetricStyle().toString());
            // }
            {
                if(daemon.isSetoverloadbit()){
                    var op = addOp(opCtxG, OpType.SETOVERLOADBIT);
                }
            }
            {
                var op = addOp(opCtxG, OpType.FABRICTIER);
                op.setNUM(daemon.getTier());
                
            }

            {
                var op = addOp(opCtxG, OpType.FABRICLSPGENINTERVAL);
                op.setNUM(daemon.getLspgeninterval());

            }
            {
                var op = addOp(opCtxG, OpType.FABRICSPFINTERVAL);
                op.setNUM(daemon.getSpfinterval());
            }
            

        }
        return opCtxG;
    }

    private  OpCtxG handleIntf(FABRICIntf openfabric_intf, boolean ismissinglevel){
        var opCtxG = OpCtxG.Of();
        Intf intf = (Intf) confg.getDstsByType(openfabric_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
        {
            var op = addOp(opCtxG, OpType.IntfName);
            op.setNAME(intf.getName());
        }
        //FIXME:PROUTERFABRIC
        {
            if(openfabric_intf.isIproutefabric()){
                var op = addOp(opCtxG, OpType.IPROUTERFABRIC);
            }
        }
        
        {
            var op = addOp(opCtxG, OpType.FABRICCSNPINTERVAL);
            op.setNUM(openfabric_intf.getCsnpInterval());
        }
        {
            var op = addOp(opCtxG, OpType.FABRICHELLOINTERVAL);
            op.setNUM(openfabric_intf.getHelloInterval());
        }
        {
            var op = addOp(opCtxG, OpType.FABRICHELLOMULTIPLIER);
            op.setNUM(openfabric_intf.getHelloMultiplier());
        }
        {
            var op = addOp(opCtxG, OpType.FABRICPSNPINTERVAL);
            op.setNUM(openfabric_intf.getPsnpInterval());
        }
        
        {
            if(openfabric_intf.isPassive()){
                var op = addOp(opCtxG, OpType.FABRICPASSIVE);
            }
        }

        return opCtxG;
    }

    private List<OpCtxG> handleIntfs(boolean ismissinglevel){
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
        for(var openfabric_intf: confg.getOpenFabricIntfOfRouter(r_name)){
            opgs.add(handleIntf(openfabric_intf, ismissinglevel));
        }
        return opgs;
    }

    private List<OpCtxG> handlePassiveIntf(boolean router_openfabric){
        boolean isAllPassive = true;
        List<OpCtxG> opCtxGS = new ArrayList<>();
        for(var openfabric_intf: confg.getOpenFabricIntfOfRouter(r_name)) {
            isAllPassive  &= openfabric_intf.isPassive();
            if (openfabric_intf.isPassive()) {
                Intf intf = (Intf) confg.getDstsByType(openfabric_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
                var opCtxg = OpCtxG.Of();
                var op = addOp(opCtxg, OpType.IntfName);
                op.setNAME(intf.getName());
                addOp(opCtxg, OpType.FABRICPASSIVE);
                opCtxGS.add(opCtxg);
            }
        }

        // if (isAllPassive && router_openfabric){
        //     var opCtxg = OpCtxG.Of();
        //     addOp(opCtxg, OpType.RISIS);
        //     var op =  addOp(opCtxg, OpType.PASSIVEINTFDEFUALT);
        //     opCtxGS.add(opCtxg);
        // }
        return opCtxGS;
    }

    /**
     * This pass will return generate core config
     * @param confg
     * @return each opCtxG is one interface or router openfabric
     */
    @Override
    public  List<OpCtxG> solve(ConfGraph confg, boolean ismissinglevel){
        this.r_name = confg.getR_name();
        this.confg = confg;
        List<OpCtxG> opgs = new ArrayList<>();
        //daemon
        var tmp1 = handleDaemon(ismissinglevel);
        var router_openfabric = !tmp1.getOps().isEmpty();
        opgs.add(tmp1);

        //intfs
        var tmp2s = handleIntfs(ismissinglevel);
        opgs.addAll(tmp2s);

        //areas
        // var tmp3s = handleAreas();
        // opgs.addAll(tmp3s);

        //areaId
        // List<OpCtxG> tmp4;
        // if (generate.ran){
        //     tmp4 = handleAREAID(ranHelper.randomInt(0, 1) == 0, router_openfabric);
        // }else tmp4 = handleAREAID(true, router_openfabric);
        // opgs.addAll(tmp4);

        //passive intf
        var tmp5 = handlePassiveIntf(router_openfabric);
        opgs.addAll(tmp5);

        var res = mergeOpCtxgToEach(opgs);
        return res;
    }
}
