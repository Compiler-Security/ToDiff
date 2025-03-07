package org.generator.lib.reducer.driver;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.reducer.pass.ospfArgPass;
import org.generator.lib.reducer.pass.reducePass;
import org.generator.lib.reducer.pass.ripArgPass;
import org.generator.lib.reducer.pass.isisArgPass;
import org.generator.lib.reducer.pass.openfabricArgPass;

public class reducer {
    /**
     * It will reduce the given opAG
     * @param opAG
     * @return rOpAG
     */
    public static int s = 0;
    public static void reduce(OpAG opAG){
        s += 1;
        var r = new reducePass();
        r.solve(opAG);
        //System.out.printf("reducer count %d, opg size %d %d\n", s, opAG.activeSetView().getOps().size(), opAG.getOps().size());
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
        switch (generate.protocol){
            case OSPF -> {ospfArgPass.solve(opaG, confG, confG.getR_name());}
            case RIP -> {ripArgPass.solve(opaG, confG, confG.getR_name());}
            case ISIS -> {isisArgPass.solve(opaG, confG, confG.getR_name());}
            case OpenFabric -> {openfabricArgPass.solve(opaG, confG, confG.getR_name());}
        }
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
