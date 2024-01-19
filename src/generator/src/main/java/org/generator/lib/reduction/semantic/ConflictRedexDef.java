package org.generator.lib.reduction.semantic;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.operation.operation.OpType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.generator.lib.operation.operation.OpType.*;

/**
 * This is the reduction Def of conflict def
 */
public class ConflictRedexDef {
    ConflictRedexDef(){
        targetOps = new ArrayList<>();
        equalArgs = new ArrayList<>();
    }
    private static HashMap<OpType, ConflictRedexDef> preprocess;

    static {
        var reduce_seed = new Object[][]{
                {IpOspfArea, new OpType[]{NETAREAID}, 0},
                {NETAREAID, new OpType[]{IpOspfArea}, 0},

                {AreaRange, new OpType[]{AreaRangeSub, AreaRangeCost}, 2},

                {IpOspfDeadInter, new OpType[]{IpOspfDeadInterMulti}, 0},
                {IpOspfHelloInter, new OpType[]{IpOspfDeadInterMulti}, 0},
                //Other instruction
                //{XXX, [], 0}
        };
        preprocess = new HashMap<>();
        for(var item: reduce_seed){
            ConflictRedexDef rdcDef;
            var opType = (OpType) item[0];
            if (preprocess.containsKey(opType)){
                rdcDef = preprocess.get(opType);
            }else{
                rdcDef = new ConflictRedexDef();
                rdcDef.lexDef = LexDef.getLexDef(opType).get(0);
                preprocess.put(opType, rdcDef);
            }
            for(var targetType: (OpType[]) item[1]){
                rdcDef.targetOps.add(targetType);
                rdcDef.equalArgs.add(rdcDef.lexDef.Args.subList(0, (int) item[2]));
            }
        }
        for (var opType : LexDef.getOpTypesToMatch()) {
            if (preprocess.containsKey(opType)) continue;
            var rdcDef = new ConflictRedexDef();
            if (opType.isUnsetOp()){
                rdcDef.targetOps.add(opType);
                rdcDef.equalArgs.add(new ArrayList<>());
            }else if (inOSPF(opType)){
                rdcDef.targetOps.clear();
                rdcDef.equalArgs.clear();
            }else continue;
            rdcDef.lexDef = LexDef.getLexDef(opType).get(0);
            preprocess.put(opType, rdcDef);
        }
    }

    public LexDef getLexDef() {
        return lexDef;
    }

    public void setLexDef(LexDef lexDef) {
        this.lexDef = lexDef;
    }

    public static ConflictRedexDef getRdcDef(OpType opType) {
        assert preprocess.containsKey(opType) : opType;
        return preprocess.get(opType);
    }

    public List<OpType> getTargetOps() {
        return targetOps;
    }

    public void setTargetOps(List<OpType> targetOps) {
        this.targetOps = targetOps;
    }

    /**
     * the target Ops to reduce
     */
    public List<OpType> targetOps;

    public List<List<String>> getEqualArgs() {
        return equalArgs;
    }

    public void setEqualArgs(List<List<String>> equalArgs) {
        this.equalArgs = equalArgs;
    }

    public List<List<String>> equalArgs;
    /**
     *op's lexDef of minimal Args, we only use the Arg filed of lexDef
     */
    public LexDef lexDef;
}
