package generator;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.phy.Router;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.reducer.driver.reducer;
import org.generator.tools.diffOp.genOps;
import org.generator.tools.frontend.ConfReader;
import org.junit.Test;

import static org.generator.util.diff.differ.compareJson;

public class generateTest {

    /**
     * r1 with 4 interfaces
     * @return
     */
    ConfGraph getConfG(){
        var confg = new ConfGraph("r1");
        confg.addNode(new Router("r1"));
        confg.addNode(new Intf("r1-eth0"));
        confg.addIntfRelation("r1-eth0", "r1");
        confg.addNode(new Intf("r1-eth1"));
        confg.addIntfRelation("r1-eth1", "r1");
        confg.addNode(new Intf("r1-eth2"));
        confg.addIntfRelation("r1-eth2", "r1");
        confg.addNode(new Intf("r1-eth3"));
        confg.addIntfRelation("r1-eth3", "r1");
        return confg;
    }
    ConfGraph getSetConfG(OpCtxG conf){
        ConfGraph g = getConfG();
        reducer.reduceToConfG(conf, g);
        return g;
    }
    @Test
    public void IrrOpTest(){
        var st = """
                router ospf
                	area 50.176.152.190 range 194.53.178.207/10
                	timers throttle spf 46245 496092 416801
                	ospf router-id 199.5.230.48
                """;
        int i = 0;
        while(true){
            i++;
            System.out.printf("testCase %d\n", i);
            //original ops
            var genOp = new genOps();
            var ori = genOp.genRandom(300, 0.6, 0.4, 2, 1, 1, "r1");
            ori = reducer.reduceToCore(ori);
            //var ori = new ConfReader().read(st);

            System.out.println("=====ori========");
            System.out.println(ori);

            var confg = getSetConfG(ori);
            generate.irrOpRatio = 0;
            //equal ops without IRR Op inserted
            var gen_equal_wo_irrOp = generate.generateEqualOfCore(ori);
            gen_equal_wo_irrOp.getOps().addAll(0, ori.getOps());
            var confg_equal_wo_irrOp = getSetConfG(gen_equal_wo_irrOp);
            if (!confg_equal_wo_irrOp.equals(confg)){
                System.out.println("======compare=======");
                System.out.println(compareJson(confg.toJson(), confg_equal_wo_irrOp.toJson()));
                System.out.println("======gen_equal_wo_irrOp_core=======");
                System.out.println(reducer.reduceToCore(gen_equal_wo_irrOp));
                System.out.println("=======gen_equal_wo_irrOp========");
                System.out.println(gen_equal_wo_irrOp);
                assert  false: "Equal OP WRONG";
            }

            generate.irrOpRatio = 0.4;
            var gen_equal = generate.generateEqualOfCore(ori);
            var confg_equal = getSetConfG(gen_equal);
            if (!confg_equal.equals(confg)){
                System.out.println("======compare=======");
                System.out.println(compareJson(confg.toJson(), confg_equal.toJson()));
                System.out.println("=====gen_equal_core========");
                System.out.println(reducer.reduceToCore(gen_equal));
                System.out.println("=======gen_equal========");
                System.out.println(gen_equal);
                assert  false: "Irrelevant OP WRONG";
            }
            break;
        }
    }
}
