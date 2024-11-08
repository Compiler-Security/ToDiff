package org.generator.lib.item.opg;

import org.generator.lib.item.IR.OpCtx;
import org.generator.tools.frontend.OspfConfWriter;

import java.util.Iterator;
import java.util.List;

public class OpCtxG extends BaseOpG<OpCtx>{
    private OpCtxG(){super();}

    /**
     * create empty OpCtxG
     * @return empty OpCtxG
     */
    public static OpCtxG Of(){
        return new OpCtxG();
    }

    public static OpCtxG Of(List<OpCtx> ops){
        var opctxg = OpCtxG.Of();
        opctxg.addOps(ops);
        return opctxg;
    }

    @Override
    public String toString() {
        var ospfConfWriter = new OspfConfWriter();
        return ospfConfWriter.write(this);
    }
}
