/**
 * This pass will gen Core OpGA from ConfGraph
 */
package org.generator.lib.generator.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.topo.edge.RelationEdge;
import org.generator.lib.item.topo.graph.ConfGraph;
import org.generator.lib.item.topo.node.NodeGen;
import org.generator.lib.item.topo.node.ospf.OSPF;
import org.generator.lib.item.topo.node.ospf.OSPFAreaSum;
import org.generator.lib.item.topo.node.ospf.OSPFDaemon;
import org.generator.lib.item.topo.node.ospf.OSPFIntf;
import org.generator.lib.item.topo.node.phy.Intf;
import org.generator.util.collections.Pair;
import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;
import org.generator.util.ran.ranHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class genCorePass {

    String r_name;
    ConfGraph confg;
    public genCorePass(){}
    private OpOspf addOp(OpCtxG opCtxG, OpType typ){
        var op = OpOspf.of(typ);
        opCtxG.addOp(OpCtx.of(op));
        return op;
    }
    public OpCtxG handleDaemon(){
        var opCtxG = OpCtxG.Of();
        var ospf_name = NodeGen.getOSPFName(r_name);
        if (confg.containsNode(ospf_name)){
            OSPF ospf = confg.getNodeNotNull(NodeGen.getOSPFName(r_name));
            addOp(opCtxG, OpType.ROSPF);
            if (ospf.getRouterId() != null){
                var op = addOp(opCtxG, OpType.RID);
                op.setID(ospf.getRouterId());
            }

            if (ospf.getAbrType() != null){
                var op = addOp(opCtxG, OpType.RABRTYPE);
                op.setNAME(ospf.getAbrType().toString());
            }

            {
                var op = addOp(opCtxG, OpType.TIMERSTHROTTLESPF);
                op.setNUM(ospf.getInitDelay());
                op.setNUM2(ospf.getMinHoldTime());
                op.setNUM3(ospf.getMaxHoldTime());
            }
           // opCtxG.addOp(new Op);
        }

        var ospf_daemon_name = NodeGen.getOSPFDaemonName(ospf_name);
        if (confg.containsNode(ospf_daemon_name)){
            OSPFDaemon daemon = confg.getNodeNotNull(ospf_daemon_name);
            {
                var op = addOp(opCtxG, OpType.WRITEMULTIPLIER);
                op.setNUM(daemon.getWritemulti());
            }
            {
                var op = addOp(opCtxG, OpType.SOCKETBUFFERSEND);
                op.setLONGNUM(daemon.getBuffersend());
            }
            {
                var op = addOp(opCtxG, OpType.SOCKETBUFFERRECV);
                op.setLONGNUM(daemon.getBufferrecv());
            }
            {
                if (daemon.getBuffersend() == daemon.getBufferrecv()){
                    var op = addOp(opCtxG, OpType.SOCKETBUFFERALL);
                    op.setLONGNUM(daemon.getBufferrecv());
                }
            }
            {
                if (!daemon.isSocketPerInterface()){
                    var op = addOp(opCtxG, OpType.NOSOCKETPERINTERFACE);
                }
            }
            {
                var op = addOp(opCtxG, OpType.MAXIMUMPATHS);
                op.setNUM(daemon.getMaxPaths());
            }
        }
        return opCtxG;
    }

    private  OpCtxG handleIntf(OSPFIntf ospf_intf){
        var opCtxG = OpCtxG.Of();
        Intf intf = (Intf) confg.getDstsByType(ospf_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
        {
            var op = addOp(opCtxG, OpType.IntfName);
            op.setNAME(intf.getName());
        }


        {
            var op = addOp(opCtxG, OpType.IpOspfCost);
            op.setNUM(ospf_intf.getCost());
        }

        //hello dead interval + multi
        if (ospf_intf.getHelloMulti() > 1){
            var op = addOp(opCtxG, OpType.IpOspfDeadInterMulti);
            op.setNUM(ospf_intf.getHelloMulti());
        }
        else{
            {
                var op = addOp(opCtxG, OpType.IpOspfDeadInter);
                op.setNUM(ospf_intf.getDeadInterval());
            }
            {
                var op = addOp(opCtxG, OpType.IpOspfHelloInter);
                op.setNUM(ospf_intf.getHelloInterval());
            }
        }

        {
            var op = addOp(opCtxG, OpType.IpOspfGRHelloDelay);
            op.setNUM(ospf_intf.getGRHelloDelay());
        }

        {
            var op = addOp(opCtxG, OpType.IpOspfNet);
            op.setNAME(ospf_intf.getNetType().toString());
        }

        //priority
        {
            var op = addOp(opCtxG, OpType.IpOspfPriority);
            op.setNUM(ospf_intf.getPriority());
        }

        //retrans
        {
            var op = addOp(opCtxG, OpType.IpOspfRetransInter);
            op.setNUM(ospf_intf.getRetansInter());
        }

        //transDelay
        {
            var op = addOp(opCtxG, OpType.IpOspfTransDealy);
            op.setNUM(ospf_intf.getTransDelay());
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
        for(var ospf_intf: confg.getOSPFIntfOfRouter(r_name)){
            opgs.add(handleIntf(ospf_intf));
        }
        return opgs;
    }
    private OpCtxG handleArea(OSPFAreaSum areaSum){
        var opCtxG = OpCtxG.Of();
        addOp(opCtxG, OpType.ROSPF);
        var area_id = areaSum.getArea();
        //shortcut
        {
            var op = addOp(opCtxG, OpType.AreaShortcut);
            op.setID(area_id);
            op.setNAME(areaSum.getShortcut().toString());
        }
        //virtual link
        if (areaSum.getVirtualLink() != null){
            var op = addOp(opCtxG, OpType.AreaVLink);
            op.setID(area_id);
            //FIXME(VLINK)
            assert false:"virtual link don't implemented";
        }
        //stub nosummary
        if (areaSum.isNosummary()){
            var op = addOp(opCtxG, OpType.AreaStubTotal);
            op.setID(area_id);
        }
        //stub
        if (areaSum.isStub() && !areaSum.isNosummary()){
            var op = addOp(opCtxG, OpType.AreaStub);
            op.setID(area_id);
        }
        //nssa
        if (areaSum.isNssa()){
            var op = addOp(opCtxG, OpType.AreaNSSA);
            op.setID(area_id);
        }
        for(var entry: areaSum.getSumEntries().entrySet()){
            var areaRange = entry.getValue();
            if (areaRange.isAdvertise()){
                //areaRange
                {
                    var op = addOp(opCtxG, OpType.AreaRange);
                    op.setID(area_id);
                    op.setIPRANGE(areaRange.getRange());
                }

                //areaRangeCost
                {
                    var op = addOp(opCtxG, OpType.AreaRangeCost);
                    op.setID(area_id);
                    op.setIPRANGE(areaRange.getRange());
                    op.setNUM(areaRange.getCost());
                }
                //areaRangeSub
                {
                    if (areaRange.getSubstitute() != null) {
                        var op = addOp(opCtxG, OpType.AreaRangeSub);
                        op.setID(area_id);
                        op.setIPRANGE(areaRange.getRange());
                        op.setIP(areaRange.getSubstitute());
                    }
                }
            }else{
                var op = addOp(opCtxG, OpType.AreaRangeNoAd);
                op.setID(area_id);
                op.setIPRANGE(areaRange.getRange());
            }
        }
        return opCtxG;
    }

    private List<OpCtxG> handleAreas(){
        var ospf_name = NodeGen.getOSPFName(r_name);
        List<OpCtxG> opgs = new ArrayList<>();
        for(var areaSum: confg.getOSPFAreaSumOfOSPF(ospf_name)){
            var opctxg = handleArea(areaSum);
            opgs.add(opctxg);
        }
        return opgs;
    }

    private List<OpCtxG> handleAREAID(boolean netAreaId, boolean router_ospf){
        List<OpCtxG> opCtxGS = new ArrayList<>();
        if (!netAreaId || !router_ospf){
            for(var ospf_intf: confg.getOSPFIntfOfRouter(r_name)){
                if (ospf_intf.getArea() != null){
                    Intf intf = (Intf) confg.getDstsByType(ospf_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
                    var opCtxg = OpCtxG.Of();
                    var op1 = addOp(opCtxg, OpType.IntfName);
                    op1.setNAME(intf.getName());
                    var op = addOp(opCtxg, OpType.IpOspfArea);
                    op.setID(ospf_intf.getArea());
                    opCtxGS.add(opCtxg);
                }
            }
        }else{
            List<Pair<IP, ID>> intfToArea = new ArrayList<>();
            for(var ospf_intf: confg.getOSPFIntfOfRouter(r_name)){
                if (ospf_intf.getArea() != null) {
                    Intf intf = (Intf) confg.getDstsByType(ospf_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
                    intfToArea.add(new Pair<>(intf.getIp(), ospf_intf.getArea()));
                }
            }
            var opCtxg = OpCtxG.Of();
            addOp(opCtxg, OpType.ROSPF);
            //TODO add prefix Tree
            for(var entry: intfToArea){
                var ip = entry.first();
                var area = entry.second();
                var op = addOp(opCtxg, OpType.NETAREAID);
                op.setIPRANGE(IPRange.of("%s/%d".formatted(ip.getAddress(), 32)));
                op.setID(area);
            }
            opCtxGS.add(opCtxg);
        }
        return opCtxGS;
    }

    private List<OpCtxG> handlePassiveIntf(boolean router_ospf){
        List<Pair<IP, ID>> intfToArea = new ArrayList<>();
        boolean isAllPassive = true;
        List<OpCtxG> opCtxGS = new ArrayList<>();
        for(var ospf_intf: confg.getOSPFIntfOfRouter(r_name)) {
            isAllPassive  &= ospf_intf.isPassive();
            if (ospf_intf.isPassive()) {
                Intf intf = (Intf) confg.getDstsByType(ospf_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
                var opCtxg = OpCtxG.Of();
                var op = addOp(opCtxg, OpType.IntfName);
                op.setNAME(intf.getName());
                addOp(opCtxg, OpType.IpOspfPassive);
                opCtxGS.add(opCtxg);
            }
        }

        if (isAllPassive && router_ospf){
            var opCtxg = OpCtxG.Of();
            addOp(opCtxg, OpType.ROSPF);
            var op =  addOp(opCtxg, OpType.PASSIVEINTFDEFUALT);
            opCtxGS.add(opCtxg);
        }
        return opCtxGS;
    }


    public static List<OpCtxG> mergeOpCtxgToEach(List<OpCtxG> opCtxG){
        Map<OpOspf, OpCtxG> merge = new HashMap<>();
        for(var opctxg: opCtxG){
            //TO check correctness, don't deal
            opctxg.toString();
            if (opctxg.getOps().isEmpty()) continue;
            var ctxOp = (OpOspf) opctxg.getOps().getFirst().getOperation();
            merge.putIfAbsent(ctxOp, OpCtxG.Of());
            merge.get(ctxOp).addOps(opctxg.getOps());
        }
        return merge.values().stream().toList();
    }

    public static OpCtxG mergeOpCtxgToOne(List<OpCtxG> opCtxGS){
        var opCtxg = OpCtxG.Of();
        for(var opctxg: opCtxGS){
            opCtxg.addOps(opctxg.getOps());
        }
        return opCtxg;
    }
    /**
     * This pass will return generate core config
     * @param confg
     * @return each opCtxG is one interface or router ospf
     */
    public  List<OpCtxG> solve(ConfGraph confg){
        this.r_name = confg.getR_name();
        this.confg = confg;
        List<OpCtxG> opgs = new ArrayList<>();
        //daemon
        var tmp1 = handleDaemon();
        var router_ospf = !tmp1.getOps().isEmpty();
        opgs.add(tmp1);

        //intfs
        var tmp2s = handleIntfs();
        opgs.addAll(tmp2s);

        //areas
        var tmp3s = handleAreas();
        opgs.addAll(tmp3s);

        //areaId
        List<OpCtxG> tmp4;
        if (generate.ran){
            tmp4 = handleAREAID(ranHelper.randomInt(0, 1) == 0, router_ospf);
        }else tmp4 = handleAREAID(true, router_ospf);
        opgs.addAll(tmp4);

        //passive intf
        var tmp5 = handlePassiveIntf(router_ospf);
        opgs.addAll(tmp5);

        var res = mergeOpCtxgToEach(opgs);
        return res;
    }
}
