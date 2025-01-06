package org.generator.lib.generator.driver;

import org.generator.lib.frontend.lexical.OpType_isis;
import org.generator.lib.generator.isis.controller.CapacityController_ISIS;
import org.generator.lib.generator.isis.controller.NormalController_ISIS;
import org.generator.lib.generator.isis.pass.*;
import org.generator.lib.generator.phy.pass.genPhyCorePass_ISIS;
import org.generator.lib.generator.phy.pass.genPhyEqualPass_ISIS;
import org.generator.lib.item.IR.OpAnalysis_ISIS;
import org.generator.lib.item.opg.OpAG_ISIS;
import org.generator.lib.item.opg.OpCtxG_ISIS;
import org.generator.lib.item.conf.graph.ConfGraph_ISIS;
import org.generator.lib.reducer.driver.reducer_ISIS;
import org.generator.lib.reducer.pass.phyArgPass_ISIS;
import org.generator.lib.reducer.semantic.CtxOpDef_ISIS;
import org.generator.util.ran.ranHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class generate_ISIS {

    public static OpCtxG_ISIS generatePhyCore(ConfGraph_ISIS confGraph){
        return genPhyCorePass_ISIS.solve(confGraph);
    }

    /**
     *  confg = confg.viewConfGraphOfRouter("r0");
     *  confg.setR_name("r0");
     * @param confGraph
     * @param isfull
     * @return
     */
    // isfull means if true, there is an instruction missing "level", and if false, there is no instruction missing "level".
    public static OpCtxG_ISIS generateCore(ConfGraph_ISIS confGraph,boolean isfull){
        var p = new genCorePass_ISIS(); 
        var res1 = p.solve(confGraph, isfull);
        //FIXME shrinkPass is very slow in huge case
        var q = new shrinkCorePass_ISIS();
        q.solve(res1, confGraph);
        return reducer_ISIS.reduceToCore(genCorePass_ISIS.mergeOpCtxgToOne(res1));
    }

    public static void addRemovedOpToController(OpAG_ISIS opas, NormalController_ISIS controller){
        Set<OpAnalysis_ISIS> ctxOps_set = new HashSet<>();
        for(var opa: opas.getOps()){
            if (opa.getCtxOp() != null && CtxOpDef_ISIS.isSetCtxOp(opa.getCtxOp().getOp().Type())){
                ctxOps_set.add(opa.getCtxOp());
            }
        }
        List<OpAnalysis_ISIS> ctxOps = ctxOps_set.stream().toList();
        var totalIrrOps = (int) opas.getOps().size() * irrOpRatio;
        Set<OpAnalysis_ISIS> activeOpAs = new HashSet<>();
        activeOpAs.addAll(controller.getOpas());
        //add totally irrelevant ops
        for(int i = 0; i < totalIrrOps; i++){
            while(true) {
                var ctxOpa = ranHelper.randomElemOfList(ctxOps);
                var op = genOpPass_ISIS.genRanOpByControl(ctxOpa.getOp().Type() == OpType_isis.IntfName);
                if(op.getOperation().Type()== OpType_isis.PSNPINTERVAL||op.getOperation().Type() == OpType_isis.CSNPINTERVAL||op.getOperation().Type() == OpType_isis.HELLOINTERVAL||op.getOperation().Type() == OpType_isis.HELLOMULTIPLIER||op.getOperation().Type() == OpType_isis.ISISPRIORITY){
                    op.getOperation().setNAME(ranHelper.randomElemOfList(List.of("level-1","level-2")));
                }
                var opa = OpAnalysis_ISIS.of(op.getOpIsis(), ctxOpa);
                if (activeOpAs.contains(opa)) continue;
                if (skipCommands(op.getOpIsis().Type())){
                    continue;
                }
                //System.out.printf("add totally irrelevant op %s\n", opa.op);
                controller.addConfig(opa, expandRatio - 1, expandRatio, expandRatio, expandRatio - 1, OpAnalysis_ISIS.STATE.REMOVED, OpAnalysis_ISIS.STATE.REMOVED);
                break;
            }
        }
        var mutateOpNum = (int) opas.getOps().size() * mutateOpRatio;
        //test
        // var ori_opas = new OpAnalysis_ISIS[3];
        // for(var opa: opas.getOps()){
        //     if (opa.getOp().Type() == OpType_isis.IPAddr){
        //         ori_opas[0] = opa;
        //         break;
        //     }
        // }

        // for(var opa: opas.getOps()){
        //     if (opa.getOp().Type() == OpType_isis.ISISPRIORITY && opa.getOp().getNAME().equals("level-2")){
        //         ori_opas[1] = opa;
        //         break;
        //     }
        // }
        // for(var opa: opas.getOps()){
        //     if (opa.getOp().Type() == OpType_isis.ISISPRIORITY && opa.getOp().getNAME().equals("level-1")){
        //         ori_opas[2] = opa;
        //         break;
        //     }
        // }
        //add some mutate op of active op
        for(int i = 0; i < mutateOpNum; i++){
            while(true) {
                var ori_opa = ranHelper.randomElemOfList(opas.getOps());
                //System.out.printf("ori_opa %s\n", ori_opa.op);
                //var ori_opa = ori_opas[i];
                assert ori_opa != null : "opa to mutate is supposed to be not null";
                //this skip op should not be mutate commands
                if (skipCommands(ori_opa.getOp().Type())){
                    continue;
                }
                if(ori_opa.getOp().Type() == OpType_isis.IPROUTERISIS){
                    continue;
                }
                var mutate_opa = actionRulePass_ISIS.mutate(ori_opa);
                if (mutate_opa != null){controller.addConfig(mutate_opa, expandRatio - 1, expandRatio, expandRatio, expandRatio - 1, OpAnalysis_ISIS.STATE.REMOVED, OpAnalysis_ISIS.STATE.REMOVED);
                    //System.out.printf("add mutate op %s\n", mutate_opa.op);
                    break;
                }
            }
        }
    }

    public static boolean skipCommands(OpType_isis opType){
        if (generate_ISIS.fastConvergence){
            switch (opType){
                case IPROUTERISIS,NET,HELLOINTERVAL,HELLOMULTIPLIER
                        -> {return true;}
            }
        }
        return false;
    }

    /***
     * if full, init opCtxG is  in the head of generate opCtxG
     * @param opCtxG
     * @param full
     * @return
     */
    public static OpCtxG_ISIS generateEqualOfCore(OpCtxG_ISIS opCtxG, boolean full){
        var opas = reducer_ISIS.reduce(opCtxG);
        var normal_controller = NormalController_ISIS.of();

        //we add active instructions to the normal_controller
        //these commands will be active in the final
        for(var opa: opas.getOps()){
            if (skipCommands(opa.getOp().Type())){
                //skipCommands should only be at once
                normal_controller.addConfig(opa, 0, 0, 0, 0, OpAnalysis_ISIS.STATE.ACTIVE, OpAnalysis_ISIS.STATE.ACTIVE);
            }else {
                normal_controller.addConfig(opa, expandRatio - 1, expandRatio, expandRatio, expandRatio - 1, OpAnalysis_ISIS.STATE.ACTIVE, OpAnalysis_ISIS.STATE.ACTIVE);
            }
        }


        //we then add other removed instructions to the normal_controller
        //these operations will be removed in the final
        //a. some totally irrelevant instructions
        //b. some mutate op of active op
        addRemovedOpToController(opas, normal_controller);


        var gen_opag = genEqualPass_ISIS.solve(normal_controller, opas);
        //System.out.printf("gen_opag  %s\n", gen_opag);
        //System.out.printf("opas  %s\n", opas);
        //remove original core ops
        if (!full) {
            gen_opag.setOpgroup(gen_opag.getOps().subList(opas.getOps().size(), gen_opag.getOps().size()));
        }
        //System.out.printf("gen_opag  %s\n", gen_opag);
        return gen_opag.toOpCtxGLeaner();
    }

    public static OpCtxG_ISIS generateEqualOfPhyCore(OpCtxG_ISIS opCtxG, double ratio, int maxRound){
        var r = new genPhyEqualPass_ISIS();
        var addPart =  r.solve(opCtxG, ratio, maxRound);
        var totalPart = OpCtxG_ISIS.Of();
        totalPart.addOps(opCtxG.getOps());
        totalPart.addOps(addPart.getOps());
        var confg0 = new ConfGraph_ISIS();
        var confg1 = new ConfGraph_ISIS();
        phyArgPass_ISIS.solve(opCtxG, confg0);
        phyArgPass_ISIS.solve(totalPart, confg1);
        if (!confg0.equals(confg1)){
            System.out.println(confg0);
            System.out.println("==============");
            System.out.println(confg1);
            assert false : "phy conf not equal!";
        }
        return addPart;
    }

    //FIXME(should turn to true when running)
    public static final boolean ran = true;

    //FIXME(should turn to true when testing)
    public static final boolean phyRan = true;

    public static final boolean insertRan = true;

    //insert irrOp, the number is irrOpRatio of activeOp
    public static double irrOpRatio = 0.4;//0.4;

    //insert mutateOp, the number is mutateRatio of activeOp
    public static double mutateOpRatio = 0.4;//0.4;
    public static int expandRatio = 1;

    //when this is true, we will set
        //timer throttle spf 1 1 1
        //ip ospf dead-interval multiplier 10
    //this is worked by set topo Attribute
    public static final boolean fastConvergence = true;
}
