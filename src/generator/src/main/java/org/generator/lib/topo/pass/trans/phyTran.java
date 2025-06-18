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

    public static class deltaNodes {
        Set<String> newIntfName, updateIntfName;
        Set<String> newRouterName, updateRouterName;

        public deltaNodes() {
            newIntfName = new HashSet<>();
            updateIntfName = new HashSet<>();
            newRouterName = new HashSet<>();
            updateRouterName = new HashSet<>();
        }

        public void addNewIntf(Intf intf){
            newIntfName.add(NodeGen.getIntfName(NodeGen.getRouterName(intf.routerId), intf.id));
        }

        public void addUpdateIntf(Intf intf){
            updateIntfName.add(NodeGen.getIntfName(NodeGen.getRouterName(intf.routerId), intf.id));
        }

        public void addNewRouter(Router router){
            newRouterName.add(NodeGen.getRouterName(router.id));
        }

        public void addUpdateRouter(Router router){
            newRouterName.add(NodeGen.getRouterName(router.id));
        }

        public boolean isNewIntf(String intf_name) {
            return newIntfName.contains(intf_name);
        }

        public boolean isUpdateIntf(String intf_name) {
            return updateIntfName.contains(intf_name);
        }

        public boolean isNewRouter(String router_name) {
            return newRouterName.contains(router_name);
        }

        public boolean isUpdateRouter(String router_name) {
            return updateRouterName.contains(router_name);
        }

        public void mergeDeltaNodes(deltaNodes _deltaNodes) {
            newIntfName.addAll(_deltaNodes.newIntfName);
            updateIntfName.addAll(_deltaNodes.updateIntfName);
            newRouterName.addAll(_deltaNodes.newRouterName);
            updateRouterName.addAll(_deltaNodes.updateRouterName);
        }
    }

    void checkRouters(List<Router> routers){
        for(var r: routers){
            for (var intf: r.intfs){
                assert intf.routerId != -1 && intf.id != -1;
            }
        }
    }

    //identify trans, but give id and router_id to all the intfs
    public void typ0Trans(List<Router> routers){
        var transG = new transGraph(routers);
        checkRouters(routers);
    }

    //delete one router
    public Pair<Boolean, deltaNodes> equalDelNode(List<Router> routers) {
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
                intfsA.forEach(deltas::addUpdateIntf);
                intfsB.forEach(deltas::addUpdateIntf);

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
                            intfsA.forEach(deltas::addUpdateIntf);
                        }else{
                            intfsA = new ArrayList<>();
                            for(var copy_intf : networkToIntfs.get(networks.get(i))) {
                                var add_intf = transG.newIntf(transG.getRouterOfIntf(copy_intf));
                                add_intf.area = area_num;
                                intfsA.add(add_intf);
                            }
                            intfsA.forEach(deltas::addNewIntf);
                        }

                        intfsB = new ArrayList<>();
                        for (var copy_intf : networkToIntfs.get(networks.get(j))) {
                            var add_intf = transG.newIntf(transG.getRouterOfIntf(copy_intf));
                            add_intf.area = area_num;
                            intfsB.add(add_intf);
                        }
                        intfsB.forEach(deltas::addNewIntf);

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
        checkRouters(routers);
        if (del_router == null) {
            return new Pair<>(false, deltas);
        } else return new Pair<>(true, deltas);
    }

    public deltaNodes solve(List<Router> routers, List<transRule> rules){
        var res = new deltaNodes();
        for(var rule: rules){
            switch (rule){
                case equalDealNode -> {res.mergeDeltaNodes(equalDelNode(routers).second());}
            }
        }
        return res;
    }
}
