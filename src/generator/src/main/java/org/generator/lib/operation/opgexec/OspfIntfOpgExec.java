package org.generator.lib.operation.opgexec;

import org.generator.lib.operation.operation.Op;
import org.generator.lib.topo.node.ospf.OSPFIntf;
import org.generator.lib.topo.graph.RelationGraph;
import org.generator.util.exec.ExecStat;
import org.jetbrains.annotations.NotNull;

public class OspfIntfOpgExec extends OpgExec{

    private ExecStat execOSPFIntfCmds(@NotNull Op op, @NotNull RelationGraph topo){
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
                cur_ospf_intf.setHelloMulti(op.getNUM());
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
    @Override
    ExecStat execOp(Op op, RelationGraph topo) {
        return execOSPFIntfCmds(op, topo);
    }
}
