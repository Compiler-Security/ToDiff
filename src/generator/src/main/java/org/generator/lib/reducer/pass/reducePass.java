
package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.reducer.semantic.*;

import java.util.List;

public class reducePass {
    OpAG opAG;


    private static boolean compareByArgs(OpOspf op1, OpOspf op2, List<String> args){
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

    /**
     * opaSource match opaTarget by BaseRedexDef
     * @param opaSource
     * @param opaTarget
     * @param def
     * @return
     */
    static boolean matchByRedexDef(OpAnalysis opaSource, OpAnalysis opaTarget, BaseRedexDef def){
        for(int i = 0; i < def.targetOps.size(); i++){
            var targetOp = def.targetOps.get(i);
            var equalArgs = def.equalArgs.get(i);
            if (targetOp == opaTarget.op.Type() && compareByArgs(opaSource.getOp(), opaTarget.getOp(), equalArgs)){
                return true;
            }
        }
        return false;
    }

    //=========INIT -> REMOVED/ACTIVE==============
    private boolean setCtxOp(OpAnalysis opa){
        if (CtxOpDef.isCtxOp(opa.op.Type())){
            //ROSPF INTFN NOROSPF ctxop is itself
            opa.setCtxOp(opa);
            return true;
        }else{
            OpAnalysis ctxOp = null;
            for(var preOpa: opAG.getOps()){
                if (preOpa.getLineNo() >= opa.getLineNo()) break;
                if (preOpa.state == OpAnalysis.STATE.ACTIVE){
                    //ROSPF -> OP not in intf group
                    if (preOpa.op.Type() == OpType.ROSPF){
                        if (CtxOpDef.shouldInROSPF(opa.op.Type()))  ctxOp  = preOpa;
                        else ctxOp = null;
                    }
                    //INTF -> OP in intf grop
                    if (preOpa.op.Type() == OpType.IntfName){
                        if (CtxOpDef.shouldInIntfN(opa.op.Type())) ctxOp = preOpa;
                        else ctxOp = null;
                    }
                }
            }
            opa.setCtxOp(ctxOp);
            return ctxOp != null;
        }
    }

    private static boolean conflict(OpAnalysis preOpa, OpAnalysis opa){
        //ctx_op should equal except IpOspfArea
        if (opa.getOp().Type() == OpType.IpOspfArea || opa.getOp().Type() == OpType.NETAREAID) return matchByRedexDef(opa, preOpa, ConflictRedexDef.getRdcDef(opa.getOp().Type()));
        if (!CtxOpDef.isCtxOp(opa.op.Type()) && !preOpa.getCtxOp().getOp().equals(opa.getCtxOp().getOp())){return false;}
        return matchByRedexDef(opa, preOpa, ConflictRedexDef.getRdcDef(opa.getOp().Type()));
    }

    public static boolean isConflict(OpAnalysis preOpa, OpAnalysis opa){
        return conflict(preOpa, opa);
    }
    private boolean hasConflict(OpAnalysis opa){
        //check ROSPF IntfName conflict
        if (opa.getOp().Type() == OpType.ROSPF || opa.getOp().Type() == OpType.IntfName){
            return false;
        }
        OpAnalysis ctxOp = null;
        for(var preOpa: opAG.getOps()){
            if (preOpa.getLineNo() >= opa.getLineNo()) break;
            if (preOpa.state != OpAnalysis.STATE.ACTIVE) continue;
            //COUNT CTXOP
            if (preOpa.op.Type() == OpType.ROSPF){
                ctxOp  = preOpa;
            }
            if (preOpa.op.Type() == OpType.IntfName){
                ctxOp = preOpa;
            }

            if (conflict(preOpa, opa)) return true;
        }
        return false;
    }


    //===========Active -> REMOVED===================
    private  boolean unsetOp(OpAnalysis preOpa, OpAnalysis opa){
        //ctx_op should equal
        if (!CtxOpDef.isCtxOp(opa.op.Type()) && !preOpa.getCtxOp().getOp().equals(opa.getCtxOp().getOp())){return false;}
        //opa should be unsetOp
        if (!opa.op.Type().isUnsetOp()) {return false;}
        return matchByRedexDef(opa, preOpa, UnsetRedexDef.getRdcDef(opa.op.Type()));
    }

    private boolean unsetCtxOp(OpAnalysis preOpa, OpAnalysis opa){
        assert preOpa.getCtxOp() != null : "when preopa is active, it's ctxOp should not be null";
        return unsetOp(preOpa.getCtxOp(), opa);
    }

    private boolean override(OpAnalysis preOpa, OpAnalysis opa){
        //ctx_op should equal
        if (!CtxOpDef.isCtxOp(opa.op.Type()) && !preOpa.getCtxOp().getOp().equals(opa.getCtxOp().getOp())){return false;}
        return matchByRedexDef(opa, preOpa, OverideRedexDef.getRdcDef(opa.op.Type()));
    }


    private void handleActive(OpAnalysis opa){
        for(var preOpa: opAG.getOps()){
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

    private void initMove(OpCtxG opCtxG){
        int lineNo = 0;
        for(var op: opCtxG.getOps()){
            OpAnalysis.STATE state;
            if (op.getOperation().Type() == OpType.INVALID){
                state = OpAnalysis.STATE.REMOVED;
            }else{
                state = OpAnalysis.STATE.SUBMITTED;
            }
            opAG.addOp(OpAnalysis.of((OpOspf) op.getOperation(), lineNo, state));
            lineNo += 1;
        }
    }

    private void initMove(OpAG opAG){
        int lineNo = 0;
        for(var opa: opAG.getOps()){
            OpAnalysis.STATE state;
            if (opa.getOp().Type() == OpType.INVALID){
                state = OpAnalysis.STATE.REMOVED;
            }else{
                state = OpAnalysis.STATE.SUBMITTED;
            }
            opa.setCtxOp(null);
            opa.setState(state);
            opa.setLineNo(lineNo);
            lineNo += 1;
        }
    }

    private void submittedMove(OpAnalysis opa){
        if (setCtxOp(opa) && !hasConflict(opa)){
            opa.setState(OpAnalysis.STATE.ACTIVE);
        }else{
            opa.setState(OpAnalysis.STATE.REMOVED);
        }
    }

    /**
     * return expanded form of opAG
     * @param opAG
     * @return
     */
    public static OpAG expandOpAG(OpAG opAG){
        var opAg_expand = new OpAG();
        for(var opa: opAG.getOps()){
            if (opa.getCtxOp() != null && CtxOpDef.isSetCtxOp(opa.getCtxOp().op.Type())){
                opAg_expand.addOp(opa.getCtxOp().copy());
            }
            opAg_expand.addOp(opa);
        }
        setOpAGLineNo(opAg_expand);
        return opAg_expand;
    }

    private static void setOpAGLineNo(OpAG opAG){
        int i = 0;
        for(var opa: opAG.getOps()){
            opa.setLineNo(i++);
        }
    }
    /**
     * This function will change OpAG to the normal form
     * normal form don't have ROSPF && INTFNAME
     * @param opAG
     * @return
     */
    public static void normOpAG(OpAG opAG){
        opAG.setOpgroup(opAG.getOps().stream().filter(opA -> !CtxOpDef.isSetCtxOp(opA.op.Type())).toList());
        setOpAGLineNo(opAG);
    }

    public OpAG solve(OpCtxG opCtxG){
        opAG = new OpAG();
        //this opCtxG is in expanded form
        initMove(opCtxG);
        for (var opa: opAG.getOps()){
            if (opa.getState() == OpAnalysis.STATE.SUBMITTED){
                submittedMove(opa);
                if (opa.getState() == OpAnalysis.STATE.ACTIVE){
                    handleActive(opa);
                }
            }
        }
        normOpAG(opAG);
        return opAG;
    }

    /**
     * calculate opA's state, should given normal opAG
     * @param normal_opag normal opAG
     */
    public void solve(OpAG normal_opag){
        opAG = expandOpAG(normal_opag);
        initMove(opAG);
        for (var opa: opAG.getOps()){
            if (opa.getState() == OpAnalysis.STATE.SUBMITTED){
                submittedMove(opa);
                if (opa.getState() == OpAnalysis.STATE.ACTIVE){
                    handleActive(opa);
                }
            }
        }
        normOpAG(opAG);
        int i = 0;
        for(var opa: opAG.getOps()){
            var opa_set = normal_opag.getOps().get(i++);
            opa_set.setState(opa.state);
            opa_set.setCtxOp(opa.getCtxOp());
            opa_set.setLineNo(opa.lineNo);
        }
    }
}
