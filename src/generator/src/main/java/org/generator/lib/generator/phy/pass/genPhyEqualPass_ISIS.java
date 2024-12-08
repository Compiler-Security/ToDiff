package org.generator.lib.generator.phy.pass;

import org.generator.lib.frontend.lexical.OpType_isis;
import org.generator.lib.generator.driver.generate_ISIS;
import org.generator.lib.generator.phy.controller.NormalController_ISIS;
import org.generator.lib.item.IR.OpCtx_ISIS;
import org.generator.lib.item.IR.OpPhy_ISIS;
import org.generator.lib.item.opg.OpCtxG_ISIS;
import org.generator.util.ran.ranHelper;

import java.util.*;
import java.util.stream.Collectors;

public class genPhyEqualPass_ISIS {
    public genPhyEqualPass_ISIS(){}
    void readToSlots(OpCtxG_ISIS phyOpg){
        slots = new ArrayList<>();
        for(var op_: phyOpg.getOps()){
            var op = op_.getOpPhy();
            switch (op.Type()){
                case NODEADD -> {
                    slots.add(NormalController_ISIS.getNodeCatg(0, 0, op.getNAME(), OpType_isis.NODEADD, NormalController_ISIS.CType.NODE));

                }
                case NODEDEL -> {
                    slots.add(NormalController_ISIS.getNodeCatg(0, 0, op.getNAME(), OpType_isis.NODEDEL, NormalController_ISIS.CType.NODE));
                }
                case NODESETISISUP -> {
                    slots.add(NormalController_ISIS.getISISCatg(0, 0, 0, op.getNAME(), OpType_isis.NODESETISISUP, NormalController_ISIS.CType.ISIS));
                }
                case NODESETISISSHUTDOWN -> {
                    slots.add(NormalController_ISIS.getISISCatg(0, 0, 0, op.getNAME(), OpType_isis.NODESETISISSHUTDOWN, NormalController_ISIS.CType.ISIS));
                }
                case NODESETISISRE -> {
                    slots.add(NormalController_ISIS.getISISCatg(0, 0, 0, op.getNAME(), OpType_isis.NODESETISISRE, NormalController_ISIS.CType.ISIS));
                }
                case INTFUP -> {
                    slots.add(NormalController_ISIS.getIntfCatg(0, 0, op.getNAME(), OpType_isis.INTFUP, NormalController_ISIS.CType.INTF));
                }
                case INTFDOWN -> {
                    slots.add(NormalController_ISIS.getIntfCatg(0, 0, op.getNAME(), OpType_isis.INTFDOWN, NormalController_ISIS.CType.INTF));
                }
                case LINKADD -> {
                    slots.add(NormalController_ISIS.getLinkCatg(0, 0, 0, op.getNAME(), op.getNAME2(), OpType_isis.LINKADD, NormalController_ISIS.CType.LINK));
                }
                case LINKDOWN -> {
                    slots.add(NormalController_ISIS.getLinkCatg(0, 0, 0, op.getNAME(), op.getNAME2(), OpType_isis.LINKDOWN, NormalController_ISIS.CType.LINK));
                }
                case LINKREMOVE -> {
                    slots.add(NormalController_ISIS.getLinkCatg(0, 0, 0, op.getNAME(), op.getNAME2(), OpType_isis.LINKREMOVE, NormalController_ISIS.CType.LINK));
                }
                default -> {
                    assert false: "error op %s".formatted(op.toString());
                }
            }
        }
    }

    public List<NormalController_ISIS> getActiveSlots(){
        return slots.stream().filter(slot -> !slot.getPossibleTypes().isEmpty()).collect(Collectors.toList());
    }

    NormalController_ISIS getSlot( NormalController_ISIS.CType cType, String name, String name2){
        return slots.stream().filter(slot -> slot.getcType() == cType && slot.equalName(name) && slot.partialEqualName2(name2)).findFirst().get();
    }

    List<NormalController_ISIS> getSlots( NormalController_ISIS.CType cType, String name, String name2) {
        return slots.stream().filter(slot -> slot.getcType() == cType && slot.equalName(name) && slot.partialEqualName2(name2)).toList();
    }
    boolean checkPreCondition(OpPhy_ISIS targetOp, NormalController_ISIS slot){
        switch (targetOp.Type()){
            //The link {add|down|remove} instruction can be generated arbitrarily.
            //The Link down instruction can be generated after the link remove instruction.
            //We ignore invalid instructions in the test framework to ensure that the link instruction is generated only when the node exists.
            case LINKADD, LINKREMOVE,LINKDOWN ->{
                var b1 = getSlot(NormalController_ISIS.CType.NODE, Arrays.stream(targetOp.getNAME().split("-")).toList().getFirst(), null).getCurType() == OpType_isis.NODEADD;
                var b2 = getSlot(NormalController_ISIS.CType.NODE, Arrays.stream(targetOp.getNAME2().split("-")).toList().getFirst(), null).getCurType() == OpType_isis.NODEADD;
                return b1 && b2;
            }
            case INTFUP, INTFDOWN -> {
                var opType =  getSlot(NormalController_ISIS.CType.LINK, targetOp.getNAME(), null).getCurType();
                return opType == OpType_isis.LINKADD;
            }
            case NODESETISISUP,NODESETISISRE, NODESETISISSHUTDOWN ->{
                return getSlot(NormalController_ISIS.CType.NODE, targetOp.getNAME(), null).getCurType() == OpType_isis.NODEADD;
            }
            case NODEADD, NODEDEL ->{ return  true;}
        }
        return false;
    }

    void handleAfterAffects(OpPhy_ISIS targetOp){
        switch (targetOp.Type()){
            //FIXME Q? Does NODEDEL need to handle node OSPF up/down also?
            case NODEDEL-> {
                var slots = getSlots(NormalController_ISIS.CType.LINK, "%s-eth[0-9]+".formatted(targetOp.getNAME()), null);
                for(var slot: slots) {
                    if (slot.getCurType() == OpType_isis.LINKADD) slot.deltaTypeNum(slot.getCurType(), 1);
                    if (slot.getCurType() == OpType_isis.LINKDOWN) {
                        slot.deltaTypeNum(OpType_isis.LINKADD, 1);
                        slot.deltaTypeNum(OpType_isis.LINKDOWN, 1);
                    }
                    if (slot.getCurType() == OpType_isis.LINKREMOVE) {
                        slot.deltaTypeNum(OpType_isis.LINKREMOVE, -1);
                    }
                    slot.setCurType(null);
                }
                //every router NODE should have OSPF
                var slot = getSlot(NormalController_ISIS.CType.ISIS, targetOp.getNAME(), null);
                if (slot.getCurType() == OpType_isis.NODESETISISUP) {
                    slot.deltaTypeNum(OpType_isis.NODESETISISUP, 1);
                    slot.setCurType(OpType_isis.NODESETISISSHUTDOWN);
                }
            }
            case LINKREMOVE -> {
                var slot = getSlot(NormalController_ISIS.CType.INTF, targetOp.getNAME(), null);
                slot.deltaTypeNum(slot.getCurType(), 1);
                slot.setCurType(null);
            }
        }
    }

    boolean genOneOp(NormalController_ISIS slot){
        var possible_types = slot.getPossibleTypes();
        if (generate_ISIS.phyRan) Collections.shuffle(possible_types);
        for(var opType: possible_types){
            var phy_op = new OpPhy_ISIS(opType);
            phy_op.setNAME(slot.getName());
            phy_op.setNAME2(slot.getName2());
            if (checkPreCondition(phy_op, slot)){
                //System.out.println(slot);
                gen.addOp(OpCtx_ISIS.of(phy_op, 0));
                slot.setCurType(opType);
                slot.deltaTypeNum(opType, -1);
                handleAfterAffects(phy_op);
                return true;
            }
        }
        return false;
    }
    public OpCtxG_ISIS solve(OpCtxG_ISIS phyOpg, double changeRatio, int mxRound){
        readToSlots(phyOpg);

        //add ratio
        int tmp = (int) (10 * changeRatio);
        for(var slot: slots){
            if (ranHelper.randomInt(0, 10) <= tmp){
                //we don't change  switch node
                if (slot.getcType() == NormalController_ISIS.CType.NODE && slot.getName().charAt(0) == 's') continue;
                slot.deltaAllTypeNum(ranHelper.randomInt(0, mxRound));
                if (slot.getcType() == NormalController_ISIS.CType.ISIS){
                    slot.deltaTypeNum(OpType_isis.NODESETISISRE, -slot.getCounterOfType(OpType_isis.NODESETISISRE));
                }
            }
        }

        gen = OpCtxG_ISIS.Of();

        var active_slots = getActiveSlots();
        while(!active_slots.isEmpty()){
            if (generate_ISIS.phyRan){
                Collections.shuffle(active_slots);
            }
            boolean canMove = false;
           // System.out.println(active_slots);
            for(var slot: active_slots){
                if (genOneOp(slot)) {canMove = true;break;}
            }
            active_slots = getActiveSlots();
            assert canMove: "can't move! %s\n%s\n%s".formatted(slots, active_slots, gen);
            //System.out.println(active_slots);
        }
        return gen;
    }
    OpCtxG_ISIS gen;
    List<NormalController_ISIS> slots;
}
