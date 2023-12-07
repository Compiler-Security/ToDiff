package org.generator.operation.opg;

import org.generator.operation.op.Operation;
import org.generator.topo.node.AbstractNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleOpGroup implements OpGroup{
    public SimpleOpGroup(){
        opgroup = new ArrayList<>();
    }
    public SimpleOpGroup(List<Operation> ops, Optional<String> target){
        opgroup = new ArrayList<>();
        this.target = target;
        ctx = new OpgExec.Ctx();
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

    private List<Operation> opgroup;

    public Optional<String> getTarget() {
        return target;
    }

    @Override
    public OpgExec.Ctx getCtx() {
        return ctx;
    }

    public void setTarget(Optional<String> target) {
        this.target = target;
    }

    private Optional<String> target;

    private OpgExec.Ctx ctx;
}
