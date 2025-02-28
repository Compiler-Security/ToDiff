package generator;

import org.generator.lib.frontend.driver.IO;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.phy.Router;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.reducer.driver.reducer;
import org.generator.lib.topo.driver.topo;
import org.generator.lib.topo.pass.base.ripRanBaseGen;
import org.generator.tools.diffOp.genOps;
import org.generator.tools.diffTopo.diffTopo;
import org.generator.tools.frontend.ConfReader;
import org.generator.tools.frontend.OspfConfWriter;
import org.junit.Test;
import org.generator.lib.topo.pass.base.ospfRanBaseGen;

import static org.generator.lib.topo.driver.topo.dumpGraphOspf;
import static org.generator.lib.topo.driver.topo.dumpGraphRip;
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
            var ori = genOp.genRandom(100, 0.6, 0.4, 2, 1, 1, "r1");
            ori = reducer.reduceToCore(ori);
            //var ori = new ConfReader().read(st);

            System.out.println("=====ori========");
            System.out.println(ori);

            var confg = getSetConfG(ori);
            generate.irrOpRatio = 0;
            //equal ops without IRR Op inserted
            var gen_equal_wo_irrOp = generate.generateEqualOfCore(ori, true);
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
            var gen_equal = generate.generateEqualOfCore(ori, true);
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

    @Test
    public void fastConvergenceOpTest(){
        var confg = topo.genGraph(1, topo.areaCount, topo.mxDegree, topo.abrRatio, false, null);
        confg = confg.viewConfGraphOfRouter("r0");
        confg.setR_name("r0");
        var core = generate.generateCore(confg);
        while(true){
            var equal = generate.generateEqualOfCore(core, true);
            for(var op: equal){
                if (generate.skipCommands(op.getOperation().Type())){
                    System.out.println(IO.writeOp(op));
                }
            }
            break;
        }
    }

    @Test
    public void IPOSPFAREA_align_test(){
        var st = """
                int r0-eth0
                ip ospf area 1
                
                router ospf
                area 1.1.1.1 range 0.0.0.3/30
                """;
        var ori = new ConfReader().read(st);
        var equal = generate.generateEqualOfCore(ori, true);
        System.out.println(equal);
    }

    @Test
    public void baseGraphDumpTest(){
        var ran = new ospfRanBaseGen();
        var rs = ran.generate(5, 3, 2, 4);
        var str = dumpGraphOspf(rs, ran);
        System.out.println(str);
        var t = topo.genGraph(5, 3, 2, 2,false, null);
        System.out.println(t);
    }

    @Test
    public void baseGraphDumpTestRip(){
        var ran = new ripRanBaseGen();
        var rs = ran.generate(8, 3, 4, 4);
        var str = dumpGraphRip(rs, ran);
        System.out.println(str);
    }

    @Test
    public void multiProtocolTest() {
        String test_st = """
                router rip
                    passive-interface r2-eth0
                    passive-interface default
                    passive-interface r1-eth0
                """;
        generate.protocol = generate.Protocol.RIP;
        for(int i = 0; i < 1; i++) {
            System.out.printf("testCase %d\n", i);
            var genOp = new genOps();

            //var ori = genOp.genRandom(100, 0.2, 0.6, 4, 0, 1, "r1");
            //var ori_use = new ConfReader().read(new OspfConfWriter().write(ori));

            var ori_use = new ConfReader().read(test_st);
            //System.out.println(ori_use);
            System.out.println(reducer.reduceToCore(ori_use));
            //System.out.println(getSetConfG(ori_use));
        }
    }
    @Test
    public void mainTest(){
        for(int i = 0; i < 100; i++){
            System.out.printf("testCase %d\n", i);
            int routerCount = 3,maxStep = 3,maxStepTime = 10, roundNum = 2;
            var diff = new diffTopo();
            var res = diff.gen(routerCount, maxStep, maxStepTime, roundNum);
        }
    }
    @Test
    public void baseGenTest(){
        var ran = new ospfRanBaseGen();
        var routers = ran.generate(5, 1, 3, 0);
        var baseGraphStr = dumpGraphOspf(routers, ran);
        System.out.println(baseGraphStr);
    }
}
