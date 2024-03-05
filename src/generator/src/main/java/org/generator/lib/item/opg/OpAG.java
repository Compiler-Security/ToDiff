package org.generator.lib.item.opg;

import org.generator.lib.item.IR.OpAnalysis;

public class OpAG extends BaseOpG<OpAnalysis>{
    public OpCtxG getRemainOps(){
        var ctxg = OpCtxG.Of();
        getOps().stream().filter(opa -> opa.state == OpAnalysis.STATE.ACTIVE && opa.getOp().Type().isSetOp()).forEach(opa -> ctxg.addOp(opa.getOp().getOpCtx()));
        return ctxg;
    }
}
