package org.generator.lib.generator.phy.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.phy.controller.NormalController;
import org.generator.lib.item.IR.OpPhy;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.util.collections.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class genPhyDiffPass {
    public genPhyDiffPass() {
        slots = new ArrayList<>();
    }

    List<NormalController> slots;

    NormalController getSlot(NormalController.CType cType, String name, String name2){
        return slots.stream().filter(slot -> slot.getcType() == cType && slot.equalName(name) && slot.partialEqualName2(name2)).findFirst().get();
    }

    boolean checkPreCondition(OpPhy targetOp){
        switch (targetOp.Type()){
            //The link {add|down|remove} instruction can be generated arbitrarily.
            //The Link down instruction can be generated after the link remove instruction.
            //We ignore invalid instructions in the test framework to ensure that the link instruction is generated only when the node exists.
            case LINKADD ->{
                var b1 = getSlot(NormalController.CType.NODE, Arrays.stream(targetOp.getNAME().split("-")).toList().getFirst(), null).getCurType() == OpType.NODEADD;
                var b2 = getSlot(NormalController.CType.NODE, Arrays.stream(targetOp.getNAME2().split("-")).toList().getFirst(), null).getCurType() == OpType.NODEADD;
                return b1 && b2;
            }
            case LINKDOWN, LINKREMOVE -> {
                getSlot(NormalController.CType.LINK, targetOp.getNAME(), targetOp.getNAME2()).getCurType()
            }
            case INTFUP, INTFDOWN -> {
                var opType =  getSlot(NormalController.CType.LINK, targetOp.getNAME(), null).getCurType();
                return opType == OpType.LINKADD;
            }
            case NODEADD, NODEDEL ->{ return  true;}

            //MULTI:
            case NODESETOSPFUP,NODESETOSPFRE, NODESETOSPFSHUTDOWN ->{
                return getSlot(NormalController.CType.NODE, targetOp.getNAME(), null).getCurType() == OpType.NODEADD;
            }
            case NODESETRIPUP,NODESETRIPRE, NODESETRIPSHUTDOWN ->{
                return getSlot(NormalController.CType.NODE, targetOp.getNAME(), null).getCurType() == OpType.NODEADD;
            }
            //----------ISIS-----------
            case NODESETISISUP,NODESETISISRE, NODESETISISSHUTDOWN ->{
                return getSlot(NormalController.CType.NODE, targetOp.getNAME(), null).getCurType() == OpType.NODEADD;
            }

            case NODESETBABELUP , NODESETBABELSHUTDOWN, NODESETBABELRE ->{
                return getSlot(NormalController.CType.NODE, targetOp.getNAME(), null).getCurType() == OpType.NODEADD;
            }
        }
        return false;
    }

    public OpCtxG solve(OpCtxG oldOpg, OpCtxG newOpg) {

    }
}
