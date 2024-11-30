package org.generator.lib.reducer.semantic;

import org.generator.lib.frontend.lexical.LexDef_isis;
import org.generator.lib.frontend.lexical.OpType_isis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.generator.lib.frontend.lexical.OpType_isis.*;

/**
 * This is the reduction Def of NoOpOSPF
 */
public class UnsetRedexDef_ISIS extends BaseRedexDef_ISIS{
    private static HashMap<OpType_isis, BaseRedexDef_ISIS> preprocess;

    static {
        var reduce_seed = new Object[][]{
                //unset operation, we can assume that we compare minimal Args of NoOp with corresponding Args of OP
                //minimal args is the first LexDef of no operation
                // {NOROSPF, new OpType_isis[]{ROSPF}},
                // {NORID, new OpType_isis[]{RID}},
                // {NORABRTYPE, new OpType_isis[]{RABRTYPE}},
                // {NONETAREAID, new OpType_isis[]{NETAREAID}},
                // {NOPASSIVEINTFDEFUALT, new OpType_isis[]{PASSIVEINTFDEFUALT}},
                // {NOTIMERSTHROTTLESPF, new OpType_isis[]{TIMERSTHROTTLESPF}},
                // {NOMAXIMUMPATHS, new OpType_isis[]{MAXIMUMPATHS}},
                // {NOWRITEMULTIPLIER, new OpType_isis[]{WRITEMULTIPLIER}},
                // {NOSOCKETBUFFERSEND, new OpType_isis[]{SOCKETBUFFERSEND}},
                // {NOSOCKETBUFFERRECV, new OpType_isis[]{SOCKETBUFFERRECV}},
                //FIXME SOCKETBUFFERALL
                //{NOSOCKETBUFFERALL, new OpType_isis[]{SOCKETBUFFERALL}},
                // {SOCKETPERINTERFACE, new OpType_isis[]{NoSOCKETPERINTERFACE}},

                // {NOAreaRange, new OpType_isis[]{AreaRange, AreaRangeNoAd, AreaRangeSub, AreaRangeCost}},
                // {NOAreaRangeNoAd, new OpType_isis[]{AreaRange, AreaRangeNoAd, AreaRangeSub, AreaRangeCost}},
                // {NOAreaRangeCost, new OpType_isis[]{AreaRange, AreaRangeNoAd, AreaRangeSub, AreaRangeCost}},

                //{NOAreaRangeSub, new OpType_isis[]{AreaRange}},


                //FIXME areaVLINK
                //{NOAreaVLink, new OpType_isis[]{AreaVLink}},
                // {NOAreaShortcut, new OpType_isis[]{AreaShortcut}},

                // {NOAreaStub, new OpType_isis[]{AreaStub, AreaStubTotal}},

                // {NORefreshTimer, new OpType_isis[]{RefreshTimer}},
                // {NOTimersLsaThrottle, new OpType_isis[]{TimersLsaThrottle}},

                //FIXME simple fix of NOAreaStubTotal
               //{NOAreaStubTotal, new OpType_isis[]{AreaStubTotal}},
                // {NOAreaNSSA, new OpType_isis[]{AreaNSSA}},
                // {NOIPAddr, new OpType_isis[]{IPAddr}},
                // {NOIpOspfArea, new OpType_isis[]{IpOspfArea}},
                // {NOIpOspfCost, new OpType_isis[]{IpOspfCost}},
                // {NOIpOspfDeadInter, new OpType_isis[]{IpOspfDeadInter}},
                // {NOIpOspfDeadInterMulti, new OpType_isis[]{IpOspfDeadInterMulti}},
                // {NOIpOspfHelloInter, new OpType_isis[]{IpOspfHelloInter}},
                // {NOIpOspfGRHelloDelay, new OpType_isis[]{IpOspfGRHelloDelay}},
                // {NOIpOspfNet, new OpType_isis[]{IpOspfNet}},
                // {NOIpOspfPriority, new OpType_isis[]{IpOspfPriority}},
                // {NOIpOspfRetransInter, new OpType_isis[]{IpOspfRetransInter}},
                // {NOIpOspfTransDelay, new OpType_isis[]{IpOspfTransDelay}},
                // {NOIpOspfPassive, new OpType_isis[]{IpOspfPassive}},
        };
        var seeds = new ArrayList<Object[]>();
        for (var item : reduce_seed) {
            var OpType_isis = (OpType_isis) item[0];
//             switch (OpType_isis){
// //                case NoAreaRangeSub -> {seeds.add(new Object[]{item[0], item[1], 2});}
//                 case NOAreaRangeCost,NOAreaRangeNoAd,NOAreaRange -> {
//                     seeds.add(new Object[]{item[0], item[1], 2});
//                 }
//                 default -> {
//                     seeds.add(new Object[]{item[0], item[1], LexDef_isis.getLexDef(OpType_isis).get(0).Args.size()});
//                 }
//             }

        }
        preprocess = new HashMap<>();
        parse(seeds, preprocess);
    }

    /**
     * find unsetOp Type by setOp
     * @param setType
     * @return
     */
    public static List<OpType_isis> getUnsetType(OpType_isis setType){
        List<OpType_isis> res = new ArrayList<>();
        for(var unsetOpType: preprocess.keySet()){
            if (getRdcDef(unsetOpType).targetOps.contains(setType)){
                res.add(unsetOpType);
            }
        }
        return res;
    }

    public static List<String> getUnsetEqualArg(OpType_isis set_op_type, OpType_isis unset_op_type){
        var rdcDef = getRdcDef(unset_op_type);
        for(int i = 0; i < rdcDef.getEqualArgs().size(); i++){
            if (rdcDef.targetOps.get(i) == set_op_type){
                return rdcDef.equalArgs.get(i);
            }
        }
        assert false: "not have this set_op %s , unset_op %s pair ".formatted(set_op_type, unset_op_type);
        return null;
    }

    public static BaseRedexDef_ISIS getRdcDef(OpType_isis opType) {
        assert preprocess.containsKey(opType) : opType;
        return preprocess.get(opType);
    }
}
