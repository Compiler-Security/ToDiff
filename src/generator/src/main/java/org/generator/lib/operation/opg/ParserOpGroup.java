package org.generator.lib.operation.opg;

import org.generator.lib.operation.operation.OpType;
import org.generator.lib.operation.operation.Op;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParserOpGroup implements OpGroup{
    public ParserOpGroup(){
        opgroup = new ArrayList<>();
    }
    public ParserOpGroup(List<Op> ops, Optional<String> target){
        opgroup = new ArrayList<>();
        this.target = target;
        setCtxOp(null);
        addOps(ops);
    }
    @Override
    public List<Op> getOps() {
        return opgroup;
    }

    @Override
    public void addOp(Op op) {
        opgroup.add(op);
    }

    @Override
    public void addOps(List<Op> ops) {
        opgroup.addAll(ops);
    }

    @Override
    public String toString() {
        return opgroup.toString();
    }

    public void setOpgroup(List<Op> opgroup) {
        this.opgroup = opgroup;
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
