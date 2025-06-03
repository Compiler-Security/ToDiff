package org.generator.lib.topo.pass.trans;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.ospf.OSPF;
import org.generator.lib.item.conf.node.ospf.OSPFIntf;
import org.generator.lib.topo.item.base.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.util.collections.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class phyTran {

    static class deltaNodes {
        List<Intf> oldIntf, newIntf;
        List<Router> newRouter, oldRouter;

        public void deltaNodes() {
            oldIntf = new ArrayList<>();
            newIntf = new ArrayList<>();
            newRouter = new ArrayList<>();
            oldRouter = new ArrayList<>();
        }
    }

    //delete one router
    public Pair<Boolean, deltaNodes> typ1Trans(List<Router> routers) {
        deltaNodes deltaNodes = new deltaNodes();

        //found a router which is not an ABR(OSPF), XXX(ISIS) and has at least two neighbors
        String r_name = null;
        //FIXME we should random routers
        for (var r : routers) {
            //FOR OSPF, we should delete router which is not an ABR
            if (generate.protocol == generate.Protocol.OSPF){
                boolean area0 = false , areax = false;
                for(var intf: r.intfs){
                    var areaNum = intf.area;
                    if (areaNum == 0){
                        area0 = true;
                    }
                    if (areaNum > 0){
                        areax = true;
                    }
                }
                if (area0 && areax) continue;
            }
            //the router should in the transit network (neighbor > 2)
            Set<String> dst_name = new HashSet<>();
            for(var intf: ){
                switch_name.add(confGraph.getSwitchOfRIntf(intf.getName()).getName());
            }
            if (switch_name.size() < 2) continue;
            r_name = r.getName();
            break;
        }

    }
}
