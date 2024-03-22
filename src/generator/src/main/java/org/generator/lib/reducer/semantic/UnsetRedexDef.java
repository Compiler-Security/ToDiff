package org.generator.lib.reducer.semantic;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.frontend.lexical.OpType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.generator.lib.frontend.lexical.OpType.*;

/**
 * This is the reduction Def of NoOpOSPF
 */
public class UnsetRedexDef extends BaseRedexDef{
    private static HashMap<OpType, BaseRedexDef> preprocess;

    static {
        var reduce_seed = new Object[][]{
                //unset operation, we can assume that we compare minimal Args of NoOp with corresponding Args of OP
                //minimal args is the first LexDef of no operation
                {NOROSPF, new OpType[]{ROSPF}},
                {NORID, new OpType[]{RID}},
                {NORABRTYPE, new OpType[]{RABRTYPE}},
                {NONETAREAID, new OpType[]{NETAREAID}},
                {NOPASSIVEINTFDEFUALT, new OpType[]{PASSIVEINTFDEFUALT}},
                {NOTIMERSTHROTTLESPF, new OpType[]{TIMERSTHROTTLESPF}},
                {NOMAXIMUMPATHS, new OpType[]{MAXIMUMPATHS}},
                {NOWRITEMULTIPLIER, new OpType[]{WRITEMULTIPLIER}},
                {NOSOCKETBUFFERSEND, new OpType[]{SOCKETBUFFERSEND}},
                {NOSOCKETBUFFERRECV, new OpType[]{SOCKETBUFFERRECV}},
                {NOSOCKETBUFFERALL, new OpType[]{SOCKETBUFFERALL}},
                {NONOSOCKETPERINTERFACE, new OpType[]{NOSOCKETPERINTERFACE}},

                {NOAreaRange, new OpType[]{AreaRange, AreaRangeNoAd, AreaRangeSub, AreaRangeCost}},
                {NOAreaRangeNoAd, new OpType[]{AreaRange, AreaRangeNoAd, AreaRangeSub, AreaRangeCost}},
                {NOAreaRangeSub, new OpType[]{AreaRange, AreaRangeNoAd, AreaRangeSub, AreaRangeCost}},
                {NOAreaRangeCost, new OpType[]{AreaRange, AreaRangeNoAd, AreaRangeSub, AreaRangeCost}},


                {NOAreaVLink, new OpType[]{AreaVLink}},
                {NOAreaShortcut, new OpType[]{AreaShortcut}},

                {NOAreaStub, new OpType[]{AreaStub, AreaStubTotal}},

                //FIXME NoAreaStubTotal will create a new AreaStub
                {NOAreaStubTotal, new OpType[]{AreaStubTotal}},
                {NOAreaNSSA, new OpType[]{AreaNSSA}},
                {NOIPAddr, new OpType[]{IPAddr}},
                {NOIpOspfArea, new OpType[]{IpOspfArea}},
                {NOIpOspfCost, new OpType[]{IpOspfCost}},
                {NOIpOspfDeadInter, new OpType[]{IpOspfDeadInter}},
                {NOIpOspfDeadInterMulti, new OpType[]{IpOspfDeadInterMulti}},
                {NOIpOspfHelloInter, new OpType[]{IpOspfHelloInter}},
                {NOIpOspfGRHelloDelay, new OpType[]{IpOspfGRHelloDelay}},
                {NOIpOspfNet, new OpType[]{IpOspfNet}},
                {NOIpOspfPriority, new OpType[]{IpOspfPriority}},
                {NOIpOspfRetransInter, new OpType[]{IpOspfRetransInter}},
                {NOIpOspfTransDealy, new OpType[]{IpOspfTransDealy}},
                {NOIpOspfPassive, new OpType[]{IpOspfPassive}},
        };
        var seeds = new ArrayList<Object[]>();
        for (var item : reduce_seed) {
            var opType = (OpType) item[0];
            switch (opType){
                case NOAreaRangeSub,NOAreaRangeCost,NOAreaRangeNoAd,NOAreaRange -> {
                    seeds.add(new Object[]{item[0], item[1], 2});
                }
                default -> {
                    seeds.add(new Object[]{item[0], item[1], LexDef.getLexDef(opType).get(0).Args.size()});
                }
            }

        }
        preprocess = new HashMap<>();
        parse(seeds, preprocess);
    }

    /**
     * find unsetOp Type by setOp
     * @param setType
     * @return
     */
    public static List<OpType> getUnsetType(OpType setType){
        List<OpType> res = new ArrayList<>();
        for(var unsetOpType: preprocess.keySet()){
            if (getRdcDef(unsetOpType).targetOps.contains(setType)){
                res.add(unsetOpType);
            }
        }
        return res;
    }
    public static BaseRedexDef getRdcDef(OpType opType) {
        assert preprocess.containsKey(opType) : opType;
        return preprocess.get(opType);
    }
}
