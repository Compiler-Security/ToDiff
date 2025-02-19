package org.generator.lib.reducer.semantic;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.frontend.lexical.OpType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.generator.lib.frontend.lexical.OpType.*;

/**
 * This is the reduction Def of override def
 * override can be two case:
 *  same instruction
 *  override to other insturction
 *  {Type, to override Type, equal args num}
 */
public class OverrideRedexDef extends BaseRedexDef {
    OverrideRedexDef(){
        super();
    }

    private static HashMap<OpType, BaseRedexDef> preprocess;

    static {
        var reduce_seed = new Object[][]{
                /*
                This says item[0] can override op in item[1]
                if the first item[2] arg is same
                 */
                //=====ZEBRA============
                {IntfName, new OpType[]{}, 0},
                {IPAddr, new OpType[]{IPAddr}, 1},
                //======OSPF============
                {ROSPF, new OpType[]{}, 0},
                {IpOspfArea, new OpType[]{}, 0},
                {NETAREAID, new OpType[]{NETAREAID}, 2},

                {AreaRangeNoAd, new OpType[]{AreaRange, AreaRangeSub, AreaRangeCost, AreaRangeNoAd}, 2},
                {AreaRange, new OpType[]{AreaRange, AreaRangeNoAd}, 2},
                {AreaRangeSub, new OpType[]{AreaRange, AreaRangeSub, AreaRangeNoAd}, 2},
                {AreaRangeCost, new OpType[]{AreaRange, AreaRangeCost, AreaRangeNoAd}, 2},
                //FIXME areaVLINK
                //{AreaVLink, new OpType[]{AreaVLink}, 1},
                {AreaShortcut, new OpType[]{AreaShortcut}, 1},

                {AreaStub, new OpType[]{AreaStub, AreaStubTotal, AreaNSSA}, 1},
                {AreaStubTotal, new OpType[]{AreaStubTotal, AreaStub, AreaNSSA}, 1},
                {AreaNSSA, new OpType[]{AreaStub, AreaNSSA, AreaStubTotal}, 1},

                {IpOspfDeadInterMulti, new OpType[]{IpOspfDeadInter, IpOspfDeadInterMulti}, 0},
                {IpOspfDeadInter, new OpType[]{IpOspfDeadInter, IpOspfDeadInterMulti}, 0},

                //==========RIP====================
                {RRIP, new OpType[]{}, 0},
                {NETWORKI, new OpType[]{}, 1},
                {NETWORKN, new OpType[]{}, 1},
                {PASSIVEINTFDEFAULT, new OpType[]{PASSIVEINTFDEFAULT, PASSIVEINTFNAME}, 0},
                {IPSPLITPOISION, new OpType[]{IPSPLITHORIZION, IPSPLITPOISION}, 0},
                {IPSPLITHORIZION, new OpType[]{IPSPLITHORIZION, IPSPLITPOISION}, 0},
                {IPSENDVERSION1, new OpType[]{IPSENDVERSION1, IPSENDVERSION2, IPSENDVERSION12}, 0},
                {IPSENDVERSION2, new OpType[]{IPSENDVERSION1, IPSENDVERSION2, IPSENDVERSION12}, 0},
                {IPSENDVERSION12, new OpType[]{IPSENDVERSION1, IPSENDVERSION2, IPSENDVERSION12}, 0},
                {IPRECVVERSION1, new OpType[]{IPRECVVERSION1, IPRECVVERSION2, IPRECVVERSION12}, 0},
                {IPRECVVERSION2, new OpType[]{IPRECVVERSION1, IPRECVVERSION2, IPRECVVERSION12}, 0},
                {IPRECVVERSION12, new OpType[]{IPRECVVERSION1, IPRECVVERSION2, IPRECVVERSION12}, 0},

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
            var rdcDef = new OverrideRedexDef();
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

    public static List<OpType> getOverrideType(OpType setType){
        List<OpType> res = new ArrayList<>();
        for(var overrideOpType: preprocess.keySet()){
            if (getRdcDef(overrideOpType).targetOps.contains(setType)){
                res.add(overrideOpType);
            }
        }
        return res;
    }

    public static List<String> getOverrideEqualArg(OpType prev_op_type, OpType cur_op_type){
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
