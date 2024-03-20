package org.generator.lib.item.opg;

import org.generator.lib.frontend.driver.IO;
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

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("[");
        for(var op: getOps()){
            b.append(IO.writeOp(op));
            b.append(",\n");
        }
        b.append("]");
        return b.toString();
    }
}
