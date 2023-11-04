package org.generator.operation;

import java.util.Map;

public class OpGen {
    public static Operation GenOperation(OpType type){
        return new Operation(type.template(), type);
    }
    public static Operation GenOperation(OpType type, Map<String, String> args){
        Operation op = GenOperation(type);
        op.putArgs(args);
        return op;
    }
}
