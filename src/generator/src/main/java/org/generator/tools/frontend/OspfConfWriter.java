package org.generator.tools.frontend;

import org.generator.lib.frontend.driver.IO;
import org.generator.lib.item.opg.OpCtxG;

public class OspfConfWriter implements ConfW{

    @Override
    public String write(OpCtxG opCtxG) {
        StringBuilder stringBuilder = new StringBuilder();
        for(var opCtx: opCtxG.getOps()){
            stringBuilder.append(IO.writeOp(opCtx));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
