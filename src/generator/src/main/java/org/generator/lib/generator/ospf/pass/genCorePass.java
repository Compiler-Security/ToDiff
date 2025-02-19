package org.generator.lib.generator.ospf.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.opg.OpCtxG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class genCorePass {
    public abstract List<OpCtxG> solve(ConfGraph confg);

    protected OpOspf addOp(OpCtxG opCtxG, OpType typ){
        var op = OpOspf.of(typ);
        opCtxG.addOp(OpCtx.of(op));
        return op;
    }

    public static List<OpCtxG> mergeOpCtxgToEach(List<OpCtxG> opCtxG){
        Map<OpOspf, OpCtxG> merge = new HashMap<>();
        for(var opctxg: opCtxG){
            //TO check correctness, don't deal
            opctxg.toString();
            if (opctxg.getOps().isEmpty()) continue;
            var ctxOp = (OpOspf) opctxg.getOps().getFirst().getOperation();
            merge.putIfAbsent(ctxOp, OpCtxG.Of());
            merge.get(ctxOp).addOps(opctxg.getOps());
        }
        return merge.values().stream().toList();
    }
}
