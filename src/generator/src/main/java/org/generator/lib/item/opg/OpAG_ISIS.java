// package org.generator.lib.item.opg;

// import org.generator.lib.generator.isis.pass.genCorePass;
// import org.generator.lib.item.IR.OpAnalysis_ISIS;
// import org.generator.lib.item.IR.OpIsis;
// import org.generator.lib.reducer.driver.reducer;
// import org.jetbrains.annotations.NotNull;
// import org.jetbrains.annotations.Nullable;

// import java.util.*;
// import java.util.stream.Collectors;

// /**
//  * Normal OpAG
//  * the opAg don't have setCtxOp, every opA has ctxOp
//  *
//  * Expand OpAG
//  * the opAG have setCtxop, every opA's ctxOp is null
//  */
// public class OpAG_ISIS extends BaseOpG<OpAnalysis_ISIS>{
//     public OpCtxG_ISIS getRemainOps(){
//         var ctxg = OpCtxG_ISIS.Of();
//         getOps().stream().filter(opa -> opa.state == OpAnalysis_ISIS.STATE.ACTIVE && opa.getOp().Type().isSetOp()).forEach(opa -> ctxg.addOp(opa.getOp().getOpCtx()));
//         return ctxg;
//     }
//     public OpAG_ISIS(){
//         OpStatus = new HashMap<>();
//     }
//     public static OpAG_ISIS of(){
//         return new OpAG_ISIS();
//     }

//     public static OpAG_ISIS of(List<OpAnalysis_ISIS> opags){
//         var opag = OpAG_ISIS.of();
//         opag.getOps().addAll(opags);
//         return opag;
//     }

//     /**
//      * This will create a new OpAG from this
//      * Each OpA is copied, however, OspfOp is not copied
//      * @return
//      */
//     @NotNull
//     public OpAG_ISIS copy(){
//         var opAG = new OpAG_ISIS();
//         this.getOps().forEach(opa -> opAG.addOp(opa.copy()));
//         opAG.updateOpStatus();
//         return opAG;
//     }

//     /**
//      * get different opAnalysis
//      * @return
//      */
//     public Set<OpAnalysis_ISIS> getSlots(){
//         return new HashSet<>(OpStatus.keySet());
//     }
//     /**
//      * find the  opA == given opA(in)
//      * If not found, return null
//      * @param opA
//      * @return
//      */
//     @Nullable
//     public OpAnalysis_ISIS findOpA(OpAnalysis_ISIS opA){
//        return  OpStatus.keySet().stream().filter(opa -> opa.equals(opA)).findAny().orElse(null);
//     }

//     /**
//      * check if opag has opa
//      * @param opA
//      * @return
//      */
//     public boolean hasOpA(OpAnalysis_ISIS opA){
//         return findOpA(opA) != null;
//     }

//     /**
//      * find the given opA status, if the opA not in OpAG, return INIT
//      * @param opA
//      * @return
//      */
//     public OpAnalysis_ISIS.STATE getOpAState(OpAnalysis_ISIS opA){
//         return OpStatus.getOrDefault(opA, OpAnalysis_ISIS.STATE.REMOVED);
//     }

//     //FIXME this is not elegant
//     /**
//      * This function filter the active ops, not include unset op, not COPY!
//      * @return
//      */
//     public OpAG_ISIS activeSetView(){
//         var opAG = OpAG_ISIS.of();
//         this.getOps().stream().filter(opa -> opa.state == OpAnalysis_ISIS.STATE.ACTIVE && opa.op.Type().isSetOp()).forEach(opAG::addOp);
//         return opAG;
//     }

//     /**
//      * one set op only last version
//      * @return
//      */
//     public List<OpAnalysis_ISIS> setList(){
//         return OpStatus.keySet().stream().filter(opa -> opa.op.Type().isSetOp()).toList();
//     }

//     public Map<OpAnalysis_ISIS, OpAnalysis_ISIS.STATE> getOpStatus() {
//         return OpStatus;
//     }

//     Map<OpAnalysis_ISIS, OpAnalysis_ISIS.STATE> OpStatus;
//     /**
//      * This method will reduce itself and return itself
//      * @return
//      */
//     public void reduce(){
//         reducer.reduce(this);
//         updateOpStatus();
//     }

//     private void updateOpStatus(){
//         //FIXME !!! IS THIS RIGHT?
//         OpStatus = new HashMap<>();
// //        getOps().forEach(opa -> {
// //            if (opa.state == OpAnalysis.STATE.ACTIVE) OpStatus.put(opa, OpAnalysis.STATE.ACTIVE);
// //            else OpStatus.putIfAbsent(opa, opa.getState());
// //        });
//         getOps().forEach(opa -> {OpStatus.put(opa, opa.getState());});
//     }


//     private OpCtxG_ISIS toOpCtx(OpAG_ISIS opAG){
//         var opaG = opAG;
//         Map<OpIsis, OpCtxG_ISIS> merge = new HashMap<>();
//         for(var opa: opaG.getOps()){
//             var ctxOp = opa.getCtxOp().getOp();
//             if (!merge.containsKey(ctxOp)){
//                 var o = OpCtxG_ISIS.Of();
//                 o.addOp(ctxOp.getOpCtx());
//                 merge.put(ctxOp, o);
//             }
//             merge.get(ctxOp).addOp(opa.getOp().getOpCtx());
//         }
//         return genCorePass.mergeOpCtxgToOne(merge.values().stream().toList());
//     }

//     /**
//      * This function will get active set OpCtxG from OpAG
//      */
//     public OpCtxG_ISIS toOpCtxGALL(){
//        return toOpCtx(this);
//     }

//     public OpCtxG_ISIS toOpCtxGActiveSet(){
//         return toOpCtx(activeSetView());
//     }

//     public OpCtxG_ISIS toOpCtxGLeaner(){
//         var opCtxg = OpCtxG_ISIS.Of();
//         OpIsis preCtxOp = null;
//         for(var opa: getOps()){
//             if (opa.getCtxOp() != null && !opa.getCtxOp().getOp().equals(preCtxOp)){
//                 opCtxg.addOp(opa.getCtxOp().getOp().getOpCtx());
//                 preCtxOp = opa.getCtxOp().getOp();
//             }
//             opCtxg.addOp(opa.getOp().getOpCtx());
//         }
//         return opCtxg;
//     }

//     @Override
//     public boolean equals(Object o) {
//         if (this == o) return true;
//         if (o == null || getClass() != o.getClass()) return false;
//         OpAG_ISIS opAG = (OpAG_ISIS) o;
//         return Objects.equals(OpStatus, opAG.OpStatus);
//     }

//     @Override
//     public int hashCode() {
//         return Objects.hash(OpStatus);
//     }

//     @Override
//     public String toString() {
//         StringBuilder b = new StringBuilder();
//         b.append("[\n");
//         for(var opa: getOps()){
//             b.append(opa);
//             b.append("(");
//             b.append(opa.state);
//             b.append(")");
//             b.append("\n");
//         }
//         b.append("]\n");
//         return b.toString();
//     }
// }
