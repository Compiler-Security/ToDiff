package org.generator.lib.item.opg;

import org.generator.lib.generator.ospf.pass.genCorePass;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.IR.OpOspf;
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

    public static OpAG of(List<OpAnalysis> opags){
        var opag = OpAG.of();
        opag.getOps().addAll(opags);
        return opag;
    }

    /**
     * This will create a new OpAG from this
     * Each OpA is copied, however, OspfOp is not copied
     * @return
     */
    @NotNull
    public OpAG copy(){
        var opAG = new OpAG();
        this.getOps().forEach(opa -> opAG.addOp(opa.copy()));
        opAG.updateOpStatus();
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
    //FIXME this is not elegant
    /**
     * This function filter the active ops, not include unset op, not COPY!
     * @return
     */
    public OpAG activeSetView(){
        var opAG = OpAG.of();
        this.getOps().stream().filter(opa -> opa.state == OpAnalysis.STATE.ACTIVE && opa.op.Type().isSetOp()).forEach(opAG::addOp);
        return opAG;
    }

    /**
     * one set op only last version
     * @return
     */
    public List<OpAnalysis> setList(){
        return OpStatus.keySet().stream().filter(opa -> opa.op.Type().isSetOp()).toList();
    }

    Map<OpAnalysis, OpAnalysis.STATE> OpStatus;
    /**
     * This method will reduce itself and return itself
     * @return
     */
    public void reduce(){
        reducer.reduce(this);
        updateOpStatus();
    }

    private void updateOpStatus(){
        OpStatus = new HashMap<>();
        getOps().forEach(opa -> {
            if (opa.state == OpAnalysis.STATE.ACTIVE) OpStatus.put(opa, OpAnalysis.STATE.ACTIVE);
            else OpStatus.putIfAbsent(opa, opa.getState());
        });
    }


    private OpCtxG toOpCtx(OpAG opAG){
        var opaG = opAG;
        Map<OpOspf, OpCtxG> merge = new HashMap<>();
        for(var opa: opaG.getOps()){
            var ctxOp = opa.getCtxOp().getOp();
            if (!merge.containsKey(ctxOp)){
                var o = OpCtxG.Of();
                o.addOp(ctxOp.getOpCtx());
                merge.put(ctxOp, o);
            }
            merge.get(ctxOp).addOp(opa.getOp().getOpCtx());
        }
        return genCorePass.mergeOpCtxgToOne(merge.values().stream().toList());
    }

    /**
     * This function will get active set OpCtxG from OpAG
     */
    public OpCtxG toOpCtxGALL(){
       return toOpCtx(this);
    }

    public OpCtxG toOpCtxGActiveSet(){
        return toOpCtx(activeSetView());
    }

    public OpCtxG toOpCtxGLeaner(){
        var opCtxg = OpCtxG.Of();
        OpOspf preCtxOp = null;
        for(var opa: getOps()){
            if (opa.getCtxOp() != null && !opa.getCtxOp().getOp().equals(preCtxOp)){
                opCtxg.addOp(opa.getCtxOp().getOp().getOpCtx());
                preCtxOp = opa.getCtxOp().getOp();
            }
            opCtxg.addOp(opa.getOp().getOpCtx());
        }
        return opCtxg;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("[\n");
        for(var opa: getOps()){
            b.append(opa);
            b.append("(");
            b.append(opa.state);
            b.append(")");
            b.append("\n");
        }
        b.append("]\n");
        return b.toString();
    }
}
