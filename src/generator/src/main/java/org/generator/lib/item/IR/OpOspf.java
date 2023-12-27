package org.generator.lib.item.IR;

import org.generator.lib.operation.operation.OpType;

public class OpOspf extends OpBase{
    OpOspf(){}
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
}
