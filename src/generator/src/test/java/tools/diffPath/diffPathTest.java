package tools.diffPath;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.topo.driver.topo;
import org.generator.lib.topo.pass.trans.phyTran;
import org.generator.util.net.IP;
import org.generator.util.net.IPBase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class diffPathTest {

    @Test public void test_() {
        var tmp = topo.genInitTransGraph(3, 2, 2, 1, false, null);
        var old_routers = tmp.first();
        var old_confg = tmp.second();
        var tmp1 = topo.transformGraph(old_routers, old_confg, new ArrayList<>(List.of(phyTran.transRule.equalDealNode)), null);
        var new_confg = tmp1.second();
        var old_phyOps = generate.generatePhyCore(old_confg);
        var new_phyOps = generate.generatePhyCore(new_confg);
        System.out.println(generate.generateDiffPhyOp(old_phyOps, new_phyOps));
        var new_routers = tmp1.first().getRouters();
        for(var old_r:old_confg.getRouters()) {
            if (new_confg.containsNode(old_r.getName())){
                old_confg.setR_name(old_r.getName());
                new_confg.setR_name(old_r.getName());
                var old_ospfOps = generate.generateCore(old_confg, false);
                var new_ospfOps = generate.generateCore(new_confg, false);
                System.out.println(generate.generateDiffProtoOp(old_ospfOps, new_ospfOps));
            }
        }
    }
}
