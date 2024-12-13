package org.generator.lib.reducer.semantic;

import org.generator.lib.frontend.lexical.LexDef_isis;
import org.generator.lib.frontend.lexical.OpType_isis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.generator.lib.frontend.lexical.OpType_isis.*;

/**
 * This is the reduction Def of conflict def
 */
public class ConflictRedexDef_ISIS extends  BaseRedexDef_ISIS{
    ConflictRedexDef_ISIS(){
        super();
    }

    private static HashMap<OpType_isis, BaseRedexDef_ISIS> preprocess;

    static {
        var reduce_seed = new Object[][]{
                //This means if OpType[] are in the previous context, then this op should be conflict and remove, third number is the compare arg num
                //FIXME: now, we do not make sure which one should be conflict.
                
                // {ISISPRIORITY, new OpType_isis[]{ISISPASSIVE}, 0},
                // {HELLOMULTIPLIER, new OpType_isis[]{HELLOINTERVAL}, 0},
                // {HELLOINTERVAL, new OpType_isis[]{HELLOMULTIPLIER}, 0},
                // {PSNPINTERVAL, new OpType_isis[]{HELLOINTERVAL}, 1},
                // {CSNPINTERVAL, new OpType_isis[]{HELLOINTERVAL}, 1},
                // Other instruction
                // {XXX, [], 0}
        };
        var seeds = new ArrayList<>(Arrays.asList(reduce_seed));
        for (var opType : LexDef_isis.getOpTypesToMatch()) {
            if (Arrays.stream(reduce_seed).anyMatch(x -> (OpType_isis)x[0] == opType)) {
                continue;
            }
            if (opType.isSetOp() || opType.isUnsetOp()){
                //Other instruction
                seeds.add(new Object[]{opType, new OpType_isis[]{}, 0});
            }
        }
        preprocess = new HashMap<>();
        parse(seeds, preprocess);
    }

    public static BaseRedexDef_ISIS getRdcDef(OpType_isis opType) {
        assert preprocess.containsKey(opType) : opType;
        return preprocess.get(opType);
    }
}
