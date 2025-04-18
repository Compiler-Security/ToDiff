package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.util.exec.ExecStat;
import org.jetbrains.annotations.NotNull;

public class openfabricIntfExecPass extends baseExecPass {

    private ExecStat execOpenFabricIntfCmds(@NotNull Op op, @NotNull ConfGraph topo){
        if (cur_openfabric_intf == null){
            return ExecStat.MISS;
        }

        switch (op.Type()){
            case IPROUTERFABRIC ->{
                cur_openfabric_intf.setIproutefabric(true);
            }
            
            case FABRICCSNPINTERVAL ->{
                cur_openfabric_intf.setCsnpInterval(op.getNUM());
                 
            }

            case FABRICPSNPINTERVAL ->{
                cur_openfabric_intf.setPsnpInterval(op.getNUM());
            }

            case FABRICHELLOINTERVAL ->{
                cur_openfabric_intf.setHelloInterval(op.getNUM());
            }

            case FABRICHELLOMULTIPLIER ->{
                cur_openfabric_intf.setHelloMultiplier(op.getNUM());
            }

            case FABRICPASSIVE ->{
                cur_openfabric_intf.setPassive(true);
            }

        }
        return ExecStat.SUCC;
    }
    @Override
    ExecStat execOp(Op op, ConfGraph topo) {
        return execOpenFabricIntfCmds(op, topo);
    }

}
