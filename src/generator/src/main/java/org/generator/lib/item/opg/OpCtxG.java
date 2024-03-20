package org.generator.lib.item.opg;

import org.generator.lib.frontend.driver.IO;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.reducer.semantic.CtxOpDef;
import org.generator.tools.frontend.OspfConfWriter;

public class OpCtxG extends BaseOpG<OpCtx>{
    private OpCtxG(){super();}

    /**
     * create empty OpCtxG
     * @return empty OpCtxG
     */
    public static OpCtxG Of(){
        return new OpCtxG();
    }

    @Override
    public String toString() {
        var ospfConfWriter = new OspfConfWriter();
        return ospfConfWriter.write(this);
    }
}
