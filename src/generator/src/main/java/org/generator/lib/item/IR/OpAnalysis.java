package org.generator.lib.item.IR;

import org.generator.lib.operation.operation.OpType;
import org.generator.lib.reducer.semantic.CtxOpDef;

import java.util.Objects;

/***
 * OpAnalysis for OpOspf
 * extends OpOspf
 * OpOspf + ctxOp(OpAnalysis) + state + lineNo
 *
 * ctxOp should be constant
 * OpOspf shoule be constant
 *
 * ctxOp's ctxOp should always be null
 */
public class OpAnalysis{
    public OpOspf getOp() {
        return op;
    }

    public void setOp(OpOspf op) {
        this.op = op;
    }

    public OpOspf op;

    /**
     *
     * @param opOspf
     * @return OpAnalysis
     * create OpAnalysis, field opOspf + STATE(INIT) + lineNo(-1)
     * if opOspf is ctxOp, set ctxOp to itself
     */
    public static OpAnalysis of(OpOspf opOspf){
        var opA = new OpAnalysis();
        opA.op = opOspf;
        opA.state = STATE.INIT;
        opA.unsetLine();
        return opA;
    }

    /**
     *
     * @param opOspf opOspf
     * @param lineNo lineNo
     * @param state state
     * @return
     * create OpAnalysis and set lineNo and state
     */
    public static OpAnalysis of(OpOspf opOspf, int lineNo, STATE state){
        var opA = of(opOspf);
        opA.setLineNo(lineNo);
        opA.setState(state);
        return opA;
    }


    public enum STATE{
        INIT,
        SUBMITTED,
        REMOVED,
        ACTIVE
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public STATE state;

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public void unsetLine(){
        this.lineNo = -1;
    }

    public boolean hasLine(){
        return lineNo != -1;
    }
    public int lineNo;

    public OpAnalysis getCtxOp() {
        return ctxOp;
    }

    public void setCtxOp(OpAnalysis ctxOp) {
        this.ctxOp = ctxOp;
    }

    public OpAnalysis ctxOp;

    //FIXME this is not elegant
    /**
     * Two OpA is equal if it's opCtx is equal,
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpAnalysis that = (OpAnalysis) o;
        if (CtxOpDef.isCtxOp(this.op.Type())) return Objects.equals(op, that.op);
        return Objects.equals(op, that.op) && Objects.equals(ctxOp, that.ctxOp);
    }

    //FIXME this is not elegant
    @Override
    public int hashCode() {
        if (CtxOpDef.isCtxOp(this.op.Type())) return Objects.hash(op, null);
        else return Objects.hash(op, ctxOp);
    }

    /**
     * Copy no ctxOp
     * This copy will deep copy ospfOp, ctxOp, others to init
     * @return
     */
    public OpAnalysis copy(){
        var opa = OpAnalysis.of(this.op.copy());
        opa.setCtxOp(ctxOp);
        return opa;
    }

    @Override
    public String toString() {
        //TODO
        return "";
    }
}
