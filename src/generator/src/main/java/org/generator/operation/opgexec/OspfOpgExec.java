package org.generator.operation.opgexec;

import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.generator.topo.graph.RelationGraph;
import org.generator.topo.node.NodeGen;
import org.generator.topo.node.NodeType;
import org.generator.topo.node.ospf.OSPF;
import org.generator.topo.node.ospf.OSPFAreaSum;
import org.generator.util.exec.ExecStat;
import org.generator.util.net.IPV4;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

public class OspfOpgExec extends OpgExec{
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
                cur_ospf.setMinHoldTime(op.getNUM2());
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
                return ExecStat.SUCC;
            }
            case  SOCKETBUFFERSEND -> {
                var num = op.getIDNUM();
                ospf_daemon.setBuffersend(num);
                return ExecStat.SUCC;
            }
            case SOCKETBUFFERRECV -> {
                var num = op.getIDNUM();
                ospf_daemon.setBufferrecv(num);
                return ExecStat.SUCC;
            }
            case SOCKETBUFFERALL -> {
                var num = op.getIDNUM();
                ospf_daemon.setBuffersend(num);
                ospf_daemon.setBufferrecv(num);
                return ExecStat.SUCC;
            }
            case NOSOCKETPERINTERFACE -> {
                ospf_daemon.setSocketPerInterface(false);
                return ExecStat.SUCC;
            }
            case CLEARIPOSPFNEIGHBOR, CLEARIPOSPFPROCESS ->{
                return ExecStat.SUCC;
            }
        }
        assert false:String.format("should not go to here %s", op.toString());
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
        boolean is_ABR = topo.getOSPFIntfOfRouter(cur_rname)
                .stream().anyMatch(x -> x.getArea().IDtoInt() == 0);
        if (op.Type().ordinal() >= OpType.AreaRange.ordinal() && op.Type().ordinal() <= OpType.AreaRangeCostINT.ordinal()) {
            if (!is_ABR) {
                return ExecStat.MISS;
            }
            if (op.getID() == null) op.setID(IPV4.IDOf(op.getNUM()));
            var areaSum = getAreaSum(op.getID(), topo);
            var areaSumEntry = getAreaSumEntry(areaSum, op.getIP());
            switch (op.Type()) {
                case AreaRange -> {
                }
                case AreaRangeAd -> {
                    areaSumEntry.setAdvertise(true);
                }
                case AreaRangeCost -> {
                    areaSumEntry.setCost(op.getNUM());
                }
                case AreaRangeAdCost -> {
                    areaSumEntry.setAdvertise(true);
                    areaSumEntry.setCost(op.getNUM());
                }
                case AreaRangeNoAd -> {
                    areaSumEntry.setAdvertise(false);
                }
                case AreaRangeSub -> {
                    areaSumEntry.setSubstitute(op.getIP2());
                }
            }
            return ExecStat.SUCC;
        }else{
            //FIXME Is these commands should be used only in ABR?
            if (op.getID() == null) op.setID(IPV4.IDOf(op.getNUM()));
            var areaSum = getAreaSum(op.getID(), topo);
            switch (op.Type()){
                case AreaVLink -> {
                    //TODO
                    assert false : "AreaVlink not implemented";
                }
                case AreaShortcut -> {
                    if (op.getNAME().equals("enable")) {
                        areaSum.setShortcut(OSPFAreaSum.shortCutType.Enable);
                    }
                    if (op.getNAME().equals("disable")){
                        areaSum.setShortcut(OSPFAreaSum.shortCutType.Disable);
                    }
                    if (op.getNAME().equals("default")){
                        areaSum.setShortcut(OSPFAreaSum.shortCutType.Default);
                    }
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

    private ExecStat execOSPFOp(@NotNull Operation op, RelationGraph topo) {
        if (op.Type().inOSPFRouterWithTopo()) {
            return execOSPFAttriCmds(op, topo);
        }else if (op.Type().inOSPFDAEMON()){
            return execOspfDaemonAttriCmds(op, topo);
        }else if (op.Type().inOSPFAREA()){
            return execOSPFAreaCmds(op, topo);
        }
        return ExecStat.MISS;
    }
    @Override
    ExecStat execOp(Operation op, RelationGraph topo) {
        return execOSPFOp(op, topo);
    }
}
