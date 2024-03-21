package tools.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.generator.lib.frontend.driver.IO;
import org.generator.lib.generator.controller.CapacityController;
import org.generator.lib.generator.controller.NormalController;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.generator.pass.genEqualPass;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.topo.graph.ConfGraph;
import org.generator.lib.item.topo.node.ospf.OSPF;
import org.generator.lib.item.topo.node.phy.Intf;
import org.generator.lib.item.topo.node.phy.Router;
import org.generator.lib.reducer.driver.reducer;
import org.generator.lib.reducer.pass.ospfArgPass;
import org.generator.lib.reducer.pass.phyArgPass;
import org.generator.lib.reducer.pass.reducePass;
import org.generator.tools.frontend.ConfReader;
import org.generator.tools.frontend.OspfConfWriter;
import org.generator.tools.testOps.genOps;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class IOTest {

    @Test
    public void genRandom(){
        var genOp = new genOps();
        var res = genOp.genRandom(100, 0.5, 0.4, 2, 0, 0.3, "r1");
        System.out.println(res);
    }
    @Test
    public void jsonTest(){
        var node = new OSPF("r1");
        var node1 = new OSPF("r2");
        node1.setInitDelay(10);
        System.out.println(node1.getJsonNode());
        System.out.println( node.getJsonNode().equals(node1.getJsonNode()));
    }

    ConfGraph getConfG(){
        var confg = new ConfGraph("r1");
        confg.addNode(new Router("r1"));
        confg.addNode(new Intf("r1-eth0"));
        confg.addIntfRelation("r1-eth0", "r1");
        confg.addNode(new Intf("r1-eth1"));
        confg.addIntfRelation("r1-eth1", "r1");
        return confg;
    }

    ConfGraph getSetConfG(OpCtxG conf){
        ConfGraph g = getConfG();
        reducer.reduceToConfG(conf, g);
        return g;
    }
    @Test
    public void Part1Test(){
//        String test_st = """
//                                router ospf
//                                int r1-eth0
//                                ip ospf area 0
//                                ip address 10.0.0.0/10
//                                router ospf
//                                area 1061954456 range 91.122.46.62/11 not-advertise
//                                area 3389220260 range 92.238.183.225/7
//                """;
//        String test_st = """
//                  router ospf
//                                area 1061954456 range 91.122.46.62/11 not-advertise
//                                area 1061954456 range 91.122.46.62/11 not-advertise
//                                write-multiplier 10
//                                int r1-eth0
//                                ip ospf area 0
//                                ip address 10.0.0.0/10
//                                ip ospf cost 20
//                                int r1-eth1
//                                ip ospf area 1
//                                ip address 11.1.1.1/10
//                """;
//        String test_st = """
//                                int r1-eth0
//                                ip ospf area 0
//                                ip address 10.0.0.0/10
//                                ip ospf cost 20
//                                router ospf
//                                area 3389220260 range 92.238.183.225/7
//                """;
//        String test_st = """
//                router ospf
//                area 202.3.101.164 range 92.238.183.225/7 cost 0
//                no router ospf
//                """;
//        String test_st = """
//                router ospf
//                	socket buffer send 1287520359
//                	area 58.167.154.44 nssa
//                	area 159.65.5.110 stub
//                """;
        var genOp = new genOps();
        var ori = genOp.genRandom(10, 0.5, 0.4, 2, 0, 1, "r1");
        //var ori = new ConfReader().read(test_st);
        var confg = getSetConfG(ori);


        var gen = generate.generateCore(confg);
        System.out.println(ori);
        System.out.println("========");
        System.out.println(gen);
        var confg_core = getSetConfG(gen);
        System.out.println(confg);
        System.out.println(confg_core);
        assert confg_core.equals(confg) : "GEN WRONG";

        var gen_equal = generate.generateEqualOfCore(gen, 1);
        System.out.println(gen_equal);
        var confg_equal = getSetConfG(gen_equal);
        assert confg_equal.equals(confg);
    }
    @Test
    public void Part2Test(){
        String test_st = """
                                router ospf
                                int r1-eth0
                                router ospf     
                                area 1061954456 range 91.122.46.62/11 not-advertise  
                                area 3389220260 range 92.238.183.225/7      
                """;
        var reader = new ConfReader();
        var opCtxG = reader.read(test_st);
        var writer = new OspfConfWriter();
        //System.out.println(writer.write(opCtxG));
        var reducer = new reducePass();
        var opas = reducer.solve(opCtxG).activeSetView().getOps();
        var normal_controller = NormalController.of();
        for(var opa: opas){
            normal_controller.addConfig(opa, 1, 2, 1, 1);
        }
        var tmp_controller = CapacityController.of(6, 0, 0, 1, 0);
        var gen_opag = genEqualPass.solve(normal_controller, tmp_controller);
        gen_opag = reducePass.expandOpAG(gen_opag);
        var print_ctx = OpCtxG.Of();
        gen_opag.getOps().forEach(opa -> print_ctx.addOp(opa.getOp().getOpCtx()));
        System.out.println(writer.write(print_ctx));
    }

    @Test
    public void phyTest(){
        String phy_st = """
                 node r1 add
                 node r2 add
                 node r3 add
                 node s1 add
                 node s2 add
                 link r1-eth0 s1-eth0 up
                 link r2-eth0 s1-eth1 up
                 link r3-eth0 s1-eth2 up
                 link r1-eth1 s2-eth0 up
                 link r2-eth1 s2-eth1 up
                """;
        var reader = new ConfReader();
        var opCtxG = reader.read(phy_st);
        var confGraph = new ConfGraph();
        phyArgPass.solve(opCtxG, confGraph);
        System.out.println(confGraph.toDot(false));
    }
    @Test
    public void IoTest(){

        String phy_st = """
                     node r1 add
                     node s1 add
                     link r1-eth0 s1-eth0 up
                     link r1-eth1 s1-eth1 up
                """;

        String ospf_st = """
                                router ospf
                                area 1061954456 range 91.122.46.62/11 not-advertise
                                area 1061954456 range 91.122.46.62/11 not-advertise 
                                write-multiplier 10
                                int r1-eth0
                                ip ospf area 0
                                ip address 10.0.0.0/10
                                ip ospf cost 20
                                int r1-eth1 
                                ip ospf area 1
                                ip address 11.1.1.1/10
                """;
        var reader = new ConfReader();
        var opCtxG = reader.read(phy_st);
        var confGraph = new ConfGraph();
        phyArgPass.solve(opCtxG, confGraph);

        var reducer = new reducePass();
        var opCtxG1 = reader.read(ospf_st);
        var rCtxg = reducer.solve(opCtxG1);
        var writer = new OspfConfWriter();
        System.out.println(writer.write(rCtxg.getRemainOps()));
        ospfArgPass.solve(rCtxg.activeSetView(), confGraph, "r1");
        System.out.println(confGraph.toDot(false));
        confGraph.getNodes().forEach(node -> System.out.printf("%s %s\n", node.getName(), node.getNodeAtrriStr()));
//        var reducer = new reducePass();
//        var rCtxg = reducer.solve(opCtxG);
//        rCtxg.reduce();
//        //rCtxg.activeSetView()
//        //var rCtxg = reducePass.expandOpAG(reducer.resolve(opCtxG));
//        System.out.println(writer.write(rCtxg.getRemainOps()));
    }
    @Test
    public void OpOspfTest(){
        var op1 = OpOspf.of();
        var op2 = OpOspf.of();
        var opCtx1 = IO.readOp("ip address 11.0.0.0/10", op1);
        var opCtx2 = IO.readOp("ip address 11.0.0.0/10", op2);
        assert opCtx1.getOperation().equals(opCtx2.getOperation());
        assert opCtx1.getOperation().hashCode() == opCtx2.getOperation().hashCode();
    }
}
