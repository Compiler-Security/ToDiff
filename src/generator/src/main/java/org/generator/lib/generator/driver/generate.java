package org.generator.lib.generator.driver;

import org.generator.lib.generator.controller.CapacityController;
import org.generator.lib.generator.controller.NormalController;
import org.generator.lib.generator.pass.genCorePass;
import org.generator.lib.generator.pass.genEqualPass;
import org.generator.lib.generator.pass.shrinkCorePass;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.topo.graph.ConfGraph;
import org.generator.lib.reducer.driver.reducer;

public class generate {
    public static OpCtxG generateCore(ConfGraph confGraph){
        var p = new genCorePass();
        var res1 = p.solve(confGraph);
        var q = new shrinkCorePass();
        q.solve(res1, confGraph);
        return reducer.reduceToCore(genCorePass.mergeOpCtxgToOne(res1));
    }

    public static OpCtxG generateEqualOfCore(OpCtxG opCtxG, int expand_ratio){
        var opas = reducer.reduce(opCtxG);
        var normal_controller = NormalController.of();
        for(var opa: opas.getOps()){
            normal_controller.addConfig(opa, expand_ratio, expand_ratio + 1, expand_ratio, expand_ratio);
        }
        var tmp_controller = CapacityController.of(opas.getOps().size(), 0, 0, 1, 0);
        var gen_opag = genEqualPass.solve(normal_controller, tmp_controller);
        return gen_opag.toOpCtxGALL();
    }
}
