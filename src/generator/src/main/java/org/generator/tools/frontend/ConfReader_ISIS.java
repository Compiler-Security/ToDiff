package org.generator.tools.frontend;

import org.generator.lib.frontend.driver.IO_ISIS;
import org.generator.lib.item.IR.*;
import org.generator.lib.item.opg.OpCtxG_ISIS;
import org.generator.lib.frontend.lexical.OpType_isis;

import java.io.*;

public class ConfReader_ISIS implements ConfR_ISIS{

    private OpCtx_ISIS invalid(String op_st){
        var new_opctx = OpCtx_ISIS.of(OpIsis.of(OpType_isis.INVALID), 0);
        new_opctx.format.addByPass("NAME", op_st);
        return new_opctx;
    }
    private OpCtx_ISIS getOperation(String op_st){
        var op = OpIsis.of();
        var opCtx = IO_ISIS.readOp(op_st, op);
        if (opCtx == null){
            return invalid(op_st);
        }
        return opCtx;
    }

    @Override
    public OpCtxG_ISIS read(BufferedReader buf) {
        var opctxg = OpCtxG_ISIS.Of();
        try {
            String line;
            while ((line = buf.readLine()) != null) {
                line = line.strip();
                if (line.isEmpty()) continue;
                opctxg.addOp(getOperation(line));
            }
            return opctxg;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public OpCtxG_ISIS read(String st) {
        return read(new BufferedReader(new StringReader(st)));
    }

    @Override
    public OpCtxG_ISIS read(File file) {
        try {
            return read(new BufferedReader(new FileReader(file)));
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
