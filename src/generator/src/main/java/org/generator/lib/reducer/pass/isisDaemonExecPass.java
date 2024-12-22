package org.generator.lib.reducer.pass;

import org.generator.lib.frontend.lexical.OpType_isis;
import org.generator.lib.item.IR.Op_ISIS;
import org.generator.lib.item.conf.graph.ConfGraph_ISIS;
import org.generator.lib.item.conf.node.NodeGen_ISIS;
import org.generator.lib.item.conf.node.NodeType_ISIS;
import org.generator.lib.item.conf.node.isis.ISIS;
import org.generator.lib.item.conf.node.isis.ISISAreaSum;
import org.generator.lib.item.conf.node.isis.ISISDaemon;
import org.generator.util.exec.ExecStat;
import org.generator.util.net.ID;
import org.generator.util.net.IPRange;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

public class isisDaemonExecPass extends baseExecPass_ISIS {
    public ExecStat execOSPFAttriCmds(@NotNull Op_ISIS op, @NotNull ConfGraph_ISIS topo) {
        if (op.Type() != OpType_isis.RISIS && (cur_router ==null || cur_isis == null)){
            return ExecStat.FAIL;
        }
        var cur_rname = cur_router.getName();
        switch (op.Type()) {
            case NET -> {
                cur_isis.setNET(op.getNET());
                return ExecStat.SUCC;
            }
            default -> {
                return ExecStat.MISS;
            }

        }
        //assert false:"should not goto here %s".formatted(op.Type());
        //return ExecStat.FAIL;
    }

    public ExecStat execOspfDaemonAttriCmds(@NotNull Op_ISIS op, @NotNull ConfGraph_ISIS topo) {
        if (cur_isis_daemon == null){
            return ExecStat.MISS;
        }
        var ospf_daemon = cur_isis_daemon;
        /*METRICSTYLE,
        ADVERTISEHIGHMETRIC,
        SETOVERLOADBIT,
        SETOVERLOADBITONSTARTUP,
        LSPMTU, */
        switch (op.Type()){
            case METRICSTYLE -> {
                assert ISISDaemon.metricstyle.of(op.getNAME()).isPresent() : "metric style name not right %s".formatted(op.getNAME());
                ospf_daemon.setMetricStyle(ISISDaemon.metricstyle.of(op.getNAME()).get());
                return ExecStat.SUCC;
            }

            case LSPMTU -> {
                ospf_daemon.setLspmtu(op.getNUM());
                return ExecStat.SUCC;
            }

            case ADVERTISEHIGHMETRIC -> {
                ospf_daemon.setAdvertisehighmetrics(true);
                return ExecStat.SUCC;
            }

            case SETOVERLOADBIT -> {
                ospf_daemon.setSetoverloadbit(true);
                return ExecStat.SUCC;
            }

            case SETOVERLOADBITONSTARTUP -> {
                ospf_daemon.setOverloadbitonstartup(op.getNUM());
                return ExecStat.SUCC;
            }
        }
        assert false:String.format("should not go to here %s", op.toString());
        return ExecStat.FAIL;
    }

    // private ISISAreaSum getAreaSum(@NotNull ID area, @NotNull ConfGraph_ISIS topo){
    //     var res = topo.<ISISAreaSum>getOrCreateNode(NodeGen_ISIS.getISISAreaSumName(cur_isis.getName(), NodeGen_ISIS.getAreaName(area)), NodeType_ISIS.ISISAreaSum);
    //     if (!res.second()){
    //         res.first().setArea(area);
    //         topo.addISISAreaSumRelation(res.first().getName(), cur_isis.getName());
    //     }
    //     return res.first();
    // }

    // private ISISAreaSum.OSPFAreaSumEntry getAreaSumEntry(@NotNull ISISAreaSum areaSum, IPRange range){
    //     return Optional.ofNullable(areaSum.getSumEntries().get(range.toString()))
    //             .orElseGet(() -> {
    //                 var entry = new ISISAreaSum.OSPFAreaSumEntry();
    //                 entry.setRange(range);
    //                 areaSum.getSumEntries().put(range.toString(), entry);
    //                 return entry;
    //             });
    // }

//     public ExecStat execOSPFAreaCmds(@NotNull Op_ISIS op, @NotNull ConfGraph_ISIS topo) {
//         if (cur_router == null || cur_isis == null) return ExecStat.MISS;
//         //FIXME num range should be deal before this
//         var cur_rname = cur_router.getName();
//         boolean is_ABR = topo.getISISIntfOfRouter(cur_rname)
//                 .stream().anyMatch(x -> x.getArea().IDtoLong() == 0);
//         if (op.Type().ordinal() >= OpType.AreaRange.ordinal() && op.Type().ordinal() <= OpType.AreaRangeCost.ordinal()) {
//             if (!is_ABR) {
//                 return ExecStat.MISS;
//             }
//             var areaSum = getAreaSum(op.getID(), topo);
//             var areaSumEntry = getAreaSumEntry(areaSum, op.getIPRANGE());
//             areaSumEntry.setAdvertise(true);
//             switch (op.Type()) {
//                 case AreaRange -> {
//                 }
// //                case AreaRangeAd -> {
// //                    areaSumEntry.setAdvertise(true);
// //                }
//                 case AreaRangeCost -> {
//                     areaSumEntry.setCost(op.getNUM());
//                 }
// //                case AreaRangeAdCost -> {
// //                    areaSumEntry.setAdvertise(true);
// //                    areaSumEntry.setCost(op.getNUM());
// //                }
//                 case AreaRangeNoAd -> {
//                     areaSumEntry.setAdvertise(false);
//                 }
//                 case AreaRangeSub -> {
//                     areaSumEntry.setSubstitute(op.getIP());
//                 }
//             }
//             return ExecStat.SUCC;
//         }else{
//             //FIXME Is these commands should be used only in ABR?
//             var areaSum = getAreaSum(op.getID(), topo);
//             switch (op.Type()){
//                 //FIXME areaVLINK
// //                case AreaVLink -> {
// //                    //FIXME(VLINK) currently we don't handle this
// //                    assert false : "AreaVlink not implemented";
// //                }
//                 case AreaShortcut -> {
//                     if (op.getNAME().equals("enable")) {
//                         areaSum.setShortcut(OSPFAreaSum.shortCutType.Enable);
//                     }
//                     if (op.getNAME().equals("disable")){
//                         areaSum.setShortcut(OSPFAreaSum.shortCutType.Disable);
//                     }
//                     if (op.getNAME().equals("default")){
//                         areaSum.setShortcut(OSPFAreaSum.shortCutType.Default);
//                     }
//                 }
//                 case AreaStub -> {
//                     areaSum.setStub(true);
//                 }
//                 case AreaStubTotal -> {
//                     areaSum.setStub(true);
//                     areaSum.setNosummary(true);
//                 }
//                 case AreaNSSA -> {
//                     areaSum.setNssa(true);
//                 }
//             }
//             return ExecStat.SUCC;
//         }
//     }

    private ExecStat execOSPFOp(@NotNull Op_ISIS op, ConfGraph_ISIS topo) {
        if (op.Type().inISISRouterWithTopo()) {
            return execOSPFAttriCmds(op, topo);
        }else if (op.Type().inISISDAEMON()){
            return execOspfDaemonAttriCmds(op, topo);
        }
        // else if (op.Type().inISISREGION()){
        //     return execOSPFAreaCmds(op, topo);
        // }
        return ExecStat.MISS;
    }
    @Override
    ExecStat execOp(Op_ISIS op, ConfGraph_ISIS topo) {
        return execOSPFOp(op, topo);
    }
}
