package org.generator.lib.generator.phy.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.item.opg.OpCtxG;

import java.util.HashMap;
import java.util.Map;

public class genPhyEqualPass {

    static Map<OpType, Integer> static_id = new HashMap<>(){{
        put(OpType.NODEADD, 0);
        put(OpType.NODEDEL, 1);
        put(OpType.NODESETOSPFUP, 0);
        put(OpType.NODESETOSPFRE, 1);
        put(OpType.INTFUP, 0);
        put(OpType.INTFDOWN, 1);
    }};

    void solve(OpCtxG phyOpg){

    }
}
