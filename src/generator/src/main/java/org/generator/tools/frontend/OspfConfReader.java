package org.generator.tools.frontend;

import org.generator.lib.frontend.driver.IO;
import org.generator.lib.frontend.pass.OpBuilderPass;
import org.generator.lib.frontend.pass.StrToLexPass;
import org.generator.lib.item.IR.*;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.operation.operation.OpType;
import org.generator.tools.frontend.ConfR;

import java.io.*;

public class OspfConfReader implements ConfR{

    private OpCtx invalid(){
        return new OpCtx(new OpOspf(OpType.INVALID));
    }
    private OpCtx getOperation(String op_st){
        var op = new OpOspf();
        var opCtx = IO.readOp(op_st, op);
        if (opCtx == null){
            return invalid();
        }
        return opCtx;
    }

    @Override
    public OpCtxG read(BufferedReader buf) {
        var opctxg = new OpCtxG();
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
