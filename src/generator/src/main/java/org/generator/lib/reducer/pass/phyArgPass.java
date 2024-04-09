package org.generator.lib.reducer.pass;

import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.conf.graph.ConfGraph;

public class phyArgPass {
    public static void solve(OpCtxG opg, ConfGraph confGraph){
        var exec = new phyExecArgPass();
        for(var opCtx: opg.getOps()){
            exec.execOp(opCtx.getOperation(), confGraph);
        }
    }
}
