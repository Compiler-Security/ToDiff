package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.util.exec.ExecStat;
import org.jetbrains.annotations.NotNull;

public class ripIntfExecPass extends baseExecPass{

    @Override
    ExecStat execOp(Op op, ConfGraph topo) {
        if (cur_rip_intf == null){
            return ExecStat.MISS;
        }
        switch (op.Type()){
            case IPSPLITHORIZION -> {
                cur_rip_intf.setSplitHorizon(false);
                cur_rip_intf.setPoison(false);
            }
            case IPSPLITPOISION -> {
                cur_rip_intf.setPoison(true);
                cur_rip_intf.setSplitHorizon(false);
            }
        }
        return ExecStat.SUCC;
    }
}
