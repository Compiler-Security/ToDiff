package org.generator.lib.reducer.semantic;

import org.generator.lib.frontend.lexical.LexDef_isis;
import org.generator.lib.frontend.lexical.OpType_isis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public  class BaseRedexDef_ISIS {
    BaseRedexDef_ISIS(){
        targetOps = new ArrayList<>();
        equalArgs = new ArrayList<>();
    }

    public static void parse(List<Object[]> reduce_seed, HashMap<OpType_isis, BaseRedexDef_ISIS> preprocess){
        for(var item: reduce_seed){
            BaseRedexDef_ISIS rdcDef;
            var opType = (OpType_isis) item[0];
            if (preprocess.containsKey(opType)){
                rdcDef = preprocess.get(opType);
            }else{
                rdcDef = new BaseRedexDef_ISIS();
                preprocess.put(opType, rdcDef);
            }
            for(var targetType: (OpType_isis[]) item[1]){
                rdcDef.targetOps.add(targetType);
                rdcDef.equalArgs.add(LexDef_isis.getLexDef(targetType).get(0).Args.subList(0, (int) item[2]));
            }
        }
    }



    public List<OpType_isis> getTargetOps() {
        return targetOps;
    }

    public void setTargetOps(List<OpType_isis> targetOps) {
        this.targetOps = targetOps;
    }

    public List<List<String>> getEqualArgs() {
        return equalArgs;
    }

    public void setEqualArgs(List<List<String>> equalArgs) {
        this.equalArgs = equalArgs;
    }

    public List<OpType_isis> targetOps;
    public List<List<String>> equalArgs;
}
