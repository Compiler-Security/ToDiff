package org.generator.lib.item.IR;

import org.generator.lib.item.opg.OpOspfG;
import org.generator.lib.operation.operation.OpType;

public class OpOspf extends OpBase{
    OpOspf(OpType type) {
        super(type);
    }

    /**
     * create the OpOspf with OpType type
     * @param type OpOspf's type
     * @return OpOspf with OpType type
     */
    public static OpOspf of(OpType type){
        return new OpOspf(type);
    }

    /**
     * create the default OpOspf, whose type is invalid
     * @return default OpOspf
     */
    public static OpOspf of(){
        return new OpOspf(OpType.INVALID);
    }

    public OpOspf getCtxOp() {
        return ctxOp;
    }

    public void setCtxOp(OpOspf ctxOp) {
        this.ctxOp = ctxOp;
    }

    public OpOspfG getOpg() {
        return opg;
    }

    public void setOpg(OpOspfG opg) {
        this.opg = opg;
    }

    /**
     * ctxOp of this op, this should equal to opg's ctxOp
     */
    private OpOspf ctxOp;

    /**
     * this op's Opg
     */
    private OpOspfG opg;
}
