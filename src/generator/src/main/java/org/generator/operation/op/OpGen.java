package org.generator.operation.op;

import java.util.Map;

public class OpGen {
    public static Operation GenOperation(OpType type){
        return new Operation( type);
    }
    public static Operation GenOperation(OpType type, Map<String, String> args){
        Operation op = GenOperation(type);
        op.putArgs(args);
        return op;
    }
}
