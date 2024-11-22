package org.generator.lib.frontend.driver;

import org.generator.lib.frontend.pass.LexToStrPass;
import org.generator.lib.frontend.pass.OpBuilderPass;
import org.generator.lib.frontend.pass.OpDumpPass;
import org.generator.lib.frontend.pass.StrToLexPass;
import org.generator.lib.item.IR.Op_ISIS;
import org.generator.lib.item.IR.OpCtx_ISIS;
import org.jetbrains.annotations.Nullable;

public class IO {
    /** this solve will parse the st_op, fill the Op and return OpCtx, if parse fail, we will return null*/
    @Nullable public static OpCtx_ISIS readOp(String op_st, Op_ISIS op){
        var lexCtx = new StrToLexPass().solve(op_st);
        return new OpBuilderPass().solve(lexCtx, op);
    }


    /** this solve will always return the string of operation, if the operation is not right, we will panic*/
    public static String writeOp(OpCtx_ISIS opCtx){
        return new LexToStrPass().solve(new OpDumpPass().solve(opCtx));
    }
}
