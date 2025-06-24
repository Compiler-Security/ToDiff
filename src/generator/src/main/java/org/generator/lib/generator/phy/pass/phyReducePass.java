package org.generator.lib.generator.phy.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.generator.phy.controller.NormalController;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpPhy;
import org.generator.lib.item.opg.OpCtxG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class phyReducePass {
    public phyReducePass() {
        slots = new ArrayList<>();
    }

    List<NormalController> slots;

    NormalController getSlot(NormalController.CType cType, String name, String name2){
        return slots.stream().filter(slot -> slot.getcType() == cType && slot.equalName(name) && slot.partialEqualName2(name2)).findFirst().get();
    }

    List<NormalController> getSlots( NormalController.CType cType, String name, String name2) {
        return slots.stream().filter(slot -> slot.getcType() == cType && slot.equalName(name) && slot.partialEqualName2(name2)).toList();
    }

    boolean checkPreCondition(OpPhy targetOp){
        switch (targetOp.Type()){
            case LINKADD ->{
                var b1 = getSlot(NormalController.CType.NODE, Arrays.stream(targetOp.getNAME().split("-")).toList().getFirst(), null);
                var b2 = getSlot(NormalController.CType.NODE, Arrays.stream(targetOp.getNAME2().split("-")).toList().getFirst(), null);
                return (b1 != null && b1.getCurType() == OpType.NODEADD) && (b2 != null && b2.getCurType() == OpType.NODEADD);
            }
            case LINKDOWN -> {
                var b1 = getSlot(NormalController.CType.LINK, targetOp.getNAME(), targetOp.getNAME2());
                return b1 != null && b1.getCurType() == OpType.LINKADD;
            }
            case LINKREMOVE -> {
                var b1 = getSlot(NormalController.CType.LINK, targetOp.getNAME(), targetOp.getNAME2());
                return b1 != null && (b1.getCurType() == OpType.LINKADD || b1.getCurType() == OpType.LINKDOWN);
            }
            case INTFUP, INTFDOWN -> {
                var b1 =  getSlot(NormalController.CType.LINK, targetOp.getNAME(), null);
                if (b1 == null) return false;
                return b1.getCurType() == OpType.LINKADD || b1.getCurType() == OpType.LINKDOWN;
            }
            case NODEADD, NODEDEL ->{ return  true;}

            //MULTI:
            case NODESETOSPFUP,NODESETOSPFRE, NODESETOSPFSHUTDOWN ->{
                var b1 = getSlot(NormalController.CType.NODE, targetOp.getNAME(), null);
                if (b1 == null) return false;
                return b1.getCurType() == OpType.NODEADD;
            }
            //----------ISIS-----------
            case NODESETISISUP,NODESETISISRE, NODESETISISSHUTDOWN ->{
                var b1 =  getSlot(NormalController.CType.NODE, targetOp.getNAME(), null);
                if (b1 == null) return false;
                return b1.getCurType() == OpType.NODEADD;
            }
        }
        return false;
    }

    void handleAfterAffects(OpPhy targetOp){
        switch (targetOp.Type()){
            case NODEDEL-> {
                var slots = getSlots(NormalController.CType.LINK, "%s-eth[0-9]+".formatted(targetOp.getNAME()), null);
                for(var slot: slots) {
                    slot.setCurType(OpType.LINKREMOVE);
                    var intfslot = getSlot(NormalController.CType.INTF, targetOp.getNAME(), null);
                    if (intfslot != null) intfslot.setCurType(null);
                }
                //MULTI:
                if (generate.protocol == generate.Protocol.OSPF){
                    //every router NODE should have OSPF
                    var slot = getSlot(NormalController.CType.OSPF, targetOp.getNAME(), null);
                    if (slot != null) slot.setCurType(OpType.NODESETOSPFSHUTDOWN);
                }
                if (generate.protocol == generate.Protocol.ISIS){
                    //every router NODE should have ISIS
                    var slot = getSlot(NormalController.CType.ISIS, targetOp.getNAME(), null);
                    if (slot != null) slot.setCurType(OpType.NODESETISISSHUTDOWN);
                }
            }
            case LINKREMOVE -> {
                var slot = getSlot(NormalController.CType.INTF, targetOp.getNAME(), null);
                if (slot != null)  slot.setCurType(null);
            }
        }
    }

    NormalController putSlot(NormalController findSlot, NormalController newSlot){
        if (findSlot != null) return findSlot;
        slots.add(newSlot);
        return newSlot;
    }
    public NormalController getSlot(OpPhy op){
        switch (op.Type()){
            case NODEADD, NODEDEL -> {
                return putSlot(getSlot(NormalController.CType.NODE, op.getNAME(), null),
                        NormalController.getNodeCatg(0, 0, op.getNAME(), op.Type(), NormalController.CType.NODE)
                );
            }
            case INTFUP, INTFDOWN -> {
                return putSlot(getSlot(NormalController.CType.INTF, op.getNAME(), null),
                        NormalController.getNodeCatg(0, 0, op.getNAME(), op.Type(), NormalController.CType.INTF)
                );
            }
            case LINKADD,LINKDOWN,LINKREMOVE -> {
                return putSlot(getSlot(NormalController.CType.LINK, op.getNAME(), op.getNAME2()),
                        NormalController.getLinkCatg(0, 0, 0, op.getNAME(), op.getNAME2(), op.Type(), NormalController.CType.LINK)
                );
            }
            //MULTI:
            case NODESETOSPFUP,NODESETOSPFRE,NODESETOSPFSHUTDOWN -> {
                return putSlot(getSlot(NormalController.CType.OSPF, op.getNAME(), null),
                        NormalController.getLinkCatg(0, 0, 0, op.getNAME(), null, op.Type(), NormalController.CType.OSPF)
                );
            }

            //---------ISIS------------
            case NODESETISISUP,NODESETISISRE,NODESETISISSHUTDOWN -> {
                return putSlot(getSlot(NormalController.CType.ISIS, op.getNAME(), null),
                        NormalController.getLinkCatg(0, 0, 0, op.getNAME(), null, op.Type(), NormalController.CType.ISIS)
                );
            }
            default -> {
                assert false: "error op %s".formatted(op.toString());
            }
        }
        return null;
    }
    OpCtx getOp(NormalController slot){
        var phy_op = new OpPhy(slot.getCurType());
        phy_op.setNAME(slot.getName());
        phy_op.setNAME2(slot.getName2());
        return OpCtx.of(phy_op, 0);
    }

    public OpCtxG toOpCtxG(){
        var opCtxG = OpCtxG.Of();
        var nodeSlots = getSlots(NormalController.CType.NODE, ".*", null);
        nodeSlots.sort(Comparator.comparing(NormalController::getName));
        for(var slot: nodeSlots) {
            if (slot.getCurType() == OpType.NODEADD) {
                opCtxG.addOp(getOp(slot));
            }
        }
        var linkSlots = getSlots(NormalController.CType.LINK, ".*", ".*");
        linkSlots.sort(Comparator.comparing(NormalController::getName).thenComparing(NormalController::getName2));
        for(var slot:linkSlots){
            if (slot.getCurType() == OpType.LINKADD) {
                opCtxG.addOp(getOp(slot));
            }
            if (slot.getCurType() == OpType.LINKDOWN){
                slot.setCurType(OpType.LINKADD);
                opCtxG.addOp(getOp(slot));
                slot.setCurType(OpType.LINKDOWN);
                opCtxG.addOp(getOp(slot));
            }
        }
        var intfSlots = getSlots(NormalController.CType.INTF, ".*", null);
        intfSlots.sort(Comparator.comparing(NormalController::getName));
        for(var slot:intfSlots){
            if (slot.getCurType() != null)  opCtxG.addOp(getOp(slot));
        }
        var ospfSlots = getSlots(NormalController.CType.OSPF, ".*", null);
        ospfSlots.sort(Comparator.comparing(NormalController::getName));
        for(var slot:ospfSlots){
            if (slot.getCurType() == OpType.NODESETOSPFUP) {
                opCtxG.addOp(getOp(slot));
            }
        }
        var isiSlots = getSlots(NormalController.CType.ISIS, ".*", null);
        isiSlots.sort(Comparator.comparing(NormalController::getName));
        for(var slot:isiSlots){
            if (slot.getCurType() == OpType.NODESETISISUP) {
                opCtxG.addOp(getOp(slot));
            }
        }
        return opCtxG;
    }

    public void reduce(OpCtxG opCtxG){
        for (var opCtx: opCtxG){
            var phyOp = opCtx.getOpPhy();
            if (!checkPreCondition(phyOp)) continue;
            var slot = getSlot(phyOp);
            slot.setCurType(phyOp.Type());
            handleAfterAffects(phyOp);
        }
    }

    public List<NormalController> dumpSlots(){
        return slots;
    }

    public OpCtxG solve(OpCtxG opCtxG) {
        reduce(opCtxG);
        return toOpCtxG();
    }
}
