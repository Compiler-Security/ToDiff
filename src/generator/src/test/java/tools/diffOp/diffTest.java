package tools.diffOp;

import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.phy.Router;
import org.generator.lib.reducer.driver.reducer;
import org.generator.tools.diffOp.diffFrr;
import org.generator.tools.diffOp.genOps;
import org.generator.tools.diffOp.readFrr;
import org.generator.tools.frontend.ConfReader;
import org.generator.tools.frontend.OspfConfWriter;
import org.junit.Test;

import java.io.File;

public class diffTest {
    ConfGraph getConfG(){
        var confg = new ConfGraph("r1");
        confg.addNode(new Router("r1"));
        confg.addNode(new Intf("r1-eth0"));
        confg.addIntfRelation("r1-eth0", "r1");
        confg.addNode(new Intf("r1-eth1"));
        confg.addIntfRelation("r1-eth1", "r1");
        confg.addNode(new Intf("r1-eth2"));
        confg.addIntfRelation("r1-eth2", "r1");
        return confg;
    }

    ConfGraph getSetConfG(OpCtxG conf){
        ConfGraph g = getConfG();
        reducer.reduceToConfG(conf, g);
        return g;
    }
    @Test
    public void testSingleRouter(){
            var frr = new readFrr();
            var g = frr.solve("/Users/shuibing/PycharmProjects/topo-fuzz/src/generator/src/test/java/tools/diffOp/frrDump.json", "r1");
            var ori_use = new ConfReader().read(new File("/Users/shuibing/PycharmProjects/topo-fuzz/src/generator/src/test/java/tools/diffOp/r1.conf"));
            var confg = getSetConfG(ori_use);
            System.out.println(g);
            System.out.println(confg);
            diffFrr.solve(g, confg, "r1");
    }

    @Test
    public void readJso1(){
            var frr = new readFrr();
            var g = frr.solve("/Users/shuibing/PycharmProjects/topo-fuzz/src/generator/src/test/java/tools/diffOp/frrDump1.json", "r1");
            var ori_use = new ConfReader().read(new File("/Users/shuibing/PycharmProjects/topo-fuzz/src/generator/src/test/java/tools/diffOp/r2.conf"));

            System.out.println(reducer.reduceToCore(ori_use));

  var confg = getSetConfG(ori_use);
        System.out.println(g);
        System.out.println(confg);
        diffFrr.solve(g, confg, "r1");
    }

    @Test
    public void genSt(){
        var genOp = new genOps();
        var ori = genOp.genRandom(1000, 0.1, 0.6, 4, 0, 1, "r1");
        new OspfConfWriter().write(ori, new File("/Users/shuibing/PycharmProjects/topo-fuzz/src/generator/src/test/java/tools/diffOp/r2.conf"));
    }
}
