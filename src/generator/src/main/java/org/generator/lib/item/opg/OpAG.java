package org.generator.lib.item.opg;

import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.reducer.driver.reducer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Normal OpAG
 * the opAg don't have setCtxOp, every opA has ctxOp
 *
 * Expand OpAG
 * the opAG have setCtxop, every opA's ctxOp is null
 */
public class OpAG extends BaseOpG<OpAnalysis>{
    public OpCtxG getRemainOps(){
        var ctxg = OpCtxG.Of();
        getOps().stream().filter(opa -> opa.state == OpAnalysis.STATE.ACTIVE && opa.getOp().Type().isSetOp()).forEach(opa -> ctxg.addOp(opa.getOp().getOpCtx()));
        return ctxg;
    }
    public OpAG(){
        OpStatus = new HashMap<>();
    }
    public static OpAG of(){
        return new OpAG();
    }

    /**
     * This copy will crate new OpAG from this
     * Each OpA is copied as well
     * @return
     */
    @NotNull
    public OpAG copy(){
        var opAG = new OpAG();
        this.getOps().forEach(op -> opAG.addOp(op.copy()));
        //FIXME this copy is very slow!!!
        opAG.reduce();
        return opAG;
    }

    /**
     * find the  opA == given opA(in)
     * If not found, return null
     * @param opA
     * @return
     */
    @Nullable
    public OpAnalysis findOpA(OpAnalysis opA){
       return  OpStatus.keySet().stream().filter(opa -> opa.equals(opA)).findAny().orElse(null);
    }

    /**
     * find the given opA status, if the opA not in OpAG, return INIT
     * @param opA
     * @return
     */
    public OpAnalysis.STATE getOpAStatus(OpAnalysis opA){
        return OpStatus.getOrDefault(opA, OpAnalysis.STATE.INIT);
    }
    /**
     * This function filter the active ops, not include unset op, not COPY!
     * @return
     */
    public OpAG activeView(){
        var opAG = OpAG.of();
        this.getOps().stream().filter(opa -> opa.state == OpAnalysis.STATE.ACTIVE && opa.op.Type().isSetOp()).forEach(opAG::addOp);
        return opAG;
    }

    public List<OpAnalysis> setOpView(){
        return this.getOps().stream().filter(opa -> opa.op.Type().isSetOp()).toList();
    }
    Map<OpAnalysis, OpAnalysis.STATE> OpStatus;
    /**
     * This method will reduce itself and return itself
     * @return
     */
    public OpAG reduce(){
        reducer.reduce(this);
        OpStatus = new HashMap<>();
        getOps().forEach(opa -> {
            if (opa.state == OpAnalysis.STATE.ACTIVE) OpStatus.put(opa, OpAnalysis.STATE.ACTIVE);
            else OpStatus.putIfAbsent(opa, opa.getState());
        });
        return this;
    }

    @Override
    public String toString() {
        return "";
    }
}
