package org.generator.lib.item.IR;

import org.generator.lib.frontend.lexical.OpType_isis;

public class OpPhy_ISIS extends OpBase_ISIS{
    public OpPhy_ISIS(OpType_isis type) {
        super(type);
    }
    public static OpPhy_ISIS Of(){return new OpPhy_ISIS(OpType_isis.INVALID);}
}
