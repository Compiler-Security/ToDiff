package org.generator.lib.topo.pass.base;

import org.generator.lib.topo.item.base.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.util.collections.UnionFind;
import org.generator.util.ran.ranHelper;

import java.util.*;

public class ripRanBaseGen implements genBase{
    @Override
    //we don't care about areaCount and abrRatio
    public List<Router> generate(int totalRouter, int areaCount, int mxDegree, int abrRatio) {
        // FIXME
        // current we just use OSPF base generate, set abrRatio=0, areaCount=1
        List<Router> routers = new ArrayList<Router>();
        var connected = new UnionFind(totalRouter);
        var totalNetwork = ranHelper.randomInt(Integer.max(1, totalRouter / 3), mxDegree * totalRouter - 1);
        Map<Integer, Set<Integer>> networkToRouters = new HashMap<>();
        for(int i = 0; i < totalNetwork; i++){
            networkToRouters.put(i, new HashSet<Integer>());
        }
        for(int i = 0; i < totalRouter; i++){
            var r = new Router(i);
            routers.add(r);
            for(int j = 0; j < ranHelper.randomInt(1, mxDegree); j++) {
                var intf = new Intf();
                intf.cost = ranHelper.randomInt(1, 10);
                intf.networkId = ranHelper.randomInt(0, totalNetwork - 1);
                r.intfs.add(intf);
                networkToRouters.get(intf.networkId).add(i);
            }
        }
        for(int i = 0; i < totalNetwork; i++){
            for(var r_1: networkToRouters.get(i)){
                for(var r_2: networkToRouters.get(i)){
                    connected.union(r_1, r_2);
                }
            }
        }
        for(int i = 0; i < totalRouter; i++){
            for(int j = i + 1; j < totalRouter; j++){
                if (!connected.connected(i, j)){
                    var intf1 = new Intf();
                    var intf2 = new Intf();
                    intf1.cost = ranHelper.randomInt(1, 10);
                    intf2.cost = ranHelper.randomInt(1, 10);
                    intf1.networkId = ranHelper.randomInt(0, totalNetwork - 1);
                    intf2.networkId = intf1.networkId;
                    routers.get(i).intfs.add(intf1);
                    routers.get(j).intfs.add(intf2);
                    connected.union(i, j);
                }
            }
        }

        int realNetwork = 0;
        Map<Integer, Integer> shrink = new HashMap<>();
        for(int i = 0; i < totalRouter; i++){
            for(var intf: routers.get(i).intfs){
                if (shrink.get(intf.networkId) == null){
                    shrink.put(intf.networkId, realNetwork++);
                }
                intf.networkId = shrink.get(intf.networkId);
            }
        }
        networkId = realNetwork;
        return routers;
    }

    public int networkId;
}
