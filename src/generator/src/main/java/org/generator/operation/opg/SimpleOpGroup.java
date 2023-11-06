package org.generator.operation.opg;

import org.generator.operation.op.Operation;

import java.util.ArrayList;
import java.util.List;

public class SimpleOpGroup implements OpGroup{
    SimpleOpGroup(){
        opgroup = new ArrayList<>();
    }
    @Override
    public List<Operation> getOps() {
        return opgroup;
    }

    @Override
    public void addOp(Operation op) {
        opgroup.add(op);
    }

    private List<Operation> opgroup;
}
