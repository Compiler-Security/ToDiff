package org.generator.tools.frontend;

import org.generator.lib.item.IR.OpCtx_ISIS;
import org.generator.lib.item.opg.OpCtxG_ISIS;

import java.io.BufferedReader;
import java.io.File;

/**
 * ConfR is read operations from conf
 * if read fail, it will return None
 * else it will return List<Operation>
 */
public interface ConfR_ISIS {

    OpCtxG_ISIS read(BufferedReader buf);

    OpCtxG_ISIS read(String st);

    OpCtxG_ISIS read(File file);
}
