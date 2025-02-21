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
                //============ZEBRA=================
                {NOIPAddr, new OpType[]{IPAddr}},

                //=============OSPF=================
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
                //FIXME SOCKETBUFFERALL
                //{NOSOCKETBUFFERALL, new OpType[]{SOCKETBUFFERALL}},
                {SOCKETPERINTERFACE, new OpType[]{NoSOCKETPERINTERFACE}},

                {NOAreaRange, new OpType[]{AreaRange, AreaRangeNoAd, AreaRangeSub, AreaRangeCost}},
                {NOAreaRangeNoAd, new OpType[]{AreaRange, AreaRangeNoAd, AreaRangeSub, AreaRangeCost}},
                {NOAreaRangeCost, new OpType[]{AreaRange, AreaRangeNoAd, AreaRangeSub, AreaRangeCost}},

                //{NOAreaRangeSub, new OpType[]{AreaRange}},


                //FIXME areaVLINK
                //{NOAreaVLink, new OpType[]{AreaVLink}},
                {NOAreaShortcut, new OpType[]{AreaShortcut}},

                {NOAreaStub, new OpType[]{AreaStub, AreaStubTotal}},

                {NORefreshTimer, new OpType[]{RefreshTimer}},
                {NOTimersLsaThrottle, new OpType[]{TimersLsaThrottle}},

                //FIXME simple fix of NOAreaStubTotal
               //{NOAreaStubTotal, new OpType[]{AreaStubTotal}},
                {NOAreaNSSA, new OpType[]{AreaNSSA}},

                {NOIpOspfArea, new OpType[]{IpOspfArea}},
                {NOIpOspfCost, new OpType[]{IpOspfCost}},
                {NOIpOspfDeadInter, new OpType[]{IpOspfDeadInter}},
                {NOIpOspfDeadInterMulti, new OpType[]{IpOspfDeadInterMulti}},
                {NOIpOspfHelloInter, new OpType[]{IpOspfHelloInter}},
                {NOIpOspfGRHelloDelay, new OpType[]{IpOspfGRHelloDelay}},
                {NOIpOspfNet, new OpType[]{IpOspfNet}},
                {NOIpOspfPriority, new OpType[]{IpOspfPriority}},
                {NOIpOspfRetransInter, new OpType[]{IpOspfRetransInter}},
                {NOIpOspfTransDelay, new OpType[]{IpOspfTransDelay}},
                {NOIpOspfPassive, new OpType[]{IpOspfPassive}},

                //==================RIP=============================
                {NORRIP, new OpType[]{RRIP}},
                {NONETWORKN, new OpType[]{NETWORKN}},
                {NONETWORKI, new OpType[]{NETWORKI}},
                {NONEIGHBOR, new OpType[]{NEIGHBOR}},
                {NOVERSION, new OpType[]{VERSION}},
                {NODEFAULTMETRIC, new OpType[]{DEFAULTMETRIC}},
                {NODISTANCE, new OpType[]{DISTANCE}},
                {NOPASSIVEINTFDEFAULT, new OpType[]{PASSIVEINTFDEFAULT}},
                {NOPASSIVEINTFNAME, new OpType[]{PASSIVEINTFNAME}},
                {NOTIMERSBASIC, new OpType[]{TIMERSBASIC}},
                {NOIPSPLITPOISION, new OpType[]{IPSPLITPOISION}},
                {NOIPSPLITHORIZION, new OpType[]{IPSPLITHORIZION, IPSPLITPOISION}},
                {NOIPSENDVERSION1, new OpType[]{IPSENDVERSION1, IPSENDVERSION2, IPSENDVERSION12}},
                {NOIPSENDVERSION2, new OpType[]{IPSENDVERSION1, IPSENDVERSION2, IPSENDVERSION12}},
                {NOIPSENDVERSION12, new OpType[]{IPSENDVERSION1, IPSENDVERSION2, IPSENDVERSION12}},
                {NOIPRECVVERSION1, new OpType[]{IPRECVVERSION1, IPRECVVERSION2, IPRECVVERSION12}},
                {NOIPRECVVERSION2, new OpType[]{IPRECVVERSION1, IPRECVVERSION2, IPRECVVERSION12}},
                {NOIPRECVVERSION12, new OpType[]{IPRECVVERSION1, IPRECVVERSION2, IPRECVVERSION12}},
        };
        var seeds = new ArrayList<Object[]>();
        for (var item : reduce_seed) {
            var opType = (OpType) item[0];
            switch (opType){
//                case NoAreaRangeSub -> {seeds.add(new Object[]{item[0], item[1], 2});}
                case NOAreaRangeCost,NOAreaRangeNoAd,NOAreaRange -> {
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

    public static List<String> getUnsetEqualArg(OpType set_op_type, OpType unset_op_type){
        var rdcDef = getRdcDef(unset_op_type);
        for(int i = 0; i < rdcDef.getEqualArgs().size(); i++){
            if (rdcDef.targetOps.get(i) == set_op_type){
                return rdcDef.equalArgs.get(i);
            }
        }
        assert false: "not have this set_op %s , unset_op %s pair ".formatted(set_op_type, unset_op_type);
        return null;
    }

    public static BaseRedexDef getRdcDef(OpType opType) {
        assert preprocess.containsKey(opType) : opType;
        return preprocess.get(opType);
    }
}
