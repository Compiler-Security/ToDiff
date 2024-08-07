package org.generator.lib.generator.driver;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.ospf.controller.CapacityController;
import org.generator.lib.generator.ospf.controller.NormalController;
import org.generator.lib.generator.ospf.pass.genCorePass;
import org.generator.lib.generator.ospf.pass.genEqualPass;
import org.generator.lib.generator.ospf.pass.genOpPass;
import org.generator.lib.generator.phy.pass.genPhyCorePass;
import org.generator.lib.generator.ospf.pass.shrinkCorePass;
import org.generator.lib.generator.phy.pass.genPhyEqualPass;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.reducer.driver.reducer;
import org.generator.lib.reducer.semantic.CtxOpDef;
import org.generator.util.ran.ranHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class generate {

    public static OpCtxG generatePhyCore(ConfGraph confGraph){
        return genPhyCorePass.solve(confGraph);
    }
    public static OpCtxG generateCore(ConfGraph confGraph){
        var p = new genCorePass();
        var res1 = p.solve(confGraph);
        //FIXME shrinkPass is very slow in huge case
        var q = new shrinkCorePass();
        q.solve(res1, confGraph);
        return reducer.reduceToCore(genCorePass.mergeOpCtxgToOne(res1));
    }

    public static void addIrrOpToController(OpAG opas, NormalController controller){
        Set<OpAnalysis> ctxOps_set = new HashSet<>();
        for(var opa: opas.getOps()){
            if (opa.getCtxOp() != null && CtxOpDef.isSetCtxOp(opa.getCtxOp().getOp().Type())){
                ctxOps_set.add(opa.getCtxOp());
            }
        }
        List<OpAnalysis> ctxOps = ctxOps_set.stream().toList();
        var totalIrrOps = (int) opas.getOps().size() * irrOpRatio;
        for(int i = 0; i < totalIrrOps; i++){
            var ctxOpa = ranHelper.randomElemOfList(ctxOps);
            var op = genOpPass.genRanOpByControl(ctxOpa.getOp().Type() == OpType.IntfName);
            var opa = OpAnalysis.of(op.getOpOspf(), ctxOpa);
            controller.addConfig(opa, expand_ratio - 1, expand_ratio, expand_ratio, 0);
        }
    }
    public static OpCtxG generateEqualOfCore(OpCtxG opCtxG){
        var opas = reducer.reduce(opCtxG);
        var normal_controller = NormalController.of();

        //we add active instructions to the normal_controller
        //these commands will be active in the final
        for(var opa: opas.getOps()){
            normal_controller.addConfig(opa, expand_ratio - 1, expand_ratio + 1, expand_ratio, expand_ratio - 1);
        }


        //we then add other irrelevant instructions to the normal_controller
        //these commands will be dead in the final
        addIrrOpToController(opas, normal_controller);


        var tmp_controller = CapacityController.of(opas.getOps().size(), 0, 0, 1, 0);
        var gen_opag = genEqualPass.solve(normal_controller, tmp_controller);
        return gen_opag.toOpCtxGLeaner();
    }

    public static OpCtxG generateEqualOfPhyCore(OpCtxG opCtxG, double ratio, int maxRound){
        var r = new genPhyEqualPass();
        return r.solve(opCtxG, ratio, maxRound);
    }

    //FIXME(should turn to true when running)
    public static final boolean ran = true;

    //FIXME(should turn to true when testing)
    public static final boolean phyRan = true;

    public static final boolean insertRan = true;

    //insert irrOp, the number is irrOpRatio of activeOp
    public static double irrOpRatio = 0.4;


    public static int expand_ratio = 1;
}
