package tools.topo;

import org.generator.lib.topo.pass.attri.ranAttriGen;
import org.generator.lib.topo.pass.base.ranBaseGen;
import org.generator.lib.topo.pass.build.topoBuild;
import org.generator.tools.diffTopo.diffTopo;
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
}
