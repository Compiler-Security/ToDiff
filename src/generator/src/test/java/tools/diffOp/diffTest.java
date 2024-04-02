package tools.diffOp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.topo.graph.ConfGraph;
import org.generator.lib.item.topo.node.phy.Intf;
import org.generator.lib.item.topo.node.phy.Router;
import org.generator.lib.reducer.driver.reducer;
import org.generator.tools.diffOp.diffFrr;
import org.generator.tools.diffOp.genOps;
import org.generator.tools.diffOp.readFrr;
import org.generator.tools.frontend.ConfReader;
import org.generator.tools.frontend.OspfConfWriter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

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
    public void readJson(){
        while (true) {
            var frr = new readFrr();
            var g = frr.solve("/Users/shuibing/PycharmProjects/topo-fuzz/src/generator/src/test/java/tools/diffOp/frrDump1.json", "r1");
            var ori_use = new ConfReader().read(new File("/Users/shuibing/PycharmProjects/topo-fuzz/src/generator/src/test/java/tools/diffOp/r1_debug.conf"));
            if (reducer.reduceToCore(ori_use).getOps().size() == 3){
                break;
            }
            //System.out.println(reducer.reduceToCore(ori_use));
        }
//        var confg = getSetConfG(ori_use);
//        System.out.println(g);
//        System.out.println(confg);
        //diffFrr.solve(g, confg, "r1");
    }

    @Test
    public void readJso1(){
            var frr = new readFrr();
            var g = frr.solve("/Users/shuibing/PycharmProjects/topo-fuzz/src/generator/src/test/java/tools/diffOp/frrDump1.json", "r1");
            var ori_use = new ConfReader().read(new File("/Users/shuibing/PycharmProjects/topo-fuzz/src/generator/src/test/java/tools/diffOp/r1.conf"));

            System.out.println(reducer.reduceToCore(ori_use));

  var confg = getSetConfG(ori_use);
        System.out.println(g);
        System.out.println(confg);
        diffFrr.solve(g, confg, "r1");
    }

    @Test
    public void genSt(){
        var genOp = new genOps();
        var ori = genOp.genRandom(1000, 0.2, 0.6, 4, 0, 1, "r1");
        System.out.println(ori);
    }
}
