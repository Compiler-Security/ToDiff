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

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.opg.OpAG;
import org.generator.util.collections.Pair;
import org.generator.util.ran.ranHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class movePass {


    /**
     * INIT,REMOVED -> REMOVED SYNWRONG, GenConf (may not ok), NoCtx (may not ok)
     * INIT,REMOVED -> ACTIVE SolveConflict
     * ACTIVE-> REMOVED UnsetOp | UnsetCtx | Overrided
     * ACTIVE-> ACTIVE Keep
     * other DisCard
     */
    private static final Map<Pair<OpAnalysis.STATE, OpAnalysis.STATE>, applyRulePass.RuleType[]> TranstionStateMap = new HashMap<>(){{
        put(new Pair<>(OpAnalysis.STATE.INIT, OpAnalysis.STATE.REMOVED), new applyRulePass.RuleType[]{applyRulePass.RuleType.SYNWrong, applyRulePass.RuleType.GenConflict, applyRulePass.RuleType.NoCtx});
        put(new Pair<>(OpAnalysis.STATE.INIT, OpAnalysis.STATE.ACTIVE), new applyRulePass.RuleType[]{applyRulePass.RuleType.SolveConflict});
        put(new Pair<>(OpAnalysis.STATE.ACTIVE, OpAnalysis.STATE.REMOVED), new applyRulePass.RuleType[]{applyRulePass.RuleType.UnsetOp, applyRulePass.RuleType.Overrided, applyRulePass.RuleType.UnsetCtx});
        put(new Pair<>(OpAnalysis.STATE.ACTIVE, OpAnalysis.STATE.ACTIVE), new applyRulePass.RuleType[]{applyRulePass.RuleType.Keep});
        put(new Pair<>(OpAnalysis.STATE.REMOVED, OpAnalysis.STATE.ACTIVE), new applyRulePass.RuleType[]{applyRulePass.RuleType.SolveConflict});
        put(new Pair<>(OpAnalysis.STATE.REMOVED, OpAnalysis.STATE.REMOVED), new applyRulePass.RuleType[]{applyRulePass.RuleType.SYNWrong, applyRulePass.RuleType.GenConflict, applyRulePass.RuleType.NoCtx});
    }};

    private static applyRulePass.RuleType[] getRules(OpAnalysis.STATE from, OpAnalysis.STATE to){
        return TranstionStateMap.getOrDefault(new Pair<>(from, to), new applyRulePass.RuleType[]{applyRulePass.RuleType.DisCard});
    }


    public  static List<applyRulePass.RuleType> getPossibleRules(OpAG opAG, OpAnalysis target_opa){
        var current_state = opAG.getOpAStatus(target_opa);
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
    public static  OpAG solve(OpAG opAG, OpAnalysis target_opa, @Nullable List<applyRulePass.RuleType> allowed_ruleType){
        /*
        TDOO For simplicity we only use dfs and currently not build condition graph
        */
        var current_state = opAG.getOpAStatus(target_opa);
        List<applyRulePass.RuleType> possibleRules;
        //System.out.printf("%s %s->%s\n", target_opa.toString(), current_state, target_opa.state);
        if (allowed_ruleType != null) possibleRules = new ArrayList<>(Arrays.stream(getRules(current_state, target_opa.state)).toList()).stream().filter(x -> allowed_ruleType.contains(x)).toList();
        else possibleRules = new ArrayList<>(List.of(getRules(current_state, target_opa.state)));
        for(var rule: possibleRules) {
            // System.out.println(rule);
            var opAG_new = applyRulePass.solve(opAG, target_opa, rule);
            if (opAG_new != null){
                if (generate.insertRan) {
                    return random_inserts(opAG_new, opAG);
                }else return opAG_new;
            }
        }
        return null;
    }
}
