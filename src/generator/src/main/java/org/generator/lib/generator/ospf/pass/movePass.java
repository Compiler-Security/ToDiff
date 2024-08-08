/*
This pass will transform OpAG to OpAG' = Move(OpAG', target_opA(opAnalysis))
Input:
    OPAG
    target opA(opOspf + status)
Output:
    list of OpAG'


ATTENTION: this pass may add multiple opA to OpAG, and may change other OpA's states in the OpAG' by the rules
 */
package org.generator.lib.generator.ospf.pass;

import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.opg.OpAG;
import org.generator.util.collections.Pair;
import org.generator.util.ran.ranHelper;

import java.util.*;

public class movePass {


    /**
     * ACTIVE-> REMOVED UnsetOp | UnsetCtx
     * ACTIVE-> ACTIVE Keep
     * other DisCard
     */
    private static final Map<Pair<OpAnalysis.STATE, OpAnalysis.STATE>, applyRulePass.RuleType[]> TranstionStateMap = new HashMap<>(){{
        put(new Pair<>(OpAnalysis.STATE.ACTIVE, OpAnalysis.STATE.REMOVED), new applyRulePass.RuleType[]{applyRulePass.RuleType.UnsetOp, applyRulePass.RuleType.UnsetCtx});
        put(new Pair<>(OpAnalysis.STATE.ACTIVE, OpAnalysis.STATE.ACTIVE), new applyRulePass.RuleType[]{applyRulePass.RuleType.Keep});
        put(new Pair<>(OpAnalysis.STATE.REMOVED, OpAnalysis.STATE.ACTIVE), new applyRulePass.RuleType[]{applyRulePass.RuleType.Keep});
        put(new Pair<>(OpAnalysis.STATE.REMOVED, OpAnalysis.STATE.REMOVED), new applyRulePass.RuleType[]{applyRulePass.RuleType.SYNWrong});
    }};

    public static applyRulePass.RuleType[] getRules(OpAnalysis.STATE from, OpAnalysis.STATE to){
        return TranstionStateMap.getOrDefault(new Pair<>(from, to), new applyRulePass.RuleType[]{applyRulePass.RuleType.DisCard});
    }


    public  static List<applyRulePass.RuleType> getPossibleRules(OpAG opAG, OpAnalysis target_opa){
        var current_state = opAG.getOpAState(target_opa);
        List<applyRulePass.RuleType> possibleRules;
        return new ArrayList<>(List.of(getRules(current_state, target_opa.state)));
    }

    private  static boolean checkEqual(OpAG currentOpAG, OpAG targetOpAG){
        currentOpAG.reduce();
        targetOpAG.reduce();
        return  currentOpAG.getOpStatus().equals(targetOpAG.getOpStatus());
    }
    public static void random_insert(OpAG targetOpAG, OpAG currentOpAG, List<OpAnalysis> remainOps){
        var move_op = remainOps.removeFirst();

        var totalOpAG = currentOpAG.copy();
        totalOpAG.addOps(remainOps);

        var r = currentOpAG.getOps().size();
        var move_max = r;
        var l = -1;
        var mid = r;
        while(l + 1 < r){
            mid = (l + r) / 2;
            //contact opAG
            totalOpAG.getOps().add(mid, move_op);
            if (checkEqual(totalOpAG, targetOpAG)){
                r = mid;
            }else{
                l = mid + 1;
            }
            totalOpAG.getOps().remove(mid);
        }

        var move_min = r;
        //System.out.printf("%d %d\n", move_min, move_max);
        //can move index range is [move_min, move_max]
        //random move
        currentOpAG.getOps().add(ranHelper.randomInt(move_min, move_max), move_op);
    }

    public static List<OpAnalysis> getNewOpas(OpAG targetOpAG, OpAG oriOpAG){
        return new ArrayList<>(targetOpAG.getOps().subList(oriOpAG.getOps().size(), targetOpAG.getOps().size()));
    }
    public static OpAG random_inserts(OpAG targetOpAG, OpAG oriOpAG){
        //get All the new generate Op
        var newOpAs = new ArrayList<>(targetOpAG.getOps().subList(oriOpAG.getOps().size(), targetOpAG.getOps().size()));
        var opAG = oriOpAG.copy();
        //We only use random insert if we should insert >=2 instructions
        if (newOpAs.size() > 1) {
            while (!newOpAs.isEmpty()) {
                random_insert(targetOpAG, opAG, newOpAs);
            }
            opAG.reduce();
            return opAG;
        }else return targetOpAG;
    }
    /**
     * move one step given by target_opa, don't change opAG
     * @param opAG
     * @param target_opa
     * @param allowed_ruleType null, or List contains allowed_ruleType
     * @return opAG_new, null if move fail
     */
    public static  Pair<List<OpAnalysis>, OpAG> solve(OpAG opAG, OpAnalysis target_opa, applyRulePass.RuleType possibleRule){
        /*
        TDOO For simplicity we only use dfs and currently not build condition graph
        */
        var opAG_new = applyRulePass.solve(opAG, target_opa, possibleRule);
        if (opAG_new == null) return  new Pair<>(new ArrayList<>(), null);
        return new Pair<>(getNewOpas(opAG_new, opAG), opAG_new);
    }
}
