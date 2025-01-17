package org.generator.lib.generator.isis.pass;

import org.generator.lib.generator.driver.generate_ISIS;
import org.generator.lib.item.IR.OpAnalysis_ISIS;
import org.generator.lib.item.IR.OpCtx_ISIS;
import org.generator.lib.item.opg.OpAG_ISIS;
import org.generator.lib.frontend.lexical.OpType_isis;
import org.generator.lib.reducer.semantic.OverrideRedexDef_ISIS;
import org.generator.lib.reducer.semantic.UnsetRedexDef_ISIS;
import org.generator.util.collections.Pair;
import org.generator.util.ran.ranHelper;

import java.util.*;

public class actionRulePass_ISIS {
    public enum ActionType{
        COPY, //copy given op
        MUTATE, //mutate given op (change args)
        BREAK, //mutate given op to syntax wrong
        UNSET, //unset given op
        NoCtx, //given op not have ctxOp
        Discard //discard given op
    }

    /**
     * it will insert OpA at random place to OpAG to ensure that
     * Reduce(OpAG + OpA) = Reduce(OpAG apply OpA)
     * `+` means add OpA in the end of the OpAG
     */
    private static void insert(OpAG_ISIS opAG, OpAnalysis_ISIS opA){
        //TODO for simplicity currently we can only add opAs in the end of OpAG
        opAG.addOp(opA.copy());
        opAG.reduce();
    }


    private static  int getRanIntNum(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof Pair<?,?> p){
            return ranHelper.randomInt((int)((Long) p.first() + 0), (int) ((Long) p.second() + 0));
        }else return ranHelper.randomInt(0, 2100000000);
    }

    private static  int getRanNoIntNum(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof Pair<?,?> p) {
            if (ranHelper.randomInt(0, 1) == 0) {
                return ranHelper.randomInt(-2100000000, (int) ((Long) p.first() - 1));
            } else return ranHelper.randomInt((int) ((Long) p.second() + 1), 2100000000);
        }else return ranHelper.randomInt(0, 2100000000);
    }

    private static long getRanLongNum(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof Pair<?,?> p){
            return ranHelper.randomLong((long) p.first(), (long) p.second());
        }else return ranHelper.randomLong(0, 0xfffffffffL);
    }

    private static long getRanNoLongNum(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof Pair<?,?> p){
            if (ranHelper.randomInt(0, 1) == 0) {
                return ranHelper.randomLong(-210000000000L, (long) p.first() - 1);
            } else return ranHelper.randomLong((long) p.second() + 1, 5L * (long)p.second());
        }else return ranHelper.randomLong(0, 0xfffffffffL);
    }

    private static String getRanName(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof List<?> p && !p.isEmpty()){
            return (String) p.get(ranHelper.randomInt(0, p.size() - 1));
        }else return ranHelper.randomStr();
    }

    private static String getRanNoName(Map<String, Object> argRange, String field){
        return ranHelper.randomStr();
    }

    private static  String getRanNoIp(){
        //TODO we can do it better
        StringBuilder b = new StringBuilder();
        b.append(ranHelper.randomInt(-500, -1));
        for(int i = 0; i < 3; i++){
            b.append('.');
            b.append(ranHelper.randomInt(-500, -1));
        }
        return b.toString();
    }

    /**
     * We change OpA's
     * @param opA
     */
    public static OpAnalysis_ISIS mutate(OpAnalysis_ISIS opA){
        //first choose one override_op type
        var override_list = OverrideRedexDef_ISIS.getOverrideType(opA.getOp().Type());
        if (override_list.isEmpty()){
            return OpAnalysis_ISIS.of(genOpPass_ISIS.genRanOpOfType(opA.getOp().Type()).getOpIsis(), opA.getCtxOp());
        }
        if (generate_ISIS.ran){
            Collections.shuffle(override_list);
        }
        // var priority_op = genOpPass_ISIS.genRanOpOfType(OpType_isis.ISISPRIORITY);
        // priority_op.getOperation().setNUM(14);
        // priority_op.getOperation().setNAME("level-2");
        for(var overrideType: override_list){
            //second generate unset_op
            var new_op = genOpPass_ISIS.genRanOpOfType(overrideType);
            // if (overrideType == OpType_isis.ISISPRIORITY){
            //     new_op = priority_op;
            // }
            
            //third make unset_equal fields to be the same
            if(overrideType == OpType_isis.IPAddr||overrideType == OpType_isis.PSNPINTERVAL||overrideType == OpType_isis.CSNPINTERVAL||overrideType == OpType_isis.HELLOINTERVAL||overrideType == OpType_isis.HELLOMULTIPLIER||overrideType == OpType_isis.ISISPRIORITY||overrideType == OpType_isis.LSPGENINTERVAL){
                genOpPass_ISIS.copyFileds(new_op.getOperation(), opA.getOp(), Collections.emptyList());
            }
            else{
                genOpPass_ISIS.copyFileds(new_op.getOperation(), opA.getOp(), OverrideRedexDef_ISIS.getOverrideEqualArg(opA.op.Type(), overrideType));
            }
            if(overrideType == OpType_isis.PSNPINTERVAL||overrideType == OpType_isis.CSNPINTERVAL||overrideType == OpType_isis.HELLOINTERVAL||overrideType == OpType_isis.HELLOMULTIPLIER||overrideType == OpType_isis.ISISPRIORITY||overrideType == OpType_isis.LSPGENINTERVAL){
                new_op.getOperation().setNAME(opA.getOp().getNAME());
            }
            if (new_op.getOpIsis().equals(opA.getOp())) continue;
            else {
                //System.out.printf("new_op %s\n", new_op.getOperation());
                return OpAnalysis_ISIS.of(new_op.getOpIsis(), opA.getCtxOp());
            }
        }
        return null;
    }

    private static OpAnalysis_ISIS broken(OpAnalysis_ISIS opA){
        var new_op = opA.getOp().copy();
        var op = opA.getOp();
        new_op.setType(OpType_isis.INVALID);
        var args =  op.getOpCtx().getFormmat().getLexDef().Args;
        var argsRange = op.getOpCtx().getFormmat().getLexDef().ArgsRange;
        if (args.isEmpty()) {
            new_op.getOpCtx().setFormmat(OpCtx_ISIS.Format.of(OpType_isis.INVALID, 0));
            new_op.setNAME(ranHelper.randomStr());
        }
        for(var arg: new_op.getOpCtx().getFormmat().getLexDef().Args) {
            switch (arg){
                case "ID","IPRANGE","IP" -> {new_op.getOpCtx().getFormmat().addByPass(arg, getRanNoIp());}
                case "NUM" -> {new_op.setNUM(getRanNoIntNum(argsRange, "NUM"));}
                case "NUM2" -> {new_op.setNUM2(getRanNoIntNum(argsRange, "NUM2"));}
                case "NUM3" -> {new_op.setNUM3(getRanNoIntNum(argsRange, "NUM3"));}
                case "LONGNUM" -> {new_op.setLONGNUM(getRanNoLongNum(argsRange, "LONGNUM"));}
                case "NAME" -> {new_op.setNAME(getRanNoName(argsRange, "NAME"));}
                case "NAME2" -> {new_op.setNAME(getRanNoName(argsRange, "NAME2"));}
                //case "NUM" -> {new_op.setNUM(argsRange.get("NUM"));}
                default -> {
                    assert false : "mutate TODO";
                }
            }
        }
        var res = OpAnalysis_ISIS.of(new_op);
        res.setCtxOp(opA.getCtxOp());
        return res;
    }

    public static OpAnalysis_ISIS unset(OpAnalysis_ISIS opA){
        //first choose one unset_op type
        var unset_list = UnsetRedexDef_ISIS.getUnsetType(opA.getOp().Type());
        if (unset_list.isEmpty()) return null;
        var unsetType = unset_list.get(0);
        if (generate_ISIS.ran){
            unsetType = ranHelper.randomElemOfList(unset_list);
        }
        //second generate unset_op
        var new_op = genOpPass_ISIS.genRanOpOfType(unsetType);
        //third make unset_equal fields to be the same
        genOpPass_ISIS.copyFileds(new_op.getOperation(), opA.getOp(), UnsetRedexDef_ISIS.getUnsetEqualArg(opA.op.Type(), unsetType));
        return OpAnalysis_ISIS.of(new_op.getOpIsis(), opA.getCtxOp());
    }

    /**
     * we add a new  OpA to opAG considering actionType
     * @param opAG
     * @param targetOpA
     * @param actionType
     * @return
     */
    public static boolean solve(OpAG_ISIS opAG, OpAnalysis_ISIS targetOpA, ActionType actionType){
        switch (actionType) {
            case COPY -> {
                insert(opAG, targetOpA);
                return true;
            }
            case MUTATE -> {
                var mutate_op = mutate(targetOpA);
                if (mutate_op == null) return false;
                insert(opAG, mutate_op);
                return true;
            }
            case BREAK -> {
                insert(opAG, broken(targetOpA));
                return true;
            }
            case UNSET -> {
                var unset_op = unset(targetOpA);
                if (unset_op == null) return false;
                insert(opAG, unset_op);
                return true;
            }
            case NoCtx -> {
                //TODO currently we don't handle this
                return false;
            }
            case Discard -> {
                //return false
                return true;
            }
        }
        return true;
    }
}
