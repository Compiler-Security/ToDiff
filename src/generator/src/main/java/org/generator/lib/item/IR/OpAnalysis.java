package org.generator.lib.item.IR;

import org.generator.lib.operation.operation.OpType;

/***
 * OpAnalysis for OpOspf
 * extends OpOspf
 * OpOspf + ctxOp(OpAnalysis) + state + lineNo
 */
public class OpAnalysis extends OpOspf{
    OpAnalysis(OpType type) {
        super(type);
        state = STATE.INIT;
        unsetLine();
    }
    public static OpAnalysis of(OpType type){
        return new OpAnalysis(type);
    }

    /**
     * create the default OpAnalysis, whose type is invalid
     * @return default OpAnalysis INVALID
     */
    public static OpAnalysis of(){
        return new OpAnalysis(OpType.INVALID);
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

    STATE state;

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
    int lineNo;

}
