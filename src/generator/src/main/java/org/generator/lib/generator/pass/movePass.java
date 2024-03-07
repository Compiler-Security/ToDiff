/*
This pass will transform OpAG to OpAG' = Move(OpAG', target_opA(opAnalysis))
Input:
    OPAG
    target opA(opOspf + status)
Output:
    list of OpAG'


ATTENTION: this pass may add multiple opA to OpAG, and may change other OpA's states in the OpAG' by the rules
 */
package org.generator.lib.generator.pass;

import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.opg.OpAG;

import java.util.ArrayList;
import java.util.List;

public class movePass {


    /**
     * INIT-> REMOVED SYNWRONG, GenConf (may not ok), NoCtx (may not ok) (FIXME currently for simplicity the last two rule we don't use)
     * INIT,REMOVE -> ACTIVE SolveConflict + Keep
     * ACTIVE-> REMOVED UnsetOp | UnsetCtx | Overrided(FIXME currently for simplicity)
     * ACTIVE-> ACTIVE Keep
     * other don't do anything
     */
    private  OpAG move(OpAG opAG, OpAnalysis target_opa){
        /*
        FIXME For simplicity we only use dfs and currently not build condition graph
        */
        var current_state = opAG.findOpAStatus(target_opa);
        switch (target_opa.state) {
            case ACTIVE -> {
                switch (current_state) {
                    case ACTIVE -> {
                        //simply COPY given target_opa
                        return applyRulePass.solve(opAG, target_opa, applyRulePass.RuleType.Keep);
                    }
                    case INIT, REMOVED -> {

                    }
                    default -> {return opAG.copy();}
                }
            }
            case REMOVED -> {
                switch (current_state){
                    case ACTIVE -> {

                    }
                    case INIT -> {

                    }
                    default -> {return opAG.copy();}
                }
            }
            default -> {assert false : "target_opa state wrong";}
        }
        return null;
    }

    public List<OpAG> solve(OpAG opAG, OpAnalysis target_opA){
        List<OpAG> res_list = new ArrayList<>();

        return res_list;
    }
}
