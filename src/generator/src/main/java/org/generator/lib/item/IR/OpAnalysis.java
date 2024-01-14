package org.generator.lib.item.IR;

/***
 * OpAnalysis for OpOspf
 * extends OpOspf
 * OpOspf + ctxOp(OpAnalysis) + state + lineNo
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

}
