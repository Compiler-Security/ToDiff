package tools.diffOp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.topo.graph.ConfGraph;
import org.generator.lib.item.topo.node.phy.Intf;
import org.generator.lib.item.topo.node.phy.Router;
import org.generator.lib.reducer.driver.reducer;
import org.generator.tools.diffOp.diffFrr;
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
        String test_st =
                """
                        interface r1-eth0
                          ip address 10.0.3.1/24
                          ip ospf cost 10
                          ip ospf area 0
                                                
                        interface r1-eth1
                          ip address 10.2.0.1/24
                          ip ospf cost 20
                          ip ospf area 1
                                                
                        interface r1-eth2
                          ip address 10.3.0.1/24
                          ip ospf cost 30
                          ip ospf priority 5
                          ip ospf area 6
                                                
                        router ospf
                          ospf router-id 172.16.0.1
                          area 1.1.1.1 stub
                        """;
        var frr = new readFrr();
        var g = frr.solve("/Users/shuibing/PycharmProjects/topo-fuzz/src/generator/src/test/java/tools/diffOp/frrDump.json", "r1");
        var ori_use = new ConfReader().read(test_st);
        var confg = getSetConfG(ori_use);
        System.out.println(g);
        System.out.println(confg);
        diffFrr.solve(g, confg, "r1");
    }
}
