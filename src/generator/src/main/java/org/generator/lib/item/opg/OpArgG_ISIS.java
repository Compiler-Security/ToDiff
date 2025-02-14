package org.generator.lib.item.opg;

import org.generator.lib.item.IR.Op_ISIS;
import org.generator.lib.frontend.lexical.OpType_isis;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OpArgG_ISIS extends BaseOpG<Op_ISIS>{
    public OpArgG_ISIS(){
        super();
    }

    public Op_ISIS getCtxOp() {
        return CtxOp;
    }

    public void setCtxOp(Op_ISIS ctxOp) {
        CtxOp = ctxOp;
    }

    private Op_ISIS CtxOp;

    public enum OpGType_ISIS{
        Phy,
        ALLCONF,
        Intf,
        ISIS,
        ISISIntf,
        Attri//Attributes
    }

    public OpGType_ISIS getTyp() {
        return typ;
    }

    public void setTyp(OpGType_ISIS typ) {
        this.typ = typ;
    }

    private OpGType_ISIS typ;

    // FIXME: Is there ontype_isis or OpGType_isis?
    public List<Op_ISIS> getOpsOfType(OpType_isis typ){
        return getOps().stream().filter(x -> x.Type() == typ).toList();
    }

    public List<Op_ISIS> popOpsOfType(OpType_isis typ){
        var res = getOpsOfType(typ);
        setOpgroup(getOps().stream().filter(x -> x.Type() != typ).toList());
        return res;
    }

}
