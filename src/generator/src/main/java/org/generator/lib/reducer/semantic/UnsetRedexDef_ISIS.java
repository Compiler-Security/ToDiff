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

                {NORISIS,new OpType_isis[]{RISIS}},
                {NOTNET, new OpType_isis[]{NET}},
                //FIXME: we need "NOATTACHEDBIT"!
                //{NOATTACHEDBIT, new OpType_isis[]{ATTACHEDBIT}},
                {NOMETRICSTYLE, new OpType_isis[]{METRICSTYLE}},
                {NOADVERTISEHIGHMETRIC, new OpType_isis[]{ADVERTISEHIGHMETRIC}},
                {NOSETOVERLOADBIT, new OpType_isis[]{SETOVERLOADBIT}},
                {NOSETOVERLOADBITONSTARTUP, new OpType_isis[]{SETOVERLOADBITONSTARTUP}},
                {NOLSPMTU, new OpType_isis[]{LSPMTU}},
                {NOISTYPE, new OpType_isis[]{ISTYPE}},
                {NOIPROUTERISIS, new OpType_isis[]{IPROUTERISIS}},
                {NOIPAddr, new OpType_isis[]{IPAddr}},
                {NOCIRCUITTYPE, new OpType_isis[]{CIRCUITTYPE}},
                {NOCSNPINTERVAL, new OpType_isis[]{CSNPINTERVAL}},
                {NOHELLOPADDING, new OpType_isis[]{HELLOPADDING}},
                {NOHELLOINTERVAL, new OpType_isis[]{HELLOINTERVAL}},
                {NOHELLOMULTIPLIER, new OpType_isis[]{HELLOMULTIPLIER}},
                {NOISISMETRICLEVEL1, new OpType_isis[]{ISISMETRICLEVEL1}},
                {NOISISMETRICLEVEL2, new OpType_isis[]{ISISMETRICLEVEL2}},
                {NONETWORKPOINTTOPOINT, new OpType_isis[]{NETWORKPOINTTOPOINT}},
                {NOISISPASSIVE, new OpType_isis[]{ISISPASSIVE}},
                {NOISISPRIORITY, new OpType_isis[]{ISISPRIORITY}},
                {NOPSNPINTERVAL, new OpType_isis[]{PSNPINTERVAL}},
                {NOTHREEWAYHANDSHAKE, new OpType_isis[]{THREEWAYHANDSHAKE}},

        };
        var seeds = new ArrayList<Object[]>();
        for (var item : reduce_seed) {
            var OpType_isis = (OpType_isis) item[0];
            // switch (OpType_isis){

            //     default -> {
            //         seeds.add(new Object[]{item[0], item[1], LexDef_isis.getLexDef(OpType_isis).get(0).Args.size()});
            //     }
            // }
            seeds.add(new Object[]{item[0], item[1], LexDef_isis.getLexDef(OpType_isis).get(0).Args.size()});
        }
        preprocess = new HashMap<>();
        parse(seeds, preprocess);
    }

    /**
     * find unsetOp Type by setOp
     * preprocess : key is unsetOpType, value is setOpType and minimal args
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
