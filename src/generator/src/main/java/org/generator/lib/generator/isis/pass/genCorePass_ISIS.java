/**
 * This pass will gen Core OpGA from ConfGraph
 */
package org.generator.lib.generator.isis.pass;

import org.generator.lib.frontend.lexical.OpType_isis;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.IR.OpCtx_ISIS;
import org.generator.lib.item.IR.OpIsis;
import org.generator.lib.item.opg.OpCtxG_ISIS;
import org.generator.lib.item.conf.edge.RelationEdge_ISIS;
import org.generator.lib.item.conf.graph.ConfGraph_ISIS;
import org.generator.lib.item.conf.node.NodeGen_ISIS;
import org.generator.lib.item.conf.node.isis.ISIS;
import org.generator.lib.item.conf.node.isis.ISISAreaSum;
import org.generator.lib.item.conf.node.isis.ISISDaemon;
import org.generator.lib.item.conf.node.isis.ISISIntf;
import org.generator.lib.item.conf.node.phy.Intf_ISIS;
import org.generator.util.collections.Pair;
import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;
import org.generator.util.ran.ranHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class genCorePass_ISIS {

    String r_name;
    ConfGraph_ISIS confg;
    public genCorePass_ISIS(){}
    private OpIsis addOp(OpCtxG_ISIS opCtxG, OpType_isis typ){
        var op = OpIsis.of(typ);
        opCtxG.addOp(OpCtx_ISIS.of(op));
        return op;
    }
    public OpCtxG_ISIS handleDaemon(){
        var opCtxG = OpCtxG_ISIS.Of();
        var isis_name = NodeGen_ISIS.getISISName(r_name);
        if (confg.containsNode(isis_name)){
            ISIS isis = confg.getNodeNotNull(NodeGen_ISIS.getISISName(r_name));
            addOp(opCtxG, OpType_isis.RISIS);
            {
                if(isis.getNET() != null){
                    var op = addOp(opCtxG, OpType_isis.NET);
                    op.setNET(isis.getNET());
                }
            }
            {
                var op = addOp(opCtxG, OpType_isis.ISTYPE);
                op.setNAME(isis.getRouterType().toString());
            }
            //FIXME: it doesn't complete yet
        }

        var isis_daemon_name = NodeGen_ISIS.getISISDaemonName(isis_name);
        if (confg.containsNode(isis_daemon_name)){
            ISISDaemon daemon = confg.getNodeNotNull(isis_daemon_name);
            {
                var op = addOp(opCtxG, OpType_isis.METRICSTYLE);
                op.setNAME(daemon.getMetricStyle().toString());
            }
            {
                if(daemon.isAdvertisehighmetrics()){
                    var op = addOp(opCtxG, OpType_isis.ADVERTISEHIGHMETRIC);
                }
            }
            {
                if(daemon.isSetoverloadbit()){
                    var op = addOp(opCtxG, OpType_isis.SETOVERLOADBIT);
                }
            }
            {
                var op = addOp(opCtxG, OpType_isis.SETOVERLOADBITONSTARTUP);
                op.setNUM(daemon.getOverloadbitonstartup());
                
            }
            {
                var op = addOp(opCtxG, OpType_isis.LSPMTU);
                op.setNUM(daemon.getLspmtu());
            }
        }
        return opCtxG;
    }

    private  OpCtxG_ISIS handleIntf(ISISIntf isis_intf, boolean isfull){
        var opCtxG = OpCtxG_ISIS.Of();
        Intf_ISIS intf = (Intf_ISIS) confg.getDstsByType(isis_intf.getName(), RelationEdge_ISIS.EdgeType.INTF).stream().findAny().get();
        {
            var op = addOp(opCtxG, OpType_isis.IntfName);
            op.setNAME(intf.getName());
        }
        //FIXME:PROUTERISIS
        {
            if(isis_intf.isIprouteisis()){
                var op = addOp(opCtxG, OpType_isis.IPROUTERISIS);
            }
        }
        /*
         *  CIRCUITTYPE,
            CSNPINTERVAL,
            HELLOINTERVAL,
            HELLOMULTIPLIER,
            NETWORKPOINTTOPOINT,
            ISISPASSIVE,
            ISISPRIORITY,
            PSNPINTERVAL,
         */
        {
            var op = addOp(opCtxG, OpType_isis.CIRCUITTYPE);
            op.setNAME(isis_intf.getLevel().toString());
        }
        if(isfull){
            {
                if(isis_intf.getCsnpIntervalLevel1() == isis_intf.getCsnpIntervalLevel2()){
                    var op = addOp(opCtxG, OpType_isis.CSNPINTERVAL);
                    op.setNUM(isis_intf.getCsnpIntervalLevel1());
                    op.setNAME("");
                }
                else{
                    var op = addOp(opCtxG, OpType_isis.CSNPINTERVAL);
                    op.setNUM(isis_intf.getCsnpIntervalLevel1());
                    op.setNAME("level-1");
                    var op2 = addOp(opCtxG, OpType_isis.CSNPINTERVAL);
                    op2.setNUM(isis_intf.getCsnpIntervalLevel2());
                    op2.setNAME("level-2");
                }
            }
            {
                if(isis_intf.getHelloIntervalLevel1() == isis_intf.getHelloIntervalLevel2()){
                    var op = addOp(opCtxG, OpType_isis.HELLOINTERVAL);
                    op.setNUM(isis_intf.getHelloIntervalLevel1());
                    op.setNAME("");
                }
                else{
                    var op = addOp(opCtxG, OpType_isis.HELLOINTERVAL);
                    op.setNUM(isis_intf.getHelloIntervalLevel1());
                    op.setNAME("level-1");
                    var op2 = addOp(opCtxG, OpType_isis.HELLOINTERVAL);
                    op2.setNUM(isis_intf.getHelloIntervalLevel2());
                    op2.setNAME("level-2");
                }
            }
            //FIXME:it is not sure

            // if(isis_intf.getHelloMultiplierlevel1()>1){
            //     var op = addOp(opCtxG, OpType_isis.HELLOMULTIPLIER);
            //     op.setNAME("level-1");
            //     op.setNUM(isis_intf.getHelloMultiplierlevel1());
            // }
            // else{
            //     var op = addOp(opCtxG, OpType_isis.HELLOINTERVAL);
            //     op.setNAME("level-1");
            //     op.setNUM(isis_intf.getHelloIntervalLevel1());
            // }

            // if(isis_intf.getHelloMultiplierlevel2() > 1){
            //     var op = addOp(opCtxG, OpType_isis.HELLOMULTIPLIER);
            //     op.setNAME("level-2");
            //     op.setNUM(isis_intf.getHelloMultiplierlevel2());
            // }
            // else{
            //     var op = addOp(opCtxG, OpType_isis.HELLOINTERVAL);
            //     op.setNAME("level-2");
            //     op.setNUM(isis_intf.getHelloIntervalLevel2());
            // }

            {
                if(isis_intf.getHelloMultiplierlevel1()==isis_intf.getHelloMultiplierlevel2()){
                    var op = addOp(opCtxG, OpType_isis.HELLOMULTIPLIER);
                    op.setNUM(isis_intf.getHelloMultiplierlevel1());
                    op.setNAME("");
                }
                else{
                    var op = addOp(opCtxG, OpType_isis.HELLOMULTIPLIER);
                    op.setNUM(isis_intf.getHelloMultiplierlevel1());
                    op.setNAME("level-1");
                    var op2 = addOp(opCtxG, OpType_isis.HELLOMULTIPLIER);
                    op2.setNUM(isis_intf.getHelloMultiplierlevel2());
                    op2.setNAME("level-2");
                }
            }

            {
                if(isis_intf.getPsnpIntervalLevel1() == isis_intf.getPsnpIntervalLevel2()){
                    var op = addOp(opCtxG, OpType_isis.PSNPINTERVAL);
                    op.setNUM(isis_intf.getPsnpIntervalLevel1());
                    op.setNAME("");
                }
                else{
                    var op = addOp(opCtxG, OpType_isis.PSNPINTERVAL);
                    op.setNUM(isis_intf.getPsnpIntervalLevel1());
                    op.setNAME("level-1");
                    var op2 = addOp(opCtxG, OpType_isis.PSNPINTERVAL);
                    op2.setNUM(isis_intf.getPsnpIntervalLevel2());
                    op2.setNAME("level-2");
                }
            }
            {
                if(isis_intf.getPriorityLevel1() == isis_intf.getPriorityLevel2()){
                    var op = addOp(opCtxG, OpType_isis.ISISPRIORITY);
                    op.setNUM(isis_intf.getPriorityLevel1());
                    op.setNAME("");
                }
                else{
                    var op = addOp(opCtxG, OpType_isis.ISISPRIORITY);
                    op.setNUM(isis_intf.getPriorityLevel1());
                    op.setNAME("level-1");
                    var op2 = addOp(opCtxG, OpType_isis.ISISPRIORITY);
                    op2.setNUM(isis_intf.getPriorityLevel2());
                    op2.setNAME("level-2");
                }
            }
        }
        else{
            {
                var op = addOp(opCtxG, OpType_isis.CSNPINTERVAL);
                op.setNUM(isis_intf.getCsnpIntervalLevel1());
                op.setNAME("level-1");
                var op2 = addOp(opCtxG, OpType_isis.CSNPINTERVAL);
                op2.setNUM(isis_intf.getCsnpIntervalLevel2());
                op2.setNAME("level-2");
            }
            {
                var op = addOp(opCtxG, OpType_isis.HELLOINTERVAL);
                op.setNUM(isis_intf.getHelloIntervalLevel1());
                op.setNAME("level-1");
                var op2 = addOp(opCtxG, OpType_isis.HELLOINTERVAL);
                op2.setNUM(isis_intf.getHelloIntervalLevel2());
                op2.setNAME("level-2");
            }
            {
                var op = addOp(opCtxG, OpType_isis.HELLOMULTIPLIER);
                op.setNUM(isis_intf.getHelloMultiplierlevel1());
                op.setNAME("level-1");
                var op2 = addOp(opCtxG, OpType_isis.HELLOMULTIPLIER);
                op2.setNUM(isis_intf.getHelloMultiplierlevel2());
                op2.setNAME("level-2");
            }
            {
                var op = addOp(opCtxG, OpType_isis.PSNPINTERVAL);
                op.setNUM(isis_intf.getPsnpIntervalLevel1());
                op.setNAME("level-1");
                var op2 = addOp(opCtxG, OpType_isis.PSNPINTERVAL);
                op2.setNUM(isis_intf.getPsnpIntervalLevel2());
                op2.setNAME("level-2");
            }
            {
                var op = addOp(opCtxG, OpType_isis.ISISPRIORITY);
                op.setNUM(isis_intf.getPriorityLevel1());
                op.setNAME("level-1");
                var op2 = addOp(opCtxG, OpType_isis.ISISPRIORITY);
                op2.setNUM(isis_intf.getPriorityLevel2());
                op2.setNAME("level-2");
            }
        }
        {
            if(isis_intf.isPassive()){
                var op = addOp(opCtxG, OpType_isis.ISISPASSIVE);
            }
        }

        {
            if(isis_intf.getNetType() == ISISIntf.ISISNetType.POINTTOPOINT){
                var op = addOp(opCtxG, OpType_isis.NETWORKPOINTTOPOINT);
            }
        }
        return opCtxG;
    }

    private List<OpCtxG_ISIS> handleIntfs(boolean isfull){
        List<OpCtxG_ISIS> opgs = new ArrayList<>();
        for(var intf: confg.getIntfsOfRouter(r_name)){
            if (intf.getIp() == null) continue;
            var opCtxG = OpCtxG_ISIS.Of();
            {
                var op = addOp(opCtxG, OpType_isis.IntfName);
                op.setNAME(intf.getName());
            }
            {
                var op = addOp(opCtxG, OpType_isis.IPAddr);
                op.setIP(intf.getIp());
            }

            opgs.add(opCtxG);
        }
        for(var isis_intf: confg.getISISIntfOfRouter(r_name)){
            opgs.add(handleIntf(isis_intf, isfull));
        }
        return opgs;
    }
//     private OpCtxG_ISIS handleArea(ISISAreaSum areaSum){
//         var opCtxG = OpCtxG_ISIS.Of();
//         addOp(opCtxG, OpType_isis.RISIS);
//         var area_id = areaSum.getArea();
//         //shortcut
//         {
//             var op = addOp(opCtxG, OpType_isis.AreaShortcut);
//             op.setID(area_id);
//             op.setNAME(areaSum.getShortcut().toString());
//         }
//         //virtual link
//         //FIXME areaVLINK
// //        if (areaSum.getVirtualLink() != null){
// //            var op = addOp(opCtxG, OpType.AreaVLink);
// //            op.setID(area_id);
// //            //FIXME(VLINK)
// //            assert false:"virtual link don't implemented";
// //        }
//         //stub nosummary
//         if (areaSum.isNosummary()){
//             var op = addOp(opCtxG, OpType.AreaStubTotal);
//             op.setID(area_id);
//         }
//         //stub
//         if (areaSum.isStub() && !areaSum.isNosummary()){
//             var op = addOp(opCtxG, OpType.AreaStub);
//             op.setID(area_id);
//         }
//         //nssa
//         if (areaSum.isNssa()){
//             var op = addOp(opCtxG, OpType.AreaNSSA);
//             op.setID(area_id);
//         }
//         for(var entry: areaSum.getSumEntries().entrySet()){
//             var areaRange = entry.getValue();
//             if (areaRange.isAdvertise()){
//                 //areaRange
//                 {
//                     var op = addOp(opCtxG, OpType.AreaRange);
//                     op.setID(area_id);
//                     op.setIPRANGE(areaRange.getRange());
//                 }

//                 //areaRangeCost
//                 {
//                     var op = addOp(opCtxG, OpType.AreaRangeCost);
//                     op.setID(area_id);
//                     op.setIPRANGE(areaRange.getRange());
//                     op.setNUM(areaRange.getCost());
//                 }
//                 //areaRangeSub
//                 {
//                     if (areaRange.getSubstitute() != null) {
//                         var op = addOp(opCtxG, OpType.AreaRangeSub);
//                         op.setID(area_id);
//                         op.setIPRANGE(areaRange.getRange());
//                         op.setIP(areaRange.getSubstitute());
//                     }
//                 }
//             }else{
//                 var op = addOp(opCtxG, OpType.AreaRangeNoAd);
//                 op.setID(area_id);
//                 op.setIPRANGE(areaRange.getRange());
//             }
//         }
//         return opCtxG;
//     }

    // private List<OpCtxG> handleAreas(){
    //     var isis_name = NodeGen.getisisName(r_name);
    //     List<OpCtxG> opgs = new ArrayList<>();
    //     //System.out.println(confg.toDot(false));
    //     for(var areaSum: confg.getisisAreaSumOfisis(isis_name)){
    //         var opctxg = handleArea(areaSum);
    //         opgs.add(opctxg);
    //     }
    //     return opgs;
    // }

    // private List<OpCtxG> handleAREAID(boolean netAreaId, boolean router_isis){
    //     List<OpCtxG> opCtxGS = new ArrayList<>();
    //     if (!netAreaId || !router_isis){
    //         for(var isis_intf: confg.getisisIntfOfRouter(r_name)){
    //             if (isis_intf.getArea() != null){
    //                 Intf intf = (Intf) confg.getDstsByType(isis_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
    //                 var opCtxg = OpCtxG.Of();
    //                 var op1 = addOp(opCtxg, OpType.IntfName);
    //                 op1.setNAME(intf.getName());
    //                 var op = addOp(opCtxg, OpType.IpisisArea);
    //                 op.setID(isis_intf.getArea());
    //                 opCtxGS.add(opCtxg);
    //             }
    //         }
    //     }else{
    //         List<Pair<IP, ID>> intfToArea = new ArrayList<>();
    //         for(var isis_intf: confg.getisisIntfOfRouter(r_name)){
    //             if (isis_intf.getArea() != null) {
    //                 Intf intf = (Intf) confg.getDstsByType(isis_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
    //                 intfToArea.add(new Pair<>(intf.getIp(), isis_intf.getArea()));
    //             }
    //         }
    //         var opCtxg = OpCtxG.Of();
    //         addOp(opCtxg, OpType.Risis);
    //         //TODO add prefix Tree
    //         for(var entry: intfToArea){
    //             var ip = entry.first();
    //             var area = entry.second();
    //             var op = addOp(opCtxg, OpType.NETAREAID);
    //             op.setIPRANGE(IPRange.of("%s/%d".formatted(ip.getAddress(), 32)));
    //             op.setID(area);
    //         }
    //         opCtxGS.add(opCtxg);
    //     }
    //     return opCtxGS;
    // }

    private List<OpCtxG_ISIS> handlePassiveIntf(boolean router_isis){
        boolean isAllPassive = true;
        List<OpCtxG_ISIS> opCtxGS = new ArrayList<>();
        for(var isis_intf: confg.getISISIntfOfRouter(r_name)) {
            isAllPassive  &= isis_intf.isPassive();
            if (isis_intf.isPassive()) {
                Intf_ISIS intf = (Intf_ISIS) confg.getDstsByType(isis_intf.getName(), RelationEdge_ISIS.EdgeType.INTF).stream().findAny().get();
                var opCtxg = OpCtxG_ISIS.Of();
                var op = addOp(opCtxg, OpType_isis.IntfName);
                op.setNAME(intf.getName());
                addOp(opCtxg, OpType_isis.ISISPASSIVE);
                opCtxGS.add(opCtxg);
            }
        }

        // if (isAllPassive && router_isis){
        //     var opCtxg = OpCtxG_ISIS.Of();
        //     addOp(opCtxg, OpType_isis.RISIS);
        //     var op =  addOp(opCtxg, OpType_isis.PASSIVEINTFDEFUALT);
        //     opCtxGS.add(opCtxg);
        // }
        return opCtxGS;
    }


    public static List<OpCtxG_ISIS> mergeOpCtxgToEach(List<OpCtxG_ISIS> opCtxG){
        Map<OpIsis, OpCtxG_ISIS> merge = new HashMap<>();
        for(var opctxg: opCtxG){
            //TO check correctness, don't deal
            opctxg.toString();
            if (opctxg.getOps().isEmpty()) continue;
            var ctxOp = (OpIsis) opctxg.getOps().getFirst().getOperation();
            merge.putIfAbsent(ctxOp, OpCtxG_ISIS.Of());
            merge.get(ctxOp).addOps(opctxg.getOps());
        }
        return merge.values().stream().toList();
    }

    public static OpCtxG_ISIS mergeOpCtxgToOne(List<OpCtxG_ISIS> opCtxGS){
        var opCtxg = OpCtxG_ISIS.Of();
        for(var opctxg: opCtxGS){
            opCtxg.addOps(opctxg.getOps());
        }
        return opCtxg;
    }
    /**
     * This pass will return generate core config
     * @param confg
     * @return each opCtxG is one interface or router isis
     */
    public  List<OpCtxG_ISIS> solve(ConfGraph_ISIS confg, boolean isfull){
        this.r_name = confg.getR_name();
        this.confg = confg;
        List<OpCtxG_ISIS> opgs = new ArrayList<>();
        //daemon
        var tmp1 = handleDaemon();
        var router_isis = !tmp1.getOps().isEmpty();
        opgs.add(tmp1);

        //intfs
        var tmp2s = handleIntfs(isfull);
        opgs.addAll(tmp2s);

        //areas
        // var tmp3s = handleAreas();
        // opgs.addAll(tmp3s);

        //areaId
        // List<OpCtxG> tmp4;
        // if (generate.ran){
        //     tmp4 = handleAREAID(ranHelper.randomInt(0, 1) == 0, router_isis);
        // }else tmp4 = handleAREAID(true, router_isis);
        // opgs.addAll(tmp4);

        //passive intf
        var tmp5 = handlePassiveIntf(router_isis);
        opgs.addAll(tmp5);

        var res = mergeOpCtxgToEach(opgs);
        return res;
    }
}
