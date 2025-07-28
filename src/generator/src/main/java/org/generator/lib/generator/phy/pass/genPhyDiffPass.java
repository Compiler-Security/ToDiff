package org.generator.lib.generator.phy.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.generator.phy.controller.NormalController;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpPhy;
import org.generator.lib.item.opg.OpCtxG;

public class genPhyDiffPass {


    OpCtx getOp(OpType type, String name1, String name2){
        var op = new OpPhy(type);
        op.setNAME(name1);
        op.setNAME2(name2);
        return OpCtx.of(op);
    }
    public OpCtxG solve(OpCtxG oldOps, OpCtxG newOps) {
        var olds = phyReducePass.solve(oldOps);
        var news = phyReducePass.solve(newOps);


        //FIXME This is the most simple version
        //We generate deltaNodes, deltaLinks, deltaIntfs, deltaProcotols
        //deltaNodes
        /*
         * OLD      NEW
         ---------------
         UP      UP
         NULL    NULL
         */
        var deltaOpsNode = OpCtxG.Of();
        for(var slot: news.getSlots(NormalController.CType.NODE, ".*", null)){
            //MUST BE NODEADD
            if (olds.getSlot(NormalController.CType.NODE, slot.getName(), null) == null){
                deltaOpsNode.addOp(getOp(OpType.NODEADD, slot.getName(), null));
            }
        }
        for(var slot: olds.getSlots(NormalController.CType.NODE, ".*", null)){
            if (news.getSlot(NormalController.CType.NODE, slot.getName(), null) == null){
                deltaOpsNode.addOp(getOp(OpType.NODEDEL, slot.getName(), null));
            }
        }
        for(var op: deltaOpsNode){olds.reduceOneOp(op.getOpPhy());}

        //deltaLinks
        /*
        OLD     NEW
        ---------------
        ADD     ADD
        DOWN    DOWN
        NULL    NULL

        old search new
        (ADD, DOWN) REMOVE
        (ADD, NULL) REMOVE
        (DOWN, ADD) ADD
        (DOWN, NULL) REMOVE

        new search old
        (NULL, ADD) ADD
        (NULL, DOWN) ADD DOWN
         */
        var deltaOpsLink = OpCtxG.Of();
        for(var slot: olds.getSlots(NormalController.CType.LINK, ".*", ".*")){
            switch (slot.getCurType()){
                case LINKADD -> {
                    var b1 = news.getSlot(NormalController.CType.LINK, slot.getName(), slot.getName2());
                    if (b1 == null || b1.getCurType() == OpType.LINKDOWN){
                        deltaOpsLink.addOp(getOp(OpType.LINKREMOVE, slot.getName(), slot.getName2()));
                    }
                }
                case LINKDOWN -> {
                    var b1 = news.getSlot(NormalController.CType.LINK, slot.getName(), slot.getName2());
                    if (b1 == null) {
                        deltaOpsLink.addOp(getOp(OpType.LINKREMOVE, slot.getName(), slot.getName2()));
                    }else if (b1.getCurType() == OpType.LINKADD){
                        deltaOpsLink.addOp(getOp(OpType.LINKADD, slot.getName(), slot.getName2()));
                    }
                }
                case LINKREMOVE -> {assert  false: "should not be LINKREMOVE";}
            }
        }
        for(var slot: news.getSlots(NormalController.CType.LINK, ".*", ".*")){
            switch (slot.getCurType()){
                case LINKADD -> {
                    var b1 = olds.getSlot(NormalController.CType.LINK, slot.getName(), slot.getName2());
                    if (b1 ==null){
                        deltaOpsLink.addOp(getOp(OpType.LINKADD, slot.getName(), slot.getName2()));
                    }
                }
                case LINKDOWN -> {
                    var b1 = olds.getSlot(NormalController.CType.LINK, slot.getName(), slot.getName2());
                    if (b1 == null) {
                        deltaOpsLink.addOp(getOp(OpType.LINKADD, slot.getName(), slot.getName2()));
                        deltaOpsLink.addOp(getOp(OpType.LINKDOWN, slot.getName(), slot.getName2()));
                    }
                }
                case LINKREMOVE -> {assert  false: "should not be LINKREMOVE";}
            }
        }
        for(var op:deltaOpsLink){olds.reduceOneOp(op.getOpPhy());}

        /*
        OLD     NEW
        -----------
        UP      UP
        DOWN    DOWN
        NULL    NULL
        OLD-UP/DOWN->NEW-NULL is done by link commands before
         */
        var deltaOpsIntf = OpCtxG.Of();
        for(var slot: news.getSlots(NormalController.CType.INTF, ".*", null)){
            switch (slot.getCurType()){
                case INTFUP -> {
                    var b1 = olds.getSlot(NormalController.CType.INTF, slot.getName(), null);
                    if (!(b1 != null && b1.getCurType() == OpType.INTFUP)){
                        deltaOpsIntf.addOp(getOp(OpType.INTFUP, slot.getName(), null));
                    }
                }
                case INTFDOWN -> {
                    var b1 = olds.getSlot(NormalController.CType.INTF, slot.getName(), null);
                    if (!(b1 != null && b1.getCurType() == OpType.INTFDOWN)){
                        deltaOpsIntf.addOp(getOp(OpType.INTFDOWN, slot.getName(), null));
                    }
                }
            }
        }
        for(var op:deltaOpsIntf){olds.reduceOneOp(op.getOpPhy());}

        /*
         * OLD      NEW
         ---------------
            UP      UP
            NULL    NULL
         */
        var deltaOpsProtocol = OpCtxG.Of();
        switch (generate.protocol){
            case OSPF -> {
                for(var slots: olds.getSlots(NormalController.CType.OSPF, ".*", null)){
                    var b1 = news.getSlot(NormalController.CType.OSPF, slots.getName(), null);
                    if (b1 == null){
                        deltaOpsProtocol.addOp(getOp(OpType.NODESETOSPFSHUTDOWN, slots.getName(), null));
                    }
                }
                for(var slots: news.getSlots(NormalController.CType.OSPF, ".*", null)){
                    var b1 = olds.getSlot(NormalController.CType.OSPF, slots.getName(), null);
                    if (b1 == null){
                        deltaOpsProtocol.addOp(getOp(OpType.NODESETOSPFUP, slots.getName(), null));
                    }
                }
            }
            case BABEL -> {
                for(var slots: olds.getSlots(NormalController.CType.BABEL, ".*", null)){
                    var b1 = news.getSlot(NormalController.CType.BABEL, slots.getName(), null);
                    if (b1 == null){
                        deltaOpsProtocol.addOp(getOp(OpType.NODESETBABELSHUTDOWN, slots.getName(), null));
                    }
                }
                for(var slots: news.getSlots(NormalController.CType.BABEL, ".*", null)){
                    var b1 = olds.getSlot(NormalController.CType.BABEL, slots.getName(), null);
                    if (b1 == null){
                        deltaOpsProtocol.addOp(getOp(OpType.NODESETBABELUP, slots.getName(), null));
                    }
                }
            }
            case RIP -> {
                for(var slots: olds.getSlots(NormalController.CType.RIP, ".*", null)){
                    var b1 = news.getSlot(NormalController.CType.RIP, slots.getName(), null);
                    if (b1 == null){
                        deltaOpsProtocol.addOp(getOp(OpType.NODESETRIPSHUTDOWN, slots.getName(), null));
                    }
                }
                for(var slots: news.getSlots(NormalController.CType.RIP, ".*", null)){
                    var b1 = olds.getSlot(NormalController.CType.RIP, slots.getName(), null);
                    if (b1 == null){
                        deltaOpsProtocol.addOp(getOp(OpType.NODESETRIPUP, slots.getName(), null));
                    }
                }
            }
            default -> {
                assert false : "genPhyDiffPass don't support %s protocol".formatted(generate.protocol);
            }
        }
        for(var op:deltaOpsProtocol){olds.reduceOneOp(op.getOpPhy());}
        var deltaOps = OpCtxG.Of();
        deltaOps.addOps(deltaOpsNode.getOps());
        deltaOps.addOps(deltaOpsLink.getOps());
        deltaOps.addOps(deltaOpsIntf.getOps());
        deltaOps.addOps(deltaOpsProtocol.getOps());
        checkEqual(olds.toOpCtxG(), news.toOpCtxG(), deltaOps);
        return deltaOps;
    }

    void checkEqual(OpCtxG olds, OpCtxG news, OpCtxG deltas){
        if (olds.getOps().size() != news.getOps().size()){
            System.out.println(olds);
            System.out.println("--------------");
            System.out.println(news);
            System.out.println("--------------");
            System.out.println(deltas);
            assert false;
        }
        for(int i = 0; i < olds.getOps().size(); i++){
            if (!olds.getOps().get(i).getOpPhy().equals(news.getOps().get(i).getOpPhy())){
                System.out.println(olds.getOps().get(i));
                System.out.println(news.getOps().get(i));
                System.out.println("--------------");
                System.out.println(olds);
                System.out.println("--------------");
                System.out.println(news);
                System.out.println("--------------");
                System.out.println(deltas);
                assert false;
            }
        }
    }
}

