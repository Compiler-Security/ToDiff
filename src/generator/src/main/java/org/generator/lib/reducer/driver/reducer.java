package org.generator.lib.reducer.driver;

import org.generator.lib.item.opg.OpAG;
import org.generator.lib.reducer.pass.reducePass;

public class reducer {
    /**
     * It will reduce the given opAG
     * @param opAG
     * @return rOpAG
     */
    public static void reduce(OpAG opAG){
        var r = new reducePass();
        r.solve(opAG);
    }
}
