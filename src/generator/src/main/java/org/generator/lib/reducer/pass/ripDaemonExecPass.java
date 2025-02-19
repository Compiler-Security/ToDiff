package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.rip.RIP;
import org.generator.util.exec.ExecStat;

public class ripDaemonExecPass extends baseExecPass{

    @Override
    ExecStat execOp(Op op, ConfGraph topo) {
        if (cur_rip == null) return ExecStat.MISS;
        switch (op.Type()){
            case NEIGHBOR -> {
                cur_rip.addNeighbor(op.getID());
            }
            case VERSION -> {
                var version = op.getNUM();
                switch (version){
                    case 1 -> {cur_rip.setVersion(RIP.RIP_VTYPE.V1);}
                    case 2 -> {cur_rip.setVersion(RIP.RIP_VTYPE.V2);}
                    default -> {return ExecStat.MISS;}
                }
            }
            case DEFAULTMETRIC -> {
                cur_rip.setMetric(op.getNUM());
            }
            case DISTANCE -> {
                cur_rip.setDistance(op.getNUM());
            }
            case TIMERSBASIC -> {
                cur_rip.setUpdate(op.getNUM());
                cur_rip.setTimeout(op.getNUM2());
                cur_rip.setGarbage(op.getNUM3());
            }
            case PASSIVEINTFDEFAULT -> {
                for(var rip_intf:topo.getRIPIntfOfRouter(cur_router.getName())){
                    rip_intf.setPassive(true);
                }
            }
            case PASSIVEINTFNAME -> {
                var intf_name = op.getNAME();
                var rip_intf_name = NodeGen.getRIPIntfName(intf_name);
                if (topo.containsNode(rip_intf_name)){
                    topo.getRIPIntf(rip_intf_name).setPassive(true);
                }
            }
            default -> {assert false: "unknown commands %s".formatted((OpOspf) op);}
        }
        return ExecStat.SUCC;
    }
}
