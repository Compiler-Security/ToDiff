package org.generator.lib.topo.driver;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.cli.*;
import org.generator.lib.item.conf.graph.ConfGraph_ISIS;
import org.generator.lib.topo.item.base.Router_ISIS;
import org.generator.lib.topo.pass.attri.ranAttriGen_ISIS;
import org.generator.lib.topo.pass.base.ranBaseGen_ISIS;
import org.generator.lib.topo.pass.build.topoBuild_ISIS;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSinkDOT;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class topo_ISIS {

    public static String dumpGraph(List<Router_ISIS> routers, ranBaseGen_ISIS ran){
        Graph graph = new MultiGraph("BaseGraph");
        for(int i = 0; i < routers.size(); i++){
            graph.addNode("r%d".formatted(i));
        }
        for(int i = 0; i < ran.networkId; i++){
            var n = graph.addNode("n%d".formatted(i));
            n.setAttribute("shape", "square");
        }
        for(int i = 0; i < routers.size(); i++){
            var r = routers.get(i);
            int j = 0;
            for(var intf: r.intfs){
                var gedge = graph.addEdge("r%d->n%d(%d)".formatted(i, intf.networkId, j), "r%d".formatted(i), "n%d".formatted(intf.networkId));
                assert intf.cost > 0: "intf cost should > 0";
                //FIXME:here has changed
                gedge.setAttribute("label", "%d:%d:%d".formatted(j, intf.cost));
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

        return stringWriter.toString();
    }

    public static ConfGraph_ISIS genGraph(int totalRouter, int areaCount, int mxDegree, int abrRatio, boolean verbose, ObjectNode dumpInfo){
        var ran = new ranBaseGen_ISIS();
        var routers = ran.generate(totalRouter, areaCount, mxDegree, abrRatio);
        var baseGraphStr = dumpGraph(routers, ran);
        if (dumpInfo != null) dumpInfo.put("routerGraph", TextNode.valueOf(baseGraphStr));
        if (verbose){
            System.out.println(baseGraphStr);
        }
        var b = new topoBuild_ISIS();
        var confg = b.solve(routers);
        var c = new ranAttriGen_ISIS();
        c.generate(confg, routers);
        var confgAttrStr = confg.toString();
        if (dumpInfo != null){
            dumpInfo.put("configGraph", confg.toDot(false));
            dumpInfo.put("configGraphAttr", confg.toString());
        }
        if (verbose){
            System.out.println("config graph");
            System.out.println(confgAttrStr);
        }
        return confg;
    }

    public static int areaCount = 3;
    public static int mxDegree = 3;

    /** 0-10**/
    public static int abrRatio = 4;
}
