package org.generator.lib.reducer.driver;

import org.generator.lib.item.opg.OpAG_ISIS;
import org.generator.lib.item.opg.OpCtxG_ISIS;
import org.generator.lib.item.conf.graph.ConfGraph_ISIS;
import org.generator.lib.reducer.pass.isisArgPass;
import org.generator.lib.reducer.pass.reducePass_ISIS;

public class reducer_ISIS {
    /**
     * It will reduce the given opAG
     * @param opAG
     * @return rOpAG
     */
    public static int s = 0;
    public static void reduce(OpAG_ISIS opAG){
        s += 1;
        var r = new reducePass_ISIS();
        r.solve(opAG);
        //System.out.printf("reducer count %d, opg size %d %d\n", s, opAG.activeSetView().getOps().size(), opAG.getOps().size());
    }

    public static OpAG_ISIS reduce(OpCtxG_ISIS opCtxG){
        var r = new reducePass_ISIS();
        return r.solve(opCtxG).activeSetView();
    }

    /**
     * This will reduce given opCtxG, and write it to confG
     * @param opCtxG
     * @param confG
     */
    public static void reduceToConfG(OpCtxG_ISIS opCtxG, ConfGraph_ISIS confG){
        var r = new reducePass_ISIS();
        var opaG = r.solve(opCtxG).activeSetView();
        isisArgPass.solve(opaG, confG, confG.getR_name());
    }

    /**
     * This function will return
     * @param opCtxG
     * @return
     */
    public static OpCtxG_ISIS reduceToCore(OpCtxG_ISIS opCtxG){
        var r = new reducePass_ISIS();
        return r.solve(opCtxG).toOpCtxGActiveSet();
    }


}
