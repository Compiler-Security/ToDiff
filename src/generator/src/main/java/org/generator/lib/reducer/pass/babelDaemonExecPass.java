package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.rip.RIP;
import org.generator.util.exec.ExecStat;

public class babelDaemonExecPass extends baseExecPass{

    @Override
    ExecStat execOp(Op op, ConfGraph topo) {
        if (cur_babel == null) return ExecStat.MISS;
        switch (op.Type()){
            case BRESENDDELAY -> {
                cur_babel.setResendDelay(op.getNUM());
            }
            case BSOMMOTHING -> {
                cur_babel.setSmoothing(op.getNUM());
            }
            case BREDISTRIBUTE -> {}
            default -> {assert false: "unknown commands %s".formatted((OpOspf) op);}
        }
        return ExecStat.SUCC;
    }
}
