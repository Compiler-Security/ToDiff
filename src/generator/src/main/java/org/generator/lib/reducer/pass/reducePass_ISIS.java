
package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.OpAnalysis_ISIS;
import org.generator.lib.item.IR.OpIsis;
import org.generator.lib.item.opg.OpCtxG_ISIS;
import org.generator.lib.item.opg.OpAG_ISIS;
import org.generator.lib.frontend.lexical.OpType_isis;
import org.generator.lib.reducer.semantic.*;

import java.util.List;

public class reducePass_ISIS {
    OpAG_ISIS opAG;

    //较两个 OpIsis 操作对象在指定的参数上的值是否相等。
    private static boolean compareByArgs(OpIsis op1, OpIsis op2, List<String> args){
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
                case "NET" -> {equal &= op1.getNET().equals(op2.getNET());}
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
    //根据给定的语义归约定义 def，判断 opaSource 是否与 opaTarget 匹配。
    static boolean matchByRedexDef(OpAnalysis_ISIS opaSource, OpAnalysis_ISIS opaTarget, BaseRedexDef_ISIS def){
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
    private boolean setCtxOp(OpAnalysis_ISIS opa){
        if (CtxOpDef_ISIS.isCtxOp(opa.op.Type())){
            //ROSPF INTFN NOROSPF ctxop is itself
            opa.setCtxOp(opa);
            return true;
        }else{
            OpAnalysis_ISIS ctxOp = null;
            for(var preOpa: opAG.getOps()){
                if (preOpa.getLineNo() >= opa.getLineNo()) break;
                if (preOpa.state == OpAnalysis_ISIS.STATE.ACTIVE){
                    //ROSPF -> OP not in intf group
                    if (preOpa.op.Type() == OpType_isis.RISIS){
                        if (CtxOpDef_ISIS.shouldInRISIS(opa.op.Type()))  ctxOp  = preOpa;
                        else ctxOp = null;
                    }
                    //INTF -> OP in intf grop
                    if (preOpa.op.Type() == OpType_isis.IntfName){
                        if (CtxOpDef_ISIS.shouldInIntfN(opa.op.Type())) ctxOp = preOpa;
                        else ctxOp = null;
                    }
                }
            }
            opa.setCtxOp(ctxOp);
            return ctxOp != null;
        }
    }

    private static boolean conflict(OpAnalysis_ISIS preOpa, OpAnalysis_ISIS opa){
        //ctx_op should equal except IpOspfArea vs. networkAreaId
        //if ((opa.getOp().Type() == OpType_isis.IpOspfArea && preOpa.getOp().Type() == OpType_isis.NETAREAID) || (preOpa.getOp().Type() == OpType.IpOspfArea && opa.getOp().Type() == OpType.NETAREAID)) return matchByRedexDef(opa, preOpa, ConflictRedexDef.getRdcDef(opa.getOp().Type()));
        if (!CtxOpDef_ISIS.isCtxOp(opa.op.Type()) && !preOpa.getCtxOp().getOp().equals(opa.getCtxOp().getOp())){return false;}
        return matchByRedexDef(opa, preOpa, ConflictRedexDef_ISIS.getRdcDef(opa.getOp().Type()));
    }

    public static boolean isConflict(OpAnalysis_ISIS preOpa, OpAnalysis_ISIS opa){
        return conflict(preOpa, opa);
    }
    private boolean hasConflict(OpAnalysis_ISIS opa){
        //check ROSPF IntfName conflict
        if (opa.getOp().Type() == OpType_isis.RISIS || opa.getOp().Type() == OpType_isis.IntfName){
            return false;
        }
        OpAnalysis_ISIS ctxOp = null;
        for(var preOpa: opAG.getOps()){
            if (preOpa.getLineNo() >= opa.getLineNo()) break;
            if (preOpa.state != OpAnalysis_ISIS.STATE.ACTIVE) continue;
            //COUNT CTXOP
            if (preOpa.op.Type() == OpType_isis.RISIS){
                ctxOp  = preOpa;
            }
            if (preOpa.op.Type() == OpType_isis.IntfName){
                ctxOp = preOpa;
            }

            if (conflict(preOpa, opa)) return true;
        }
        return false;
    }


    //===========Active -> REMOVED===================
    private  boolean unsetOp(OpAnalysis_ISIS preOpa, OpAnalysis_ISIS opa){
        //ctx_op should equal
        if (!CtxOpDef_ISIS.isCtxOp(opa.op.Type()) && !preOpa.getCtxOp().getOp().equals(opa.getCtxOp().getOp())){return false;}
        //opa should be unsetOp
        if (!opa.op.Type().isUnsetOp()) {return false;}
        return matchByRedexDef(opa, preOpa, UnsetRedexDef_ISIS.getRdcDef(opa.op.Type()));
    }

    private boolean unsetCtxOp(OpAnalysis_ISIS preOpa, OpAnalysis_ISIS opa){
        assert preOpa.getCtxOp() != null : "when preopa is active, it's ctxOp should not be null";
        return unsetOp(preOpa.getCtxOp(), opa);
    }

    private boolean override(OpAnalysis_ISIS preOpa, OpAnalysis_ISIS opa){
        //ctx_op should equal
        if (!CtxOpDef_ISIS.isCtxOp(opa.op.Type()) && !preOpa.getCtxOp().getOp().equals(opa.getCtxOp().getOp())){return false;}
        return matchByRedexDef(opa, preOpa, OverrideRedexDef_ISIS.getRdcDef(opa.op.Type()));
    }


    private void   handleActive(OpAnalysis_ISIS opa){
        for(var preOpa: opAG.getOps()){
            //HANDLE OP front of opa
            if (preOpa.lineNo >= opa.lineNo) break;
            if (preOpa.state != OpAnalysis_ISIS.STATE.ACTIVE) continue;
            if (unsetOp(preOpa, opa)){
                preOpa.setState(OpAnalysis_ISIS.STATE.REMOVED);
                continue;
            }
            if (unsetCtxOp(preOpa, opa)){
                preOpa.setState(OpAnalysis_ISIS.STATE.REMOVED);
                continue;
            }
            if (override(preOpa, opa)){
                preOpa.setState(OpAnalysis_ISIS.STATE.REMOVED);
                continue;
            }
        }
    }

    private void initMove(OpCtxG_ISIS opCtxG){
        int lineNo = 0;
        for(var op: opCtxG.getOps()){
            OpAnalysis_ISIS.STATE state;
            if (op.getOperation().Type() == OpType_isis.INVALID){
                state = OpAnalysis_ISIS.STATE.REMOVED;
            }else{
                state = OpAnalysis_ISIS.STATE.SUBMITTED;
            }
            opAG.addOp(OpAnalysis_ISIS.of((OpIsis) op.getOperation(), lineNo, state));
            lineNo += 1;
        }
    }

    private void initMove(OpAG_ISIS opAG){
        int lineNo = 0;
        for(var opa: opAG.getOps()){
            OpAnalysis_ISIS.STATE state;
            if (opa.getOp().Type() == OpType_isis.INVALID){
                state = OpAnalysis_ISIS.STATE.REMOVED;
            }else{
                state = OpAnalysis_ISIS.STATE.SUBMITTED;
            }
            opa.setCtxOp(null);
            opa.setState(state);
            opa.setLineNo(lineNo);
            lineNo += 1;
        }
    }

    private void submittedMove(OpAnalysis_ISIS opa){
        if (setCtxOp(opa) && !hasConflict(opa)){
            opa.setState(OpAnalysis_ISIS.STATE.ACTIVE);
        }else{
            opa.setState(OpAnalysis_ISIS.STATE.REMOVED);
        }
    }

    /**
     * return expanded form of opAG
     * @param opAG
     * @return
     */
    public static OpAG_ISIS expandOpAG(OpAG_ISIS opAG){
        var opAg_expand = new OpAG_ISIS();
        for(var opa: opAG.getOps()){
            if (opa.getCtxOp() != null && CtxOpDef_ISIS.isSetCtxOp(opa.getCtxOp().op.Type())){
                opAg_expand.addOp(opa.getCtxOp().copy());
            }
            opAg_expand.addOp(opa);
        }
        setOpAGLineNo(opAg_expand);
        return opAg_expand;
    }

    private static void setOpAGLineNo(OpAG_ISIS opAG){
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
    public static void normOpAG(OpAG_ISIS opAG){
        opAG.setOpgroup(opAG.getOps().stream().filter(opA -> !CtxOpDef_ISIS.isSetCtxOp(opA.op.Type())).toList());
        setOpAGLineNo(opAG);
    }

    public OpAG_ISIS solve(OpCtxG_ISIS opCtxG){
        opAG = new OpAG_ISIS();
        //this opCtxG is in expanded form
        initMove(opCtxG);
        for (var opa: opAG.getOps()){
            if (opa.getState() == OpAnalysis_ISIS.STATE.SUBMITTED){
                submittedMove(opa);
                if (opa.getState() == OpAnalysis_ISIS.STATE.ACTIVE){
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
    public void solve(OpAG_ISIS normal_opag){
        opAG = expandOpAG(normal_opag);
        initMove(opAG);
        for (var opa: opAG.getOps()){
            if (opa.getState() == OpAnalysis_ISIS.STATE.SUBMITTED){
                submittedMove(opa);
                if (opa.getState() == OpAnalysis_ISIS.STATE.ACTIVE){
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
