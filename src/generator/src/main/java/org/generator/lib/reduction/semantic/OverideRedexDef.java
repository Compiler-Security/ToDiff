package org.generator.lib.reduction.semantic;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.operation.operation.OpType;

import java.util.HashMap;

import static org.generator.lib.operation.operation.OpType.*;

/**
 * This is the reduction Def of override def
 * override can be two case:
 *  same instruction
 *  override to other insturction
 */
public class OverideRedexDef {
    private static HashMap<OpType, OverideRedexDef> preprocess;

    static {
        var reduce_seed = new Object[][]{
                {ROSPF, new OpType[]{}, 0},
                {IntfName, new OpType[]{}, 0},
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

                //Other instruction
                //{XXX, new OpType[XXX], 0}
        };
        preprocess = new HashMap<>();
        for (var item : reduce_seed) {
            var rdcDef = new OverideRedexDef();
            var opType = (OpType) item[0];
            rdcDef.targetOps = (OpType[]) item[1];
            rdcDef.lexDef = LexDef.getLexDef(opType).get(0);
            preprocess.put(opType, rdcDef);
        }
    }

    public OpType[] getTargetOps() {
        return targetOps;
    }

    public void setTargetOps(OpType[] targetOps) {
        this.targetOps = targetOps;
    }

    public LexDef getLexDef() {
        return lexDef;
    }

    public void setLexDef(LexDef lexDef) {
        this.lexDef = lexDef;
    }

    public static OverideRedexDef getRdcDef(OpType opType) {
        assert preprocess.containsKey(opType) : opType;
        return preprocess.get(opType);
    }

    /**
     * the target Ops to reduce
     */
    public OpType[] targetOps;

    /**
     *op's lexDef of minimal Args, we only use the Arg filed of lexDef
     */
    public LexDef lexDef;
}
