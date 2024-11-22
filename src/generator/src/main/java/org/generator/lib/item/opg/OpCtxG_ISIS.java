package org.generator.lib.item.opg;

import org.generator.lib.item.IR.OpCtx_ISIS;
import org.generator.tools.frontend.IsisConfWriter;

import java.util.Iterator;
import java.util.List;

public class OpCtxG_ISIS extends BaseOpG<OpCtx_ISIS>{
    private OpCtxG_ISIS(){super();}

    /**
     * create empty OpCtxG
     * @return empty OpCtxG
     */
    public static OpCtxG_ISIS Of(){
        return new OpCtxG_ISIS();
    }

    public static OpCtxG_ISIS Of(List<OpCtx_ISIS> ops){
        var opctxg = OpCtxG_ISIS.Of();
        opctxg.addOps(ops);
        return opctxg;
    }

    @Override
    public String toString() {
        var ospfConfWriter = new OspfConfWriter();
        return ospfConfWriter.write(this);
    }
}
