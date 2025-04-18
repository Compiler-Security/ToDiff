package org.generator.lib.topo.pass.base;

import org.generator.lib.topo.item.base.Intf_ISIS;
import org.generator.lib.topo.item.base.Router_ISIS;
import org.generator.util.collections.UnionFind;
import org.generator.util.ran.ranHelper;

import java.util.*;
import java.util.stream.Collectors;




public class openfabricRanBaseGen implements genBase_ISIS {
    public openfabricRanBaseGen() {}

    public int networkId;

    /**
     * @param area (0: T0, 1: T1, 2: T2)
     * @return routers in the same area
     */
    List<Router_ISIS> getRoutersOfArea(int area) {
        return routers.stream().filter(router -> router.area == area).collect(Collectors.toList());
    }

    List<Router_ISIS> routers;

    public List<Router_ISIS> generate_network(int t0Count, int t1Count, int t2Count, int maxremainingIntfs) {
        routers = new ArrayList<>();
        networkId = 0;
        
        // 路由器ID计数器
        int routerId = 0;
        
        // 第一阶段: 生成不同层次的路由器
        // T0层路由器(边缘层)
        for (int i = 0; i < t0Count; i++) {
            // T0层路由器使用Area 0标识
            Router_ISIS router = new Router_ISIS(routerId++, 2, 0);
            // 为连接到T1层预留足够的接口
            int intfCount = t1Count;
            for (int j = 0; j < intfCount; j++) {
                Intf_ISIS intf = new Intf_ISIS();
                intf.cost = ranHelper.randomInt(1, 65535);
                router.intfs.add(intf);
            }
            routers.add(router);
        }
        
        // T1层路由器(核心层)
        for (int i = 0; i < t1Count; i++) {
            // T1层路由器使用Area 1标识
            Router_ISIS router = new Router_ISIS(routerId++, 2, 1);
            // 需要足够的接口连接T0和T2
            int intfCount = t0Count + t2Count;
            for (int j = 0; j < intfCount; j++) {
                Intf_ISIS intf = new Intf_ISIS();
                intf.cost = ranHelper.randomInt(1, 65535);
                router.intfs.add(intf);
            }
            routers.add(router);
        }
        
        // T2层路由器(超核心层)
        for (int i = 0; i < t2Count; i++) {
            // T2层路由器使用Area 2标识
            Router_ISIS router = new Router_ISIS(routerId++, 2, 2);
            // 需要足够的接口连接T1层
            int intfCount = t1Count;
            for (int j = 0; j < intfCount; j++) {
                Intf_ISIS intf = new Intf_ISIS();
                intf.cost = ranHelper.randomInt(1, 65535);
                router.intfs.add(intf);
            }
            routers.add(router);
        }
        
        // 第二阶段: 连接T0和T1层路由器
        List<Router_ISIS> t0Routers = getRoutersOfArea(0);
        List<Router_ISIS> t1Routers = getRoutersOfArea(1);
        
        // 每个T0设备都应连接到每个T1设备(如果接口数量允许)
        for (Router_ISIS t0Router : t0Routers) {
            List<Intf_ISIS> t0Intfs = t0Router.getUnconnectedIntfs();
            int t0IntfIndex = 0;
            
            for (Router_ISIS t1Router : t1Routers) {
                // 如果T0没有足够接口，则停止连接
                if (t0IntfIndex >= t0Intfs.size()) {
                    break;
                }
                
                List<Intf_ISIS> t1Intfs = t1Router.getUnconnectedIntfs();
                // 确保T1也有未连接的接口
                if (t1Intfs.isEmpty()) {
                    continue;
                }
                
                // 连接T0和T1的接口
                Intf_ISIS t0Intf = t0Intfs.get(t0IntfIndex++);
                Intf_ISIS t1Intf = t1Intfs.get(0);
                
                // 设置相同的networkId以表示连接
                t0Intf.networkId = networkId;
                t1Intf.networkId = networkId;
                networkId++;
            }
        }
        
        // 第三阶段: 连接T1和T2层路由器
        List<Router_ISIS> t2Routers = getRoutersOfArea(2);
        
        // 每个T1设备都应连接到每个T2设备(如果接口数量允许)
        for (Router_ISIS t1Router : t1Routers) {
            List<Intf_ISIS> t1Intfs = t1Router.getUnconnectedIntfs();
            int t1IntfIndex = 0;
            
            for (Router_ISIS t2Router : t2Routers) {
                // 如果T1没有足够接口，则停止连接
                if (t1IntfIndex >= t1Intfs.size()) {
                    break;
                }
                
                List<Intf_ISIS> t2Intfs = t2Router.getUnconnectedIntfs();
                // 确保T2也有未连接的接口
                if (t2Intfs.isEmpty()) {
                    continue;
                }
                
                // 连接T1和T2的接口
                Intf_ISIS t1Intf = t1Intfs.get(t1IntfIndex++);
                Intf_ISIS t2Intf = t2Intfs.get(0);
                
                // 设置相同的networkId以表示连接
                t1Intf.networkId = networkId;
                t2Intf.networkId = networkId;
                networkId++;
            }
        }
        // third: add network for remaining interfaces
        for (Router_ISIS router : routers) {
            var remainingIntfnum = ranHelper.randomInt(0, maxremainingIntfs);
            for (int i = 0; i < remainingIntfnum; i++) {
                var intf = new Intf_ISIS();
                intf.cost = ranHelper.randomInt(1, 65535);
                intf.networkId = networkId;
                networkId++;
                router.intfs.add(intf);
            }
        }
        

        return routers;
    }
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
        // 根据比例分配路由器
        int ratio = areaCount > 0 ? areaCount: 4; // 默认比例为4:2:1
        int totalParts = ratio + (ratio/2) + 1; // 如果ratio是4，则总份数为4+2+1=7
        
        int t0Count = totalRouter * ratio / totalParts;
        int t1Count = totalRouter * (ratio/2) / totalParts;
        int t2Count = totalRouter / totalParts;
        
        // 确保每层至少有一个设备
        t0Count = Math.max(1, t0Count);
        t1Count = Math.max(1, t1Count);
        t2Count = Math.max(1, t2Count);
        
        // 调整总数以匹配分配
        int adjustedTotal = t0Count + t1Count + t2Count;
        if (adjustedTotal > totalRouter) {
            // 如果超出，优先减少T2和T1
            int excess = adjustedTotal - totalRouter;
            while (excess > 0 && t2Count > 1) {
                t2Count--;
                excess--;
            }
            while (excess > 0 && t1Count > 1) {
                t1Count--;
                excess--;
            }
            while (excess > 0 && t0Count > 1) {
                t0Count--;
                excess--;
            }
        } else if (adjustedTotal < totalRouter) {
            // 如果不足，优先增加T0，然后是T1，最后是T2
            int deficit = totalRouter - adjustedTotal;
            while (deficit > 0) {
                t0Count++;
                deficit--;
                if (deficit <= 0) break;
                
                t1Count++;
                deficit--;
                if (deficit <= 0) break;
                
                t2Count++;
                deficit--;
            }
        }
         // 再次检查总数是否符合要求
        adjustedTotal = t0Count + t1Count + t2Count;
        if (adjustedTotal != totalRouter) {
            System.out.println("警告: 调整后的设备总数(" + adjustedTotal + 
                            ")与要求的总数(" + totalRouter + ")不匹配。");
        }
        
        return generate_network(t0Count, t1Count, t2Count, mxDegree);
    }
    
}