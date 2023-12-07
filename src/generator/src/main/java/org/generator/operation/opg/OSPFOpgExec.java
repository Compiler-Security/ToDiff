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
import org.generator.util.exception.Unimplemented;
import org.generator.util.exec.ExecStat;
import org.generator.util.net.IPV4;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;


public class OSPFOpgExec {


    public OSPFOpgExec() {
        cur_intf = null;
        cur_ospf = null;
        cur_ospf_intf = null;
        cur_router = null;
    }



    public ExecStat execOSPFAttriCmds(@NotNull Operation op, @NotNull RelationGraph topo) {
        if (op.Type() != OpType.ROSPF && (cur_router ==null || cur_ospf == null)){
            return ExecStat.FAIL;
        }
        var cur_rname = cur_router.getName();
        switch (op.Type()) {
            case RID -> {
                cur_ospf.setRouterId(op.getID());
                return ExecStat.SUCC;
            }
            case RABRTYPE -> {
                return OSPF.ABR_TYPE.of(op.getNAME())
                        .stream().peek(cur_ospf::setAbrType)
                        .findAny()
                        .map(__ -> ExecStat.SUCC)
                        .orElse(ExecStat.FAIL);
            }
            case PASSIVEINTFDEFUALT -> {
                return topo.getOSPFIntfOfRouter(cur_rname).stream()
                        .peek(x -> x.setPassive(true))
                        .findFirst()
                        .map(__ -> ExecStat.SUCC)
                        .orElse(ExecStat.MISS);
            }

            case TIMERSTHROTTLESPF -> {
                int max_number = Stream.of(op.getNUM(), op.getNUM2(), op.getNUM3()).max(Integer::compareTo).get();
                cur_ospf.setInitDelay(op.getNUM());
                cur_ospf.setInitHoldTime(op.getNUM2());
                cur_ospf.setMaxHoldTime(op.getNUM3());
                return ExecStat.SUCC;
            }
        }
        assert false:"should not goto here";
        return ExecStat.FAIL;
    }

    public ExecStat execOspfDaemonAttriCmds(@NotNull Operation op, @NotNull RelationGraph topo) {
        if (cur_ospf_daemon == null){
            return ExecStat.MISS;
        }
        var ospf_daemon = cur_ospf_daemon;
        switch (op.Type()){
            case MAXIMUMPATHS -> {
                var num = op.getNUM();
                ospf_daemon.setMaxPaths(num);
                return ExecStat.SUCC;
            }
            case WRITEMULTIPLIER -> {
                var num = op.getNUM();
                ospf_daemon.setWritemulti(num);
            }
            case  SOCKETBUFFERSEND -> {
                //FIXME num should be long!
                var num = op.getNUM();
                ospf_daemon.setBuffersend(num);
                return ExecStat.SUCC;
            }
            case SOCKETBUFFERRECV -> {
                var num = op.getNUM();
                ospf_daemon.setBufferrecv(num);
                return ExecStat.SUCC;
            }
            case SOCKETBUFFERALL -> {
                var num = op.getNUM();
                ospf_daemon.setBuffersend(num);
                ospf_daemon.setBufferrecv(num);
                return ExecStat.SUCC;
            }
            case NOSOCKETPERINTERFACE -> {
                ospf_daemon.setSocketPerInterface(false);
                return ExecStat.SUCC;
            }
        }
        assert false:"should not go to here";
        return ExecStat.FAIL;
    }

    private OSPFAreaSum getAreaSum(@NotNull IPV4 area, @NotNull RelationGraph topo){
        var res = topo.<OSPFAreaSum>getOrCreateNode(NodeGen.getOSPFAreaSumName(cur_ospf.getName(), NodeGen.getAreaName(area)), NodeType.OSPFAreaSum);
        if (!res.second()){
            topo.addOSPFAreaSumRelation(res.first().getName(), cur_ospf.getName());
        }
        return res.first();
    }

    private OSPFAreaSum.OSPFAreaSumEntry getAreaSumEntry(@NotNull OSPFAreaSum areaSum, IPV4 range){
        return Optional.ofNullable(areaSum.getSumEntries().get(range.toString()))
                .orElseGet(() -> {
                    var entry = new OSPFAreaSum.OSPFAreaSumEntry();
                    entry.setRange(range);
                    areaSum.getSumEntries().put(range.toString(), entry);
                    return entry;
                });
    }

    public ExecStat execOSPFAreaCmds(@NotNull Operation op, @NotNull RelationGraph topo) {
        if (cur_router == null || cur_ospf == null) return ExecStat.MISS;
        //FIXME num range should be deal before this
        var cur_rname = cur_router.getName();
        Boolean is_ABR = topo.getOSPFIntfOfRouter(cur_rname)
                .stream().anyMatch(x -> x.getArea().toInt() == 0);
        if (op.Type().ordinal() >= OpType.AreaRange.ordinal() && op.Type().ordinal() <= OpType.AreaRangeCostINT.ordinal()) {
            if (!is_ABR) {
                return ExecStat.MISS;
            }
            if (op.getID() == null) op.setID(IPV4.Of(op.getNUM()));
            var areaSum = getAreaSum(op.getID(), topo);
            var areaSumEntry = getAreaSumEntry(areaSum, op.getIP());
            switch (op.Type()) {
                case AreaRange, AreaRangeINT -> {
                }
                case AreaRangeAd, AreaRangeAdINT -> {
                    areaSumEntry.setAdvertise(true);
                }
                case AreaRangeCost, AreaRangeCostINT -> {
                    areaSumEntry.setCost(op.getNUM());
                }
                case AreaRangeAdCost, AreaRangeAdCostINT -> {
                    areaSumEntry.setAdvertise(true);
                    areaSumEntry.setCost(op.getNUM());
                }
                case AreaRangeNoAd, AreaRangeNoAdINT -> {
                    areaSumEntry.setAdvertise(false);
                }
                case AreaRangeSub, AreaRangeSubINT -> {
                    areaSumEntry.setSubstitute(op.getIP2());
                }
            }
            return ExecStat.SUCC;
        }else{
            //FIXME Is these commands should be used only in ABR?
            if (op.getID() == null) op.setID(IPV4.Of(op.getNUM()));
            var areaSum = getAreaSum(op.getID(), topo);
            switch (op.Type()){
                case AreaVLink -> {
                    //TODO
                }
                case AreaShortcut -> {
                    areaSum.setShortcut(true);
                }
                case AreaStub -> {
                    areaSum.setStub(true);
                }
                case AreaStubTotal -> {
                    areaSum.setStub(true);
                    areaSum.setNosummary(true);
                }
                case AreaNSSA -> {
                    areaSum.setNssa(true);
                }
            }
            return ExecStat.SUCC;
        }
    }

    private ExecStat execOSPFIntfCmds(@NotNull Operation op, @NotNull RelationGraph topo){
        if (cur_ospf_intf == null){
            return ExecStat.MISS;
        }
        switch (op.Type()){
            case IpOspfCost -> {
                cur_ospf_intf.setCost(op.getNUM());
            }
            case IpOspfDeadInter -> {
                cur_ospf_intf.setDeadInterval(op.getNUM());
            }
            case IpOspfDeadInterMulti -> {
                cur_ospf_intf.setHelloPerSec(op.getNUM());
                cur_ospf_intf.setHelloInterval(0);
            }
            case IpOspfHelloInter -> {
                cur_ospf_intf.setHelloInterval(op.getNUM());
            }
            case IpOspfGRHelloDelay -> {
                cur_ospf_intf.setGRHelloDelay(op.getNUM());
            }
            case IpOspfNet -> {
                return OSPFIntf.OSPFNetType.of(op.getNAME())
                        .map(x -> {cur_ospf_intf.setNetType(x); return ExecStat.SUCC;})
                        .orElse(ExecStat.MISS);
            }
            case IpOspfPriority -> {
                cur_ospf_intf.setPriority(op.getNUM());
            }
            case IpOspfRetransInter -> {
                cur_ospf_intf.setRetansInter(op.getNUM());
            }
            case IpOspfTransDealy -> {
                cur_ospf_intf.setTransDelay(op.getNUM());
            }
            case IpOspfPassive -> {
                cur_ospf_intf.setPassive(true);
            }
        }
        return ExecStat.SUCC;
    }
    private ExecStat execOSPFOp(@NotNull Operation op, RelationGraph topo) {
        if (OpType.inOSPFRouterWithTopo(op.Type())) {
            return execOSPFAttriCmds(op, topo);
        }else if (OpType.inOSPFDAEMON(op.Type())){
            return execOspfDaemonAttriCmds(op, topo);
        }else if (OpType.inOSPFAREA(op.Type())){
            return execOSPFAreaCmds(op, topo);
        }
        return ExecStat.MISS;
    }

    public void ExecOpGroup(OpGroup opg, RelationGraph topo) {
        var target = opg.getTarget();
        if (target.isPresent()) {
            String target_st = target.get();
            if (topo.containsNode(target_st)){
                if (topo.getNode(target_st).get() instanceof Router r) {
                    cur_router = r;
                }
            }else{
                assert false:String.format("target %s not exist!", target_st);
            }

        }
        for (var op : opg.getOps()) {
            if (OpType.inPhy(op.Type())) {

            } else if (OpType.inOSPF(op.Type())) {
                execOSPFOp(op, topo);
            } else {
                new Unimplemented();
            }
        }
    }




    //context
    public OSPF getCur_ospf() {
        return cur_ospf;
    }

    public void setCur_ospf(OSPF cur_ospf) {
        this.cur_ospf = cur_ospf;
    }

    public OSPFDaemon getCur_ospf_daemon() {
        return cur_ospf_daemon;
    }

    public void setCur_ospf_daemon(OSPFDaemon cur_ospf_daemon) {
        this.cur_ospf_daemon = cur_ospf_daemon;
    }

    public Router getCur_router() {
        return cur_router;
    }

    public void setCur_router(Router cur_router) {
        this.cur_router = cur_router;
    }

    public Intf getCur_intf() {
        return cur_intf;
    }

    public void setCur_intf(Intf cur_intf) {
        this.cur_intf = cur_intf;
    }

    public OSPFIntf getCur_ospf_intf() {
        return cur_ospf_intf;
    }

    public void setCur_ospf_intf(OSPFIntf cur_ospf_intf) {
        this.cur_ospf_intf = cur_ospf_intf;
    }

    OSPF cur_ospf;
    OSPFDaemon cur_ospf_daemon;
    Router cur_router;
    Intf cur_intf;
    OSPFIntf cur_ospf_intf;

}
