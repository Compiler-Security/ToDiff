package org.generator.operation.opg;

import org.generator.operation.op.Operation;

import java.util.List;

public interface OpGroup {
    List<Operation> getOps();
    void addOp(Operation op);
}
