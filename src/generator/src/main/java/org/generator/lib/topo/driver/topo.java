package org.generator.lib.topo.driver;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.topo.item.base.Router;
import org.generator.lib.topo.pass.attri.ospfRanAttriGen;
import org.generator.lib.topo.pass.attri.ripRanAttriGen;
import org.generator.lib.topo.pass.base.ospfRanBaseGen;
import org.generator.lib.topo.pass.base.ripRanBaseGen;
import org.generator.lib.topo.pass.build.topoBuild;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSinkDOT;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class topo {

    public static String dumpGraphOspf(List<Router> routers, ospfRanBaseGen ran){
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
                gedge.setAttribute("label", "p%d:a%d".formatted(j, intf.area));
                j++;
            }
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

    public static String dumpGraphRip(List<Router> routers, ripRanBaseGen ran){
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
                //FIXME we should print cost
//                assert intf.cost > 0: "intf cost should > 0";
//                gedge.setAttribute("label", "p%d:a%d".formatted(j, intf.area));
                j++;
            }
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

    public static ConfGraph genGraph(int totalRouter, int areaCount, int mxDegree, int abrRatio, boolean verbose, ObjectNode dumpInfo){
        List<Router> routers = null;
        String baseGraphStr = null;
        //MULTI:
        switch (generate.protocol){
            case OSPF -> {
                var ran = new ospfRanBaseGen();
                routers = ran.generate(totalRouter, areaCount, mxDegree, abrRatio);
                baseGraphStr = dumpGraphOspf(routers, ran);
            }
            case RIP -> {
                var ran = new ripRanBaseGen();
                routers = ran.generate(totalRouter, areaCount, mxDegree, abrRatio);
                baseGraphStr = dumpGraphRip(routers, ran);
            }
        }
        if (dumpInfo != null) dumpInfo.put("routerGraph", TextNode.valueOf(baseGraphStr));
        if (verbose){
            System.out.println(baseGraphStr);
        }
        var b = new topoBuild();
        var confg = b.solve(routers);
        //MULTI:
        switch (generate.protocol){
            case OSPF -> {
                var c = new ospfRanAttriGen();
                c.generate(confg, routers);
            }
            case RIP -> {
                var c = new ripRanAttriGen();
                c.generate(confg, routers);
            }
        }

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
