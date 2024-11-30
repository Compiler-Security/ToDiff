package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op_ISIS;
import org.generator.lib.item.conf.graph.ConfGraph_ISIS;
import org.generator.lib.item.conf.node.isis.ISISIntf;
import org.generator.util.exec.ExecStat;
import org.jetbrains.annotations.NotNull;

public class isisIntfExecPass extends baseExecPass_ISIS {

    private ExecStat execISISIntfCmds(@NotNull Op_ISIS op, @NotNull ConfGraph_ISIS topo){
        if (cur_isis_intf == null){
            return ExecStat.MISS;
        }
        switch (op.Type()){
            case ISISPRIORITY ->{
                cur_isis_intf.setPriorityLevel1(op.getNUM());
            }
            // case IpIsisCost -> {
            //     cur_isis_intf.setCost(op.getNUM());
            // }
            // case IpOspfDeadInter -> {
            //     cur_isis_intf.setDeadInterval(op.getNUM());
            // }
            // case IpOspfDeadInterMulti -> {
            //     cur_isis_intf.setHelloMulti(op.getNUM());
            //     cur_isis_intf.setHelloInterval(0);
            //     cur_isis_intf.setDeadInterval(1);
            // }
            // case IpOspfHelloInter -> {
            //     if (cur_isis_intf.getHelloMulti()  == 1) {
            //         cur_isis_intf.setHelloInterval(op.getNUM());
            //     }
            // }
            // case IpOspfGRHelloDelay -> {
            //     cur_isis_intf.setGRHelloDelay(op.getNUM());
            // }
            // case IpOspfNet -> {
            //     return OSPFIntf.OSPFNetType.of(op.getNAME())
            //             .map(x -> {cur_isis_intf.setNetType(x); return ExecStat.SUCC;})
            //             .orElse(ExecStat.MISS);
            // }
            // case IpOspfPriority -> {
            //     cur_isis_intf.setPriority(op.getNUM());
            // }
            // case IpOspfRetransInter -> {
            //     cur_isis_intf.setRetansInter(op.getNUM());
            // }
            // case IpOspfTransDelay -> {
            //     cur_isis_intf.setTransDelay(op.getNUM());
            // }
            // case IpOspfPassive -> {
            //     cur_isis_intf.setPassive(true);
            // }
        }
        return ExecStat.SUCC;
    }
    @Override
    ExecStat execOp(Op_ISIS op, ConfGraph_ISIS topo) {
        return execISISIntfCmds(op, topo);
    }

}
