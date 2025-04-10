package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.rip.RIP;
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
            case IPSENDVERSION1 -> {
                cur_rip_intf.setSendVersion(RIP.RIP_VTYPE.V1);
            }
            case IPSENDVERSION2 -> {
                cur_rip_intf.setSendVersion(RIP.RIP_VTYPE.V2);
            }
            case IPSENDVERSION12 -> {
                cur_rip_intf.setSendVersion(RIP.RIP_VTYPE.V12);
            }
            case IPRECVVERSION1 -> {
                cur_rip_intf.setRecvVersion(RIP.RIP_VTYPE.V1);
            }
            case IPRECVVERSION2 -> {
                cur_rip_intf.setRecvVersion(RIP.RIP_VTYPE.V2);
            }
            case IPRECVVERSION12 -> {
                cur_rip_intf.setRecvVersion(RIP.RIP_VTYPE.V12);
            }
        }
        return ExecStat.SUCC;
    }
}
