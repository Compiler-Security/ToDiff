package org.generator.lib.reduction.semantic;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.operation.operation.OpType;

import java.util.HashMap;

import static org.generator.lib.operation.operation.OpType.*;

/**
 * This is the reduction Def of NoOpOSPF
 */
public class UnsetRedexDef {
    private static HashMap<OpType, OverideRedexDef> preprocess;

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
