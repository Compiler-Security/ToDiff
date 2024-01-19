package org.generator.lib.reduction.semantic;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.operation.operation.OpType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public  class BaseRedexDef {
    BaseRedexDef(){
        targetOps = new ArrayList<>();
        equalArgs = new ArrayList<>();
    }

    public static void preprocess(Object[][] reduce_seed){
        preprocess = new HashMap<>();
        for(var item: reduce_seed){
            BaseRedexDef rdcDef;
            var opType = (OpType) item[0];
            if (preprocess.containsKey(opType)){
                rdcDef = preprocess.get(opType);
            }else{
                rdcDef = new BaseRedexDef();
                preprocess.put(opType, rdcDef);
            }
            for(var targetType: (OpType[]) item[1]){
                rdcDef.targetOps.add(targetType);
                rdcDef.equalArgs.add(LexDef.getLexDef(targetType).get(0).Args.subList(0, (int) item[2]));
            }
        }
    }
    private static HashMap<OpType, BaseRedexDef> preprocess;
    public static BaseRedexDef getRdcDef(OpType opType) {
        assert preprocess.containsKey(opType) : opType;
        return preprocess.get(opType);
    }

    public List<OpType> getTargetOps() {
        return targetOps;
    }

    public void setTargetOps(List<OpType> targetOps) {
        this.targetOps = targetOps;
    }

    public List<List<String>> getEqualArgs() {
        return equalArgs;
    }

    public void setEqualArgs(List<List<String>> equalArgs) {
        this.equalArgs = equalArgs;
    }

    public List<OpType> targetOps;
    public List<List<String>> equalArgs;
}
