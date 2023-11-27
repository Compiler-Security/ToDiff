package org.generator.operation.opg;

import org.generator.operation.op.OpGen;
import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.generator.topo.edge.RelationEdge;
import org.generator.topo.graph.RelationGraph;
import org.generator.topo.node.NodeGen;
import org.generator.topo.node.NodeType;
import org.generator.topo.node.ospf.*;
import org.generator.topo.node.phy.Intf;
import org.generator.topo.node.phy.Router;
import org.generator.util.collections.Pair;
import org.generator.util.exception.Unimplemented;
import org.generator.util.exec.ExecStat;
import org.generator.util.net.IPV4;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;


public class OpgExec {


    public OpgExec() {
        cur_intf = Optional.empty();
        cur_ospf = Optional.empty();
        cur_ospf_intf = Optional.empty();
        cur_router = Optional.empty();
    }

    private ExecStat execPhyOp(Operation op, RelationGraph topo) {
        switch (op.Type()) {
            case NODEADD -> {
                switch (NodeGen.getPhyNodeTypeByName(op.getNAME())) {
                    case NodeType.Router -> {
                        return topo.addNode(NodeGen.new_Router(op.getNAME()));
                    }
                    case NodeType.Switch -> {
                        return topo.addNode(NodeGen.new_Switch(op.getNAME()));
                    }
                    default -> {
                        new Unimplemented();
                    }
                }
            }
            case NODEDEL -> {
                var node = topo.getNode(op.getNAME());

                //check node present
                if (node.isEmpty()) {
                    return ExecStat.MISS;
                } else {
                    return topo.delNode(node.get());
                }
            }
            case NODESETOSPFUP -> {
                var r_name = op.getNAME();
                var ospf_name = NodeGen.getOSPFName(r_name);

                //check miss condition
                if (!topo.containsNode(r_name) || topo.containsNode(ospf_name)) return ExecStat.MISS;

                //new ospf
                OSPF ospf = NodeGen.new_OSPF(ospf_name);
                ospf.setStatus(OSPF.OSPF_STATUS.INIT);
                topo.addNode(ospf);

                // new relation edge
                return topo.addOSPFRelation(ospf_name, r_name);
            }
            case NODESETOSPFRE -> {
                var r_name = op.getNAME();
                var ospf_name = NodeGen.getOSPFName(r_name);
                //check condition
                if (!topo.containsNode(ospf_name)) return ExecStat.MISS;

                //change ospf status
                var ospf = (OSPF) topo.getNode(ospf_name).get();
                ospf.setStatus(OSPF.OSPF_STATUS.Restart);

                return ExecStat.SUCC;
            }
            case INTFUP -> {
                var intf_name = op.getNAME();

                //check condition
                if (!topo.containsNode(intf_name)) return ExecStat.MISS;
                var intf = (Intf) topo.getNode(intf_name).get();

                //set intf up
                intf.setUp(true);
                return ExecStat.SUCC;
            }
            case INTFDOWN -> {
                var intf_name = op.getNAME();

                //check condition
                if (!topo.containsNode(intf_name)) return ExecStat.MISS;
                var intf = (Intf) topo.getNode(intf_name).get();

                //set intf down
                intf.setUp(false);
                return ExecStat.SUCC;
            }
            case LINKUP -> {
                var intf1_name = op.getNAME();
                var intf2_name = op.getNAME2();

                //link one end should always be a switch
                var node1_name = NodeGen.getPhyNodeNameFromIntfName(intf1_name);
                var node2_name = NodeGen.getPhyNodeNameFromIntfName(intf2_name);
                assert NodeGen.getPhyNodeTypeByName(node2_name) == NodeType.Switch : String.format("link operation %s not right", op);

                //check condition
                if (!topo.containsNode(node1_name) || !topo.containsNode(node2_name)) return ExecStat.MISS;
                if (topo.containsEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK) && topo.containsEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK))
                    return ExecStat.MISS;
                if (topo.containsNode(intf1_name) || topo.containsNode(intf2_name)) return ExecStat.MISS;

                Intf intf1 = NodeGen.new_Intf(intf1_name);
                Intf intf2 = NodeGen.new_Intf(intf2_name);
                topo.addNode(intf1);
                topo.addNode(intf2);
                topo.addEdge(node1_name, intf1_name, RelationEdge.EdgeType.INTF);
                topo.addEdge(intf1_name, node1_name, RelationEdge.EdgeType.PhyNODE);
                topo.addEdge(node2_name, intf2_name, RelationEdge.EdgeType.INTF);
                topo.addEdge(intf2_name, node2_name, RelationEdge.EdgeType.PhyNODE);

                //add edges in both directions
                topo.addEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK);
                topo.addEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK);
                return ExecStat.SUCC;
            }
            case LINKDOWN -> {
                //link down is equal to intf1 down && intf2 down
                var intf1_name = op.getNAME();
                var intf2_name = op.getNAME2();

                //check condition
                if (!topo.containsNode(intf1_name) || !topo.containsNode(intf2_name)) return ExecStat.MISS;


                var op1 = OpGen.GenOperation(OpType.INTFDOWN);
                op1.setNAME(op.getNAME());
                var op2 = OpGen.GenOperation(OpType.INTFDOWN);
                op2.setNAME(op.getNAME());
                execPhyOp(op1, topo);
                execPhyOp(op2, topo);
                return ExecStat.SUCC;
            }
            case LINKREMOVE -> {
                var intf1_name = op.getNAME();
                var intf2_name = op.getNAME2();

                //check condition
                if (!topo.containsEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK) || !topo.containsEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK))
                    return ExecStat.MISS;

                //delete edges in both directions
                topo.delEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK);
                topo.delEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK);
                return ExecStat.SUCC;
            }
            default -> {
                assert false : "type error";
            }
        }
        return ExecStat.MISS;
    }

    private ExecStat execOSPFRouterWithTopo(@NotNull Operation op, @NotNull RelationGraph topo) {
        switch (op.Type()) {
            case ROSPF -> {
                if (cur_router.isEmpty()) {
                    return ExecStat.MISS;
                }
                var routerName = cur_router.get().getName();
                var ospfNodeName = NodeGen.getOSPFName(routerName);
                var res = topo.getOrCrateNode(ospfNodeName, NodeType.OSPF);
                cur_ospf = Optional.of((OSPF) res.first());
                if (res.second()) {
                    //FIXME if router ospf double, what should we do
                    var OSPFNode = (OSPF) res.first();
                    OSPFNode.setStatus(OSPF.OSPF_STATUS.UP);
                    var ospfdaemonName = NodeGen.getOSPFDaemonName(OSPFNode.getName());
                    assert !topo.containsNode(ospfdaemonName);
                    var res1 = topo.<OSPFDaemon>getOrCreateNode(ospfdaemonName, NodeType.OSPFDaemon);
                    var ospfDaemon = res1.first();
                    topo.addOSPFAreaRelation(ospfNodeName, ospfdaemonName);
                } else {
                    return ExecStat.MISS;
                }
            }
//            case ROSPFNUM -> {}
//            case ROSPFVRF -> {}
            case RID -> {
                if (cur_ospf.isEmpty()) {
                    return ExecStat.MISS;
                }
                cur_ospf.get().setRouterId(op.getID());
                return ExecStat.SUCC;
            }
            case RABRTYPE -> {
                if (cur_ospf.isEmpty()) {
                    return ExecStat.MISS;
                }
                var typ = op.getNAME();
                for (var abr_type : OSPF.ABR_TYPE.values()) {
                    if (abr_type.match(typ)) {
                        cur_ospf.get().setAbrType(abr_type);
                        return ExecStat.SUCC;
                    }
                }
            }
            case NETAREAID -> {
                //FIXME we should to know how long the conf last
                if (cur_ospf.isEmpty() || cur_router.isEmpty()) {
                    return ExecStat.MISS;
                }
                var router = cur_router.get();
                var ospf = cur_ospf.get();
                var ip = op.getIP();
                var area = op.getID();
                for (var e : topo.getEdgesByType(router.getName(), RelationEdge.EdgeType.INTF)) {
                    var intf = (Intf) e.getDst();
                    if (ip.contains(intf.getIp())) {
                        var ospf_intf_name = NodeGen.getOSPFIntfName(intf.getName());
                        var res = topo.getOrCrateNode(ospf_intf_name, NodeType.OSPFIntf);
                        //set OSPFIntf
                        OSPFIntf ospfintf = (OSPFIntf) res.first();
                        if (!res.second()) {
                            topo.addOSPFIntfRelation(ospf_intf_name, intf.getName(), ospf.getName());
                        }
                        //set area
                        var res1 = topo.getOrCrateNode(NodeGen.getAreaName(area), NodeType.OSPFArea);
                        OSPFArea ospfarea = (OSPFArea) res1.first();
                        if (!res1.second()) {
                            topo.addOSPFAreaRelation(ospfarea.getName(), ospf_intf_name);
                        }
                    }
                }
                return ExecStat.SUCC;
            }
            case NETAREAIDNUM -> {
                //we change this to NETAREAID
                var num = op.getNUM();
                var op_new = new Operation( OpType.NETAREAIDNUM);
                op_new.setIP(IPV4.Of(num));
                return execOSPFRouterWithTopo(op_new, topo);
            }
            case PASSIVEINTFDEFUALT -> {
                for(var ospfintf: topo.getOSPFIntfOfRouter(cur_rname)){
                    ospfintf.setPassive(true);
                }
            }
            case TIMERSTHROTTLESPF -> {
                int max_number = Stream.of(op.getNUM(), op.getNUM2(), op.getNUM3()).max(Integer::compareTo).get();
                if (cur_ospf.isPresent() && 0 <= max_number && max_number <= 600000){
                    getCur_ospf().setInitDelay(op.getNUM());
                    getCur_ospf().setInitHoldTime(op.getNUM2());
                    getCur_ospf().setMaxHoldTime(op.getNUM3());
                }else{
                    return ExecStat.MISS;
                }
            }
        }
        return ExecStat.MISS;
    }

    private ExecStat execOSPFDAEMON(@NotNull Operation op, @NotNull RelationGraph topo) {
        if (cur_ospf.isEmpty()) return ExecStat.MISS;
        var ospfdaemonName = NodeGen.getOSPFDaemonName(cur_ospfname);
        switch (op.Type()){
            case CLEARIPOSPFPROCESS -> {}
            case CLEARIPOSPFNEIGHBOR -> {}
            case MAXIMUMPATHS -> {
                if (!topo.containsNode(ospfdaemonName)) return ExecStat.MISS;
                var num = op.getNUM();
                if (1 <= num && num <= 64) {
                    topo.<OSPFDaemon>getNodeNotNull(ospfdaemonName).setMaxPaths(num);
                    return ExecStat.SUCC;
                }else{
                    return ExecStat.MISS;
                }
            }
            case WRITEMULTIPLIER -> {
                if (!topo.containsNode(ospfdaemonName)) return ExecStat.MISS;
                var num = op.getNUM();
                if (1 <= num && num <= 100) {
                    topo.<OSPFDaemon>getNodeNotNull(ospfdaemonName).setWritemulti(num);
                    return ExecStat.SUCC;
                }else{
                    return ExecStat.MISS;
                }
            }
            case  SOCKETBUFFERSEND -> {
                //FIXME num should be long!
                if (!topo.containsNode(ospfdaemonName)) return ExecStat.MISS;
                var num = op.getNUM();
                if (1 <= num && num <= 4000000000L) {
                    topo.<OSPFDaemon>getNodeNotNull(ospfdaemonName).setBuffersend(num);
                    return ExecStat.SUCC;
                }else{
                    return ExecStat.MISS;
                }
            }
            case SOCKETBUFFERRECV -> {
                if (!topo.containsNode(ospfdaemonName)) return ExecStat.MISS;
                var num = op.getNUM();
                if (1 <= num && num <= 4000000000L) {
                    topo.<OSPFDaemon>getNodeNotNull(ospfdaemonName).setBufferrecv(num);
                    return ExecStat.SUCC;
                }else{
                    return ExecStat.MISS;
                }
            }
            case SOCKETBUFFERALL -> {
                if (!topo.containsNode(ospfdaemonName)) return ExecStat.MISS;
                var num = op.getNUM();
                if (1 <= num && num <= 4000000000L) {
                    topo.<OSPFDaemon>getNodeNotNull(ospfdaemonName).setBuffersend(num);
                    topo.<OSPFDaemon>getNodeNotNull(ospfdaemonName).setBufferrecv(num);
                    return ExecStat.SUCC;
                }else{
                    return ExecStat.MISS;
                }
            }
            case NOSOCKETPERINTERFACE -> {
                if (!topo.containsNode(ospfdaemonName)) return ExecStat.MISS;
                topo.<OSPFDaemon>getNodeNotNull(ospfdaemonName).setSocketPerInterface(false);
                return ExecStat.SUCC;
            }

        }
        return ExecStat.MISS;
    }


    private Optional<Pair<Set<OSPFNet>, OSPFAreaSum.OSPFAreaSumEntry>> getOSPFAREARANGE(Operation op, @NotNull RelationGraph topo){
        boolean meet = false;
        for (var ospfintf: topo.getOSPFIntfOfRouter(cur_rname)){
            if (topo.hasOSPFIntfOfArea(ospfintf.getName()) && topo.getOSPFIntfOfArea(ospfintf.getName()).getArea().toInt() == 0){
                meet = true;
                break;
            }
        }
        if (!meet) return Optional.empty();
        Set<OSPFNet> ospfnets= new HashSet<>();
        for (var ospfnet: topo.getOSPFNetOfOSPFArea(NodeGen.getAreaName(op.getID()))){
            if (!ospfnet.isHide() && op.getIP().contains(ospfnet.getIp())){
                ospfnets.add(ospfnet);
            }
        }
        if (ospfnets.isEmpty()) return Optional.empty();
        if (cur_ospf.isEmpty()) return Optional.empty();
        var area = op.getID();
        var areaName = NodeGen.getOSPFAreaName(op.getID());
        var areaSumName = NodeGen.getOSPFAreaSumName(cur_ospfname, areaName);

        var res = topo.<OSPFAreaSum>getOrCreateNode(areaSumName, NodeType.OSPFAreaSum);
        OSPFAreaSum areaSum = res.first();
        if (!res.second()){
            topo.addOSPFAreaSumRelation(areaSumName, cur_ospfname);
        }

        if (!areaSum.getSumEntries().containsKey(area.toInt())){
            areaSum.getSumEntries().put(area.toInt(), new OSPFAreaSum.OSPFAreaSumEntry());
            areaSum.getSumEntries().get(area.toInt()).setRange(op.getIP());
        }

        var areaSumEntry = areaSum.getSumEntries().get(area.toInt());
        return Optional.of(new Pair<>(ospfnets, areaSumEntry));
    }

    private OSPFAreaSum createOSPFAreaSum(Operation op, RelationGraph topo){
        var area = op.getID();
        var areaName = NodeGen.getOSPFAreaName(op.getID());
        var areaSumName = NodeGen.getOSPFAreaSumName(cur_ospfname, areaName);

        var res = topo.<OSPFAreaSum>getOrCreateNode(areaSumName, NodeType.OSPFAreaSum);
        OSPFAreaSum areaSum = res.first();
        if (!res.second()){
            topo.addOSPFAreaSumRelation(areaSumName, cur_ospfname);
        }
        return areaSum;
    }
    private ExecStat execOSPFAREA(@NotNull Operation op, @NotNull RelationGraph topo) {

        if (!cur_router.isPresent()) return ExecStat.MISS;
        switch (op.Type()){
            case AreaRange -> {
                var res = getOSPFAREARANGE(op, topo);
                if (res.isEmpty()) return ExecStat.MISS;
                var areaSumEntry = res.get().second();
                var ospfnets = res.get().first();
                areaSumEntry.setRange(op.getIP());
                areaSumEntry.getNet().clear();
                areaSumEntry.getNet().addAll(ospfnets);
            }
            case AreaRangeAd -> {
                var res = getOSPFAREARANGE(op, topo);
                if (res.isEmpty()) return ExecStat.MISS;
                var areaSumEntry = res.get().second();
                areaSumEntry.setAdvertise(true);
            }
            case AreaRangeAdCost -> {
                if (0 <= op.getNUM() && op.getNUM() <= 16777215){
                    var res = getOSPFAREARANGE(op, topo);
                    if (res.isEmpty()) return ExecStat.MISS;
                    var areaSumEntry = res.get().second();
                    areaSumEntry.setAdvertise(true);
                    areaSumEntry.setCost(op.getNUM());
                }else{
                    return ExecStat.MISS;
                }
            }
            case AreaRangeNoAd -> {
                var res = getOSPFAREARANGE(op, topo);
                if (res.isEmpty()) return ExecStat.MISS;
                var areaSumEntry = res.get().second();
                areaSumEntry.setAdvertise(false);
            }
            case AreaRangeSub -> {
                var res = getOSPFAREARANGE(op, topo);
                if (res.isEmpty()) return ExecStat.MISS;
                var areaSumEntry = res.get().second();
                areaSumEntry.setSubstitute(op.getIP2());
            }
            case AreaRangeCost -> {
                if (0 <= op.getNUM() && op.getNUM() <= 16777215){
                    var res = getOSPFAREARANGE(op, topo);
                    if (res.isEmpty()) return ExecStat.MISS;
                    var areaSumEntry = res.get().second();
                    areaSumEntry.setCost(op.getNUM());
                }else{
                    return ExecStat.MISS;
                }
            }
            case AreaRangeINT -> {
                if (0 <= op.getNUM() && op.getNUM() <= 16777215){
                    op.setID(IPV4.Of(op.getNUM()));
                    var res = getOSPFAREARANGE(op, topo);
                    if (res.isEmpty()) return ExecStat.MISS;
                    var areaSumEntry = res.get().second();
                    var ospfnets = res.get().first();
                    areaSumEntry.setRange(op.getIP());
                    areaSumEntry.getNet().clear();
                    areaSumEntry.getNet().addAll(ospfnets);
                }else{
                    return ExecStat.MISS;
                }
            }
            case AreaRangeAdINT ->{
                if (0 <= op.getNUM() && op.getNUM() <= 16777215){
                    op.setID(IPV4.Of(op.getNUM()));
                    var res = getOSPFAREARANGE(op, topo);
                    if (res.isEmpty()) return ExecStat.MISS;
                    var areaSumEntry = res.get().second();
                    areaSumEntry.setAdvertise(true);
                }else{
                    return ExecStat.MISS;
                }
            }
            case AreaRangeAdCostINT ->{
                if (0 <= op.getNUM2() && op.getNUM2() <= 16777215){
                    op.setID(IPV4.Of(op.getNUM2()));
                    var res = getOSPFAREARANGE(op, topo);
                    if (res.isEmpty()) return ExecStat.MISS;
                    var areaSumEntry = res.get().second();
                    areaSumEntry.setAdvertise(true);
                    areaSumEntry.setCost(op.getNUM());
                }else{
                    return ExecStat.MISS;
                }
            }
            case AreaRangeNoAdINT -> {
                if (0 <= op.getNUM() && op.getNUM() <= 16777215){
                    op.setID(IPV4.Of(op.getNUM()));
                    var res = getOSPFAREARANGE(op, topo);
                    if (res.isEmpty()) return ExecStat.MISS;
                    var areaSumEntry = res.get().second();
                    areaSumEntry.setAdvertise(false);
                }else{
                    return ExecStat.MISS;
                }
            }
            case AreaRangeSubINT -> {
                if (0 <= op.getNUM() && op.getNUM() <= 16777215){
                    op.setID(IPV4.Of(op.getNUM()));
                    var res = getOSPFAREARANGE(op, topo);
                    if (res.isEmpty()) return ExecStat.MISS;
                    var areaSumEntry = res.get().second();
                    areaSumEntry.setSubstitute(op.getIP2());
                }else{
                    return ExecStat.MISS;
                }
            }
            case AreaRangeCostINT -> {
                if (0 <= op.getNUM2() && op.getNUM2() <= 16777215){
                    op.setID(IPV4.Of(op.getNUM2()));
                    var res = getOSPFAREARANGE(op, topo);
                    if (res.isEmpty()) return ExecStat.MISS;
                    var areaSumEntry = res.get().second();
                    areaSumEntry.setCost(op.getNUM2());
                }else{
                    return ExecStat.MISS;
                }
            }
            case AreaVLink -> {
                if (cur_ospf.isEmpty()) return ExecStat.MISS;
                var areaSumName = createOSPFAreaSum(op, topo);
                assert  areaSumName != null;
                //FIXME we should use ID2
                //areaSumName.setVirtualLink(op.getID2());
            }
            case AreaShortcut -> {
                if (cur_ospf.isEmpty()) return ExecStat.MISS;
                var areaSumName = createOSPFAreaSum(op, topo);
                assert  areaSumName != null;
                //FIXME set shortcut init value false
                areaSumName.setShortcut(true);
            }
            case AreaStub -> {
                if (cur_ospf.isEmpty()) return ExecStat.MISS;
                var areaSumName = createOSPFAreaSum(op, topo);
                assert  areaSumName != null;
                areaSumName.setStub(true);
            }
            case AreaStubTotal -> {
                if (cur_ospf.isEmpty()) return ExecStat.MISS;
                var areaSumName = createOSPFAreaSum(op, topo);
                assert  areaSumName != null;
                areaSumName.setStub(true);
                areaSumName.setNosummary(true);
            }
            case AreaNSSA -> {
                if (cur_ospf.isEmpty()) return ExecStat.MISS;
                var areaSumName = createOSPFAreaSum(op, topo);
                assert  areaSumName != null;
                areaSumName.setNssa(true);
            }
        }
        return ExecStat.MISS;
    }
    private ExecStat execOSPFOp(@NotNull Operation op, RelationGraph topo) {
        if (OpType.inOSPFRouterWithTopo(op.Type())) {
            return execOSPFRouterWithTopo(op, topo);
        }else if (OpType.inOSPFDAEMON(op.Type())){
            return execOSPFDAEMON(op, topo);
        }else if (OpType.inOSPFAREA(op.Type())){
            return execOSPFAREA(op, topo);
        }
        return ExecStat.MISS;
    }

    public void ExecOpGroup(OpGroup opg, RelationGraph topo) {
        var target = opg.getTarget();
        if (target.isPresent()) {
            String target_st = target.get();
            if (topo.containsNode(target_st)){
                if (topo.getNode(target_st).get() instanceof Router r) {
                    cur_router = Optional.of(r);
                }
            }else{
                assert false:String.format("target %s not exist!", target_st);
            }

        }
        for (var op : opg.getOps()) {
            if (OpType.inPhy(op.Type())) {
                execPhyOp(op, topo);
            } else if (OpType.inOSPF(op.Type())) {
                execOSPFOp(op, topo);
            } else {
                new Unimplemented();
            }
        }
    }


    public OSPF getCur_ospf() {
        return cur_ospf.get();
    }

    public void setCur_ospf(OSPF cur_ospf) {
        this.cur_ospf = Optional.ofNullable(cur_ospf);
        cur_ospfname = getCur_ospf().getName();
    }

    public Intf getCur_intf() {
        return cur_intf.get();
    }

    public void setCur_intf(Intf cur_intf) {
        this.cur_intf = Optional.ofNullable(cur_intf);
        cur_intfname = getCur_intf().getName();
        cur_ospfintfname = NodeGen.getOSPFIntfName(cur_intfname);
    }

    public OSPFIntf getCur_ospf_intf() {
        return cur_ospf_intf.get();
    }

    public void setCur_ospf_intf(OSPFIntf cur_ospf_intf) {
        this.cur_ospf_intf = Optional.of(cur_ospf_intf);
    }

    public Router getCur_router() {
        return cur_router.get();
    }

    public void setCur_router(Router cur_router) {
        this.cur_router = Optional.ofNullable(cur_router);
        this.cur_rname = getCur_router().getName();
        this.cur_ospfname = NodeGen.getOSPFName(cur_rname);
    }

    //Context

    String cur_rname;
    String cur_ospfname;
    String cur_intfname;
    String cur_ospfintfname;

    @NotNull  Optional<OSPF> cur_ospf;
    @NotNull Optional<Intf> cur_intf;
    @NotNull Optional<OSPFIntf> cur_ospf_intf;

    @NotNull Optional<Router> cur_router;
}
