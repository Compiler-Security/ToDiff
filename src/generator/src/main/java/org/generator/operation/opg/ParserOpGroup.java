package org.generator.operation.opg;

import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParserOpGroup implements OpGroup{
    public ParserOpGroup(){
        opgroup = new ArrayList<>();
    }
    public ParserOpGroup(List<Operation> ops, Optional<String> target){
        opgroup = new ArrayList<>();
        this.target = target;
        setCtxOp(null);
        addOps(ops);
    }
    @Override
    public List<Operation> getOps() {
        return opgroup;
    }

    @Override
    public void addOp(Operation op) {
        opgroup.add(op);
    }

    @Override
    public void addOps(List<Operation> ops) {
        opgroup.addAll(ops);
    }

    @Override
    public String toString() {
        return opgroup.toString();
    }

    public void setOpgroup(List<Operation> opgroup) {
        this.opgroup = opgroup;
    }

    private List<Operation> opgroup;

    public Optional<String> getTarget() {
        return target;
    }


    public void setTarget(Optional<String> target) {
        this.target = target;
    }

    private Optional<String> target;


    public Operation getCtxOp() {
        return CtxOp;
    }

    public void setCtxOp(Operation ctxOp) {
        CtxOp = ctxOp;
    }

    private Operation CtxOp;

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

    public List<Operation> getOpsOfType(OpType typ){
        return getOps().stream().filter(x -> x.Type() == typ).toList();
    }

    public List<Operation> popOpsOfType(OpType typ){
        var res = getOpsOfType(typ);
        setOpgroup(getOps().stream().filter(x -> x.Type() != typ).toList());
        return res;
    }

}
