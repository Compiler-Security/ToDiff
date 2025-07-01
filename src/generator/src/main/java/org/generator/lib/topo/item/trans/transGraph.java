package org.generator.lib.topo.item.trans;

import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.topo.item.base.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.lib.topo.item.base.baseItemHelper;

import java.util.*;
import java.util.stream.Collectors;

public class transGraph {
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

    deltaNodes delta;
    List<Router> routers;
    Map<Intf, Router> intfToRouter;
    Map<Integer, Set<Intf>> networkToIntf;
    int id, networkId = 0;
    public transGraph(List<Router> routers){
        this.routers = baseItemHelper.copyFrom(routers);
        intfToRouter = new HashMap<>();
        networkToIntf = new HashMap<>();
        delta = new deltaNodes();
        for(var router: routers){
            if (router.id > id) id = router.id;
            int i = 0;
            for(var intf: router.intfs){
                if (intf.id != -1) i = intf.id;
                else intf.id = i++;
                intf.routerId = router.id;
                if (intf.networkId  > networkId) networkId = intf.networkId;
                intfToRouter.put(intf, router);
                if (!networkToIntf.containsKey(intf.networkId)) networkToIntf.put(intf.networkId, new HashSet<>());
                networkToIntf.get(intf.networkId).add(intf);
            }
        }
    }

    public deltaNodes getDeltaNodes() {
        return delta;
    }

    public void cleanDeltaNodes(){
        delta = new deltaNodes();
    }

    public void removeRouter(Router router){
        routers.remove(router);
        for(var intf: router.intfs){
            intfToRouter.remove(intf);
            networkToIntf.get(intf.networkId).remove(intf);
        }
    }
    public Integer getNewNetworkId(){
        return ++networkId;
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

    public List<Router> getRouters(){
        return routers;
    }

    public Router newRouter(){
        var r = new Router(++id);
        routers.add(r);
        delta.addNewRouter(r);
        return r;
    }


    public Intf newIntf(Router router){
        var intf = new Intf(router.intfs.size(), router.id);
        intfToRouter.put(intf, router);
        router.intfs.add(intf);
        delta.addNewIntf(intf);
        return intf;
    }

    public void updateIntf(Intf intf){
        delta.addUpdateIntf(intf);
    }

    public void addIntfToNetworkId(Intf intf, Integer networkId){
        if (networkToIntf.containsKey(intf.networkId)) networkToIntf.get(intf.networkId).remove(intf);
        intf.networkId = networkId;
        if (!networkToIntf.containsKey(intf.networkId)) networkToIntf.put(intf.networkId, new HashSet<>());
        networkToIntf.get(intf.networkId).add(intf);
    }

    public void checkRouters(){
        for(var r: routers){
            for (var intf: r.intfs){
                assert intf.routerId != -1 && intf.id != -1;
            }
        }
    }

    public int getNetworkId(){
        return networkId;
    }

}
