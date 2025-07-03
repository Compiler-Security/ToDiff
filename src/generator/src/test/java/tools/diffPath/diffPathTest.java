package tools.diffPath;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.topo.driver.topo;
import org.generator.lib.topo.item.base.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.lib.topo.pass.attri.ospfRanAttriGen;
import org.generator.lib.topo.pass.build.topoBuild;
import org.generator.lib.topo.pass.trans.phyTran;
import org.generator.util.collections.Pair;
import org.generator.util.net.IP;
import org.generator.util.net.IPBase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class diffPathTest {

    Intf getIntf(int routerId, int id, int area, int cost, int networkId){
        var intf = new Intf(id, routerId);
        intf.networkId = networkId;
        intf.cost = cost;
        intf.area = area;
        return intf;
    }
    Pair<List<Router>, ConfGraph> getDelNodeTestRouters(){
        List<Router> routers = new ArrayList<>();
        for(int i = 0; i < 5; i++) routers.add(new Router(i));
        for(int i = 0; i < 3; i++) {
            routers.get(0).intfs.add(getIntf(0, i, 0, i + 1, i));
        }
        routers.get(1).intfs.add(getIntf(1, 0, 0, 4, 0));
        routers.get(2).intfs.add(getIntf(2, 0, 0, 5, 0));
        routers.get(3).intfs.add(getIntf(3, 0, 0, 6, 1));
        routers.get(4).intfs.add(getIntf(4, 0, 0, 7, 2));
        var b = new topoBuild();
        var confg = b.solve(routers);
        var c = new ospfRanAttriGen();
        c.generate(confg, routers);
        return new Pair<>(routers, confg);
    }
    @Test public void test_() {
        //var tmp = topo.genInitTransGraph(3, 2, 2, 1, false, null);
        var tmp = getDelNodeTestRouters();
        var old_routers = tmp.first();
        var old_confg = tmp.second();
        var tmp1 = topo.transformGraph(old_routers, old_confg, new ArrayList<>(List.of(phyTran.transRule.equalDealNode)), null);
        var new_confg = tmp1.second();
        var old_phyOps = generate.generatePhyCore(old_confg);
        var new_phyOps = generate.generatePhyCore(new_confg);
        System.out.println(generate.generateDiffPhyOp(old_phyOps, new_phyOps));
        var new_routers = tmp1.first().getRouters();
        for(var old_r:old_confg.getRouters()) {
            System.out.println(old_r.getName());
            if (new_confg.containsNode(old_r.getName())){
                var old_rConfg = old_confg.viewConfGraphOfRouter(old_r.getName());
                old_rConfg.setR_name(old_r.getName());
                var new_rConfg = new_confg.viewConfGraphOfRouter(old_r.getName());
                new_rConfg.setR_name(old_r.getName());
                var old_ospfOps = generate.generateCore(old_rConfg, false);
                var new_ospfOps = generate.generateCore(new_rConfg, false);
                System.out.println(old_ospfOps);
                System.out.println(new_ospfOps);
                System.out.println(generate.generateDiffProtoOp(old_ospfOps, new_ospfOps));
                break;
            }
        }
    }
}
