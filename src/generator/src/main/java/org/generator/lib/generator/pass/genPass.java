/**
 * This pass will give a normal controller of target OPAG, and return a generate OPAG
 */
package org.generator.lib.generator.pass;

import org.generator.lib.generator.controller.CapacityController;
import org.generator.lib.generator.controller.NormalController;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.opg.OpAG;

import java.util.List;

public class genPass {

    static boolean checkOpAG(OpAG opAG, NormalController controller, CapacityController tmp_controller){
        for(var opa: opAG.setOpView()){
            var current_state = opAG.getOpAStatus(opa);
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

    static void updateController(OpAG opAG, NormalController controller, CapacityController tmp_controller){
        for(var opa: opAG.setOpView()){
            var current_state = opAG.getOpAStatus(opa);
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
    public static OpAG solve(NormalController controller, CapacityController tmp_controller){
        //TODO in the simpleset version, we don't add
        var opag = OpAG.of();
        while(!controller.getCanMoveOpas().isEmpty() || !tmp_controller.getCanMoveOpas().isEmpty()){
            var action_list = controller.getCanMoveOpas();
            action_list.addAll(tmp_controller.getCanMoveOpas());
            //TODO we should random pick one
            for(var actionOpa_old: action_list){
                var actionOpa = actionOpa_old.copy();
                List<OpAnalysis.STATE> actionStates;
                if (controller.hasConfigOfOpa(actionOpa)) {
                    actionStates = controller.getValidMoveStatesOfOpa(actionOpa);
                } else {
                    actionStates = tmp_controller.getValidMoveStatesOfOpa(actionOpa);
                }

                boolean succ = false;
                //TODO we should random pick one state
                for (var action_state : actionStates) {
                    actionOpa.setState(action_state);
                    var possible_opag = movePass.solve(opag, actionOpa, null);
                    if (possible_opag == null) continue;
                    if (checkOpAG(possible_opag, controller, tmp_controller)) {
                        updateController(possible_opag, controller, tmp_controller);
                        opag = possible_opag;
                        succ = true;
                        break;
                    }
                }
                if (succ) break;
            }
            System.out.println(opag.getRemainOps().toString());
        }
        return opag;
    }
}
