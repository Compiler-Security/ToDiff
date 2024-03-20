package org.generator.lib.reducer.driver;

import org.generator.lib.item.opg.OpAG;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.topo.graph.ConfGraph;
import org.generator.lib.reducer.pass.ospfArgPass;
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

    /**
     * This will reduce given opCtxG, and write it to confG
     * @param opCtxG
     * @param confG
     * @param r_name
     */
    public static void reduceToConfG(OpCtxG opCtxG, ConfGraph confG, String r_name){
        var r = new reducePass();
        var opaG = r.solve(opCtxG);
        ospfArgPass.solve(opaG, confG, r_name);
    }
}
