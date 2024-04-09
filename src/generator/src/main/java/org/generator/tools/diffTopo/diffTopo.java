package org.generator.tools.diffTopo;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.topo.driver.topo;
import org.generator.tools.frontend.OspfConfWriter;

public class diffTopo {

    OpCtxG getConfOfRouter(String r_name, ConfGraph g){
        var confg = g.viewConfGraphOfRouter(r_name);
        confg.setR_name(r_name);
        return generate.generateCore(confg);
    }

    OpCtxG getConfOfPhy(ConfGraph g){
        return generate.generatePhyCore(g);
    }

    public void main(){
        var router_count = 3;
        var confg = topo.genGraph(router_count, 2, 2, 3, true);
        System.out.println("phy");
        System.out.println(new OspfConfWriter().write(getConfOfPhy(confg)));
        for(int i = 0; i < router_count; i++){
            var r_name = NodeGen.getRouterName(i);
            var opCtxG = getConfOfRouter(r_name, confg);
            System.out.println(r_name);
            System.out.println(new OspfConfWriter().write(opCtxG));
        }
    }
}
