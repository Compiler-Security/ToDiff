package graph;

import org.generator.tools.gen.RandomGen;
import org.generator.lib.operation.conf.OspfConfParser;
import org.generator.lib.operation.conf.PhyConfParser;
import org.generator.lib.operation.conf.OspfConfReader;
import org.generator.lib.operation.opg.ParserOpGroup;
import org.generator.lib.item.topo.graph.RelationGraph;
import org.generator.lib.item.topo.node.ospf.OSPF;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Optional;
import java.util.StringJoiner;

public class RelationGraphIntfExecTest {


    @Test
    public void phyExecSimpleTest(){
        var reader = new OspfConfReader();
        String test_st = """
                node r1 add
                node s1 add
                node r1 set ospf up
                link r1-eth0 s1-eth0 up
                """;
        var ops = reader.read(test_st).get();
        //ops.forEach(op -> {assert op.Type() != OpType.INVALID : String.format("stmt error %s", op.toString());});
        var opg = new ParserOpGroup(ops, Optional.empty());
        System.out.println(opg);
        RelationGraph topo = new RelationGraph();
        PhyConfParser.parse(opg, topo);
        System.out.println(topo);
    }

    @Test
    public void dotTest(){
        var reader = new OspfConfReader();
        String test_st = """
                node r1 add
                node s1 add
                node r1 set ospf up
                link r1-eth0 s1-eth0 up
                """;
        var ops = reader.read(test_st).get();
        //ops.forEach(op -> {assert op.Type() != OpType.INVALID : String.format("stmt error %s", op.toString());});
        var opg = new ParserOpGroup(ops, Optional.empty());
        System.out.println(opg);
        RelationGraph topo = new RelationGraph();
        PhyConfParser.parse(opg, topo);
        System.out.println(topo);
        System.out.println(topo.toDot(true));
    }


    private void run_cmd_str(boolean isPhy, String cmd, RelationGraph topo, @NotNull Optional<String> target){
        var reader = new OspfConfReader();
        var ops = reader.read(cmd).get();
        //ops.forEach(op -> {assert op.Type() != OpType.INVALID : String.format("stmt error %s", op.toString());});
        var opg = new ParserOpGroup(ops, target);
        if (isPhy){
            PhyConfParser.parse(opg, topo);
        }else {
            OspfConfParser.parse(opg, topo, target.get());
            StringJoiner joiner = new StringJoiner("\n");
            opg.getOps().forEach(x -> joiner.add(x.toString()));
            System.out.println("=====deduced conf=====");
            System.out.println(joiner.toString());
        }
    }

    private RelationGraph getBaseTopo(){
        String test_st = """
                node r1 add
                node s1 add
                link r1-eth0 s1-eth0 up
                link r1-eth1 s1-eth1 up
                """;
        RelationGraph topo = new RelationGraph();
        run_cmd_str(true, test_st, topo, Optional.empty());
        return topo;
    }
    @Test
    public void routerOperationTest(){
//        String test_st = """
//                interface r1-eth0
//                interface r1-eth2
//                ip ospf area 2
//                ip ospf passive
//                ip ospf network non-broadcast
//                ip ospf graceful-restart hello-delay 1732
//                interface r1-eth4
//                ip ospf passive
//                router ospf
//                area 116.176.127.134 range 223.180.81.197/10
//                network 212.56.192.129/23 area 180.232.150.53
//                passive-interface default
//                area 145.162.90.118 shortcut enable
//                area 164.98.104.221 range 84.204.154.130/10
//                network 137.114.215.31/27 area 10.51.223.58
//                area 149.171.129.19 shortcut default
//                ospf router-id 172.187.67.200
//                area 233.239.24.110 range 194.95.172.26/10 not-advertise
//                """;
        String test_st = """
                    interface r1-eth0
                    ip ospf area 2
                    ip address 10.0.0.0/30
                    router ospf
                    ospf abr-type cisco
                    maximum-paths 2
                    no router ospf
                    router ospf
                """;
        var topo = getBaseTopo();
        run_cmd_str(false, test_st, topo, Optional.of("r1"));
        //System.out.println(topo);
        System.out.println("=====OSPF config=====");
        topo.dumpOfRouter("r1");
        System.out.println("=====relation graph=====");
        System.out.println(topo.toDot(true));
    }

    @Test
    public void testNodeAttrStr(){
        var ospf = new OSPF("r1");
        System.out.println(ospf.getNodeAtrriStr());
    }

    private RelationGraph getBaseTopo1(){
        String test_st = """
                node r1 add
                node s1 add
                link r1-eth0 s1-eth0 up
                link r1-eth1 s1-eth1 up
                link r1-eth2 s1-eth2 up
                """;
        RelationGraph topo = new RelationGraph();
        run_cmd_str(true, test_st, topo, Optional.empty());
        return topo;
    }
    @Test
    public void testGenerator(){
        for(int i = 0; i < 1; i++) {
            var gen = new RandomGen();
            var ls = gen.genRandom(30, 0.5, 0.3, 5, 0, 0.9, "r1");
            StringJoiner joiner = new StringJoiner("\n");
            //System.out.println(ls);
            ls.getOps().forEach(x -> joiner.add(x.toString()));
            System.out.println(joiner.toString());
            run_cmd_str(false, joiner.toString(), getBaseTopo1(), Optional.of("r1"));
        }
    }
}
