package org.generator.lib.topo.pass.base;
import org.generator.lib.topo.item.base.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.util.collections.UnionFind;
import org.generator.util.ran.ranHelper;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.max;


class ranConnect{
    private void init(){
        routerToId = new HashMap<>();
        idToRouter = new HashMap<>();
        int s = 0;
        for(var router: nodes){
            routerToId.put(router, s);
            idToRouter.put(s, router);
            s++;
        }
        subGraph = new UnionFind(nodes.size());
    }

    private List<Router> getRouterOfSubGraphId(int subId){
        return routerToId.entrySet().stream().filter(entry-> subGraph.find(entry.getValue()) == subId).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private Integer getSubGraphIdOfRouter(Router r){
        return subGraph.find(routerToId.get(r));
    }

    private Map<Integer, Set<Intf>> getIntfOfSubGraphs(){
        Map<Integer, Set<Intf>> res = new HashMap<>();
        for(var subId: subGraph.getComponents()){
            Set<Intf> s = new HashSet<>();
            res.put(subId, s);
            for(var r: getRouterOfSubGraphId(subId)){
                s.addAll(r.getUnconnectedIntfsOfArea(area));
            }
        }
        return res;
    }

    private void addOneNetwork(Map<Integer, Set<Intf>> subgraphIntfs){
        handleOrals(subgraphIntfs);
        handleConnect(subgraphIntfs);
    }

    private void handleConnect(Map<Integer, Set<Intf>> subgraphIntfs){
        var choseGraphs = ranHelper.randomElemsOfList(subgraphIntfs.keySet().stream().toList());
        for(int i = 1; i < choseGraphs.size(); i++){
            subGraph.union(choseGraphs.get(0), choseGraphs.get(i));
        }
        List<Intf> choseIntfs = new ArrayList<>();
        for(var subId: choseGraphs){
            choseIntfs.addAll(ranHelper.randomElemsOfList(subgraphIntfs.get(subId).stream().toList()));
        }
        //1 2 or 3...
        for(var intf: choseIntfs){
            intf.networkId = networkId;
        }
        networkId++;
    }

    private void handleOrals(Map<Integer, Set<Intf>> subgraphIntfs){
        for(var entry: subgraphIntfs.entrySet()) {
            var subId = entry.getKey();
            var s = entry.getValue();
            if (s.isEmpty()) {
                if (getRouterOfSubGraphId(subId).isEmpty()){
                    System.out.println("ok");
                }
                var routers = ranHelper.randomElemsOfList(getRouterOfSubGraphId(subId));
                for (var r : routers) {
                    var intf = new Intf();
                    r.intfs.add(intf);
                    intf.area = area;
                    s.add(intf);
                }
            }
        }
    }
    public void generate(List<Router> nodes, int area, int networkId){
        this.nodes = nodes;
        this.area = area;
        this.networkId = networkId;
        init();
        while(true){
            var subGraphIntfs = getIntfOfSubGraphs();
            if (subGraphIntfs.size() == 1 && subGraphIntfs.values().stream().findAny().get().isEmpty()) break;
            addOneNetwork(subGraphIntfs);
        }
    }

    public int networkId;
    int area;
    List<Router> nodes;
    UnionFind subGraph;
    Map<Router, Integer> routerToId;
    Map<Integer, Router> idToRouter;
}
public class ranGen implements genBase{
    public ranGen(){}
    public int networkId;

    List<Router> getRoutersOfArea(int area){
        return routers.stream().filter(router -> router.getAreas().contains(area)).collect(Collectors.toList());
    }
    List<Router> routers;
    public List<Router> generate(int totalRouter, int areaCount, int mxDegree, int abrRatio){
        routers = new ArrayList<>();
        networkId = 0;
        Set<Integer> actualArea = new HashSet<>();
        for(int i = 0; i < totalRouter; i++){
            var r = new Router(i);
            var intfNum = ranHelper.randomInt(1, mxDegree);
            var isABR = (ranHelper.randomInt(1, 10) <= abrRatio) && intfNum > 1;
            var primaryArea = ranHelper.randomInt(0, areaCount);
            var primaryNum = intfNum;
            if (isABR) primaryNum = ranHelper.randomInt(1, intfNum - 1);
            r.abr = isABR;
            routers.add(r);
            for(int j = 0; j < intfNum; j++){
                var intf = new Intf();
                if (j < primaryNum){
                    intf.area = primaryArea;
                }else{
                    intf.area = ranHelper.randomInt(1, areaCount);
                }
                actualArea.add(intf.area);
                r.intfs.add(intf);
            }
        }
        Map<Integer, Integer> renumber = new HashMap<>();
        int ns = 0;
        for(int i = 0; i <= areaCount; i++){
            if (actualArea.contains(i)) renumber.put(i, ns++);
        }
        for(var r: routers){
            for (var intf: r.intfs){
                intf.area = renumber.get(intf.area);
            }
        }
        areaCount = ns - 1;

        //connect areas
        for(int i = 1; i <= areaCount; i++){
            var c = new ranConnect();
            c.generate(getRoutersOfArea(i), i, networkId);
            networkId = c.networkId;
        }
        //connect to 0 area TODO shortcut
        var zero_routers = getRoutersOfArea(0);
        //FIXME we should better choose oral area to connect(area's node all don't have 0 interface)
        for(int i = 1; i <= areaCount; i++){
            var toChoseRouters = getRoutersOfArea(i);
            var choseRouters = ranHelper.randomElemsOfList(toChoseRouters, max(1, toChoseRouters.size() * abrRatio / 10));
            for(var r: choseRouters){
                var intf = new Intf();
                r.intfs.add(intf);
                intf.area = 0;
            }
            zero_routers.addAll(choseRouters);
        }
        zero_routers = zero_routers.stream().distinct().collect(Collectors.toList());
        var c = new ranConnect();
        c.generate(zero_routers, 0, networkId);
        networkId = c.networkId;
        return routers;
    }
}
