package org.generator.lib.topo.pass.trans;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.topo.item.base.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.lib.topo.item.trans.transGraph;
import org.generator.util.collections.Pair;

import java.util.*;

public class phyTran {

    static class deltaNodes {
        List<Intf> newIntf, updateIntf;
        List<Router> newRouter, updateRouter;

        public void deltaNodes() {
            newIntf = new ArrayList<>();
            newRouter = new ArrayList<>();
            updateIntf = new ArrayList<>();
            updateRouter = new ArrayList<>();
        }

        public boolean isNewIntf(Intf intf){
            return newIntf.contains(intf);
        }

        public boolean isNewRouter(Router router){
            return newRouter.contains(router);
        }
    }

    //delete one router
    public Pair<Boolean, deltaNodes> typ1Trans(List<Router> routers) {
        var deltas = new deltaNodes();

        //found a router which is not an ABR(OSPF), XXX(ISIS) and has at least two neighbors
        Router del_router = null;
        var transG = new transGraph(routers);
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
            //the router should in the at least 2 transit network
            if (transG.getNetworkOfRouter(r).size() < 2){
                continue;
            }
            del_router = r;

            //remove del_router and link

            //First we should count all the cost of networks through del_router
            List<Integer> networks = transG.getNetworkOfRouter(r);
            Map<Integer, Integer> deltaCost = new HashMap<>();
            Map<Integer, List<Router>> networkToRouters = new HashMap<>();
            Map<Integer, List<Intf>> networkToIntfs = new HashMap<>();
            for(var networkId: networks){
                var intfs = transG.filterIntfOfRouter(del_router, transG.getIntfsOfNetwork(networkId));
                var micost = intfs.stream().map(i -> i.cost).min(Integer::compareTo).get();
                deltaCost.put(networkId, micost);
                networkToRouters.put(networkId, transG.getLinkedRouters(intfs.getFirst()));
                networkToIntfs.put(networkId, transG.getLinkedIntfs(intfs.getFirst()));
            }

            //Next we link del_router's neighbors pair by pair
            for(int i = 0; i < networks.size(); i++){
                for(int j = i + 1; j < networks.size(); j++){
                    if (i == 0){
                        //we reuse the old interfaces
                        deltas.updateIntf.addAll(networkToIntfs.get(networks.get(i)));
                        deltas.updateIntf.addAll(networkToIntfs.get(networks.get(j)));


                    }else{
                        //we add the new interfaces
                    }
                }
            }
            break;
        }
        if (del_router == null){return new Pair<>(false, deltaNodes);}
        else return new Pair<>(true, deltaNodes);
    }
}
