package org.generator.lib.topo.item.trans;

import org.generator.lib.topo.item.base.Intf;
import org.generator.lib.topo.item.base.Router;

import java.util.*;

public class transGraph {
    List<Router> routers;
    Map<Intf, Router> intfToRouter;
    Map<Integer, Set<Intf>> networkToIntf;
    public void transGraph(List<Router> routers){
        this.routers = routers;
        intfToRouter = new HashMap<>();
        networkToIntf = new HashMap<>();
        routers.forEach(router -> {router.intfs.forEach(intf -> {intfToRouter.put(intf, router);});});
        for(var router: routers){

        }
        routers.forEach(router -> {router.intfs.forEach(intf -> {networkToIntf.put(intf.networkId, networkToIntf.getOrDefault(intf.networkId, new HashSet<>(List.of(intf))))});});
    }
}
