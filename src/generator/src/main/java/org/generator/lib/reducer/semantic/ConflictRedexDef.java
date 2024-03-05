package org.generator.lib.reducer.semantic;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.operation.operation.OpType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.generator.lib.operation.operation.OpType.*;

/**
 * This is the reduction Def of conflict def
 */
public class ConflictRedexDef extends  BaseRedexDef{
    ConflictRedexDef(){
        super();
    }

    private static HashMap<OpType, BaseRedexDef> preprocess;

    static {
        var reduce_seed = new Object[][]{
                {IpOspfArea, new OpType[]{NETAREAID}, 0},
                {NETAREAID, new OpType[]{IpOspfArea}, 0},

                {AreaRange, new OpType[]{AreaRangeSub, AreaRangeCost}, 2},

                {IpOspfDeadInter, new OpType[]{IpOspfDeadInterMulti}, 0},
                {IpOspfHelloInter, new OpType[]{IpOspfDeadInterMulti}, 0},
                // Other instruction
                // {XXX, [], 0}
        };
        var seeds = new ArrayList<>(Arrays.asList(reduce_seed));
        for (var opType : LexDef.getOpTypesToMatch()) {
            if (Arrays.stream(reduce_seed).anyMatch(x -> (OpType)x[0] == opType)) {
                continue;
            }
            if (opType.isSetOp() || opType.isUnsetOp()){
                //Other instruction
                seeds.add(new Object[]{opType, new OpType[]{}, 0});
            }
        }
        preprocess = new HashMap<>();
        parse(seeds, preprocess);
    }

    public static BaseRedexDef getRdcDef(OpType opType) {
        assert preprocess.containsKey(opType) : opType;
        return preprocess.get(opType);
    }
}
