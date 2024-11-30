package org.generator.lib.reducer.semantic;

import org.generator.lib.frontend.lexical.OpType_isis;

public class CtxOpDef_ISIS {
    public static boolean isCtxOp(OpType_isis type){
        return type == OpType_isis.RISIS || type == OpType_isis.IntfName || type == OpType_isis.NORISIS;
    }

    public static boolean isSetCtxOp(OpType_isis type){
        return type == OpType_isis.RISIS || type == OpType_isis.IntfName;
    }

    public static boolean isUnsetCtxOp(OpType_isis type){
        return type == OpType_isis.NORISIS;
    }

    public static boolean shouldInIntfN(OpType_isis type){
        return type.isIntfOp();
    }

    public static  boolean shouldInRISIS(OpType_isis type){
        return type.isRouterOp();
    }
}
