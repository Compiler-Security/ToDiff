package org.generator.operation;

import java.util.Map;

public class OpGen {
    public static StaticOp GenOperation(OpType type){
        return new StaticOp(type.template(), type);
    }
    public static operation GenOperation(OpType type, Map<String, String> args){
        operation op = GenOperation(type);
        op.setArgs(args);
        return op;
    }
}
