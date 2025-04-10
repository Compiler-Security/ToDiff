package org.generator.lib.generator.driver;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.ospf.controller.NormalController;
import org.generator.lib.generator.ospf.pass.*;
import org.generator.lib.generator.phy.pass.genPhyCorePass;
import org.generator.lib.generator.phy.pass.genPhyEqualPass;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.reducer.driver.reducer;
import org.generator.lib.reducer.pass.phyArgPass;
import org.generator.lib.reducer.semantic.CtxOpDef;
import org.generator.util.ran.ranHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class generate {

    public static OpCtxG generatePhyCore(ConfGraph confGraph){
        return genPhyCorePass.solve(confGraph);
    }

    /**
     *  confg = confg.viewConfGraphOfRouter("r0");
     *  confg.setR_name("r0");
     * @param confGraph
     * @param ismissinglevel
     * @return
     */
    // ismissinglevel means if true, there is an instruction missing "level", and if false, there is no instruction missing "level".
    public static OpCtxG generateCore(ConfGraph confGraph,boolean ismissinglevel){
        List<OpCtxG> res1 = null;
        //MULTI:
        switch (generate.protocol){
            case OSPF -> {
                var p = new genCorePassOspf();
                res1 = p.solve(confGraph, true);}
            case RIP -> {
                var p = new genCorePassRip();
                res1 = p.solve(confGraph, true);
            }
            case ISIS ->{
                var p = new genCorePassIsis();
                res1 = p.solve(confGraph, ismissinglevel);
            }
            case OpenFabric -> {
                var p = new genCorePassOpenfabric();
                res1 = p.solve(confGraph, true);
            }
        }
        //FIXME shrinkPass is very slow in huge case
        var q = new shrinkCorePass();
        q.solve(res1, confGraph);
        return reducer.reduceToCore(OpCtxG.mergeOpCtxgToOne(res1));
    }

    public static void addRemovedOpToController(OpAG opas, NormalController controller){
        Set<OpAnalysis> ctxOps_set = new HashSet<>();
        for(var opa: opas.getOps()){
            if (opa.getCtxOp() != null && CtxOpDef.isSetCtxOp(opa.getCtxOp().getOp().Type())){
                ctxOps_set.add(opa.getCtxOp());
            }
        }
        List<OpAnalysis> ctxOps = ctxOps_set.stream().toList();
        var totalIrrOps = (int) opas.getOps().size() * irrOpRatio;
        Set<OpAnalysis> activeOpAs = new HashSet<>();
        activeOpAs.addAll(controller.getOpas());
        //add totally irrelevant ops
        for(int i = 0; i < totalIrrOps; i++){
            while(true) {
                var ctxOpa = ranHelper.randomElemOfList(ctxOps);
                var op = genOpPass.genRanOpByControl(ctxOpa.getOp().Type() == OpType.IntfName);
                if(op.getOperation().Type()== OpType.PSNPINTERVAL||op.getOperation().Type() == OpType.CSNPINTERVAL||op.getOperation().Type() == OpType.HELLOINTERVAL||op.getOperation().Type() == OpType.HELLOMULTIPLIER||op.getOperation().Type() == OpType.ISISPRIORITY||op.getOperation().Type() == OpType.LSPGENINTERVAL||op.getOperation().Type() == OpType.SPFINTERVAL){
                    op.getOperation().setNAME(ranHelper.randomElemOfList(List.of("level-1","level-2")));
                }
                var opa = OpAnalysis.of(op.getOpOspf(), ctxOpa);
                if (activeOpAs.contains(opa)) continue;
                if (skipCommands(op.getOpOspf().Type())){
                    continue;
                }
                //System.out.println(opa);
                controller.addConfig(opa, expandRatio - 1, expandRatio, expandRatio, expandRatio - 1, OpAnalysis.STATE.REMOVED, OpAnalysis.STATE.REMOVED);
                break;
            }
        }
        var mutateOpNum = (int) opas.getOps().size() * mutateOpRatio;
        //add some mutate op of active op
        for(int i = 0; i < mutateOpNum; i++){
            while(true) {
                var ori_opa = ranHelper.randomElemOfList(opas.getOps());
                assert ori_opa != null : "opa to mutate is supposed to be not null";
                //this skip op should not be mutate commands
                if (skipCommands(ori_opa.getOp().Type())){
                    continue;
                }
                if(ori_opa.getOp().Type() == OpType.IPROUTERISIS || ori_opa.getOp().Type() == OpType.IPROUTERFABRIC){
                    continue;
                }
                var mutate_opa = actionRulePass.mutate(ori_opa);
                if (mutate_opa != null){controller.addConfig(mutate_opa, expandRatio - 1, expandRatio, expandRatio, expandRatio - 1, OpAnalysis.STATE.REMOVED, OpAnalysis.STATE.REMOVED);
                    break;
                }
            }
        }
    }

    public static boolean skipCommands(OpType opType){
        if (generate.fastConvergence){
            switch (opType){
                case TIMERSTHROTTLESPF, IpOspfHelloInter, IpOspfDeadInter, IpOspfDeadInterMulti, RefreshTimer, TimersLsaThrottle, IpOspfRetransInter
                        -> {return true;}
            }
        }
        //We don't change RID for lsa maxage timeout reason
        //See https://github.com/FRRouting/frr/issues/17135
        if (opType == OpType.RID){
            return true;
        }

        //----------ISIS--------------
        if (generate.fastConvergence){
            switch (opType){
                case IPROUTERISIS,NET,ISTYPE,CIRCUITTYPE,HELLOINTERVAL,HELLOMULTIPLIER,PSNPINTERVAL,CSNPINTERVAL,LSPGENINTERVAL,SPFINTERVAL, SETOVERLOADBITONSTARTUP,SETOVERLOADBIT,LSPMTU, ADVERTISEHIGHMETRIC
                        -> {return true;}
            }
        }

        //----------OpenFabric--------------

        if (generate.fastConvergence){
            switch (opType){
                case IPROUTERFABRIC,NET,FABRICHELLOINTERVAL,FABRICHELLOMULTIPLIER,FABRICLSPGENINTERVAL,FABRICSPFINTERVAL
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
    public static OpCtxG generateEqualOfCore(OpCtxG opCtxG, boolean full){
        var opas = reducer.reduce(opCtxG);
        var normal_controller = NormalController.of();

        //we add active instructions to the normal_controller
        //these commands will be active in the final
        for(var opa: opas.getOps()){
            if (skipCommands(opa.getOp().Type())){
                //skipCommands should only be at once
                normal_controller.addConfig(opa, 0, 0, 0, 0, OpAnalysis.STATE.ACTIVE, OpAnalysis.STATE.ACTIVE);
            }else {
                normal_controller.addConfig(opa, expandRatio - 1, expandRatio, expandRatio, expandRatio - 1, OpAnalysis.STATE.ACTIVE, OpAnalysis.STATE.ACTIVE);
            }
        }


        //we then add other removed instructions to the normal_controller
        //these operations will be removed in the final
        //a. some totally irrelevant instructions
        //b. some mutate op of active op
        addRemovedOpToController(opas, normal_controller);


        var gen_opag = genEqualPass.solve(normal_controller, opas);
        //remove original core ops
        if (!full) {
            gen_opag.setOpgroup(gen_opag.getOps().subList(opas.getOps().size(), gen_opag.getOps().size()));
        }
        return gen_opag.toOpCtxGLeaner();
    }

    public static OpCtxG generateEqualOfPhyCore(OpCtxG opCtxG, double ratio, int maxRound){
        var r = new genPhyEqualPass();
        var addPart =  r.solve(opCtxG, ratio, maxRound);
        var totalPart = OpCtxG.Of();
        totalPart.addOps(opCtxG.getOps());
        totalPart.addOps(addPart.getOps());
        var confg0 = new ConfGraph();
        var confg1 = new ConfGraph();
        phyArgPass.solve(opCtxG, confg0);
        phyArgPass.solve(totalPart, confg1);
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
    public static double irrOpRatio = 0.4;

    //insert mutateOp, the number is mutateRatio of activeOp
    public static double mutateOpRatio = 0.4;
    public static int expandRatio = 1;

    //when this is true, we will set
        //timer throttle spf 1 1 1
        //ip ospf dead-interval multiplier 10
    //this is worked by set topo Attribute
    public static final boolean fastConvergence = true;

    public static enum Protocol{
        OSPF,
        ISIS,
        RIP,
        OpenFabric
    }

    public static Protocol protocol = Protocol.OSPF;
}
