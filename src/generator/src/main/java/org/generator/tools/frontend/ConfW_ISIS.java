package org.generator.tools.frontend;

import org.generator.lib.item.IR.OpBase_ISIS;
import org.generator.lib.item.opg.OpCtxG_ISIS;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public interface ConfW_ISIS{
    String write(OpCtxG_ISIS opCtxG);
    default void write(OpCtxG_ISIS opCtxG, StringBuilder stringBuilder){
        stringBuilder.append(write(opCtxG));
    }

    default  void write(OpCtxG_ISIS opCtxG, File file){
        file.setWritable(true);
        //assert file.canWrite(): "file should be writable!";
        try {
            var fw = new FileWriter(file);
            fw.write(write(opCtxG));
            fw.close();
        }catch (IOException e){
            e.printStackTrace();
            assert false;
        }
    }
}
