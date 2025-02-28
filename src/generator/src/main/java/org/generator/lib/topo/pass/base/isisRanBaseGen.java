package org.generator.lib.topo.pass.base;

import org.generator.lib.topo.item.base.Intf_ISIS;
import org.generator.lib.topo.item.base.Router_ISIS;
import org.generator.util.collections.UnionFind;
import org.generator.util.ran.ranHelper;

import java.util.*;
import java.util.stream.Collectors;


class ranConnect_ISIS {
    private void init() {
        routerToId = new HashMap<>();
        idToRouter = new HashMap<>();
        int s = 0;
        for (var router : nodes) {
            routerToId.put(router, s);
            idToRouter.put(s, router);
            s++;
        }
        subGraph = new UnionFind(nodes.size());
    }

    private List<Router_ISIS> getRouterOfSubGraphId(int subId) {
        return routerToId.entrySet().stream().filter(entry -> subGraph.find(entry.getValue()) == subId).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private Integer getSubGraphIdOfRouter(Router_ISIS r) {
        return subGraph.find(routerToId.get(r));
    }

    private Map<Integer, Set<Intf_ISIS>> getIntfOfSubGraphs() {
        Map<Integer, Set<Intf_ISIS>> res = new HashMap<>();
        for (var subId : subGraph.getComponents()) {
            Set<Intf_ISIS> s = new HashSet<>();
            res.put(subId, s);
            for (var r : getRouterOfSubGraphId(subId)) {
                s.addAll(r.getUnconnectedIntfs());
            }
        }
        return res;
    }

    private void addOneNetwork(Map<Integer, Set<Intf_ISIS>> subgraphIntfs) {
        handleOrals(subgraphIntfs);
        handleConnect(subgraphIntfs);
    }

    private void handleConnect(Map<Integer, Set<Intf_ISIS>> subgraphIntfs){
        var choseGraphs = ranHelper.randomElemsOfList(subgraphIntfs.keySet().stream().toList());
        for(int i = 1; i < choseGraphs.size(); i++){
            subGraph.union(choseGraphs.get(0), choseGraphs.get(i));
        }
        List<Intf_ISIS> choseIntfs = new ArrayList<>();
        for(var subId: choseGraphs){
            choseIntfs.addAll(ranHelper.randomElemsOfList(subgraphIntfs.get(subId).stream().toList(), 1));
        }
        //1 2 or 3...
        for(var intf: choseIntfs){
            intf.networkId = networkId;
        }
        networkId++;
    }
    private void handleOrals(Map<Integer, Set<Intf_ISIS>> subgraphIntfs) {
        for (var entry : subgraphIntfs.entrySet()) {
            var subId = entry.getKey();
            var s = entry.getValue();
            if (s.isEmpty()) {
                if (getRouterOfSubGraphId(subId).isEmpty()) {
                    System.out.println("ok");
                }
                var routers = ranHelper.randomElemsOfList(getRouterOfSubGraphId(subId));
                for (var r : routers) {
                    var intf = new Intf_ISIS();
                    r.intfs.add(intf);
                    intf.cost = ranHelper.randomInt(1, 65535);
                    s.add(intf);
                }
            }
        }
    }

    public void generate(List<Router_ISIS> nodes, int area, int networkId) {
        this.nodes = nodes;
        this.area = area;
        this.networkId = networkId;
        init();
        while (true) {
            var subGraphIntfs = getIntfOfSubGraphs();
            if (subGraphIntfs.size() == 1 && subGraphIntfs.values().stream().findAny().get().isEmpty()) break;
            addOneNetwork(subGraphIntfs);
        }
    }

    public int networkId;
    int area;
    List<Router_ISIS> nodes;
    UnionFind subGraph;
    Map<Router_ISIS, Integer> routerToId;
    Map<Integer, Router_ISIS> idToRouter;
}

public class isisRanBaseGen implements genBase_ISIS {
    public isisRanBaseGen() {}

    public int networkId;

    List<Router_ISIS> getRoutersOfArea(int area) {
        return routers.stream().filter(router -> router.area == area).collect(Collectors.toList());
    }

    List<Router_ISIS> routers;

    /**
     * we generate cost, area, network connection of routers/interfaces here
     *
     * @param totalRouter
     * @param areaCount
     * @param mxDegree
     * @param abrRatio
     * @return
     */
    public List<Router_ISIS> generate(int totalRouter, int areaCount, int mxDegree, int abrRatio) {
        routers = new ArrayList<>();
        networkId = 0;
        Set<Integer> actualArea = new HashSet<>();
        var backboneRouters = new ArrayList<Router_ISIS>();
        // first stage: generate routers
        for (int i = 0; i < totalRouter; i++) {
            int level = ranHelper.randomInt(0, 1); // 0: L1, 1: L2, 2: L1-2
            int area;
            int tmp_area = ranHelper.randomInt(1, areaCount);
            if (level == 0) { // L1 router is in non-backbone area
                area = tmp_area;
            } else { // L2 router is in backbone area
                area = 0;
            }
            var intfNum = ranHelper.randomInt(1, mxDegree);
            var isL12 = (ranHelper.randomInt(1, 10) <= abrRatio) && intfNum > 1;
            var L12_num = getRoutersOfArea(tmp_area).stream().filter(r -> r.level == 2).count();
            if (isL12 && L12_num == 0) {
                level = 2;
                area = tmp_area;
            }
            var r = new Router_ISIS(i, level, area);
            routers.add(r);
            actualArea.add(area);
            // generate interfaces
            for (int j = 0; j < intfNum; j++) {
                var intf = new Intf_ISIS();
                intf.cost = ranHelper.randomInt(1, 65535);
                r.intfs.add(intf);
            }
        }

        Map<Integer, Integer> renumber = new HashMap<>();
        int ns = 0;
        for(int i = 0; i <= areaCount; i++){
            if (actualArea.contains(i)) renumber.put(i, ns++);
        }
        for(var r: routers){
            r.area = renumber.get(r.area);
        }
        areaCount = ns - 1;
        //make sure all routers in the area 0 are L2 routers
        var l2Routers = getRoutersOfArea(0);
        for (var r : l2Routers) {
            if (r.level == 0 || r.level == 2) {
                r.level = 1;
            }
        }

        //add all L2 routers in the backbone area
        for(var r : l2Routers){
            backboneRouters.add(r);
        }
        

        // second stage: connect routers in the same non-backbone area
        for (int i = 1; i <= areaCount; i++) {
            var areaRouters = getRoutersOfArea(i);
            var c = new ranConnect_ISIS();
            c.generate(areaRouters, i, networkId); 
            networkId = c.networkId;
        }

        // third stage: connect L1-2 with L1 and add L1-2 in the backbone area
        l2Routers = backboneRouters.stream()
            .filter(r -> r.level == 1)
            .collect(Collectors.toList());
        for(int i = 1; i <= areaCount; i++) {
            var areaRouters = getRoutersOfArea(i);
            if(areaRouters.isEmpty()) continue;
            var l12Routers = areaRouters.stream()
                .filter(r -> r.level == 2)
                .collect(Collectors.toList());
            if(l12Routers.isEmpty()){
                // if there is no L1-2 router, choose a L1 router to become a L1-2 router
                var selectedRouter = ranHelper.randomElemsOfList(areaRouters, 1).get(0);
                selectedRouter.level = 2;
                l12Routers.add(selectedRouter);
            }
            if (l12Routers.size()>1) {
                throw new RuntimeException("L1-2 routers are more than 1");
                
            }
            else{
                // if all the intfs of L1-2 routers are not connected, choose a L1 router to connect with L1-2 routers
                var connectedL12Intfs = l12Routers.stream().flatMap(r -> r.getConnectedIntfs().stream()).collect(Collectors.toList());
                if(connectedL12Intfs.isEmpty()){
                    while(true){
                        var selectedRouter = ranHelper.randomElemsOfList(areaRouters, 1).get(0);
                        if(l12Routers.contains(selectedRouter)){
                            continue;
                        }
                        // add intfs
                        var intf1 = new Intf_ISIS();
                        var intf2 = new Intf_ISIS();
                        intf1.cost = ranHelper.randomInt(1, 65535);
                        intf2.cost = ranHelper.randomInt(1, 65535);
                        selectedRouter.intfs.add(intf1);
                        l12Routers.get(0).intfs.add(intf2);

                        // set networkId
                        intf1.networkId = networkId;
                        intf2.networkId = networkId;
                        networkId++;
                    }
                }
            }
            for (var r : l12Routers) {
                backboneRouters.add(r);
            }
        }  



        // fourth stage: connect L1-2 routers
        // var alll12Routers = backboneRouters.stream()
        //     .filter(r -> r.level == 2)
        //     .collect(Collectors.toList());

        // if(alll12Routers.isEmpty() == false) {
        //     var c = new ranConnect_ISIS();
        //     c.generate(alll12Routers, 0, networkId);  
        //     networkId = c.networkId;
        // }


        // fifth stage: connect routers in the backbone area 
        var allbackbonerouters = backboneRouters.stream()
            .filter(r -> r.level == 1||r.level == 2)
            .collect(Collectors.toList());

        var c = new ranConnect_ISIS();
        c.generate(allbackbonerouters, 0, networkId); 
        networkId = c.networkId;
        
        return routers;
    }
}