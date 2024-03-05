/*
This pass will transform OpAG to OpAG' = Move(OpAG', target_opA(opAnalysis))
Input:
    OPAG
    target opA(opOspf + status)
Output:
    list of OpAG'


ATTENTION: this pass may add multiple opA to OpAG, and may change other OpA's states in the OpAG' by the rules
 */
package org.generator.lib.generator.pass;

import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.opg.OpAG;

import java.util.ArrayList;
import java.util.List;

public class movePass {
    private  OpAG move(OpAG opAG, OpAnalysis target_opA, int idx){
        var res = OpAG.of();

        return res;
    }

    public List<OpAG> solve(OpAG opAG, OpAnalysis target_opA){
        List<OpAG> res_list = new ArrayList<>();

        return res_list;
    }
}
