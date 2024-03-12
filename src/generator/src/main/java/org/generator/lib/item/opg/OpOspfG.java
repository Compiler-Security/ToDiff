package org.generator.lib.item.opg;

import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.frontend.lexical.OpType;

import java.util.List;

public class OpOspfG extends BaseOpG<OpOspf>{

    private OpOspfG(){super();}

    private OpOspfG(OpOspf ctxOp){
        super();
        this.CtxOp = ctxOp;
    }

    public List<OpOspf> getOpsOfType(OpType typ){
        return getOps().stream().filter(x -> x.Type() == typ).toList();
    }

    public List<OpOspf> popOpsOfType(OpType typ){
        var res = getOpsOfType(typ);
        setOpgroup(getOps().stream().filter(x -> x.Type() != typ).toList());
        return res;
    }

    /**
     * create empty OpOspfG
     * @return empty OpOspfG, ctxOp = null
     */
    public static OpOspfG Of(){
        return new OpOspfG();
    }

    /**
     * create empty OpOspfG with ctxop
     * @return empty OpOspfG
     */
    public static OpOspfG Of(OpOspf ctxOp){
        return new OpOspfG(ctxOp);
    }

    public OpOspf getCtxOp() {
        return CtxOp;
    }

    public void setCtxOp(OpOspf ctxOp) {
        CtxOp = ctxOp;
    }

    private OpOspf CtxOp;
}
