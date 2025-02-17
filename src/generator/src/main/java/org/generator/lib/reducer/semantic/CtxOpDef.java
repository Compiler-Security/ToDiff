package org.generator.lib.reducer.semantic;

import org.generator.lib.frontend.lexical.OpType;

public class CtxOpDef {
    public static boolean isCtxOp(OpType type){
        return type == OpType.RRIP || type == OpType.IntfName || type == OpType.NORRIP;
    }

    public static boolean isSetCtxOp(OpType type){
        return type == OpType.RRIP || type == OpType.IntfName;
    }

    public static boolean isUnsetCtxOp(OpType type){
        return type == OpType.NORRIP;
    }

    public static boolean shouldInIntf(OpType type){
        return type.isIntfOp();
    }

    public static  boolean shouldInGlobal(OpType type){
        return type.isRouterOp();
    }

    public static boolean isCtxGlobalOp(OpType type){return type == OpType.RRIP;}

    public static boolean isCtxIntfOp(OpType type){return type == OpType.IntfName;}
}
