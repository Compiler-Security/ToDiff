package org.generator.lib.frontend.driver;

import org.generator.lib.frontend.pass.LexToStrPass_ISIS;
import org.generator.lib.frontend.pass.OpBuilderPass_ISIS;
import org.generator.lib.frontend.pass.OpDumpPass_ISIS;
import org.generator.lib.frontend.pass.StrToLexPass_ISIS;
import org.generator.lib.item.IR.Op_ISIS;
import org.generator.lib.item.IR.OpCtx_ISIS;
import org.jetbrains.annotations.Nullable;

public class IO_ISIS {
    /** this solve will parse the st_op, fill the Op and return OpCtx, if parse fail, we will return null*/
    @Nullable public static OpCtx_ISIS readOp(String op_st, Op_ISIS op){
        var lexCtx = new StrToLexPass_ISIS().solve(op_st);
        return new OpBuilderPass_ISIS().solve(lexCtx, op);
    }


    /** this solve will always return the string of operation, if the operation is not right, we will panic*/
    public static String writeOp(OpCtx_ISIS opCtx){
        return new LexToStrPass_ISIS().solve(new OpDumpPass_ISIS().solve(opCtx));
    }
}
