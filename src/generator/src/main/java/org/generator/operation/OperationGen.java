package org.generator.operation;

import java.util.Map;

public class OperationGen {
    public static operation GenOperation(OpType type){
        switch (type){
            case NODEADD -> {return new DynamicOp("node [NODENAME] add", type);}
            case NODEDEL -> {return new DynamicOp("node [NODENAME] del", type);}
        }
        return new DynamicOp("", OpType.EMPTY);
    }
    public static operation GenOperation(OpType type, Map<String, String> args){
        operation op = GenOperation(type);
        op.setArgs(args);
        return op;
    }
}
