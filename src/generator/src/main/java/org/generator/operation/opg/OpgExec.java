package org.generator.operation.opg;

import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.generator.topo.graph.RelationGraph;
import org.generator.util.collections.Pair;
import org.generator.util.exec.ExecStat;

import java.util.ArrayList;
import java.util.List;

public abstract class OpgExec {
    abstract ExecStat execOp(Operation op, RelationGraph topo);
    List<Pair<Operation, ExecStat>> execOps(OpGroup opg, RelationGraph topo){
        List<Pair<Operation, ExecStat>> l = new ArrayList<>();
        assert opg.getOps().stream().allMatch(x -> OpType.inPhy(x.Type())) : "there is op not in phyOpGroup";
        for(var op: opg.getOps()){
            var res = execOp(op, topo);
            l.add(new Pair<>(op, res));
        }
        return l;
    }
}
