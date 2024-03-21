package org.generator.lib.reducer.semantic;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.frontend.lexical.OpType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.generator.lib.frontend.lexical.OpType.*;

/**
 * This is the reduction Def of override def
 * override can be two case:
 *  same instruction
 *  override to other insturction
 *  {Type, to override Type, equal args num}
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

                {AreaRangeNoAd, new OpType[]{AreaRange, AreaRangeSub, AreaRangeCost, AreaRangeNoAd}, 2},
                {AreaRange, new OpType[]{AreaRange, AreaRangeNoAd}, 2},
                {AreaRangeSub, new OpType[]{AreaRange, AreaRangeSub, AreaRangeNoAd}, 2},
                {AreaRangeCost, new OpType[]{AreaRange, AreaRangeCost, AreaRangeNoAd}, 2},

                {IpOspfDeadInterMulti, new OpType[]{IpOspfDeadInter, IpOspfHelloInter,IpOspfDeadInterMulti}, 0}

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
