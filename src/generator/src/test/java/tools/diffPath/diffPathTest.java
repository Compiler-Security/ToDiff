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

    }
}
