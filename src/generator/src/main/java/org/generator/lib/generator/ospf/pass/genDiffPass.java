package org.generator.lib.generator.ospf.pass;


import org.generator.lib.generator.ospf.controller.NormalController;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.reducer.driver.reducer;

public class genDiffPass {
    public OpCtxG solve(OpCtxG oldOps, OpCtxG newOps) {
        var oldOpas = reducer.reduce(oldOps);
        var newOpas = reducer.reduce(newOps);
        var normal_controller = NormalController.of();

        for(var opa: oldOpas) {
            if (!newOpas.hasOpA(opa)){
                normal_controller.addConfig(opa, 0, 0, 1, 0, OpAnalysis.STATE.ACTIVE, OpAnalysis.STATE.REMOVED);
            }
        }

        for(var opa: newOpas) {
            if (!oldOpas.hasOpA(opa)){
                normal_controller.addConfig(opa, 0, 1, 0, 0, OpAnalysis.STATE.REMOVED, OpAnalysis.STATE.ACTIVE);
            }
        }

        var gen_opag = genEqualPass.solve(normal_controller, oldOpas);
        gen_opag.setOpgroup(gen_opag.getOps().subList(newOpas.getOps().size(), gen_opag.getOps().size()));
        return gen_opag.toOpCtxGLeaner();
    }
}
