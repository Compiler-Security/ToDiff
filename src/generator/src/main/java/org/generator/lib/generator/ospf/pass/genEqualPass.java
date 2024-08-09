/**
 * This pass will give a normal controller of target OPAG, and return a generate OPAG
 */
package org.generator.lib.generator.ospf.pass;

import org.generator.lib.generator.ospf.controller.NormalController;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.reducer.driver.reducer;

import java.util.*;

public class genEqualPass {


    static boolean checkPreCondition(List<OpAnalysis> new_opas, OpAG new_opag){
        //all the new_opas should be active
        if (new_opas.isEmpty()) return false;
        for(var new_opa : new_opas){
            assert  new_opag.getOpAState(new_opa) != OpAnalysis.STATE.INIT: "op state should be active/removed";
            if (new_opag.getOpAState(new_opa) != OpAnalysis.STATE.ACTIVE){
                return false;
            }
        }
        return true;
    }

    static boolean handleAfterEffects(OpAnalysis triggle_opa, List<OpAnalysis> new_opas, OpAG new_opag,NormalController slots){
        for(var slot: slots.getOpas()){
            if (slot.equals(triggle_opa)){
                slots.moveToStateOfOpa(slot, triggle_opa.getState());
            }else{
                slots.revertToStateOfOpa(slot, new_opag.getOpAState(slot));
            }
        }
        for(var opa: new_opas){
            if (!slots.hasConfigOfOpa(opa) && opa.getOp().Type().isSetOp()){
                slots.addConfig(opa, 0, 0, 0, 0, OpAnalysis.STATE.ACTIVE, OpAnalysis.STATE.ACTIVE);
            }
        }
        return true;
    }

    //FIXME(should turn to true when running)
    public static OpAG solve(NormalController slots){
        var cur_opag = OpAG.of();
        int s = 0;
        while(!slots.getCanMoveOpas().isEmpty()){
            var actionOpas = slots.getCanMoveOpas();
            if (generate.ran){
                Collections.shuffle(actionOpas);
            }
            boolean succ = false;
            for(var actionOpa: actionOpas){
                //enumerate slot to move
                var dstStates = slots.getValidMoveStatesOfOpa(actionOpa);
                if (generate.ran){
                    Collections.shuffle(dstStates);
                }
                var srcState = slots.getConfigStateOfOpa(actionOpa);
                for(var dstState: dstStates){
                    //enumerate target state
                    for(var rule: movePass.getRules(srcState, dstState)){
                        //enumerate generated op by different rules
                        var actionSlot = actionOpa.copy();
                        actionSlot.setState(dstState);
                        //these opas are supposed to be active
                        var tmp= movePass.solve(cur_opag, actionSlot, rule);
                        var new_opas = tmp.first();
                        var new_opag = tmp.second();

                        //check precondition
                        if (!checkPreCondition(new_opas, new_opag)) continue;

                        //if ok, handle after effects
                        handleAfterEffects(actionSlot, new_opas, new_opag,  slots);
                        //then randomly insert opas to gen_opag
                        if (generate.insertRan){
                            cur_opag = movePass.random_inserts(new_opag, cur_opag);
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
                List<NormalController.GenConfig> wrongOps = new ArrayList<>();
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
                    System.out.println(reducer.reduceToCore(cur_opag.toOpCtxGActiveSet()));
                } else{
                    break;
                }
            }
        }
        return cur_opag;
    }
}
