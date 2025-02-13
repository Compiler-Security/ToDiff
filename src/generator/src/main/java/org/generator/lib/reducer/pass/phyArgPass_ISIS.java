package org.generator.lib.reducer.pass;

import org.generator.lib.item.opg.OpCtxG_ISIS;
import org.generator.lib.item.conf.graph.ConfGraph_ISIS;

public class phyArgPass_ISIS {
    public static void solve(OpCtxG_ISIS opg, ConfGraph_ISIS confGraph){
        var exec = new phyExecArgPass_ISIS();
        for(var opCtx: opg.getOps()){
            exec.execOp(opCtx.getOperation(), confGraph);
        }
    }
}
