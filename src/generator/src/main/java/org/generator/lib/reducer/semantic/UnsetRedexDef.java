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
                {NORRIP, new OpType[]{RRIP}},
                {NONETWORKN, new OpType[]{NONETWORKN}},
                {NONETWORKI, new OpType[]{NONETWORKI}},
                {NONEIGHBOR, new OpType[]{NEIGHBOR}},
                {NOVERSION, new OpType[]{VERSION}},
                {NODEFAULTMETRIC, new OpType[]{DEFAULTMETRIC}},
                {NODISTANCE, new OpType[]{DISTANCE}},
                {NOPASSIVEINTFDEFAULT, new OpType[]{PASSIVEINTFDEFAULT}},
                {NOPASSIVEINTFNAME, new OpType[]{PASSIVEINTFNAME}},
                {NOTIMERSBASIC, new OpType[]{TIMERSBASIC}},
                //FIXME SOCKETBUFFERALL
                //{NOSOCKETBUFFERALL, new OpType[]{SOCKETBUFFERALL}},
                {NOIPAddr, new OpType[]{IPAddr}},

                {NOIPSPLITPOISION, new OpType[]{IPSPLITPOISION}},
                {NOIPSPLITHORIZION, new OpType[]{IPSPLITHORIZION, IPSPLITPOISION}},
        };
        var seeds = new ArrayList<Object[]>();
        for (var item : reduce_seed) {
            var opType = (OpType) item[0];
            switch (opType){
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
