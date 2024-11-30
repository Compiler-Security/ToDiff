package org.generator.lib.reducer.semantic;

import org.generator.lib.frontend.lexical.LexDef_isis;
import org.generator.lib.frontend.lexical.OpType_isis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.generator.lib.frontend.lexical.OpType_isis.*;

/**
 * This is the reduction Def of override def
 * override can be two case:
 *  same instruction
 *  override to other insturction
 *  {Type, to override Type, equal args num}
 */
public class OverrideRedexDef_ISIS extends BaseRedexDef_ISIS {
    OverrideRedexDef_ISIS(){
        super();
    }

    private static HashMap<OpType_isis, BaseRedexDef_ISIS> preprocess;

    static {
        var reduce_seed = new Object[][]{
                /*
                This says item[0] can override op in item[1]
                if the first item[2] arg is same
                 */
                {RISIS, new OpType_isis[]{}, 0},
                {IntfName, new OpType_isis[]{}, 0},
                {IPAddr, new OpType_isis[]{IPAddr}, 1},

                // {IpIsisArea, new OpType_isis[]{}, 0},
                // {NETAREAID, new OpType_isis[]{NETAREAID}, 2},

                // {AreaRangeNoAd, new OpType_isis[]{AreaRange, AreaRangeSub, AreaRangeCost, AreaRangeNoAd}, 2},
                // {AreaRange, new OpType_isis[]{AreaRange, AreaRangeNoAd}, 2},
                // {AreaRangeSub, new OpType_isis[]{AreaRange, AreaRangeSub, AreaRangeNoAd}, 2},
                // {AreaRangeCost, new OpType_isis[]{AreaRange, AreaRangeCost, AreaRangeNoAd}, 2},
                // //FIXME areaVLINK
                // //{AreaVLink, new OpType[]{AreaVLink}, 1},
                // {AreaShortcut, new OpType_isis[]{AreaShortcut}, 1},

                // {AreaStub, new OpType_isis[]{AreaStub, AreaStubTotal, AreaNSSA}, 1},
                // {AreaStubTotal, new OpType_isis[]{AreaStubTotal, AreaStub, AreaNSSA}, 1},
                // {AreaNSSA, new OpType_isis[]{AreaStub, AreaNSSA, AreaStubTotal}, 1},

                // {IpIsisDeadInterMulti, new OpType_isis[]{IpIsisDeadInter, IpIsisDeadInterMulti}, 0},
                // {IpIsisDeadInter, new OpType_isis[]{IpIsisDeadInter, IpIsisDeadInterMulti}, 0}
                //Other set instruction
                //{XXX, new OpType[XXX], 0}

                //Other unset instruction
                //{XXX, new {}, 0}
        };
        var seeds = new ArrayList<>(Arrays.asList(reduce_seed));
        for (var opType : LexDef_isis.getOpTypesToMatch()) {
            if (Arrays.stream(reduce_seed).anyMatch(x -> (OpType_isis)x[0] == opType)) {
                continue;
            }
            var rdcDef = new OverrideRedexDef_ISIS();
            if (opType.isUnsetOp()){
                //Other unset instruction
                seeds.add(new Object[]{opType, new OpType_isis[]{}, 0});
            }else if (opType.isSetOp()){
                //Other set instruction
                seeds.add(new Object[]{opType, new OpType_isis[]{opType}, 0});
            }else continue;
        }
        preprocess = new HashMap<>();
        parse(seeds, preprocess);
    }

    public static BaseRedexDef_ISIS getRdcDef(OpType_isis opType) {
        assert preprocess.containsKey(opType) : opType;
        return preprocess.get(opType);
    }

    public static List<OpType_isis> getOverrideType(OpType_isis setType){
        List<OpType_isis> res = new ArrayList<>();
        for(var overrideOpType: preprocess.keySet()){
            if (getRdcDef(overrideOpType).targetOps.contains(setType)){
                res.add(overrideOpType);
            }
        }
        return res;
    }

    public static List<String> getOverrideEqualArg(OpType_isis prev_op_type, OpType_isis cur_op_type){
        var rdcDef = getRdcDef(cur_op_type);
        for(int i = 0; i < rdcDef.getEqualArgs().size(); i++){
            if (rdcDef.targetOps.get(i) == prev_op_type){
                return rdcDef.equalArgs.get(i);
            }
        }
        assert false: "not have this cur_op_type %s , prev_op_type %s pair ".formatted(prev_op_type, cur_op_type);
        return null;
    }
}
