package org.generator.lib.generator.driver;

import org.generator.lib.generator.pass.genCorePass;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.topo.graph.ConfGraph;

public class generate {
    public static OpCtxG generateCore(ConfGraph confGraph){
        var p = new genCorePass();
        var res1 = p.solve(confGraph);
        return genCorePass.mergeOpCtxgToOne(res1);
    }
}
