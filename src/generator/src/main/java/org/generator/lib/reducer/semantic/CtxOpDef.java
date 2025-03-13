package org.generator.lib.reducer.semantic;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.driver.generate;

public class CtxOpDef {
    public static boolean isCtxOp(OpType type){
        return type == OpType.IntfName
                || type == OpType.ROSPF || type == OpType.NOROSPF
                || type == OpType.RRIP || type == OpType.NORRIP
                || type == OpType.RISIS || type == OpType.NORISIS
                || type == OpType.RBABEL || type == OpType.NORBABEL
                ;

    }

    public static boolean isSetCtxOp(OpType type){
        return type == OpType.IntfName
                || type == OpType.ROSPF
                || type == OpType.RRIP
                || type == OpType.RISIS
                || type == OpType.RBABEL
                ;
    }

//    public static boolean isUnsetCtxOp(OpType type){
//        return type == OpType.NOROSPF;
//    }

    public static boolean shouldInIntf(OpType type){
        return type.isIntfOp();
    }

    public static  boolean shouldInRouter(OpType type){
        return type.isRouterOp();
    }

    public static boolean isCtxRouterOp(OpType type){
        return  type == OpType.ROSPF ||
                type == OpType.RRIP ||
                type == OpType.RISIS ||
                type == OpType.RBABEL;
    }

    public static boolean isCtxIntfOp(OpType type){return type == OpType.IntfName;}

    public static OpType getCtxRouterOp(){
        switch (generate.protocol){
            case OSPF: return OpType.ROSPF;
            case RIP: return OpType.RRIP;
            case ISIS: return OpType.RISIS;
            case BABEL: return OpType.RBABEL;
        }
        assert false;
        return null;
    }

    public static OpType getCtxIntfOp(){
        return OpType.IntfName;
    }
}
