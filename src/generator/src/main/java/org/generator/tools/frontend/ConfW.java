package org.generator.tools.frontend;

import org.generator.lib.item.IR.OpBase;
import org.generator.lib.item.opg.OpCtxG;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public interface ConfW{
    String write(OpCtxG opCtxG);
    default void write(OpCtxG opCtxG, StringBuilder stringBuilder){
        stringBuilder.append(write(opCtxG));
    }

    default  void write(OpCtxG opCtxG, File file){
        assert file.canWrite(): "file should be writable!";
        try {
            new FileWriter(file).write(write(opCtxG));
        }catch (IOException e){
            e.printStackTrace();
            assert false;
        }
    }
}
