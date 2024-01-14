package org.generator.lib.item.IR;

import org.generator.lib.operation.operation.OpType;

/***
 * OpAnalysis for OpOspf
 * extends OpOspf
 * OpOspf + ctxOp(OpAnalysis) + state + lineNo
 */
public class OpAnalysis{
    public OpOspf getOpOspf() {
        return opOspf;
    }

    public void setOpOspf(OpOspf opOspf) {
        this.opOspf = opOspf;
    }

    public OpOspf opOspf;

    /**
     *
     * @param opOspf
     * @return OpAnalysis
     * create OpAnalysis, field opOspf + STATE(INIT) + lineNo(-1)
     */
    public static OpAnalysis of(OpOspf opOspf){
        var opA = new OpAnalysis();
        opA.opOspf = opOspf;
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

}
