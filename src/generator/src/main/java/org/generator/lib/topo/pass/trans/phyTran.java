package org.generator.lib.topo.pass.trans;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.topo.item.base.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.lib.topo.item.trans.transGraph;
import org.generator.lib.topo.pass.base.ripRanBaseGen;
import org.generator.util.collections.Pair;
import org.generator.util.ran.ranHelper;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class phyTran {

    public static enum transRule{
        addSubGraph,
        equalDealNode,
        switchToRouter,
        fakeEdge,
        ;
        static final List<transRule> l = new ArrayList<transRule>();
        static {
            l.addAll(Arrays.asList(transRule.values()));
        }
        public transRule getRule(int id){
            assert id < l.size():"not have transRule %d".formatted(id);
            return l.get(id);
        }
    }

    static Pair<Integer, List<Intf>> getShortestPath(transGraph subG,
                                                     Router rStart,
                                                     Router rEnd) {

        // --------- 初始化 ---------
        Map<Router, Integer> dist      = new HashMap<>();
        Map<Router, Intf>    prevIntf  = new HashMap<>();
        Map<Router, Router>  prevRtr   = new HashMap<>();

        PriorityQueue<Pair<Integer, Router>> pq =
                new PriorityQueue<>(Comparator.comparingInt(p -> (Integer) p.first()));

        dist.put(rStart, 0);
        pq.add(new Pair<>(0, rStart));

        // --------- Dijkstra 主循环 ---------
        while (!pq.isEmpty()) {
            Pair<Integer, Router> curPair = pq.poll();
            int    curDist = curPair.first();
            Router u       = curPair.second();

            if (curDist > dist.get(u)) continue;     // 过期条目
            if (u.equals(rEnd)) break;               // 已到达目标

            for (Intf intf : u.intfs) {
                int w = intf.cost > 0 ? intf.cost : 1;     // 默认 cost=1
                for (Router v : subG.getLinkedRouters(intf)) {

                    int newDist = curDist + w;
                    if (newDist < dist.getOrDefault(v, Integer.MAX_VALUE)) {
                        dist.put(v, newDist);
                        prevIntf.put(v, intf);
                        prevRtr.put(v, u);
                        pq.add(new Pair<>(newDist, v));
                    }
                }
            }
        }

        // --------- 构造结果 ---------
        if (!dist.containsKey(rEnd)) {
            return new Pair<>(Integer.MAX_VALUE, Collections.emptyList());
        }

        List<Intf> path = new ArrayList<>();
        for (Router v = rEnd; !v.equals(rStart); v = prevRtr.get(v)) {
            Intf outIntf = prevIntf.get(v);
            if (outIntf == null) assert false: "outInf should not be null %s".formatted(outIntf);    // 理论上不应发生
            path.add(outIntf);
        }
        Collections.reverse(path);

        return new Pair<>(dist.get(rEnd), path);
    }

    static void changeCostForPath(List<Intf> intfs, int deltaCost, Set<Intf> modifiedIntfs){
        var changedIntfs = new ArrayList<>(intfs.stream().filter(intf -> !modifiedIntfs.contains(intf)).toList());
        changedIntfs.sort(Comparator.comparing((Intf intf) -> intf.cost));
        if (deltaCost > 0){
            //miPath < target dist
            //FIXME we should use more random delta ways
            var d = deltaCost;
            changedIntfs.forEach(intf -> intf.cost += d / changedIntfs.size());
            changedIntfs.getFirst().cost += deltaCost % changedIntfs.size();
        }else{
            //miPath > target dist
            deltaCost = -deltaCost;
            //FIXME we should use random delta
            for(var intf: changedIntfs){
                int delta = min(deltaCost, intf.cost - 1);
                deltaCost -= delta;
                intf.cost -= delta;
                if (deltaCost == 0) break;
            }
            assert deltaCost == 0;
        }
        modifiedIntfs.addAll(changedIntfs);
    }

    static void changeSubGraphForRouter(transGraph subG, Router rStart, Router rEnd, int targetDist){
        Set<Intf> modifiedIntfs = new HashSet<>();
        var res = getShortestPath(subG, rStart, rEnd);
        var miDist = res.first();
        var path = res.second();
        if (miDist > targetDist){
            changeCostForPath(path, targetDist - miDist, modifiedIntfs);
        }else if (miDist < targetDist){
            while(miDist < targetDist) {
                //FIXME 7-15 we can only remain one path to targetDist, and other > targetDist
                changeCostForPath(path, targetDist - miDist, modifiedIntfs);
                res = getShortestPath(subG, rStart, rEnd);
                miDist = res.first();
                path = res.second();
            }
        }
        assert getShortestPath(subG, rStart, rEnd).first() == targetDist;
    }

    static Pair<Router, Router> changeSubGraph(transGraph subG, int dista, int distb){
        assert subG.getRouters().size() >= 2: "subG size should >=2";
        //FIXME 7-15 random pick
        var ra = subG.getRouters().getFirst();
        var rb = subG.getRouters().getLast();
        changeSubGraphForRouter(subG, ra, rb, dista);
        changeSubGraphForRouter(subG, rb, ra, distb);
        return new Pair<>(ra, rb);
    }

    static int getNetworkIdInGraph(int networkId, Map<Integer, Integer> newNetworkId, transGraph transG){
        if (!newNetworkId.containsKey(networkId)){newNetworkId.put(networkId, transG.getNewNetworkId());}
        return newNetworkId.get(networkId);
    }

    static void mergeOneRouter(Router r, Router subr, int area, transGraph transG, Map<Integer, Integer> newNetworkId){
        for(var intf: subr.intfs){
            var new_intf = transG.newIntf(r, intf.cost, area, getNetworkIdInGraph(intf.networkId, newNetworkId, transG));
            transG.addIntfToNetworkId(new_intf, new_intf.networkId);
        }
    }

    static void addOneRouter(Router subr, int area, transGraph transG, Map<Integer, Integer> newNetworkId){
        var r = transG.newRouter();
        mergeOneRouter(r, subr, area, transG, newNetworkId);
    }

    static void mergeSubGraphToGraph(Router ra, Router subRa, Router rb, Router subRb, transGraph transG, transGraph subTransG, int area){
        Map<Integer, Integer> newNetworkId = new HashMap<>();
        mergeOneRouter(ra, subRa, area, transG, newNetworkId);
        mergeOneRouter(rb, subRb, area, transG, newNetworkId);
        for(var router: subTransG.getRouters()){
            if (router.equals(subRa) || router.equals(subRb)) continue;
            addOneRouter(router, area, transG, newNetworkId);
        }
    }

    public static boolean addSubGraph(transGraph transG){
        //TODO 7-15 choose random routers
        Router ra = null, rb = null;
        Intf intfa = null, intfb = null;
        //FIXME 7-15 we should random choose two interface's of one routers
        for(var networkId: transG.getNetworkIds()){
            var intfs = transG.getIntfsOfNetwork(networkId);
            if (intfs.size() != 2) continue;
            intfa = intfs.getFirst();
            intfb = intfs.getLast();
            ra = transG.getRouterOfIntf(intfa);
            rb = transG.getRouterOfIntf(intfb);
            if (ra.equals(rb)) continue;
        }
        if (ra == null || rb == null) return false;
        var subGraphGen = new ripRanBaseGen();
        int totalRouter;
        if (generate.transRan){totalRouter = ranHelper.randomInt(3, 10);}
        else totalRouter = 5;
        //FIXME 7-15 mxDegree should be set to a good value
        //FIXME 7-15 cost should be set accrroding to intfa.cost and intfb.cost
        //FIXME ra, rb should not be directly connected by subnetwork generated by the subGraph
        var subG = subGraphGen.generate(totalRouter, 0, 3, 0);

        var transSubG = new transGraph(subG);

        //changeGraph
        var res = changeSubGraph(transSubG, intfa.cost, intfb.cost);

        //merge subTransGraph to the transGraph
        mergeSubGraphToGraph(ra, res.first(), rb, res.second(), transG, transSubG, intfa.area);

        return true;
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

    /**
     * We change one switch with at least three routers into one router connect this routers
     * r1----s1----r2
     *        |
     *        |
     *        r3
     *
     * r1(-delta)----(delta)rNew(delta)---(-delta)r2
     *                       |(delta)
     *                       |
     *                       r3
     * @param transG
     * @return
     */
    public static boolean switchToRouter(transGraph transG) {
        //TODO: 7-4 random switch ID
        for(int i = 0; i < transG.getNetworkId(); i++){
            var intfs = transG.getIntfsOfNetwork(i);
            Set<Router> rs = new HashSet<>();
            intfs.forEach(intf-> rs.add(transG.getRouterOfIntf(intf)));
            if (rs.size() < 3) continue;
            int miCost = intfs.stream().map(intf -> intf.cost).min(Integer::compareTo).get();
            if (miCost == 1) continue;
            //TODO:7-4 select a random cost between [1,miCost)
            int deltaCost = miCost -1;
            var newR = transG.newRouter();
            var area = intfs.getFirst().area;
            //For each router's all interfaces, we add to an same subnet and connect to the new_router
            for(var r: rs){
                var newNetwork = transG.getNewNetworkId();
                //connect router r's interface of network I to the new network
                r.getIntfsOfNetwork(i).forEach(intf ->
                    {intf.networkId = newNetwork; intf.cost -= deltaCost;}
                );
                //connect router newR to the new network
                transG.newIntf(newR, deltaCost, area, newNetwork);
            }
            transG.getDeltaNodes().addUpdateNetworkId(i);
            return true;
        }
        return false;
    }

    /**
     * Find two router and add one fake edge(cost 65535) in the one area
     * @param transG
     * @return
     */
    public static boolean fakeEdge(transGraph transG){
        if (transG.getRouters().size() < 2) return false;
        Router r1 = null, r2 = null;
        while(true){
            r1 = ranHelper.randomElemOfList(transG.getRouters());
            r2 = ranHelper.randomElemOfList(transG.getRouters());
            if (r1.getAreas() == null) continue;
            if (!r1.equals(r2)) break;
        }
        var area = ranHelper.randomElemOfList(r1.getAreas().stream().toList());
        //FIXME 7-4 cost should set to an more reasonable value
        int cost = 65535;
        int networkId = transG.getNewNetworkId();
        transG.newIntf(r1, cost, area, networkId);
        transG.newIntf(r2, cost, area, networkId);
        return true;
    }


    public static transGraph solve(List<Router> routers, List<transRule> rules){
        var transG = new transGraph(routers);
        for(var rule: rules){
            switch (rule){
                case equalDealNode -> {equalDelNode(transG);}
                case addSubGraph -> {addSubGraph(transG);}
            }
        }
        return transG;
    }
}
