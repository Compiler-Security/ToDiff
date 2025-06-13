package org.generator.lib.topo.pass.trans;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.topo.item.base.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.lib.topo.item.trans.transGraph;
import org.generator.util.collections.Pair;

import java.util.*;

public class phyTran {

    public static class deltaNodes {
        Set<Intf> newIntf, updateIntf;
        Set<Router> newRouter, updateRouter;

        public deltaNodes() {
            newIntf = new HashSet<>();
            updateIntf = new HashSet<>();
            newRouter = new HashSet<>();
            updateRouter = new HashSet<>();
        }

        public boolean isNewIntf(Intf intf) {
            return newIntf.contains(intf);
        }

        public boolean isNewRouter(Router router) {
            return newRouter.contains(router);
        }

        public void mergeDeltaNodes(deltaNodes _deltaNodes) {
            newIntf.addAll(_deltaNodes.newIntf);
            updateIntf.addAll(_deltaNodes.updateIntf);
            newRouter.addAll(_deltaNodes.newRouter);
            updateRouter.addAll(_deltaNodes.updateRouter);
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
            if (generate.protocol == generate.Protocol.OSPF) {
                boolean area0 = false, areax = false;
                for (var intf : r.intfs) {
                    var areaNum = intf.area;
                    if (areaNum == 0) {
                        area0 = true;
                    }
                    if (areaNum > 0) {
                        areax = true;
                    }
                }
                if (area0 && areax) continue;
            }
            //the router should in the at least 2 transit network
            if (transG.getNetworkOfRouter(r).size() < 2) {
                continue;
            }
            del_router = r;
            var area_num = r.intfs.getFirst().area;
            //remove del_router and link

            //First we should count all the cost of networks through del_router
            List<Integer> networks = transG.getNetworkOfRouter(r);
            Map<Integer, Integer> deltaCost = new HashMap<>();
            //Map<Integer, List<Router>> networkToRouters = new HashMap<>();
            Map<Integer, List<Intf>> networkToIntfs = new HashMap<>();
            for (var networkId : networks) {
                var intfs = transG.filterIntfOfRouter(del_router, transG.getIntfsOfNetwork(networkId));
                var micost = intfs.stream().map(i -> i.cost).min(Integer::compareTo).get();
                deltaCost.put(networkId, micost);
                //networkToRouters.put(networkId, transG.getLinkedRouters(intfs.getFirst()));
                networkToIntfs.put(networkId, transG.getLinkedIntfs(intfs.getFirst()));
            }

            //Next we link del_router's neighbors pair by pair
            //We should fill area, new cost, new networkId
            if (networks.size() == 2) {
                List<Intf> intfsA = null, intfsB = null;
                intfsA = networkToIntfs.get(networks.get(0));
                intfsB = networkToIntfs.get(networks.get(1));
                deltas.updateIntf.addAll(networkToIntfs.get(networks.get(0)));
                deltas.updateIntf.addAll(networkToIntfs.get(networks.get(1)));

                var new_network = transG.getNewNetworkId();
                for (var dst_intf : networkToIntfs.get(networks.get(0))) {
                    dst_intf.cost += deltaCost.get(networks.get(1));
                    transG.addIntfToNetworkId(dst_intf, new_network);
                }

                for (var dst_intf : networkToIntfs.get(networks.get(0))) {
                    dst_intf.cost += deltaCost.get(networks.get(1));
                    transG.addIntfToNetworkId(dst_intf, new_network);
                }
            } else {
                //we first link one old interfaces of a network to one new interfaces of a network
                for (int i = 0; i < networks.size(); i++) {
                    List<Intf> intfsA = null, intfsB = null;
                    for (int j = (i + 1) % networks.size(); (j < networks.size() && i < networks.size() - 1) || (j == 0 && i == networks.size() - 1); j++) {
                        if (j == (i + 1) % networks.size()) {
                            intfsA = networkToIntfs.get(networks.get(i));
                            deltas.updateIntf.addAll(intfsA);
                        }else{
                            intfsA = new ArrayList<>();
                            for(var copy_intf : networkToIntfs.get(networks.get(i))) {
                                var add_intf = new Intf();
                                add_intf.area = area_num;
                                transG.getRouterOfIntf(copy_intf).intfs.add(add_intf);
                                intfsA.add(add_intf);
                            }
                            deltas.newIntf.addAll(intfsA);
                        }

                        intfsB = new ArrayList<>();
                        for (var copy_intf : networkToIntfs.get(networks.get(j))) {
                            var add_intf = new Intf();
                            add_intf.area = area_num;
                            transG.getRouterOfIntf(copy_intf).intfs.add(add_intf);
                            intfsB.add(add_intf);
                        }
                        deltas.newIntf.addAll(intfsB);

                        var new_network = transG.getNewNetworkId();
                        for (var dst_intf : networkToIntfs.get(networks.get(i))) {
                            dst_intf.cost += deltaCost.get(networks.get(j));
                            transG.addIntfToNetworkId(dst_intf, new_network);
                        }

                        for (var dst_intf : networkToIntfs.get(networks.get(j))) {
                            dst_intf.cost += deltaCost.get(networks.get(i));
                            transG.addIntfToNetworkId(dst_intf, new_network);
                        }
                    }
                }
            }
            transG.removeRouter(del_router);
            break;
        }
        if (del_router == null) {
            return new Pair<>(false, deltas);
        } else return new Pair<>(true, deltas);
    }
}
