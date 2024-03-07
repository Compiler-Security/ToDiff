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
package org.generator.lib.generator.pass;

import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.reducer.driver.reducer;
import org.generator.lib.reducer.pass.reducePass;
import org.generator.util.collections.Pair;
import org.junit.Rule;

public class applyRulePass {

    public enum RuleType{
        SolveConflict,
        GenConflict,
        UnsetOp,
        UnsetCtx,
        Overrided,
        Keep,
        SYNWrong,
        NoCtx
    }


    public static OpAG solve(OpAG opAG, OpAnalysis target_opa, RuleType ruleType) {
        //IF target meet, return current opAGNew
        switch (ruleType){
            case Keep -> {
                actionRulePass.solve(opAG, target_opa, actionRulePass.ActionType.COPY);
                return opAG;
            }
        }
        return null;
    }
}
