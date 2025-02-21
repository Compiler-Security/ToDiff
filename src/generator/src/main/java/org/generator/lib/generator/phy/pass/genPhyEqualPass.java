package org.generator.lib.generator.phy.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.generator.phy.controller.NormalController;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpPhy;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.util.ran.ranHelper;

import java.util.*;
import java.util.stream.Collectors;

public class genPhyEqualPass {
    public genPhyEqualPass(){}
    void readToSlots(OpCtxG phyOpg){
        slots = new ArrayList<>();
        for(var op_: phyOpg.getOps()){
            var op = op_.getOpPhy();
            switch (op.Type()){
                case NODEADD -> {
                    slots.add(NormalController.getNodeCatg(0, 0, op.getNAME(), OpType.NODEADD, NormalController.CType.NODE));

                }
                case NODEDEL -> {
                    slots.add(NormalController.getNodeCatg(0, 0, op.getNAME(), OpType.NODEDEL, NormalController.CType.NODE));
                }
                case INTFUP -> {
                    slots.add(NormalController.getIntfCatg(0, 0, op.getNAME(), OpType.INTFUP, NormalController.CType.INTF));
                }
                case INTFDOWN -> {
                    slots.add(NormalController.getIntfCatg(0, 0, op.getNAME(), OpType.INTFDOWN, NormalController.CType.INTF));
                }
                case LINKADD -> {
                    slots.add(NormalController.getLinkCatg(0, 0, 0, op.getNAME(), op.getNAME2(), OpType.LINKADD, NormalController.CType.LINK));
                }
                case LINKDOWN -> {
                    slots.add(NormalController.getLinkCatg(0, 0, 0, op.getNAME(), op.getNAME2(), OpType.LINKDOWN, NormalController.CType.LINK));
                }
                case LINKREMOVE -> {
                    slots.add(NormalController.getLinkCatg(0, 0, 0, op.getNAME(), op.getNAME2(), OpType.LINKREMOVE, NormalController.CType.LINK));
                }
                //MULTI:
                case NODESETOSPFUP -> {
                    slots.add(NormalController.getOSPFCatg(0, 0, 0, op.getNAME(), OpType.NODESETOSPFUP, NormalController.CType.OSPF));
                }
                case NODESETOSPFSHUTDOWN -> {
                    slots.add(NormalController.getOSPFCatg(0, 0, 0, op.getNAME(), OpType.NODESETOSPFSHUTDOWN, NormalController.CType.OSPF));
                }
                case NODESETOSPFRE -> {
                    slots.add(NormalController.getOSPFCatg(0, 0, 0, op.getNAME(), OpType.NODESETOSPFRE, NormalController.CType.OSPF));
                }
                case NODESETRIPUP -> {
                    slots.add(NormalController.getRIPCatg(0, 0, 0, op.getNAME(), OpType.NODESETRIPUP, NormalController.CType.RIP));
                }
                case NODESETRIPSHUTDOWN -> {
                    slots.add(NormalController.getRIPCatg(0, 0, 0, op.getNAME(), OpType.NODESETRIPSHUTDOWN, NormalController.CType.RIP));
                }
                case NODESETRIPRE -> {
                    slots.add(NormalController.getRIPCatg(0, 0, 0, op.getNAME(), OpType.NODESETRIPRE, NormalController.CType.RIP));
                }
                default -> {
                    assert false: "error op %s".formatted(op.toString());
                }
            }
        }
    }

    public List<NormalController> getActiveSlots(){
        return slots.stream().filter(slot -> !slot.getPossibleTypes().isEmpty()).collect(Collectors.toList());
    }

    NormalController getSlot( NormalController.CType cType, String name, String name2){
        return slots.stream().filter(slot -> slot.getcType() == cType && slot.equalName(name) && slot.partialEqualName2(name2)).findFirst().get();
    }

    List<NormalController> getSlots( NormalController.CType cType, String name, String name2) {
        return slots.stream().filter(slot -> slot.getcType() == cType && slot.equalName(name) && slot.partialEqualName2(name2)).toList();
    }
    boolean checkPreCondition(OpPhy targetOp, NormalController slot){
        switch (targetOp.Type()){
            //The link {add|down|remove} instruction can be generated arbitrarily.
            //The Link down instruction can be generated after the link remove instruction.
            //We ignore invalid instructions in the test framework to ensure that the link instruction is generated only when the node exists.
            case LINKADD, LINKREMOVE,LINKDOWN ->{
                var b1 = getSlot(NormalController.CType.NODE, Arrays.stream(targetOp.getNAME().split("-")).toList().getFirst(), null).getCurType() == OpType.NODEADD;
                var b2 = getSlot(NormalController.CType.NODE, Arrays.stream(targetOp.getNAME2().split("-")).toList().getFirst(), null).getCurType() == OpType.NODEADD;
                return b1 && b2;
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
        }
        return false;
    }

    void handleAfterAffects(OpPhy targetOp){
        switch (targetOp.Type()){
            //FIXME Q? Does NODEDEL need to handle node OSPF up/down also?
            case NODEDEL-> {
                var slots = getSlots(NormalController.CType.LINK, "%s-eth[0-9]+".formatted(targetOp.getNAME()), null);
                for(var slot: slots) {
                    if (slot.getCurType() == OpType.LINKADD) slot.deltaTypeNum(slot.getCurType(), 1);
                    if (slot.getCurType() == OpType.LINKDOWN) {
                        slot.deltaTypeNum(OpType.LINKADD, 1);
                        slot.deltaTypeNum(OpType.LINKDOWN, 1);
                    }
                    if (slot.getCurType() == OpType.LINKREMOVE) {
                        slot.deltaTypeNum(OpType.LINKREMOVE, -1);
                    }
                    slot.setCurType(null);
                }
                //MULTI:
                if (generate.protocol == generate.Protocol.OSPF){
                    //every router NODE should have OSPF
                    var slot = getSlot(NormalController.CType.OSPF, targetOp.getNAME(), null);
                    if (slot.getCurType() == OpType.NODESETOSPFUP) {
                        slot.deltaTypeNum(OpType.NODESETOSPFUP, 1);
                        slot.setCurType(OpType.NODESETOSPFSHUTDOWN);
                    }
                }
                if (generate.protocol == generate.Protocol.RIP){
                    //every router NODE should have OSPF
                    var slot = getSlot(NormalController.CType.RIP, targetOp.getNAME(), null);
                    if (slot.getCurType() == OpType.NODESETRIPUP) {
                        slot.deltaTypeNum(OpType.NODESETRIPUP, 1);
                        slot.setCurType(OpType.NODESETRIPSHUTDOWN);
                    }
                }

            }
            case LINKREMOVE -> {
                var slot = getSlot(NormalController.CType.INTF, targetOp.getNAME(), null);
                slot.deltaTypeNum(slot.getCurType(), 1);
                slot.setCurType(null);
            }
        }
    }

    boolean genOneOp(NormalController slot){
        var possible_types = slot.getPossibleTypes();
        if (generate.phyRan) Collections.shuffle(possible_types);
        for(var opType: possible_types){
            var phy_op = new OpPhy(opType);
            phy_op.setNAME(slot.getName());
            phy_op.setNAME2(slot.getName2());
            if (checkPreCondition(phy_op, slot)){
                //System.out.println(slot);
                gen.addOp(OpCtx.of(phy_op, 0));
                slot.setCurType(opType);
                slot.deltaTypeNum(opType, -1);
                handleAfterAffects(phy_op);
                return true;
            }
        }
        return false;
    }
    public OpCtxG solve(OpCtxG phyOpg, double changeRatio, int mxRound){
        readToSlots(phyOpg);

        //add ratio
        int tmp = (int) (10 * changeRatio);
        for(var slot: slots){
            if (ranHelper.randomInt(0, 10) <= tmp){
                //we don't change  switch node
                if (slot.getcType() == NormalController.CType.NODE && slot.getName().charAt(0) == 's') continue;
                slot.deltaAllTypeNum(ranHelper.randomInt(0, mxRound));
                //MULTI:
                if (slot.getcType() == NormalController.CType.OSPF){
                    slot.deltaTypeNum(OpType.NODESETOSPFRE, -slot.getCounterOfType(OpType.NODESETOSPFRE));
                }
                if (slot.getcType() == NormalController.CType.RIP){
                    slot.deltaTypeNum(OpType.NODESETRIPRE, -slot.getCounterOfType(OpType.NODESETRIPRE));
                }
            }
        }

        gen = OpCtxG.Of();

        var active_slots = getActiveSlots();
        while(!active_slots.isEmpty()){
            if (generate.phyRan){
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
    OpCtxG gen;
    List<NormalController> slots;
}
