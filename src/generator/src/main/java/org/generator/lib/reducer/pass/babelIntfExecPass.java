package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.util.exec.ExecStat;

public class babelIntfExecPass extends baseExecPass{
    @Override
    ExecStat execOp(Op op, ConfGraph topo) {
        if (cur_babel_intf == null){
            return ExecStat.MISS;
        }
        switch (op.Type()){
            case BWIRE -> {
                if (op.getNAME().equals("wired")){
                    cur_babel_intf.setWired(true);
                }
                if (op.getNAME().equals("wireless")){
                    cur_babel_intf.setWired(false);
                }
            }
            case BSPLITHORIZON -> {
                cur_babel_intf.setSplitHorizon(false);
            }
            case BHELLOINTERVAL -> {
                cur_babel_intf.setHelloInterval(op.getNUM());
            }
            case BUPDATEINTERVAL -> {
                cur_babel_intf.setUpdateInterval(op.getNUM());
            }
            case BCHANELNOINTEFERING -> {
                if (op.getNAME().equals("interfering")){
                    cur_babel_intf.setNointerfering(false);
                }
                if (op.getNAME().equals("noninterfering")){
                    cur_babel_intf.setNointerfering(true);
                }
            }
            case BRXCOST -> {
                cur_babel_intf.setRxcost(op.getNUM());
            }
            case BRTTDECAY -> {
                cur_babel_intf.setRttDecay(op.getNUM());
            }
            case BRTTMIN -> {
                cur_babel_intf.setRttMin(op.getNUM());
            }
            case BRTTMAX -> {
                cur_babel_intf.setRttMax(op.getNUM());
            }
            case BPENALTY -> {
                cur_babel_intf.setPenalty(op.getNUM());
            }
            case BENABLETIMESTAMP -> {
                cur_babel_intf.setTimeStamps(true);
            }
            case IPAddr6 -> {}
            default -> {
                assert false: "no exec op %s".formatted(op);
            }
        }
        return ExecStat.SUCC;
    }
}
