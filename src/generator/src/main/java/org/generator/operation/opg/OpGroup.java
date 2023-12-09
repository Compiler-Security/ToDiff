package org.generator.operation.opg;

import org.generator.operation.op.Operation;

import java.util.List;
import java.util.Optional;

public interface OpGroup {
    List<Operation> getOps();
    void addOp(Operation op);

    void addOps(List<Operation> ops);

    Optional<String> getTarget();

}
