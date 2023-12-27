package org.generator.tools.frontend;

import org.generator.lib.frontend.driver.IO;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.operation.operation.OpType;

public class OspfConfWriter implements ConfW{

    @Override
    public String write(OpCtxG opCtxG) {
        StringBuilder stringBuilder = new StringBuilder();
        for(var opCtx: opCtxG.getOps()){
            if (opCtx.getOperation().Type() == OpType.INVALID){
                stringBuilder.append("INVALID: ");
            }
            stringBuilder.append(IO.writeOp(opCtx));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
