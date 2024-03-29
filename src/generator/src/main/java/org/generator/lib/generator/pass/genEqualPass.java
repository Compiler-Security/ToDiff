/**
 * This pass will give a normal controller of target OPAG, and return a generate OPAG
 */
package org.generator.lib.generator.pass;

import org.generator.lib.generator.controller.CapacityController;
import org.generator.lib.generator.controller.NormalController;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.opg.OpAG;

import java.util.*;

public class genEqualPass {

    static boolean checkOpAG(OpAG opAG, NormalController controller, CapacityController tmp_controller, OpAnalysis target_opa){
        for(var opa: opAG.setList()){
            var current_state = opAG.getOpAStatus(opa);
            if (!opa.equals(target_opa)){
                if (controller.hasConfigOfOpa(opa)){
                    if (controller.getConfigStateOfOpa(opa) == current_state) continue;
                }
                if (tmp_controller.hasConfigOfOpa(opa)){
                    if (tmp_controller.getConfigStateOfOpa(opa) == current_state) continue;
                }
            }
            if (controller.hasConfigOfOpa(opa)){
                if (!controller.canMoveStateOfOpa(opa, current_state)) return false;
            }else{
                if (tmp_controller.hasConfigOfOpa(opa)){
                    if (!tmp_controller.canMoveStateOfOpa(opa, current_state)) return false;
                }else{
                    if (!tmp_controller.canAddConfig()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    static void updateController(OpAG opAG, NormalController controller, CapacityController tmp_controller, OpAnalysis target_opa){
        for(var opa: opAG.setList()){
            var current_state = opAG.getOpAStatus(opa);
            if (!opa.equals(target_opa)){
                if (controller.hasConfigOfOpa(opa)){
                    if (controller.getConfigStateOfOpa(opa) == current_state) continue;
                }
                if (tmp_controller.hasConfigOfOpa(opa)){
                    if (tmp_controller.getConfigStateOfOpa(opa) == current_state) continue;
                }
            }
            if (controller.hasConfigOfOpa(opa)){
                controller.moveToStateOfOpa(opa, current_state);
            }else{
                if (tmp_controller.hasConfigOfOpa(opa)){
                    tmp_controller.moveToStateOfOpa(opa, current_state);
                }else{
                    tmp_controller.addConfig(opa);
                }
            }
        }
    }

    //FIXME(should turn to true when running)
    public static OpAG solve(NormalController controller, CapacityController tmp_controller){
        var opag = OpAG.of();
        int s = 0;
        while(!controller.getCanMoveOpas().isEmpty() || !tmp_controller.getCanMoveOpas().isEmpty()){
            var action_list = controller.getCanMoveOpas();
            action_list.addAll(tmp_controller.getCanMoveOpas());
            //System.out.println(action_list.size());
            if (generate.ran) {
                Collections.shuffle(action_list);
            }
            boolean succ = false;
            for(var actionOpa_old: action_list){
                var actionOpa = actionOpa_old.copy();
                List<OpAnalysis.STATE> actionStates;
                if (controller.hasConfigOfOpa(actionOpa)) {
                    actionStates = controller.getValidMoveStatesOfOpa(actionOpa);
                } else {
                    actionStates = tmp_controller.getValidMoveStatesOfOpa(actionOpa);
                }
                if (generate.ran){
                    Collections.shuffle(actionStates);
                }
                for (var action_state : actionStates) {
                    actionOpa.setState(action_state);
                    //FIXME This is a bug, we should try different when do possible_opag, set this to 20 is safe I think
                    var possibleRules = movePass.getPossibleRules(opag, actionOpa);
                    if (generate.ran){
                        Collections.shuffle(possibleRules);
                    }
                    for(var rule: possibleRules){
                        var possible_opag = movePass.solve(opag, actionOpa, List.of(rule));
                        s++;
                        if (possible_opag == null) continue;
                        if (checkOpAG(possible_opag, controller, tmp_controller, actionOpa)) {
                            updateController(possible_opag, controller, tmp_controller, actionOpa);
                            //System.out.printf("%s %s\n", rule, actionOpa);
                            opag = possible_opag;
                            succ = true;
                            break;
                        }
                    }
                    if (succ) break;
                }
                if (succ) break;
            }
            //System.out.println(opag);
//            System.out.println(controller);
//            System.out.println(tmp_controller);
            assert succ: "all op can not move!";

        }
        //System.out.printf("total move time %d\n", s);
        return opag;
    }
}
