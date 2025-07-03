package org.generator.lib.topo.pass.trans;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.topo.item.base.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.lib.topo.item.trans.transGraph;
import org.generator.util.collections.Pair;

import java.util.*;

public class phyTran {

    public static enum transRule{
        equalDealNode;
        static final List<transRule> l = new ArrayList<transRule>();
        static {
            l.addAll(Arrays.asList(transRule.values()));
        }
        public transRule getRule(int id){
            assert id < l.size():"not have transRule %d".formatted(id);
            return l.get(id);
        }
    }

    //delete one router
    public static boolean equalDelNode(transGraph transG) {
        //found a router which is not an ABR(OSPF), XXX(ISIS) and has at least two neighbors
        Router del_router = null;
        //TODO 7-3 we should random routers
        //TODO 7-3 stub networks
        //TODO 7-3 remain old networks
        for (var r : transG.getRouters()) {
            //FOR OSPF, we should delete router which is not an ABR
            var area_num = -1;
            if (generate.protocol == generate.Protocol.OSPF) {
                boolean isAbr = false;
                for (var intf : r.intfs) {
                   if (area_num == -1) area_num = intf.area;
                   else if (area_num != intf.area) isAbr = true;
                }
                if (isAbr) continue;
            }
            //the router should in the at least 2 transit network
            if (transG.getNetworkOfRouter(r).size() < 2) {
                continue;
            }
            del_router = r;
            //remove del_router and link

            //First we should count all the cost of networks through del_router
            List<Integer> networks = transG.getNetworkOfRouter(r);
            Map<Integer, Integer> deltaCost = new HashMap<>();
            Map<Integer, List<Intf>> networkToIntfs = new HashMap<>();
            for (var networkId : networks) {
                var intfs = transG.filterIntfOfRouter(del_router, transG.getIntfsOfNetwork(networkId));
                var micost = intfs.stream().map(i -> i.cost).min(Integer::compareTo).get();
                deltaCost.put(networkId, micost);
                networkToIntfs.put(networkId, transG.getLinkedIntfs(intfs.getFirst()));
            }

            //update network, we have changed this network and shall be not compared
            networks.forEach(transG.getDeltaNodes()::addUpdateNetworkId);

            //Next we link del_router's neighbors pair by pair
            //We should fill area, new cost, new networkId
            if (networks.size() == 2) {
                List<Intf> intfsA = null, intfsB = null;
                intfsA = networkToIntfs.get(networks.get(0));
                intfsB = networkToIntfs.get(networks.get(1));

                var new_network = transG.getNewNetworkId();

                //new network, we should not compare them
                transG.getDeltaNodes().addNewNetworkId(new_network);

                for (var dst_intf : networkToIntfs.get(networks.get(0))) {
                    dst_intf.cost += deltaCost.get(networks.get(1));
                    transG.updateIntf(dst_intf);
                    transG.addIntfToNetworkId(dst_intf, new_network);
                }

                for (var dst_intf : networkToIntfs.get(networks.get(0))) {
                    dst_intf.cost += deltaCost.get(networks.get(1));
                    transG.updateIntf(dst_intf);
                    transG.addIntfToNetworkId(dst_intf, new_network);
                }
            } else {
                //we first link one old interfaces of a network to one new interfaces of a network
                for (int i = 0; i < networks.size(); i++) {
                    List<Intf> intfsA = null, intfsB = null;
                    for (int j = (i + 1) % networks.size(); (j < networks.size() && i < networks.size() - 1) || (j == 0 && i == networks.size() - 1); j++) {
                        if (j == (i + 1) % networks.size()) {
                            intfsA = networkToIntfs.get(networks.get(i));
                            intfsA.forEach(transG::updateIntf);
                        }else{
                            intfsA = new ArrayList<>();
                            for(var copy_intf : networkToIntfs.get(networks.get(i))) {
                                var add_intf = transG.newIntf(transG.getRouterOfIntf(copy_intf));
                                add_intf.area = area_num;
                                add_intf.cost = copy_intf.cost;
                                intfsA.add(add_intf);
                            }
                        }

                        intfsB = new ArrayList<>();
                        for (var copy_intf : networkToIntfs.get(networks.get(j))) {
                            var add_intf = transG.newIntf(transG.getRouterOfIntf(copy_intf));
                            add_intf.area = area_num;
                            add_intf.cost = copy_intf.cost;
                            intfsB.add(add_intf);
                        }

                        var new_network = transG.getNewNetworkId();
                        for (var dst_intf : intfsA) {
                            dst_intf.cost += deltaCost.get(networks.get(j));
                            transG.addIntfToNetworkId(dst_intf, new_network);
                        }

                        for (var dst_intf : intfsB) {
                            dst_intf.cost += deltaCost.get(networks.get(i));
                            transG.addIntfToNetworkId(dst_intf, new_network);
                        }
                    }
                }
            }
            transG.removeRouter(del_router);
            break;
        }
        transG.checkRouters();
        return del_router != null;
    }

    public static transGraph solve(List<Router> routers, List<transRule> rules){
        var transG = new transGraph(routers);
        for(var rule: rules){
            switch (rule){
                case equalDealNode -> {equalDelNode(transG);}
            }
        }
        return transG;
    }
}
