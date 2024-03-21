package org.generator.lib.reducer.driver;

import org.generator.lib.generator.pass.genCorePass;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.topo.graph.ConfGraph;
import org.generator.lib.reducer.pass.ospfArgPass;
import org.generator.lib.reducer.pass.reducePass;

import java.util.HashMap;
import java.util.Map;

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

    public static OpAG reduce(OpCtxG opCtxG){
        var r = new reducePass();
        return r.solve(opCtxG).activeSetView();
    }

    /**
     * This will reduce given opCtxG, and write it to confG
     * @param opCtxG
     * @param confG
     */
    public static void reduceToConfG(OpCtxG opCtxG, ConfGraph confG){
        var r = new reducePass();
        var opaG = r.solve(opCtxG).activeSetView();
        ospfArgPass.solve(opaG, confG, confG.getR_name());
    }

    /**
     * This function will return
     * @param opCtxG
     * @return
     */
    public static OpCtxG reduceToCore(OpCtxG opCtxG){
        var r = new reducePass();
        return r.solve(opCtxG).toOpCtxGActiveSet();
    }


}
