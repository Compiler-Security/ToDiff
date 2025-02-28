package org.generator.lib.item.IR;

import org.generator.lib.frontend.lexical.OpType;

public class OpPhy extends OpBase{
    public OpPhy(OpType type) {
        super(type);
    }
    public static OpPhy Of(){return new OpPhy(OpType.INVALID);}
}
