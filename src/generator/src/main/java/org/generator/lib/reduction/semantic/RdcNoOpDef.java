package org.generator.lib.reduction.semantic;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.operation.operation.OpType;

import java.util.HashMap;

import static org.generator.lib.operation.operation.OpType.*;

/**
 * This is the reduction Def of NoOpOSPF
 */
public class RdcNoOpDef {
    private static HashMap<OpType, RdcNoOpDef> preprocess;

    static {
        var reduce_seed = new Object[][]{
                //SET opertaion, we can assume that different LexDef of one type has same Args
                {ROSPF, new OpType[]{ROSPF}},
                {IntfName, new OpType[]{IntfName}},
                {RID, new OpType[]{RID}},
                {RABRTYPE, new OpType[]{RABRTYPE}},
                {NETAREAID, new OpType[]{NETAREAID}},
                {PASSIVEINTFDEFUALT, new OpType[]{PASSIVEINTFDEFUALT}},
                {TIMERSTHROTTLESPF, new OpType[]{TIMERSTHROTTLESPF}},
                {CLEARIPOSPFPROCESS, new OpType[]{CLEARIPOSPFPROCESS}},
                {CLEARIPOSPFNEIGHBOR, new OpType[]{CLEARIPOSPFNEIGHBOR}},
                {MAXIMUMPATHS, new OpType[]{MAXIMUMPATHS}},
                {WRITEMULTIPLIER, new OpType[]{WRITEMULTIPLIER}},
                {SOCKETBUFFERSEND, new OpType[]{SOCKETBUFFERSEND}},
                {SOCKETBUFFERRECV, new OpType[]{SOCKETBUFFERRECV}},
                {SOCKETBUFFERALL, new OpType[]{SOCKETBUFFERALL}},
                {NOSOCKETPERINTERFACE, new OpType[]{NOSOCKETPERINTERFACE}},
                {AreaRange, new OpType[]{AreaRange}},
                {AreaRangeNoAd, new OpType[]{AreaRangeNoAd}},
                {AreaRangeSub, new OpType[]{AreaRangeSub}},
                {AreaRangeCost, new OpType[]{AreaRangeCost}},
                {AreaVLink, new OpType[]{AreaVLink}},
                {AreaShortcut, new OpType[]{AreaShortcut}},
                {AreaStub, new OpType[]{AreaStub}},
                {AreaStubTotal, new OpType[]{AreaStubTotal}},
                {AreaNSSA, new OpType[]{AreaNSSA}},
                {IPAddr, new OpType[]{IPAddr}},
                {IpOspfArea, new OpType[]{IpOspfArea}},
                {IpOspfCost, new OpType[]{IpOspfCost}},
                {IpOspfDeadInter, new OpType[]{IpOspfDeadInter}},
                {IpOspfDeadInterMulti, new OpType[]{IpOspfDeadInterMulti}},
                {IpOspfHelloInter, new OpType[]{IpOspfHelloInter}},
                {IpOspfGRHelloDelay, new OpType[]{IpOspfGRHelloDelay}},
                {IpOspfNet, new OpType[]{IpOspfNet}},
                {IpOspfPriority, new OpType[]{IpOspfPriority}},
                {IpOspfRetransInter, new OpType[]{IpOspfRetransInter}},
                {IpOspfTransDealy, new OpType[]{IpOspfTransDealy}},
                {IpOspfPassive, new OpType[]{IpOspfPassive}},

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
            var rdcDef = new RdcNoOpDef();
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

    public static RdcNoOpDef getRdcDef(OpType opType) {
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
