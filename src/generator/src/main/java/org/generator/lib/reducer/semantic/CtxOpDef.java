package org.generator.lib.reducer.semantic;

import org.generator.lib.operation.operation.OpType;

public class CtxOpDef {
    public static boolean isCtxOp(OpType type){
        return type == OpType.ROSPF || type == OpType.IntfName || type == OpType.NOROSPF;
    }

    public static boolean isSetCtxOp(OpType type){
        return type == OpType.ROSPF || type == OpType.IntfName;
    }

    public static boolean isUnsetCtxOp(OpType type){
        return type == OpType.NOROSPF;
    }

    public static boolean shouldInIntfN(OpType type){
        return type.isIntfOp();
    }

    public static  boolean shouldInROSPF(OpType type){
        return type.isRouterOp();
    }
}
