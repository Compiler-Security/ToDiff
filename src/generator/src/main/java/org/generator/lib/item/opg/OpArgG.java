package org.generator.lib.item.opg;

import org.generator.lib.item.IR.Op;
import org.generator.lib.frontend.lexical.OpType;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OpArgG extends BaseOpG<Op>{
    public OpArgG(){
        super();
    }

    public Op getCtxOp() {
        return CtxOp;
    }

    public void setCtxOp(Op ctxOp) {
        CtxOp = ctxOp;
    }

    private Op CtxOp;

    public enum OpGType{
        Phy,
        ALLCONF,
        Intf,
        OSPF,
        OSPFIntf,
        Attri,
        RIP,
        RIPIntf,
        ISIS,
        ISISIntf,
        FABRIC,
        FABRICIntf,
        BABEL,
        BABELIntf,
        //MULTI:
    }

    public OpGType getTyp() {
        return typ;
    }

    public void setTyp(OpGType typ) {
        this.typ = typ;
    }

    private OpGType typ;

    public List<Op> getOpsOfType(OpType typ){
        return getOps().stream().filter(x -> x.Type() == typ).toList();
    }

    public List<Op> popOpsOfType(OpType typ){
        var res = getOpsOfType(typ);
        setOpgroup(getOps().stream().filter(x -> x.Type() != typ).toList());
        return res;
    }

}
