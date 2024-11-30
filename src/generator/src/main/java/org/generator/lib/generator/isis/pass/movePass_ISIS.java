/*
This pass will transform OpAG to OpAG' = Move(OpAG', target_opA(OpAnalysis_ISIS))
Input:
    OPAG
    target opA(opOspf + status)
Output:
    list of OpAG'


ATTENTION: this pass may add multiple opA to OpAG, and may change other OpA's states in the OpAG' by the rules
 */
package org.generator.lib.generator.isis.pass;

import org.generator.lib.item.IR.OpAnalysis_ISIS;
import org.generator.lib.item.opg.OpAG_ISIS;
import org.generator.util.collections.Pair;
import org.generator.util.ran.ranHelper;

import java.util.*;

public class movePass_ISIS {


    /**
     * ACTIVE-> REMOVED UnsetOp | UnsetCtx
     * ACTIVE-> ACTIVE Keep
     * other DisCard
     */
    private static final Map<Pair<OpAnalysis_ISIS.STATE, OpAnalysis_ISIS.STATE>, applyRulePass_ISIS.RuleType[]> TranstionStateMap = new HashMap<>(){{
        put(new Pair<>(OpAnalysis_ISIS.STATE.ACTIVE, OpAnalysis_ISIS.STATE.REMOVED), new applyRulePass_ISIS().RuleType[]{applyRulePass.RuleType.UnsetOp, applyRulePass.RuleType.UnsetCtx}); //FIXME if we want override, we should insert some mutate ops(final state is dead)
        put(new Pair<>(OpAnalysis_ISIS.STATE.ACTIVE, OpAnalysis_ISIS.STATE.ACTIVE), new applyRulePass_ISIS.RuleType[]{applyRulePass.RuleType.Keep});
        put(new Pair<>(OpAnalysis_ISIS.STATE.REMOVED, OpAnalysis_ISIS.STATE.ACTIVE), new applyRulePass_ISIS.RuleType[]{applyRulePass.RuleType.Keep}); //FIXME if we want the op(final state is dead) to be active, we should use SolveConflict instead
        put(new Pair<>(OpAnalysis_ISIS.STATE.REMOVED, OpAnalysis_ISIS.STATE.REMOVED), new applyRulePass.RuleType[]{applyRulePass.RuleType.SYNWrong});
    }};

    public static applyRulePass.RuleType[] getRules(OpAnalysis_ISIS.STATE from, OpAnalysis_ISIS.STATE to){
        return TranstionStateMap.getOrDefault(new Pair<>(from, to), new applyRulePass.RuleType[]{applyRulePass.RuleType.DisCard});
    }


    public  static List<applyRulePass.RuleType> getPossibleRules(OpAG_ISIS opAG, OpAnalysis_ISIS target_opa){
        var current_state = opAG.getOpAState(target_opa);
        List<applyRulePass.RuleType> possibleRules;
        return new ArrayList<>(List.of(getRules(current_state, target_opa.state)));
    }

    private  static boolean checkEqual(OpAG_ISIS currentOpAG, OpAG_ISIS targetOpAG){
        currentOpAG.reduce();
        targetOpAG.reduce();
        return  currentOpAG.getOpStatus().equals(targetOpAG.getOpStatus());
    }
    public static void random_insert(OpAG_ISIS targetOpAG, OpAG_ISIS currentOpAG, List<OpAnalysis_ISIS> remainOps){
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

    public static List<OpAnalysis_ISIS> getNewOpas(OpAG_ISIS targetOpAG, OpAG_ISIS oriOpAG){
        return new ArrayList<>(targetOpAG.getOps().subList(oriOpAG.getOps().size(), targetOpAG.getOps().size()));
    }
    public static OpAG_ISIS random_inserts(OpAG_ISIS targetOpAG, OpAG_ISIS oriOpAG){
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
    public static  Pair<List<OpAnalysis_ISIS>, OpAG_ISIS> solve(OpAG_ISIS opAG, OpAnalysis_ISIS target_opa, applyRulePass.RuleType possibleRule){
        /*
        TDOO For simplicity we only use dfs and currently not build condition graph
        */
        var opAG_new = applyRulePass_ISIS.solve(opAG, target_opa, possibleRule);
        if (opAG_new == null) return  new Pair<>(new ArrayList<>(), null);
        return new Pair<>(getNewOpas(opAG_new, opAG), opAG_new);
    }
}
