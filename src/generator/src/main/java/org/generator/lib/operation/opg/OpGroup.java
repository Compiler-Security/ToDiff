package org.generator.lib.operation.opg;

import org.generator.lib.operation.operation.Op;

import java.util.List;
import java.util.Optional;

public interface OpGroup {
    List<Op> getOps();
    void addOp(Op op);

    void addOps(List<Op> ops);

    Optional<String> getTarget();

}
