package org.generator.lib.topo.pass.trans;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.ospf.OSPF;
import org.generator.lib.item.conf.node.ospf.OSPFIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.phy.Router;
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

    //    public static class transPlan{
//        int typ_1, typ_2, typ_3, typ_4;
//        public transPlan(int _typ_1, int _typ_2, int _typ_3, int _typ_4){
//            typ_1 = _typ_1;
//            typ_2 = _typ_2;
//            typ_3 = _typ_3;
//            typ_4 = _typ_4;
//        }
//
//    }
//    public void trans(int typ_1, int typ_2, int typ_3, int typ_4, ConfGraph confGraph){
//        int total_trans_num = typ_1 + typ_2 + typ_3 + typ
//    }
    //delete one router
    public Pair<Boolean, deltaNodes> typ1Trans(ConfGraph confGraph) {
        deltaNodes deltaNodes = new deltaNodes();

        //found a router which is not an ABR(OSPF), XXX(ISIS) and has at least two neighbors
        String r_name = null;
        //FIXME we should random routers
        for (var r : confGraph.getRouters()) {
            if (generate.protocol == generate.Protocol.OSPF){
                boolean area0 = false , areax = false;
                for(var intf:confGraph.getIntfsOfRouter(r.getName())){
                    var areaNum = confGraph.<OSPFIntf>getDstByType(intf.getName(), RelationEdge.EdgeType.OSPFINTF).getArea().toLong();
                    if (areaNum == 0){
                        area0 = true;
                    }
                    if (areaNum > 0){
                        areax = true;
                    }
                }
                if (area0 && areax) continue;
            }
            Set<String> switch_name = new HashSet<>();
            for(var intf: confGraph.getIntfsOfRouter(r.getName())){
                switch_name.add(confGraph.getSwitchOfRIntf(intf.getName()).getName());
            }
            if (switch_name.size() < 2) continue;
            r_name = r.getName();
            break;
        }

    }
}
