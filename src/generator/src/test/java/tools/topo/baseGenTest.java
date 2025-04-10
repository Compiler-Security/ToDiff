package tools.topo;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.reducer.driver.reducer;
import org.generator.lib.reducer.pass.phyArgPass;
import org.generator.lib.topo.driver.topo;
import org.generator.lib.topo.item.base.Intf_ISIS;
import org.generator.lib.topo.item.base.Router_ISIS;
import org.generator.lib.topo.pass.attri.isisRanAttriGen;
import org.generator.lib.topo.pass.attri.ospfRanAttriGen;
import org.generator.lib.topo.pass.base.isisRanBaseGen;
import org.generator.lib.topo.pass.base.ospfRanBaseGen;
import org.generator.lib.topo.pass.build.topoBuild;
import org.generator.lib.topo.pass.build.topoBuild_ISIS;
import org.generator.tools.diffOp.genOps;
import org.generator.tools.diffTopo.diffTopo;
import org.generator.tools.frontend.OspfConfWriter;
import org.generator.tools.frontend.PhyConfReader;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSinkDOT;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import static org.generator.util.diff.differ.compareJson;
public class baseGenTest {
    @Test
    public void testRandomBaseGen(){
        var ran = new ospfRanBaseGen();
        var routers = ran.generate(3, 2, 2, 3);
        Graph graph = new MultiGraph("BaseGraph");
        for(int i = 0; i < routers.size(); i++){
            graph.addNode("r%d".formatted(i));
        }
        for(int i = 0; i < ran.networkId; i++){
            graph.addNode("n%d".formatted(i));
        }
        for(int i = 0; i < routers.size(); i++){
            var r = routers.get(i);
            int j = 0;
            for(var intf: r.intfs){
                var gedge = graph.addEdge("r%d->n%d(%d)".formatted(i, intf.networkId, j), "r%d".formatted(i), "n%d".formatted(intf.networkId));
               gedge.setAttribute("label", "%d".formatted(intf.area));
                j++;
            }
        }
        for(int i = 0; i < ran.networkId; i++){
            var nodeName = "n%d".formatted(i);
            var node = graph.getNode(nodeName);
            if (node.edges().toList().size() != 2) continue;
            var src1 = node.getEdge(0).getSourceNode();
            var src2 = node.getEdge(1).getSourceNode();
            var gedge = graph.addEdge("%s->%s(%d)".formatted(src1, src2, i), src1, src2);
            gedge.setAttribute("label", node.getEdge(0).getAttribute("label"));
            graph.removeNode(node);
        }
        FileSinkDOT fileSinkDOT = new FileSinkDOT(false);
        StringWriter stringWriter = new StringWriter();
        try {
            fileSinkDOT.writeAll(graph, stringWriter);
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(stringWriter.toString());
        var b = new topoBuild();
        var confG = b.solve(routers);
        System.out.println(confG.toDot(false));

        var c = new ospfRanAttriGen();
        c.generate(confG, routers);
        System.out.println(confG.toJson().toPrettyString());
    }


    @Test
    public void testRandomBaseGen_ISIS(){
        int x = 0;
        generate.protocol = generate.Protocol.ISIS;
        while(true)
        {
            x++;
            System.out.printf("testCase %d\n", x);
            
            var ran = new isisRanBaseGen();
            var routers = ran.generate(3, 2, 2, 3);
            Graph graph = new MultiGraph("BaseGraph");
            for(int i = 0; i < routers.size(); i++){
                var node = graph.addNode("r%d".formatted(i));
                var router = routers.get(i);
                // 格式：area=X,level=Y
                node.setAttribute("label", "area=%d,level=%d".formatted(
                    router.area, 
                    router.level
            ));
            }
            for(int i = 0; i < ran.networkId; i++){
                graph.addNode("n%d".formatted(i));
            }
            for(int i = 0; i < routers.size(); i++){
                var r = routers.get(i);
                int j = 0;
                for(var intf: r.intfs){
                    var gedge = graph.addEdge("r%d->n%d(%d)".formatted(i, intf.networkId, j), "r%d".formatted(i), "n%d".formatted(intf.networkId));
                //gedge.setAttribute("label", "%d".formatted(intf.cost));
                    j++;
                }
            }
            for(int i = 0; i < ran.networkId; i++){
                var nodeName = "n%d".formatted(i);
                var node = graph.getNode(nodeName);
                if (node.edges().toList().size() != 2) continue;
                var src1 = node.getEdge(0).getSourceNode();
                var src2 = node.getEdge(1).getSourceNode();
                var gedge = graph.addEdge("%s->%s(%d)".formatted(src1, src2, i), src1, src2);
                //gedge.setAttribute("label", node.getEdge(0).getAttribute("label"));
                graph.removeNode(node);
            }
            FileSinkDOT fileSinkDOT = new FileSinkDOT(false);
            StringWriter stringWriter = new StringWriter();
            try {
                fileSinkDOT.writeAll(graph, stringWriter);
            }catch (IOException e){
                e.printStackTrace();
            }
            //System.out.println(stringWriter.toString());
            //System.out.println("===============");
            var b = new topoBuild_ISIS();
            var confG = b.solve(routers);
            //System.out.println(confG.toDot(false));
            //System.out.println("===============");
            var c = new isisRanAttriGen();
            c.generate(confG, routers);
            //System.out.println(confG.toJson().toPrettyString());
            //System.out.println("===============");
             //generate isis core commands, all the round is same
            var router_count = routers.size();
            var routers_name = new ArrayList<String>();
            for(int i = 0; i < router_count; i++){
                routers_name.add(NodeGen.getRouterName(i));
            }
            List<OpCtxG> isis_cores = new ArrayList<>();
            //System.out.println(confG);
            for(int i = 0; i < router_count; i++) {
                isis_cores.add(getConfOfRouter(routers_name.get(i), confG, false, false));
            }
            List<OpCtxG> isis_cores_isfull = new ArrayList<>();
            for(int i = 0; i < router_count; i++) {
                isis_cores_isfull.add(getConfOfRouter(routers_name.get(i), confG, false, true));
            }
            List<OpCtxG> isis_cores_equal = new ArrayList<>();
            for(int i = 0; i < router_count; i++) {
                var opCtxG = generate.generateEqualOfCore(isis_cores.get(i), true);
                isis_cores_equal.add(opCtxG);
            }

            for(int i = 0; i < router_count; i++) {
                var confg_equal = getSetConfG_ISIS(isis_cores_equal.get(i), routers.get(i), i);
                var confg_gen = getSetConfG_ISIS(isis_cores_isfull.get(i), routers.get(i), i);
                if (!confg_equal.equals(confg_gen)){
                    System.out.println(isis_cores_isfull.get(i));
                    System.out.println("===============");
                    System.out.println(isis_cores_equal.get(i));
                    System.out.println(compareJson(confg_gen.toJson(), confg_equal.toJson()));
                }

             }
            // System.out.println(isis_cores.get(0));
            // System.out.println("===============");
            // System.out.println(isis_cores_equal.get(0));
            // for(int j = 0; j < router_count; j++) {
            //     System.out.println(routers_name.get(j));
            //     System.out.println(new IsisConfWriter().write(isis_cores.get(j)));
            //     System.out.println("===============");
            // }
            if(x == 100)
            {
                break;
            }
        }
        
    }
    OpCtxG getConfOfRouter(String r_name, ConfGraph g, boolean mutate, boolean isfull){
        var confg = g.viewConfGraphOfRouter(r_name);
        confg.setR_name(r_name);
        if (mutate) {
            return generate.generateEqualOfCore(generate.generateCore(confg, isfull), false);
        }else{
            return generate.generateCore(confg, isfull);
        }
    }

    ConfGraph getSetConfG_ISIS(OpCtxG conf, Router_ISIS router, int i){
        ConfGraph g = getConfG_ISIS(NodeGen.getRouterName(i), router);
        reducer.reduceToConfG(conf, g);
        return g;
    }
    ConfGraph getConfG_ISIS(String r_name, Router_ISIS router){
        var confg = new ConfGraph(r_name);
        confg.addNode(new org.generator.lib.item.conf.node.phy.Router(r_name));
        for(int i = 0; i < router.intfs.size(); i++){
            var intf_name = NodeGen.getIntfName(r_name, i);
            confg.addNode(new Intf(intf_name));
            confg.addIntfRelation(intf_name, r_name);
        }
        return confg;
    }






    @Test
    public void testDiffTopo(){
        var d = new diffTopo();
        d.main();
    }

    @Test
    public void testRanSplit() {
        var genOp = new genOps();
        var ori = genOp.genRandom(2, 0.2, 0.6, 4, 0, 1, "r1");
        var res = diffTopo.ranSplitOspfConf(ori, 10);
        for(var opctxg: res){
            System.out.println("=======");
            System.out.println(new OspfConfWriter().write(opctxg));
            System.out.println("=======");
            //opg.addOps(opctxg.getOps());
        }
    }

    @Test
    public void testGen() {
        //while(true) {
            var diff = new diffTopo();
            diff.gen(1, 1, 1, 1);
        //}
    }

    OpCtxG getConfOfPhy(ConfGraph g){
        return generate.generatePhyCore(g);
    }

    @Test
    public void testPhyEqualDebug(){
        var router_count = 3;
        var string_st = """
        node r0 add
        node s1 add
        node s3 add
        node s4 add
        link r0-eth2 s1-eth0 add
        link r0-eth1 s3-eth0 add
        link r0-eth0 s4-eth0 add
        intf r0-eth0 up
        intf r0-eth1 up
        intf r0-eth2 up
        node r0 set OSPF up""";
        var a = new PhyConfReader();
        var phyConf = a.read(string_st);
        //while(true) {
        var phyEqualConf = generate.generateEqualOfPhyCore(phyConf, 1, 1);
        var totalConf = OpCtxG.Of();
        totalConf.addOps(phyConf.getOps());
        totalConf.addOps(phyEqualConf.getOps());
        var confg = new ConfGraph();
        var confg1 = new ConfGraph();
        phyArgPass.solve(phyConf, confg);
        System.out.println(totalConf);
        phyArgPass.solve(totalConf, confg1);
        assert confg.equals(confg1);
//        System.out.println(confg);
//        System.out.println(confg1);
//        //}
//        System.out.println(phyEqualConf);
    }

    @Test
    public void testPhyEqual(){
        while(true) {
            var router_count = 5;
            var confg = topo.genGraph(router_count, 3, 4, 3, true, null);
            var phyConf = getConfOfPhy(confg);
            System.out.println(phyConf.toString());
            var phyEqualConf = generate.generateEqualOfPhyCore(phyConf, 0.4, 1);
            // var phyEqualConf = generate.generateEqualOfPhyCore(phyConf, 0.4, 1);
            var totalConf = OpCtxG.Of();
            totalConf.addOps(phyConf.getOps());
            totalConf.addOps(phyEqualConf.getOps());
            var confg0 = new ConfGraph();
            var confg1 = new ConfGraph();
            phyArgPass.solve(phyConf, confg0);
            System.out.println(totalConf);
            phyArgPass.solve(totalConf, confg1);
            assert confg0.equals(confg1);
        }
    }

    @Test
    public void fixLinkEqual(){
        var router_count = 3;
        var string_st = """
        node r0 add
        node s0 add
        node r0 set OSPF up
        link r0-eth0 s0-eth0 add
        intf r0-eth0 up
        """;
        var a = new PhyConfReader();
        var phyConf = a.read(string_st);
        //while(true) {
        var phyEqualConf = generate.generateEqualOfPhyCore(phyConf, 1, 1);
        System.out.println(phyEqualConf);
    }
}
