package org.generator.lib.reduction.pass;

import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.opg.RCtxG;
import org.generator.lib.operation.operation.OpType;

import java.awt.desktop.AboutEvent;

public class reducePass {
    RCtxG rCtxG;

    private void initMove(OpCtxG opCtxG){
        int lineNo = 0;
        for(var op: opCtxG.getOps()){
            OpAnalysis.STATE state;
            if (op.getOperation().Type() == OpType.INVALID){
                state = OpAnalysis.STATE.REMOVED;
            }else{
                state = OpAnalysis.STATE.SUBMITTED;
            }
            rCtxG.addOp(OpAnalysis.of((OpOspf) op.getOperation(), lineNo, state));
        }
    }

    private boolean hasConflict(OpAnalysis opa){
        //TODO
        return false;
    }

    private boolean setCtxOp(OpAnalysis opa){
        //TODO
        return true;
    }

    private  boolean unsetOp(OpAnalysis preOpa, OpAnalysis opa){
        //TODO
        return false;
    }

    private boolean unsetCtx(OpAnalysis preOpa, OpAnalysis opa){
        //TODO
        return false;
    }

    private boolean override(OpAnalysis preOpa, OpAnalysis opa){
        //TODO
        return false;
    }
    private void handleActive(OpAnalysis opa){
        for(var preOpa: rCtxG.getOps()){
            //HANDLE OP front of opa
            if (preOpa.lineNo >= opa.lineNo) break;
            if (unsetOp(preOpa, opa)){
                preOpa.setState(OpAnalysis.STATE.REMOVED);
                continue;
            }
            if (unsetCtx(preOpa, opa)){
                preOpa.setState(OpAnalysis.STATE.REMOVED);
                continue;
            }
            if (override(preOpa, opa)){
                preOpa.setState(OpAnalysis.STATE.REMOVED);
                continue;
            }
        }
    }

    private void submittedMove(OpAnalysis opa){
        if (!hasConflict(opa) && setCtxOp(opa)){
            opa.setState(OpAnalysis.STATE.ACTIVE);
        }else{
            opa.setState(OpAnalysis.STATE.REMOVED);
        }
    }
    public RCtxG resolve(OpCtxG opCtxG){
        rCtxG = new RCtxG();
        initMove(opCtxG);
        for (var opa: rCtxG.getOps()){
            if (opa.getState() == OpAnalysis.STATE.SUBMITTED){
                submittedMove(opa);
                if (opa.getState() == OpAnalysis.STATE.ACTIVE){
                    handleActive(opa);
                }
            }
        }
        return rCtxG;
    }
}
