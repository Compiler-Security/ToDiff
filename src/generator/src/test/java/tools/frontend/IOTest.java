package tools.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import static org.generator.util.diff.differ.compareJson;
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
        String test_st = """
                router ospf
                                             	area 107.28.78.175 range 35.28.218.125/18 not-advertise
                                             	ospf router-id 168.219.114.215
                                             	area 133.123.212.234 range 20.5.190.138/21 not-advertise
                                             	area 95.79.142.63 range 90.113.215.120/30 advertise
                                             	maximum-paths 3
                                             	timers throttle spf 445887 201188 509088
                                             	area 76.172.244.87 stub
                                             	ospf router-id 78.73.154.19
                                             	network 253.23.188.36/16 area 30.148.80.223
                                             	area 201.232.235.69 stub no-summary
                                             	area 16.102.252.107 range 196.37.189.218/32 not-advertise
                                             	ospf router-id 156.209.174.26
                                             	ospf abr-type ibm
                                             	ospf abr-type standard
                                             	area 7.173.254.22 range 224.233.45.48/3 substitute 41.137.113.99/16
                                             int r1-eth0
                                             	ip ospf retransmit-interval 8542
                                             	ip ospf hello-interval 63045
                                             	ip ospf network broadcast
                                             	ip ospf priority 39
                                             router ospf
                                             	socket buffer send 2833452020
                                             	no area 72.214.127.194 stub no-summary
                                             	area 106.247.45.189 nssa
                                             	area 1.163.28.158 stub no-summary
                                             	maximum-paths 10
                                             	area 167.27.29.66 stub
                                             	timers throttle spf 66206 370278 531233
                                             	area 43.200.211.154 range 188.81.33.54/15
                                             	no socket-per-interface
                                             	no maximum-paths 39
                                             	no area 51.107.20.167 range 207.83.140.7/3 advertise
                                             	no ip ospf dead-interval 14630
                                             	ospf abr-type shortcut
                                             	write-multiplier 41
                                             	write-multiplier 54
                                             	area 196.67.143.76 nssa
                                             	area 138.105.156.137 range 154.46.248.41/17 advertise
                                             	ospf abr-type shortcut
                                             	area 240.109.16.49 range 157.238.193.12/15 advertise cost 10605565
                                             	area 161.83.33.178 shortcut enable
                                             	write-multiplier 73
                                             	no socket-per-interface
                                             	maximum-paths 14
                                             	maximum-paths 46
                                             	area 212.109.228.205 range 199.235.14.211/27 substitute 246.103.247.214/1
                                             	area 59.185.40.255 range 143.177.42.245/32 substitute 175.98.95.88/20
                                             	area 137.180.127.23 range 246.33.53.233/32 substitute 40.67.18.210/9
                                             	socket buffer send 489717680
                                             int r1-eth0
                                             	ip ospf area 214.78.201.172
                                             	ip ospf network broadcast
                                             	ip ospf network non-broadcast
                                             	ip ospf network broadcast
                                             	ip ospf dead-interval minimal hello-multiplier 19
                                             	ip ospf dead-interval minimal hello-multiplier 12
                                             	ip ospf cost 7172
                                             	ip ospf priority 109
                                             	ip ospf transmit-dealy 44706
                                             	ip ospf graceful-restart hello-delay 1386
                                             	ip ospf passive
                                             	ip ospf passive
                                             	no area 86.168.152.117 range 116.55.143.81/9 cost 14425995
                                             	ip ospf cost 24908
                                             	ip ospf graceful-restart hello-delay 271
                                             	ip ospf dead-interval minimal hello-multiplier 15
                                             	ip ospf dead-interval minimal hello-multiplier 4
                                             	ip ospf passive
                                             	ip ospf cost 53690
                                             	ip ospf hello-interval 15460
                                             	ip ospf retransmit-interval 46272
                                             	ip ospf network broadcast
                                             	ip ospf retransmit-interval 23025
                                             	ip ospf retransmit-interval 64881
                                             	ip ospf cost 21656
                                             	ip ospf network broadcast
                                             	ip ospf area 162.34.122.84
                                             	ip ospf dead-interval 6754
                                             	ip ospf priority 157
                                             	ip ospf hello-interval 47256
                                             	ip ospf hello-interval 2695
                                             	ip ospf cost 25375
                                             	ip ospf area 142.205.238.217
                                             router ospf
                                             	ospf router-id 132.127.195.137
                                             	socket buffer all 2602556424
                                             	area 237.15.248.127 range 133.90.170.9/28 substitute 185.49.199.159/18
                                             	area 251.8.240.31 range 255.213.51.230/17 not-advertise
                                             	no socket-per-interface
                                             	area 19.144.176.92 shortcut disable
                                             	maximum-paths 39
                                             	area 31.143.201.174 stub no-summary
                                             	socket buffer recv 1759152831
                                             	socket buffer recv 3525570020
                                             	no ip ospf dead-interval
                                             int r1-eth1
                                             	ip ospf network non-broadcast
                                             	ip ospf retransmit-interval 22334
                                             	no ip ospf hello-interval
                                             	no area 9.231.134.24 nssa
                                             	no socket buffer send
                                             int r1-eth0
                                             	ip ospf dead-interval minimal hello-multiplier 6
                                             	ip ospf hello-interval 8392
                                             	no socket-per-interface
                                             router ospf
                                             	socket buffer all 1623371685
                """;
        while(true) {
            var genOp = new genOps();
            //var ori = genOp.genRandom(100, 0.5, 0.4, 2, 0, 1, "r1");
            var ori = new ConfReader().read(test_st);
            var confg = getSetConfG(ori);
            var gen = generate.generateCore(confg);
            //System.out.println(ori);
            System.out.println("========");
            System.out.println(gen);
            var confg_core = getSetConfG(gen);
            //System.out.println(confg);
            //System.out.println(confg_core);
            if (!confg_core.equals(confg)){
                System.out.println(compareJson(confg.toJson(),confg_core.toJson()).toPrettyString());
            }
            assert confg_core.equals(confg) : "GEN WRONG";
            break;
        }
//        var gen_equal = generate.generateEqualOfCore(gen, 1);
//        System.out.println(gen_equal);
//        var confg_equal = getSetConfG(gen_equal);
//        assert confg_equal.equals(confg);
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
