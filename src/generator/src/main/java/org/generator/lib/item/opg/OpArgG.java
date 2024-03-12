package org.generator.lib.item.opg;

import org.generator.lib.item.IR.Op;
import org.generator.lib.frontend.lexical.OpType;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OpArgG extends BaseOpG<Op>{
    public OpArgG(){
        opgroup = new ArrayList<>();
    }
    public OpArgG(List<Op> ops, Optional<String> target){
        opgroup = new ArrayList<>();
        this.target = target;
        setCtxOp(null);
        addOps(ops);
    }


    public void addOp(Op op) {
        opgroup.add(op);
    }


    @Override
    public String toString() {
        return opgroup.toString();
    }

    private List<Op> opgroup;

    public Optional<String> getTarget() {
        return target;
    }


    public void setTarget(Optional<String> target) {
        this.target = target;
    }

    private Optional<String> target;


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
        Attri
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
