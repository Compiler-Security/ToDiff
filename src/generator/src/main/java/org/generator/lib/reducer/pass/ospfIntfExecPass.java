package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op;
import org.generator.lib.item.topo.graph.ConfGraph;
import org.generator.lib.item.topo.node.ospf.OSPFIntf;
import org.generator.util.exec.ExecStat;
import org.jetbrains.annotations.NotNull;

public class ospfIntfExecPass extends baseExecPass {

    private ExecStat execOSPFIntfCmds(@NotNull Op op, @NotNull ConfGraph topo){
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
    ExecStat execOp(Op op, ConfGraph topo) {
        return execOSPFIntfCmds(op, topo);
    }

}
