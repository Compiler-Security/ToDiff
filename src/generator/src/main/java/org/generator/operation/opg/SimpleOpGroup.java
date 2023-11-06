package org.generator.operation.opg;

import org.generator.operation.op.Operation;

import java.util.ArrayList;
import java.util.List;

public class SimpleOpGroup implements OpGroup{
    public SimpleOpGroup(){
        opgroup = new ArrayList<>();
    }
    public SimpleOpGroup(List<Operation> ops){
        opgroup = new ArrayList<>();
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
}
