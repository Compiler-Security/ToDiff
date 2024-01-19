package org.generator.lib.reduction.semantic;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.operation.operation.OpType;

import java.awt.font.OpenType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.generator.lib.operation.operation.OpType.*;

/**
 * This is the reduction Def of override def
 * override can be two case:
 *  same instruction
 *  override to other insturction
 */
public class OverideRedexDef extends BaseRedexDef {
    OverideRedexDef(){
        super();
    }

    private static HashMap<OpType, BaseRedexDef> preprocess;

    static {
        var reduce_seed = new Object[][]{
                {ROSPF, new OpType[]{}, 0},
                {IntfName, new OpType[]{}, 0},
                {IPAddr, new OpType[]{}, 0},
                //完全一样
                {IpOspfArea, new OpType[]{IpOspfArea}, 1},
                {NETAREAID, new OpType[]{NETAREAID}, 2},

                {AreaRangeNoAd, new OpType[]{AreaRange, AreaRangeSub, AreaRangeCost, AreaRangeNoAd}, 0},
                {AreaRange, new OpType[]{AreaRange}, 2},
                {AreaRangeSub, new OpType[]{AreaRange, AreaRangeSub}, 2},
                {AreaRangeCost, new OpType[]{AreaRange, AreaRangeCost}, 2},
                {AreaRange, new OpType[]{AreaRangeNoAd}, 0},
                {AreaRangeSub, new OpType[]{AreaRangeNoAd}, 0},
                {AreaRangeCost, new OpType[]{AreaRangeNoAd}, 0},

                {IpOspfDeadInterMulti, new OpType[]{IpOspfDeadInter, IpOspfHelloInter}, 0}

                //Other set instruction
                //{XXX, new OpType[XXX], 0}

                //Other unset instruction
                //{XXX, new {}, 0}
        };
        var seeds = new ArrayList<>(Arrays.asList(reduce_seed));
        for (var opType : LexDef.getOpTypesToMatch()) {
            if (Arrays.stream(reduce_seed).anyMatch(x -> (OpType)x[0] == opType)) {
                continue;
            }
            var rdcDef = new OverideRedexDef();
            if (opType.isUnsetOp()){
                //Other unset instruction
                seeds.add(new Object[]{opType, new OpType[]{}, 0});
            }else if (opType.isSetOp()){
                //Other set instruction
                seeds.add(new Object[]{opType, new OpType[]{opType}, 0});
            }else continue;
        }
        preprocess = new HashMap<>();
        parse(seeds, preprocess);
    }

    public static BaseRedexDef getRdcDef(OpType opType) {
        assert preprocess.containsKey(opType) : opType;
        return preprocess.get(opType);
    }
}
