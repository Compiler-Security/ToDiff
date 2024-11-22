package org.generator.lib.item.IR;

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
public class OpAnalysis_ISIS{
    public OpIsis getOp() {
        return op;
    }

    public void setOp(OpIsis op) {
        this.op = op;
    }

    public OpIsis op;

    /**
     *
     * @param opIsis
     * @return OpAnalysis
     * create OpAnalysis, field opOspf + STATE(INIT) + lineNo(-1)
     * if opOspf is ctxOp, set ctxOp to itself
     */
    public static OpAnalysis_ISIS of(OpIsis opIsis){
        var opA = new OpAnalysis_ISIS();
        opA.op = opIsis;
        opA.state = STATE.INIT;
        opA.unsetLine();
        opA.ctxOp = null;
        return opA;
    }

    /**
     *
     * @param opIsis
     * @param ctxOp
     * @return
     */
    public static OpAnalysis_ISIS of(OpIsis opIsis, OpAnalysis_ISIS ctxOp){
        var opa = OpAnalysis_ISIS.of(opIsis);
        opa.setCtxOp(ctxOp);
        return opa;
    }

    /**
     *
     * @param opIsis opIsis
     * @param lineNo lineNo
     * @param state state
     * @return
     * create OpAnalysis and set lineNo and state
     */
    public static OpAnalysis_ISIS of(OpIsis opIsis, int lineNo, STATE state){
        var opA = of(opIsis);
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

    public OpAnalysis_ISIS getCtxOp() {
        return ctxOp;
    }

    public void setCtxOp(OpAnalysis_ISIS ctxOp) {
        this.ctxOp = ctxOp;
    }

    public OpAnalysis_ISIS ctxOp;


    public boolean ctxOpEqual(OpAnalysis_ISIS a, OpAnalysis_ISIS b){
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.getCtxOp().getOp().equals(b.getCtxOp().getOp());
    }
    /**
     * Two OpA is equal iff it's op and ctxOp is equal,
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpAnalysis_ISIS that = (OpAnalysis_ISIS) o;
        return Objects.equals(op, that.op) && ctxOpEqual(getCtxOp(), that.getCtxOp());
    }

    //XXX this is not elegant
    @Override
    public int hashCode() {
        if (ctxOp == null) return Objects.hash(op, null);
        else return Objects.hash(op, ctxOp.getOp());
    }

    /**
     * Copy Opa
     * Op is shallow copy
     * @return
     */
    public OpAnalysis_ISIS copy(){
        var opa = OpAnalysis_ISIS.of(this.op);
        opa.setCtxOp(ctxOp);
        opa.setState(this.state);
        opa.setLineNo(this.lineNo);
        return opa;
    }

    @Override
    public String toString() {
        if (ctxOp == null)
            return  op.toString() + "(" + state + ")[null]";
        else return op.toString() + "(" + state + ")" + "[" + String.format("%s", ctxOp.getOp()) + "]";
    }
}
