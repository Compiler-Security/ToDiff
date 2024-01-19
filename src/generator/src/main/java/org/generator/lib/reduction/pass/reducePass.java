package org.generator.lib.reduction.pass;

import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.opg.RCtxG;
import org.generator.lib.operation.operation.OpType;
import org.generator.lib.reduction.semantic.ConflictRedexDef;
import org.generator.lib.reduction.semantic.CtxOpDef;
import org.generator.lib.reduction.semantic.OverideRedexDef;
import org.generator.lib.reduction.semantic.UnsetRedexDef;

import java.util.Arrays;
import java.util.List;

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

    boolean conflict(OpAnalysis preOpa, OpAnalysis opa){
        if (!preOpa.ctxOp.equals(opa.ctxOp)) return false;
        var odef = ConflictRedexDef.getRdcDef(opa.getOp().Type());
        for(int i = 0; i < odef.targetOps.size(); i++){
            var targetOp = odef.targetOps.get(i);
            var equalArgs = odef.equalArgs.get(i);
            if (targetOp == preOpa.op.Type() && compareByArgs(preOpa.getOp(), opa.getOp(), equalArgs)){
                return true;
            }
        }
        return false;
    }

    private boolean hasConflict(OpAnalysis opa){
        for(var preOpa:rCtxG.getOps()){
            if (preOpa.getLineNo() >= opa.getLineNo()) break;
            if (conflict(preOpa, opa)) return true;
        }
        return false;
    }

    private boolean setCtxOp(OpAnalysis opa){
        if (CtxOpDef.isCtxOpSelf(opa.op.Type())){
            //ROSPF INTFN NOROSPF ctxop is itself
            opa.setCtxOp(opa);
            return true;
        }else{
            OpAnalysis ctxOp = null;
            for(var preOpa: rCtxG.getOps()){
                if (preOpa.getLineNo() >= opa.getLineNo()) break;
                if (preOpa.state == OpAnalysis.STATE.ACTIVE){
                    //ROSPF -> OP not in intf group
                    if (preOpa.op.Type() == OpType.ROSPF && CtxOpDef.isCtxOpROSPF(opa.op.Type())){
                        ctxOp  = preOpa;
                    }
                    //INTF -> OP in intf grop
                    if (preOpa.op.Type() == OpType.IntfName && CtxOpDef.isCtxOpIntfN(opa.op.Type())){
                        ctxOp = preOpa;
                    }
                }
            }
            opa.setCtxOp(ctxOp);
            return ctxOp != null;
        }
    }

    private boolean compareByArgs(OpOspf op1, OpOspf op2, List<String> args){
        var equal = true;
        for(var arg: args){
            switch (arg){
                case "IP" -> {equal &= op1.getIP().equals(op2.getIP());}
                case "ID" -> {equal &= op1.getID().equals(op2.getID());}
                case "IPRANGE" -> {equal &= op1.getIPRANGE().equals(op2.getIPRANGE());}
                case "NAME" -> {equal &= op1.getNAME().equals(op2.getNAME());}
                case "NAME2" -> {equal &= op1.getNAME2().equals(op2.getNAME2());}
                case "NUM" -> {equal &= op1.getNUM().equals(op2.getNUM());}
                case "NUM2" -> {equal &= op1.getNUM2().equals(op2.getNUM2());}
                case "NUM3" -> {equal &= op1.getNUM3().equals(op2.getNUM3());}
                case "LONGNUM" -> {equal &= op1.getLONGNUM().equals(op2.getLONGNUM());}
                default -> {assert false: "arg not right";}
            }
        }
        return equal;
    }
    private  boolean unsetOp(OpAnalysis preOpa, OpAnalysis opa){
        //ctx_op should equal
        if (!preOpa.getCtxOp().getOp().equals(opa.getCtxOp().getOp())){
            return false;
        }
        if (opa.op.Type().isUnsetOp()){
            //current Op should be unsetOp
            var def = UnsetRedexDef.getRdcDef(opa.op.Type());

            if (Arrays.stream(def.getTargetOps()).anyMatch(typ -> typ == preOpa.getOp().Type())){
                //preOpa's type is unsetOp's target Type
                //compare all args, if equal, then unset is true, else unset is false
                return compareByArgs(preOpa.getOp(), opa.getOp(), def.getLexDef().Args);
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    private boolean unsetCtxOp(OpAnalysis preOpa, OpAnalysis opa){
        assert preOpa.getCtxOp() != null : "when preopa is active, it's ctxOp should not be null";
        return unsetOp(preOpa.getCtxOp(), opa);
    }

    private boolean override(OpAnalysis preOpa, OpAnalysis opa){
        //TODO
        //ctxOp should be same
        if (!preOpa.ctxOp.equals(opa.ctxOp)) return false;
        var odef = OverideRedexDef.getRdcDef(opa.getOp().Type());
        for(int i = 0; i < odef.targetOps.size(); i++){
            var targetOp = odef.targetOps.get(i);
            var equalArgs = odef.equalArgs.get(i);
            if (targetOp == preOpa.op.Type() && compareByArgs(preOpa.getOp(), opa.getOp(), equalArgs)){
                return true;
            }
        }
        return false;
    }
    private void handleActive(OpAnalysis opa){
        for(var preOpa: rCtxG.getOps()){
            //HANDLE OP front of opa
            if (preOpa.lineNo >= opa.lineNo) break;
            if (preOpa.state != OpAnalysis.STATE.ACTIVE) continue;
            if (unsetOp(preOpa, opa)){
                preOpa.setState(OpAnalysis.STATE.REMOVED);
                continue;
            }
            if (unsetCtxOp(preOpa, opa)){
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
        if (setCtxOp(opa) && !hasConflict(opa)){
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
