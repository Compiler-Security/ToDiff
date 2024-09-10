package tools.topo;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.reducer.pass.phyArgPass;
import org.generator.lib.topo.driver.topo;
import org.generator.lib.topo.pass.attri.ranAttriGen;
import org.generator.lib.topo.pass.base.ranBaseGen;
import org.generator.lib.topo.pass.build.topoBuild;
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

public class baseGenTest {
    @Test
    public void testRandomBaseGen(){
        var ran = new ranBaseGen();
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

        var c = new ranAttriGen();
        c.generate(confG, routers);
        System.out.println(confG.toJson().toPrettyString());
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
}
