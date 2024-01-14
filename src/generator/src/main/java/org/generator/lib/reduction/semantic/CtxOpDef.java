package org.generator.lib.reduction.semantic;

import org.generator.lib.operation.operation.OpType;

public class CtxOpDef {
    public static boolean isCtxOpSelf(OpType type){
        return type == OpType.ROSPF || type == OpType.IntfName || type == OpType.NOROSPF;
    }

    public static boolean isCtxOpIntfN(OpType type){
        return type.inOSPFINTF();
    }

    public static  boolean isCtxOpROSPF(OpType type){
        return type.inOSPFRouterWithTopo() || type.inOSPFDAEMON() || type.inOSPFAREA();
    }
}
