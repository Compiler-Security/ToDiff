package org.generator.lib.operation.operation;

import java.util.Map;

public class OpGen {
    public static Op GenOperation(OpType type){
        return new Op( type);
    }
    public static Op GenOperation(OpType type, Map<String, String> args){
        Op op = GenOperation(type);
        op.putArgs(args);
        return op;
    }
}
