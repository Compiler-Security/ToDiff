package tools.frontend;

import org.generator.lib.frontend.driver.IO;
import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.ospf.controller.CapacityController;
import org.generator.lib.generator.ospf.controller.NormalController;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.generator.ospf.pass.actionRulePass;
import org.generator.lib.generator.ospf.pass.genEqualPass;
import org.generator.lib.generator.ospf.pass.genOpPass;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.ospf.OSPF;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.phy.Router;
import org.generator.lib.reducer.driver.reducer;
import org.generator.lib.reducer.pass.ospfArgPass;
import org.generator.lib.reducer.pass.phyArgPass;
import org.generator.lib.reducer.pass.reducePass;
import org.generator.tools.frontend.ConfReader;
import org.generator.tools.frontend.OspfConfWriter;
import org.generator.tools.diffOp.genOps;
import org.generator.tools.diffOp.genOps_ISIS;
import org.generator.util.net.ID;
import org.generator.util.net.IPRange;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static org.generator.util.diff.differ.compareJson;
import static org.junit.Assert.assertEquals;

public class IOTest_ISIS {

    @Test
    public void genRandom(){
        var genOp = new genOps_ISIS();
        var res = genOp.genRandom(100, 0.5, 0.4, 2, 0, 0.3, "r1");
        System.out.println(res);
		System.out.println("=========");
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
    public void mutateOpTest(){
        var opA = OpAnalysis.of(genOpPass.genRanOpOfType(OpType.IpOspfDeadInter).getOpOspf());
        System.out.println(opA.getOp());
        var opA_new = actionRulePass.mutate(opA);
        System.out.println(opA_new);
    }

    @Test
    public void unsetOpTest(){
        var opA = OpAnalysis.of(OpOspf.of(OpType.AreaRange));
        opA.getOp().setID(ID.of(0));
        opA.getOp().setIPRANGE(IPRange.of(10, 5));
        System.out.println(opA.getOp());
        var opA_new = actionRulePass.mutate(opA);
        System.out.println(opA_new);
    }
    @Test
    public void reduceTest(){
        String test_st = """
                                int r1-eth0
                                    ip address 11.1.1.1/10
                                    ip address 10.1.1.1/10 
                """;
        var ori = new ConfReader().read(test_st);
        System.out.println(reducer.reduceToCore(ori));
    }

    @Test
    public void genCorePart1Test(){
        int i = 0;
        while(true) {
            i++;
            System.out.printf("testCase %d\n", i);
            var genOp = new genOps();
            var ori = genOp.genRandom(10000, 0.2, 0.6, 4, 0, 1, "r1");
            var ori_use = new ConfReader().read(new OspfConfWriter().write(ori));
            var confg = getSetConfG(ori_use);
            var gen = generate.generateCore(confg);
            var confg_core = getSetConfG(gen);
            if (!confg_core.equals(confg)) {
                try {
                    FileWriter fileWriter = new FileWriter("output.txt");
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                    bufferedWriter.write(ori_use.toString());
                    bufferedWriter.newLine();
                    bufferedWriter.write(gen.toString());
                    bufferedWriter.newLine();
                    bufferedWriter.write(compareJson(confg.toJson(), confg_core.toJson()).toPrettyString());

                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert false;
                break;
            }
        }
    }

    @Test
    public void genCorePart1TestDebug(){
        var test_st = """
                interface r1-eth1
                                             	ip address 171.218.69.198/3
                                             	ip ospf cost 10
                                             	ip ospf dead-interval 40
                                             	ip ospf hello-interval 10
                                             	ip ospf graceful-restart hello-delay 43
                                             	ip ospf network broadcast
                                             	ip ospf priority 240
                                             	ip ospf retransmit-interval 5
                                             	ip ospf transmit-dealy 37083
                                             	ip ospf passive
                                             router ospf
                                             	ospf router-id 200.18.217.232
                                             	ospf abr-type standard
                                             	timers throttle spf 513091 309026 499491
                                             	write-multiplier 20
                                             	socket buffer send 2792755050
                                             	socket buffer recv 3790630531
                                             	maximum-paths 20
                                             	area 221.86.3.150 shortcut default
                                             	area 221.86.3.150 stub no-summary
                                             	area 136.160.61.211 shortcut enable
                                             	area 147.60.105.24 shortcut enable
                                             	area 228.168.219.224 shortcut default
                                             	area 228.168.219.224 nssa
                                             	area 228.215.236.77 shortcut default
                                             	area 228.215.236.77 range 144.98.96.235/25 cost 0
                                             	area 228.215.236.77 range 144.98.96.235/25 substitute 93.237.252.85/31
                                             	area 105.16.249.21 shortcut default
                                             	area 105.16.249.21 range 64.163.211.68/20 cost 0
                                             	area 11.5.53.198 shortcut default
                                             	area 11.5.53.198 range 158.98.105.217/24 not-advertise
                                             	area 41.53.137.163 shortcut default
                                             	area 19.7.21.239 shortcut default
                                             	area 19.7.21.239 range 166.62.18.160/4 cost 0
                                             	area 169.225.22.8 shortcut default
                                             	area 169.225.22.8 range 149.6.187.207/23 cost 0
                                             	area 223.206.71.52 shortcut default
                                             	area 223.206.71.52 range 221.165.192.37/15 cost 7644877
                                             	area 93.17.18.119 shortcut default
                                             	area 93.17.18.119 stub
                                             	area 195.193.157.237 shortcut default
                                             	area 195.193.157.237 range 234.229.146.27/28 not-advertise
                                             	area 52.127.90.190 shortcut default
                                             	area 52.127.90.190 nssa
                                             	area 119.32.52.57 shortcut default
                                             	area 119.32.52.57 range 65.192.87.207/1 cost 0
                                             	area 170.121.15.162 shortcut default
                                             	area 170.121.15.162 stub
                                             	area 50.180.240.215 shortcut default
                                             	area 50.180.240.215 stub
                                             	area 8.135.34.213 shortcut default
                                             	area 8.135.34.213 nssa
                                             	area 237.192.162.62 shortcut default
                                             	area 237.192.162.62 nssa
                                             	area 31.177.186.16 shortcut default
                                             	area 31.177.186.16 range 144.218.242.190/6 cost 0
                                             	area 5.0.55.174 shortcut default
                                             	area 5.0.55.174 stub
                                             	area 183.42.223.122 shortcut default
                                             	area 183.42.223.122 range 209.229.160.121/8 cost 0
                                             	area 183.42.223.122 range 209.229.160.121/8 substitute 120.193.101.55/24
                                             	area 83.182.62.23 shortcut enable
                                             	area 23.15.214.52 shortcut default
                                             	area 23.15.214.52 range 160.106.64.224/18 cost 0
                                             	area 23.15.214.52 range 160.106.64.224/18 substitute 44.9.236.100/30
                                             	area 221.184.26.126 shortcut default
                                             	area 221.184.26.126 range 26.248.35.52/23 cost 7943737
                                             	area 21.239.102.216 shortcut disable
                                             	area 67.39.144.187 shortcut default
                                             	area 67.39.144.187 range 120.21.148.232/14 cost 0
                                             	area 255.81.232.47 shortcut disable
                                             	area 134.119.56.182 shortcut default
                                             	area 134.119.56.182 range 216.9.181.46/20 not-advertise
                                             	area 40.90.99.213 shortcut enable
                                             	area 220.241.150.236 shortcut enable
                                             	area 168.9.252.48 shortcut default
                                             	area 168.9.252.48 stub
                                             	area 15.204.173.186 shortcut default
                                             	area 15.204.173.186 stub
                                             	area 104.164.228.90 shortcut enable
                                             	area 74.200.45.11 shortcut default
                                             	area 74.200.45.11 stub
                                             	area 9.112.29.139 shortcut enable
                                             	area 66.18.50.252 shortcut default
                                             	area 66.18.50.252 nssa
                                             	area 92.247.243.44 shortcut default
                                             	area 92.247.243.44 range 221.11.46.188/10 cost 0
                                             	area 92.247.243.44 range 221.11.46.188/10 substitute 200.156.183.172/30
                                             	area 88.231.222.113 shortcut default
                                             	area 88.231.222.113 stub no-summary
                                             	area 39.75.226.129 shortcut default
                                             	area 39.75.226.129 range 94.124.203.179/30 cost 0
                                             	area 39.75.226.129 range 94.124.203.179/30 substitute 223.119.10.41/19
                                             	area 208.204.139.147 shortcut default
                                             	area 208.204.139.147 range 133.18.120.106/22 cost 0
                                             	area 208.204.139.147 range 133.18.120.106/22 substitute 75.247.30.230/10
                                             	area 96.46.209.208 shortcut default
                                             	area 96.46.209.208 range 237.83.154.165/3 not-advertise
                                             	area 21.82.156.221 shortcut default
                                             	area 65.70.68.74 shortcut default
                                             	area 65.70.68.74 range 164.31.52.84/29 cost 0
                                             	area 65.70.68.74 range 164.31.52.84/29 substitute 103.167.110.38/30
                                             	area 159.11.129.41 shortcut default
                                             	area 159.11.129.41 nssa
                                             	area 170.240.7.98 shortcut default
                                             	area 170.240.7.98 stub
                                             	area 183.142.109.67 shortcut default
                                             	area 183.142.109.67 stub no-summary
                                             	area 27.13.71.67 shortcut default
                                             	area 27.13.71.67 stub
                                             	area 50.57.77.68 shortcut default
                                             	area 50.57.77.68 range 159.69.77.76/11 cost 0
                                             	area 50.57.77.68 range 159.69.77.76/11 substitute 168.21.109.7/6
                                             	area 216.9.208.144 shortcut default
                                             	area 216.9.208.144 range 169.119.71.20/31 cost 12921442
                                             	area 173.136.142.227 shortcut default
                                             	area 173.136.142.227 stub
                                             	area 101.208.144.218 shortcut default
                                             	area 101.208.144.218 stub
                                             	area 182.74.5.247 shortcut default
                                             	area 182.74.5.247 stub
                                             	area 196.190.63.103 shortcut default
                                             	area 196.190.63.103 nssa
                                             	area 150.157.229.55 shortcut default
                                             	area 150.157.229.55 stub no-summary
                                             	area 50.194.49.198 shortcut default
                                             	area 50.194.49.198 range 202.251.3.98/5 cost 5318894
                                             	area 27.21.156.40 shortcut default
                                             	area 27.21.156.40 range 128.253.142.151/26 not-advertise
                                             	area 60.110.26.217 shortcut default
                                             	area 60.110.26.217 stub no-summary
                                             	area 36.59.229.78 shortcut default
                                             	area 36.59.229.78 range 100.191.246.38/20 cost 0
                                             	area 204.113.18.70 shortcut default
                                             	area 204.113.18.70 nssa
                                             	area 178.35.211.195 shortcut default
                                             	area 178.35.211.195 stub
                                             	area 238.17.228.14 shortcut enable
                                             	area 211.83.234.98 shortcut disable
                                             	area 106.113.211.239 shortcut default
                                             	area 106.113.211.239 range 151.40.143.128/30 not-advertise
                                             	area 219.198.132.151 shortcut default
                                             	area 219.198.132.151 range 111.9.227.114/23 cost 0
                                             	area 219.198.132.151 range 111.9.227.114/23 substitute 51.213.67.155/18
                                             	area 76.113.149.38 shortcut default
                                             	area 76.113.149.38 range 159.65.86.245/8 cost 6917944
                                             	area 37.136.248.252 shortcut default
                                             	area 37.136.248.252 range 27.31.186.44/26 not-advertise
                                             	area 237.210.234.35 shortcut enable
                                             	area 184.120.216.223 shortcut default
                                             	area 184.120.216.223 range 29.241.104.107/18 cost 0
                                             	area 184.120.216.223 range 29.241.104.107/18 substitute 107.251.91.132/3
                                             	area 196.248.158.57 shortcut default
                                             	area 196.248.158.57 range 221.219.240.34/9 cost 0
                                             	area 196.248.158.57 range 221.219.240.34/9 substitute 47.47.184.57/15
                                             	area 46.234.73.25 shortcut default
                                             	area 46.234.73.25 stub no-summary
                                             	area 242.40.254.76 shortcut default
                                             	area 140.240.53.134 shortcut default
                                             	area 140.240.53.134 range 51.140.165.46/32 cost 11306823
                                             	area 227.102.134.61 shortcut default
                                             	area 227.102.134.61 range 85.153.133.44/20 cost 2968674
                                             	area 34.105.111.248 shortcut default
                                             	area 34.105.111.248 range 204.47.35.242/10 cost 13315478
                                             	area 83.151.19.70 shortcut default
                                             	area 138.187.63.231 shortcut default
                                             	area 138.187.63.231 nssa
                                             	area 111.19.124.186 shortcut default
                                             	area 111.19.124.186 nssa
                                             	area 111.142.232.139 shortcut default
                                             	area 111.142.232.139 range 230.207.146.151/12 cost 0
                                             	area 11.119.146.71 shortcut default
                                             	area 11.119.146.71 stub no-summary
                                             	area 107.241.37.15 shortcut default
                                             	area 107.241.37.15 stub
                                             	area 155.99.242.232 shortcut enable
                                             	area 133.211.4.106 shortcut default
                                             	area 133.211.4.106 range 115.25.56.5/7 cost 0
                                             	area 133.211.4.106 range 115.25.56.5/7 substitute 187.120.89.43/32
                                             	area 229.47.85.175 shortcut disable
                                             	network 56.226.210.235/16 area 0.0.0.2
                                             	network 251.0.119.100/4 area 0.0.0.0
                                             	network 169.219.106.238/1 area 0.0.0.3
                                             	network 171.218.69.198/3 area 0.0.0.2
                                             interface r1-eth3
                                             	ip address 56.226.210.235/16
                                             	ip ospf cost 10
                                             	ip ospf dead-interval minimal hello-multiplier 13
                                             	ip ospf graceful-restart hello-delay 274
                                             	ip ospf network broadcast
                                             	ip ospf priority 1
                                             	ip ospf retransmit-interval 5
                                             	ip ospf transmit-dealy 51159
                                             interface r1-eth0
                                             	ip address 169.219.106.238/1
                                             	ip ospf cost 10
                                             	ip ospf dead-interval minimal hello-multiplier 16
                                             	ip ospf graceful-restart hello-delay 1795
                                             	ip ospf network non-broadcast
                                             	ip ospf priority 46
                                             	ip ospf retransmit-interval 5
                                             	ip ospf transmit-dealy 17761
                                             	ip ospf passive
                                             interface r1-eth2
                                             	ip address 251.0.119.100/4
                                             	ip ospf cost 34791
                                             	ip ospf dead-interval minimal hello-multiplier 10
                                             	ip ospf graceful-restart hello-delay 1060
                                             	ip ospf network non-broadcast
                                             	ip ospf priority 218
                                             	ip ospf retransmit-interval 5
                                             	ip ospf transmit-dealy 3934
                                             	ip ospf passive
                """;
        var ori = new ConfReader().read(test_st);
        var confg = getSetConfG(ori);
        System.out.println(confg.toString());
    }
  //  @Test
//    public void generatorTest(){
//        String test_st = """
//                interface r1-eth0
//                	ip address 82.144.2.106/3
//                	ip ospf area 0.0.0.2
//                interface r1-eth1
//                	ip address 89.183.104.6/1
//                	ip ospf area 0.0.0.3
//                int r1-eth2
//                	ip address 237.151.161.95/16
//                	ip ospf area 0.0.0.1
//                int r1-eth3
//                	ip address 117.132.165.79/15
//                	ip ospf area 0.0.0.0
//                router ospf
//                    network 1.1.1.1/10 area 2
//                """;
//        int i = 0;
//        while(true) {
//            i++;
//            System.out.printf("testCase %d\n", i);
//            var genOp = new genOps();
//            var ori = genOp.genRandom(100, 0.2, 0.6, 4, 0, 1, "r1");
//            //var ori = new ConfReader().read(test_st);
//
//            var ori_use = new ConfReader().read(new OspfConfWriter().write(ori));
//            //System.out.println(ori_use);
//            var confg = getSetConfG(ori_use);
//            var gen = generate.generateCore(confg);
//            //var gen = new ConfReader().read(test_st1);
//            //System.out.println(gen.getOps().size());
//            //System.out.println(reducer.reduceToCore(ori));
//            //System.out.println("========");
//            //System.out.println(gen);
//            var confg_core = getSetConfG(gen);
//            //System.out.println(confg);
//            //System.out.println(confg_core);
//            if (!confg_core.equals(confg)) {
//                System.out.println(gen);
//                System.out.println(compareJson(confg.toJson(), confg_core.toJson()).toPrettyString());
//                //System.out.println(confg_core.toJson().toPrettyString());
//            }
//            assert confg_core.equals(confg) : "CORE WRONG";
//            reducer.s = 0;
//            var gen_equal = generate.generateEqualOfCore(gen);
//            //System.out.println(gen_equal);
//            var confg_equal = getSetConfG(gen_equal);
//            if (!confg_equal.equals(confg)){
//                System.out.println(gen);
//                System.out.println("===============");
//                System.out.println(gen_equal);
//                System.out.println(compareJson(confg.toJson(), confg_equal.toJson()));
//            }
//            assert confg_equal.equals(confg) : "MUTATE WRONG";
//            //break;
//        }
//    }



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
        var gen_opag = genEqualPass.solve(normal_controller, OpAG.of(opas));
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
                 link r1-eth0 s1-eth0 add
                 link r2-eth0 s1-eth1 add
                 link r3-eth0 s1-eth2 add
                 link r1-eth1 s2-eth0 add
                 link r2-eth1 s2-eth1 add
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
                     link r1-eth0 s1-eth0 add
                     link r1-eth1 s1-eth1 add
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
