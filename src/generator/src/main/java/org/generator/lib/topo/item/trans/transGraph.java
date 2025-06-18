package org.generator.lib.topo.item.trans;

import org.generator.lib.topo.item.base.Intf;
import org.generator.lib.topo.item.base.Router;

import java.util.*;
import java.util.stream.Collectors;

public class transGraph {
    List<Router> routers;
    Map<Intf, Router> intfToRouter;
    Map<Integer, Set<Intf>> networkToIntf;
    int id, networkId = 0;
    public transGraph(List<Router> routers){
        this.routers = routers;
        intfToRouter = new HashMap<>();
        networkToIntf = new HashMap<>();
        for(var router: routers){
            if (router.id > id) id = router.id;
            int i = 0;
            for(var intf: router.intfs){
                if (intf.id != -1) i = intf.id;
                else intf.id = i++;
                intf.routerId = router.id;
                if (intf.networkId  > networkId) networkId = intf.networkId;
                intfToRouter.put(intf, router);
                if (networkToIntf.containsKey(intf.networkId)){
                    networkToIntf.get(intf.networkId).add(intf);
                }else{
                    networkToIntf.put(intf.networkId, new HashSet<>());
                }
            }
        }
    }

    public void removeRouter(Router router){
        routers.remove(router);
    }
    public Integer getNewNetworkId(){
        return ++networkId;
    }

    public Router getNewRouter(){
        return new Router(++id);
    }

    public List<Intf> getLinkedIntfs(Intf intf){
        return networkToIntf.get(intf.networkId).stream().filter(i -> intfToRouter.get(i).equals(intfToRouter.get(intf))).toList();
    }

    public List<Router> getLinkedRouters(Intf intf){
        return getLinkedIntfs(intf).stream().map(i -> intfToRouter.get(i)).distinct().collect(Collectors.toList());
    }

    public List<Router> getLinkedRouters(Router router){
        return router.intfs.stream().flatMap(i -> getLinkedRouters(i).stream()).distinct().collect(Collectors.toList());
    }

    public List<Integer> getNetworkOfRouter(Router router){
        return router.intfs.stream().map(intf -> intf.networkId).distinct().collect(Collectors.toList());
    }

    public List<Intf> filterIntfOfRouter(Router router, List<Intf> intfs){
        return intfs.stream().filter(i -> router.equals(intfToRouter.get(i))).collect(Collectors.toList());
    }

    public List<Intf> getIntfsOfNetwork(Integer networkId){
        return networkToIntf.get(networkId).stream().toList();
    }

    public Router getRouterOfIntf(Intf intf){
        return intfToRouter.get(intf);
    }

    public Router newRouter(){
        var r = new Router(++id);
        routers.add(r);
        return r;
    }

    public Intf newIntf(Router router){
        var intf = new Intf(router.intfs.size(), router.id);
        intfToRouter.put(intf, router);
        router.intfs.add(intf);
        return intf;
    }

    public void addIntfToNetworkId(Intf intf, Integer networkId){
        if (networkToIntf.containsKey(intf.networkId)) networkToIntf.get(intf.networkId).remove(intf);
        intf.networkId = networkId;
        networkToIntf.get(intf.networkId).add(intf);
    }

    public List<Router> getRouters(){
        return routers;
    }

}
