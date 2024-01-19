package org.generator.lib.reduction.semantic;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.operation.operation.OpType;

import java.util.ArrayList;
import java.util.HashMap;

import static org.generator.lib.operation.operation.OpType.*;

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
                {NOAreaRangeNoAd, new OpType[]{AreaRangeNoAd}},
                {NOAreaRangeSub, new OpType[]{AreaRangeSub}},
                //FIXME is this no lexical right?
                {NOAreaRangeCost, new OpType[]{AreaRangeCost}},
                {NOAreaVLink, new OpType[]{AreaVLink}},
                {NOAreaShortcut, new OpType[]{AreaShortcut}},
                //FIXME is this no target right, apply it to areaStubTotal?
                {NOAreaStub, new OpType[]{AreaStub}},
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
            seeds.add(new Object[]{item[0], item[1], LexDef.getLexDef(opType).get(0).Args.size()});
        }
        preprocess = new HashMap<>();
        parse(seeds, preprocess);
    }

    public static BaseRedexDef getRdcDef(OpType opType) {
        assert preprocess.containsKey(opType) : opType;
        return preprocess.get(opType);
    }
}
