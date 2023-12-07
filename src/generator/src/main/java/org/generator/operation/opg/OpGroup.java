package org.generator.operation.opg;

import org.generator.operation.op.Operation;
import org.generator.topo.node.AbstractNode;

import java.util.List;
import java.util.Optional;

public interface OpGroup {
    List<Operation> getOps();
    void addOp(Operation op);

    void addOps(List<Operation> ops);

    Optional<String> getTarget();

    OpgExec.Ctx getCtx();

    enum OpGType{
        Phy,
        ALLCONF,
        Intf,
        OSPF,
        OSPFIntf,
        Attri
    }
}
