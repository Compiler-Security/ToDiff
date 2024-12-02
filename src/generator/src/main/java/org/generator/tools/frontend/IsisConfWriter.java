package org.generator.tools.frontend;

import org.generator.lib.frontend.driver.IO_ISIS;
import org.generator.lib.item.opg.OpCtxG_ISIS;
import org.generator.lib.frontend.lexical.OpType_isis;
import org.generator.lib.reducer.semantic.CtxOpDef_ISIS;

public class IsisConfWriter implements ConfW_ISIS{

    @Override
    public String write(OpCtxG_ISIS opCtxG) {
        StringBuilder stringBuilder = new StringBuilder();
        for(var opCtx: opCtxG.getOps()){
            if (opCtx.getOperation().Type() == OpType_isis.INVALID){
                stringBuilder.append("INVALID: ");
            }
            if (!CtxOpDef_ISIS.isCtxOp(opCtx.getOperation().Type())){
                stringBuilder.append("\t");
            }
            stringBuilder.append(IO_ISIS.writeOp(opCtx));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
