/*
 * This pass apply one Rule to OpAG
 * Input
 * OpAG
 * Rule
 *
 * Output
 * OPAG'
 *
 * The algorithm:
 * 1. Input Rule is the root node of Topological rule graph
 * 2. Before apply one Rule, we must meet preRules of this rule
 * We can demonstrate that this rule forms a topological graph without circle
 * It's worth to mention that when we apply one Rule, other rules in the graph may be applied as well
 * 3. So every time we apply from root node use dfs or bfs by postorder, we apply the rule of this node
 * if and only if all the preRule of this node is meet, we build rule graph at the same time
 * 4. We dynamically construct topological rule graph when applying rules
 * That is:
 * solve rules use rule graph <-> build rule graph by solving rules
 *
 * 5. We can demonstrate that the max num of rules of the rule graph is limited, so the algorithm always terminates
 */
package org.generator.lib.generator.ospf.pass;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.reducer.pass.reducePass;
import org.generator.util.ran.ranHelper;

import java.util.Arrays;
import java.util.List;

public class applyRulePass {

    public enum RuleType{
        SolveConflict, //after solve conflict, we then add target_op like keep
        GenConflict,
        UnsetOp,
        UnsetCtx,
        Overrided,
        Keep,
        SYNWrong,
        NoCtx,
        DisCard
    }



    private static List<OpAnalysis> conflictOps(OpAG opAG, OpAnalysis target_opa){
        //opAG.reduce();
        return opAG.activeSetView().getOps().stream().filter(opa -> reducePass.isConflict(opa, target_opa)).toList();
    }
    /**
     * This pass will apply given rule to the opAG, and return a new opAG
     * @param opAG
     * @param target_opa
     * @param ruleType
     * @return  opAG_new, null if apply fail
     */
    public static OpAG solve(OpAG opAG, OpAnalysis target_opa, RuleType ruleType) {
        var opAG_new = opAG.copy();
        //IF target meet, return current opAGNew
        //System.out.println(ruleType);
        switch (ruleType){
            case Keep -> {
                actionRulePass.solve(opAG_new, target_opa, actionRulePass.ActionType.COPY);
                return opAG_new;
            }
            case SolveConflict -> {
                var conflict_ops = conflictOps(opAG_new, target_opa);
                while(!conflict_ops.isEmpty()){
                    var handle_opa = conflict_ops.get(0);
                    if (generate.ran){
                        handle_opa = ranHelper.randomElemOfList(conflict_ops);
                    }
                    var expect_opa = handle_opa.copy();
                    expect_opa.setState(OpAnalysis.STATE.REMOVED);
                    var opa_next = movePass.solve(opAG_new, expect_opa, RuleType.UnsetOp);
                    if (opa_next != null){
                        opAG_new = opa_next;
                        conflict_ops = conflictOps(opAG_new, target_opa);
                    }else{
                        assert false: "conflict should always be solved";
                    }
                }
                actionRulePass.solve(opAG_new, target_opa, actionRulePass.ActionType.COPY);
                return opAG_new;
            }
            case GenConflict -> {
                //TODO currently we don't use this rule
                return null;
            }
            case UnsetOp -> {
                actionRulePass.solve(opAG_new, target_opa, actionRulePass.ActionType.UNSET);
                return opAG_new;
            }
            case UnsetCtx -> {
                if (!actionRulePass.solve(opAG_new, target_opa.ctxOp.getCtxOp(), actionRulePass.ActionType.UNSET)){
                    //unset IntfName
                    return null;
                }else return opAG_new; //unset ROSPF
            }
            case Overrided -> {
                if (!actionRulePass.solve(opAG_new, target_opa, actionRulePass.ActionType.MUTATE)){
                    //some instruction don't have args
                    return null;
                }
                return opAG_new;
            }
            case SYNWrong -> {
                if (!actionRulePass.solve(opAG_new, target_opa, actionRulePass.ActionType.BREAK)){
                    return null;
                }
                return opAG_new;
            }
            case NoCtx -> {
                if (!actionRulePass.solve(opAG_new, target_opa, actionRulePass.ActionType.NoCtx)){
                    return null;
                }
                return opAG_new;
            }
        }
        return null;
    }
}
