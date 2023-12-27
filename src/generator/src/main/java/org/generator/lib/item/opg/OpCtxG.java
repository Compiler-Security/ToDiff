package org.generator.lib.item.opg;

import org.generator.lib.item.IR.OpCtx;

public class OpCtxG extends BaseOpG<OpCtx>{
    private OpCtxG(){super();}

    /**
     * create empty OpCtxG
     * @return empty OpCtxG
     */
    public static OpCtxG Of(){
        return new OpCtxG();
    }
}
