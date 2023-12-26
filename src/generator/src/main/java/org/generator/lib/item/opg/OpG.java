package org.generator.lib.item.opg;

import org.generator.lib.item.IR.OpBase;
import org.generator.lib.operation.operation.OpType;

import java.util.List;

public class OpG extends BaseOpG<OpBase>{
    public List<OpBase> getOpsOfType(OpType typ){
        return getOps().stream().filter(x -> x.Type() == typ).toList();
    }

    public List<OpBase> popOpsOfType(OpType typ){
        var res = getOpsOfType(typ);
        setOpgroup(getOps().stream().filter(x -> x.Type() != typ).toList());
        return res;
    }
}
