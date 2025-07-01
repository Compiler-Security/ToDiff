package tools.diffPath;

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
        var routers = tmp.first();
        var confg = tmp.second();
        var tmp1 = topo.transformGraph(routers, confg, new ArrayList<>(List.of(phyTran.transRule.equalDealNode)), null);
        System.out.println(tmp1.second().toDot(false));
    }
}
