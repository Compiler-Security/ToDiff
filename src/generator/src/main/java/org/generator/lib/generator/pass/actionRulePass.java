package org.generator.lib.generator.pass;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.reducer.semantic.OverideRedexDef;
import org.generator.lib.reducer.semantic.UnsetRedexDef;
import org.generator.util.collections.Pair;
import org.generator.util.net.ID;
import org.generator.util.net.IPRange;
import org.generator.util.ran.ranHelper;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class actionRulePass {
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
    private static void insert(OpAG opAG, OpAnalysis opA){
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
    private static OpAnalysis mutate(OpAnalysis opA){
        //TODO we can use other type of op to override
        var op = opA.getOp();
        var new_op = op.copy();
        var args =  op.getOpCtx().getFormmat().getLexDef().Args;
        var argsRange = op.getOpCtx().getFormmat().getLexDef().ArgsRange;
        var redex_def = OverideRedexDef.getRdcDef(op.Type());
        if (redex_def.targetOps.isEmpty()) return null;
        var target_opType = redex_def.targetOps.get(0);
        var equal_args = redex_def.getEqualArgs().get(0);

        if (args.isEmpty()) return null;
        for(var arg: args){
            if (!equal_args.contains(arg)){
                switch (arg){
                    case "ID" -> {new_op.setID(ranHelper.randomID());}
                    case "IPRANGE" -> {new_op.setIPRANGE(ranHelper.randomIpRange());}
                    case "IP" -> {new_op.setIP(ranHelper.randomIP());}
                    case "NUM" -> {new_op.setNUM(getRanIntNum(argsRange, "NUM"));}
                    case "NUM2" -> {new_op.setNUM2(getRanIntNum(argsRange, "NUM2"));}
                    case "NUM3" -> {new_op.setNUM3(getRanIntNum(argsRange, "NUM3"));}
                    case "LONGNUM" -> {new_op.setLONGNUM(getRanLongNum(argsRange, "LONGNUM"));}
                    case "NAME" -> {new_op.setNAME(getRanName(argsRange, "NAME"));}
                    case "NAME2" -> {new_op.setNAME(getRanName(argsRange, "NAME2"));}
                    //case "NUM" -> {new_op.setNUM(argsRange.get("NUM"));}
                    default -> {
                        assert false : "mutate TODO";
                    }
                }
            }
        }
        if (new_op.equals(op)) return null;
        assert !new_op.equals(op) : "mutate should not be same";
        var res = OpAnalysis.of(new_op);
        res.setCtxOp(opA.getCtxOp());
        System.out.printf("mutate: %s -> %s\n", opA, res);
        return res;
    }

    private static OpAnalysis broken(OpAnalysis opA){
        var new_op = opA.getOp().copy();
        var op = opA.getOp();
        new_op.setType(OpType.INVALID);
        var args =  op.getOpCtx().getFormmat().getLexDef().Args;
        var argsRange = op.getOpCtx().getFormmat().getLexDef().ArgsRange;
        if (args.isEmpty()) {
            new_op.getOpCtx().setFormmat(OpCtx.Format.of(OpType.INVALID, 0));
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
        var res = OpAnalysis.of(new_op);
        res.setCtxOp(opA.getCtxOp());
        return res;
    }

    private static OpAnalysis unset(OpAnalysis opA){
        var new_op = opA.getOp().copy();
        if (new_op.Type().isUnsetOp()) return null;
        var unset_list = UnsetRedexDef.getUnsetType(new_op.Type());
        if (unset_list.isEmpty()) return null;
        OpType unsetType;
        if (generate.ran){
            unsetType = ranHelper.randomElemOfList(unset_list);
        }
        unsetType = unset_list.get(0);
        new_op.setType(unsetType);
        new_op.getOpCtx().setFormmat(OpCtx.Format.of(unsetType, 0));
        if (generate.ran) {
            new_op.getOpCtx().setFormmat(OpCtx.Format.of(unsetType, ranHelper.randomInt(0, LexDef.getLexDefNum(unsetType) - 1)));
        }
        var res = OpAnalysis.of(new_op);
        res.setCtxOp(opA.getCtxOp());
        return res;
    }
    /**
     * we add a new  OpA to opAG considering actionType
     * @param opAG
     * @param targetOpA
     * @param actionType
     * @return
     */
    public static boolean solve(OpAG opAG, OpAnalysis targetOpA, ActionType actionType){
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
