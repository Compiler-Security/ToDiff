package org.generator.lib.generator.pass;

import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.operation.operation.OpType;
import org.generator.lib.reducer.semantic.UnsetRedexDef;
import org.generator.util.net.ID;
import org.generator.util.net.IPRange;

public class actionRulePass {
    public enum ActionType{
        COPY, //copy given op
        MUTATE, //mutate given op (change args)
        BREAK, //mutate given op to syntax wrong
        UNSET, //unset given op
        CLEAR, //discard given op
    }

    /**
     * it will insert OpA at random place to OpAG to ensure that
     * Reduce(OpAG + OpA) = Reduce(OpAG apply OpA)
     * `+` means add OpA in the end of the OpAG
     */
    private static void insert(OpAG opAG, OpAnalysis opA){
        //FIXME for simplicity currently we can only add opAs in the end of OpAG
        opAG.addOp(opA.copy());
        opAG.reduce();
    }

    /**
     * We change OpA's
     * @param opA
     */
    private static OpAnalysis mutate(OpAnalysis opA){
        var op = opA.getOp();
        var new_op = op.copy();
        for(var arg: op.getOpCtx().getFormmat().getLexDef().Args){
            //TODO we should choose random args to mutate
            //TODO random mutate
            switch (arg){
                case "ID" -> {new_op.setID(ID.of(0));}
                case "IPRANGE" -> {new_op.setIPRANGE(IPRange.of(5, 3));}
                default -> {
                    assert false : "mutate TODO";
                }//TODO
            }
        }
        return OpAnalysis.of(new_op);
    }

    private static OpAnalysis broken(OpAnalysis opA){
        //TODO we should broken the command accroding to opA
        var new_op = opA.getOp().copy();
        new_op.setType(OpType.INVALID);
        for(var arg: new_op.getOpCtx().getFormmat().getLexDef().Args) {
            new_op.getOpCtx().getFormmat().addByPass(arg, "XXXX");
            new_op.getOpCtx().getFormmat().addByPass(arg, "XXXX");
        }
        return OpAnalysis.of(new_op);
    }

    private static OpAnalysis unset(OpAnalysis opA){
        var new_op = opA.getOp().copy();
        if (new_op.Type().isUnsetOp()) return null;
        var unset_list = UnsetRedexDef.getUnsetType(new_op.Type());
        if (unset_list.isEmpty()) return null;
        //TODO we can unset it choose different unset command and format idx
        var unsetType = unset_list.get(0);
        new_op.setType(unsetType);
        new_op.getOpCtx().setFormmat(OpCtx.Format.of(unsetType, 0));
        return OpAnalysis.of(new_op);
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
                insert(opAG, mutate(targetOpA));
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
            }
            case CLEAR -> {
                //DO NOTHING
            }
        }
        return true;
    }
}
