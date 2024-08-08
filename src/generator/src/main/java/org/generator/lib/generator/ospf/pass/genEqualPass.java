/**
 * This pass will give a normal controller of target OPAG, and return a generate OPAG
 */
package org.generator.lib.generator.ospf.pass;

import org.generator.lib.generator.ospf.controller.CapacityController;
import org.generator.lib.generator.ospf.controller.NormalController;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.opg.OpAG;

import java.util.*;

public class genEqualPass {


    static boolean checkPreCondition(List<OpAnalysis> new_opas, OpAG new_opag){
        //all the new_opas should be active
        for(var new_opa : new_opas){
            if (new_opag.getOpAState(new_opa) != OpAnalysis.STATE.ACTIVE) return false;
        }
        return true;
    }

    static boolean handleAfterEffects(List<OpAnalysis> new_opas, OpAG new_opag, OpAG cur_opag, NormalController slots){
        //handle op in cur_opag but not in new_opas, these ops should be totally in slots
        for(var cur_opa: slots.getOpas()) assert cur_opag.hasOpA(cur_opa): "slots > genOpAG";
        for(var cur_opa: cur_opag.getSlots()) assert  slots.hasConfigOfOpa(cur_opa): "slots < genOpAG";
        for(var cur_opa: cur_opag.getSlots()){
            //in cur_opag but not in new_opas;
            if (new_opas.contains(cur_opa)) continue;

            var new_state = new_opag.getOpAState(cur_opa);
            var old_state = slots.getConfigStateOfOpa(cur_opa);
            if (old_state != new_state){
                slots.reverseToStateOfOpa(cur_opa, new_state);
            }
        }
        for(var new_opa: new_opas){
            var new_state = new_opag.getOpAState(new_opa);
            if (slots.hasConfigOfOpa(new_opa)){
                //if new_opa in slots, we move it to new_state, with cost 1
                slots.moveToStateOfOpa(new_opa, new_state);
            }else{
                //if new_opa not in slots, we should keep it in active
                slots.addConfig(new_opa, 0, 0, 0, 0, OpAnalysis.STATE.ACTIVE);
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
                for(var dstState: slots.getValidMoveStatesOfOpa(actionOpa)){
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
                        handleAfterEffects(new_opas, new_opag, cur_opag, slots);
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
                System.out.println("===============");
                System.out.println(actionOpas);
                System.out.println("===============");
                System.out.println(slots);
                System.out.println("===============");
                System.out.println(cur_opag.toOpCtxGActiveSet());
            }
            assert succ: "all ops can not move!";
        }
        return cur_opag;
    }
}
