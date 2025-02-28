package org.generator.tools.frontend;

import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.opg.OpCtxG;

import java.io.BufferedReader;
import java.io.File;

/**
 * ConfR is read operations from conf
 * if read fail, it will return None
 * else it will return List<Operation>
 */
public interface ConfR {

    OpCtxG read(BufferedReader buf);

    OpCtxG read(String st);

    OpCtxG read(File file);
}
