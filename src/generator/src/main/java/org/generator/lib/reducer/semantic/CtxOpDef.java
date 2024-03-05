package org.generator.lib.reducer.semantic;

import org.generator.lib.operation.operation.OpType;

public class CtxOpDef {
    public static boolean isCtxOpSelf(OpType type){
        return type == OpType.ROSPF || type == OpType.IntfName || type == OpType.NOROSPF;
    }

    public static boolean shouldInIntfN(OpType type){
        return type.isIntfOp();
    }

    public static  boolean shouldInROSPF(OpType type){
        return type.isRouterOp();
    }
}
