package org.generator.lib.reducer.semantic;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.frontend.lexical.OpType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public  class BaseRedexDef {
    BaseRedexDef(){
        targetOps = new ArrayList<>();
        equalArgs = new ArrayList<>();
    }

    public static void parse(List<Object[]> reduce_seed, HashMap<OpType, BaseRedexDef> preprocess){
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
                var args = LexDef.getLexDef(targetType).get(0).Args;
                int count = (int)item[2];
                if(count > 0) {
                    // take the first count args
                    rdcDef.equalArgs.add(args.subList(0, count));
                } else {
                    // take the last count args
                    rdcDef.equalArgs.add(args.subList(args.size() + count, args.size()));
                }
            }
        }
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
