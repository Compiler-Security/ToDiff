package org.generator.tools.frontend;

import org.generator.lib.frontend.driver.IO;
import org.generator.lib.item.IR.*;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.frontend.lexical.OpType;

import java.io.*;

public class ConfReader implements ConfR{

    private OpCtx invalid(String op_st){
        var new_opctx = OpCtx.of(OpOspf.of(OpType.INVALID), 0);
        new_opctx.format.addByPass("NAME", op_st);
        return new_opctx;
    }
    private OpCtx getOperation(String op_st){
        var op = OpOspf.of();
        var opCtx = IO.readOp(op_st, op);
        if (opCtx == null){
            return invalid(op_st);
        }
        return opCtx;
    }

    @Override
    public OpCtxG read(BufferedReader buf) {
        var opctxg = OpCtxG.Of();
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
    public OpCtxG read(String st) {
        return read(new BufferedReader(new StringReader(st)));
    }

    @Override
    public OpCtxG read(File file) {
        try {
            return read(new BufferedReader(new FileReader(file)));
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
