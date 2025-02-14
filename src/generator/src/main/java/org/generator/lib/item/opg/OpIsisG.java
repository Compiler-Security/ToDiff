package org.generator.lib.item.opg;

import org.generator.lib.item.IR.OpIsis;
import org.generator.lib.frontend.lexical.OpType_isis;

import java.util.List;

public class OpIsisG extends BaseOpG<OpIsis>{

    private OpIsisG(){super();}

    private OpIsisG(OpIsis ctxOp){
        super();
        this.CtxOp = ctxOp;
    }

    public List<OpIsis> getOpsOfType(OpType_isis typ){
        return getOps().stream().filter(x -> x.Type() == typ).toList();
    }

    public List<OpIsis> popOpsOfType(OpType_isis typ){
        var res = getOpsOfType(typ);
        setOpgroup(getOps().stream().filter(x -> x.Type() != typ).toList());
        return res;
    }

    /**
     * create empty OpOspfG
     * @return empty OpOspfG, ctxOp = null
     */
    public static OpIsisG Of(){
        return new OpIsisG();
    }

    /**
     * create empty OpOspfG with ctxop
     * @return empty OpOspfG
     */
    public static OpIsisG Of(OpIsis ctxOp){
        return new OpIsisG(ctxOp);
    }

    public OpIsis getCtxOp() {
        return CtxOp;
    }

    public void setCtxOp(OpIsis ctxOp) {
        CtxOp = ctxOp;
    }

    private OpIsis CtxOp;
}
