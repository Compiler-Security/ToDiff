package org.generator.lib.frontend.pass;

import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.frontend.lexical.LexCtx;
import org.jetbrains.annotations.NotNull;

public class OpDumpPass {

    /** This will always return the corresponding LexCtx, always success
     * in: opctx out: LexCtx, bypass can put token->String directly to LexCtx*/
    @NotNull public LexCtx solve(OpCtx opctx){
        var lexCtx = new LexCtx();
        lexCtx.lexDef = opctx.format.lexDef;
        lexCtx.opType = opctx.getOperation().Type();
        var tokenMap = lexCtx.tokenMap;
        var op = opctx.getOperation();
        if (op.getNAME() != null){
            tokenMap.put("NAME", op.getNAME());
        }
        if (op.getNAME2() != null){
            tokenMap.put("NAME2", op.getNAME2());
        }
        if (op.getIP() != null){
            tokenMap.put("IP", op.getIP().toString());
        }
        if (op.getID() != null){
            if (opctx.format.IDISNUM){
                tokenMap.put("ID", op.getID().toLong().toString());
            }else {
                tokenMap.put("ID", op.getID().toString());
            }
        }
        if (op.getIPRANGE() != null){
            tokenMap.put("IPRANGE", op.getIPRANGE().toString());
        }
        if (op.getNUM() != null){
            tokenMap.put("NUM", op.getNUM().toString());
        }
        if (op.getNUM2() != null){
            tokenMap.put("NUM2", op.getNUM2().toString());
        }
        if (op.getNUM3() != null){
            tokenMap.put("NUM3", op.getNUM3().toString());
        }
        if (op.getLONGNUM() != null){
            tokenMap.put("LONGNUM", op.getLONGNUM().toString());
        }
        if(op.getNET()!=null){
            tokenMap.put("NET", op.getNET().toString());
        }
        var bypass = opctx.getFormmat().getByPass();
        if (bypass != null) {
            tokenMap.putAll(bypass);
        }
        return lexCtx;
    }
}
