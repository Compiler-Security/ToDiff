/**
 * This pass will give a normal controller of target OPAG, and return a generate OPAG
 */
package org.generator.lib.generator.isis.pass;

import org.generator.lib.generator.isis.controller.NormalController_ISIS;
import org.generator.lib.generator.driver.generate_ISIS;
import org.generator.lib.item.IR.OpAnalysis_ISIS;
import org.generator.lib.item.opg.OpAG_ISIS;
import org.generator.lib.reducer.driver.reducer_ISIS;

import java.util.*;

public class genEqualPass_ISIS {


    static boolean checkPreCondition(List<OpAnalysis_ISIS> new_opas, OpAG_ISIS new_opag){
        //all the new_opas should be active
        if (new_opas.isEmpty()) return false;
        for(var new_opa : new_opas){
            assert  new_opag.getOpAState(new_opa) != OpAnalysis_ISIS.STATE.INIT: "op state should be active/removed";
            if (new_opag.getOpAState(new_opa) != OpAnalysis_ISIS.STATE.ACTIVE){
                return false;
            }
        }
        return true;
    }

    static boolean handleAfterEffects(OpAnalysis_ISIS triggle_opa, List<OpAnalysis_ISIS> new_opas, OpAG_ISIS new_opag,NormalController_ISIS slots){
        for(var slot: slots.getOpas()){
            if (slot.equals(triggle_opa)){
                slots.moveToStateOfOpa(slot, triggle_opa.getState());
            }else{
                slots.revertToStateOfOpa(slot, new_opag.getOpAState(slot));
            }
        }
        for(var opa: new_opas){
            if (!slots.hasConfigOfOpa(opa) && opa.getOp().Type().isSetOp()){
                slots.addConfig(opa, 0, 0, 0, 0, OpAnalysis_ISIS.STATE.ACTIVE, OpAnalysis_ISIS.STATE.ACTIVE);
            }
        }
        return true;
    }

    //FIXME(should turn to true when running)
    public static OpAG_ISIS solve(NormalController_ISIS slots, OpAG_ISIS init_opag){
        var cur_opag = init_opag.copy();
        int s = 0;
        while(!slots.getCanMoveOpas().isEmpty()){
            var actionOpas = slots.getCanMoveOpas();
            if (generate_ISIS.ran){
                Collections.shuffle(actionOpas);
            }
            boolean succ = false;
            for(var actionOpa: actionOpas){
                //enumerate slot to move
                var dstStates = slots.getValidMoveStatesOfOpa(actionOpa);
                if (generate_ISIS.ran){
                    Collections.shuffle(dstStates);
                }
                var srcState = slots.getConfigStateOfOpa(actionOpa);
                for(var dstState: dstStates){
                    //enumerate target state
                    for(var rule: movePass_ISIS.getRules(srcState, dstState)){
                        //enumerate generated op by different rules
                        var actionSlot = actionOpa.copy();
                        actionSlot.setState(dstState);
                        //these opas are supposed to be active
                        var tmp= movePass_ISIS.solve(cur_opag, actionSlot, rule);
                        var new_opas = tmp.first();
                        var new_opag = tmp.second();

                        //check precondition
                        if (!checkPreCondition(new_opas, new_opag)) continue;

                        //if ok, handle after effects
                        handleAfterEffects(actionSlot, new_opas, new_opag,  slots);
                        //then randomly insert opas to gen_opag
                        if (generate_ISIS.insertRan){
                            cur_opag = movePass_ISIS.random_inserts(new_opag, cur_opag);
                        }
                        else cur_opag = new_opag;
                        succ = true;
                        break;
                    }
                    if (succ) break;
                }
                if (succ) break;
            }
            if(!succ){
                List<NormalController_ISIS.GenConfig> wrongOps = new ArrayList<>();
                for(var opa: actionOpas){
                    assert slots.hasConfigOfOpa(opa);
                    if (slots.getConfigStateOfOpa(opa) != slots.getConfigFinalStateOfOpa(opa)){
                        wrongOps.add(slots.getConfigOfOpa(opa));
                    }
                }
                if (!wrongOps.isEmpty()) {
                    assert false: "all ops can not move!";
                    System.out.println("=====wrong_slots========");
                    System.out.println(wrongOps);
                    System.out.println("=====cur_opag_core=======");
                    System.out.println(reducer_ISIS.reduceToCore(cur_opag.toOpCtxGActiveSet()));
                } else{
                    break;
                }
            }
        }
        return cur_opag;
    }
}
