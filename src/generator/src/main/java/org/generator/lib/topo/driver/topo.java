package org.generator.lib.topo.driver;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.topo.item.base.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.lib.topo.item.base.Router_ISIS;
import org.generator.lib.topo.item.base.baseItemHelper;
import org.generator.lib.topo.item.trans.transGraph;
import org.generator.lib.topo.pass.attri.babelRanAttriGen;
import org.generator.lib.topo.pass.attri.isisRanAttriGen;
import org.generator.lib.topo.pass.attri.ospfRanAttriGen;
import org.generator.lib.topo.pass.attri.ripRanAttriGen;
import org.generator.lib.topo.pass.attri.openfabricRanAttriGen;
import org.generator.lib.topo.pass.base.ospfRanBaseGen;
import org.generator.lib.topo.pass.base.ripRanBaseGen;
import org.generator.lib.topo.pass.base.isisRanBaseGen;
import org.generator.lib.topo.pass.base.openfabricRanBaseGen;
import org.generator.lib.topo.pass.build.topoBuild;
import org.generator.lib.topo.pass.build.topoBuild_ISIS;
import org.generator.lib.topo.pass.trans.babelAttriTran;
import org.generator.lib.topo.pass.trans.ospfAttriTran;
import org.generator.lib.topo.pass.trans.phyTran;
import org.generator.lib.topo.pass.trans.phyTran.transRule;
import org.generator.util.collections.Pair;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSinkDOT;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class topo {
    public static String dumpGraphBABELTrans(List<Router> routers, int networkId, ConfGraph confGraph){
        Graph graph = new MultiGraph("BaseGraph");
        for(int i = 0; i < routers.size(); i++){
            graph.addNode("r%d".formatted(routers.get(i).id));
        }
        var networkIps = confGraph.getNetworks();
        for(int i = 0; i < networkId; i++){
            var n = graph.addNode("n%d".formatted(i));
            if (networkIps.containsKey(i)) {
                n.setAttribute("label", "n%d\n%s".formatted(i, networkIps.get(i).toRangeString()));
            }
            n.setAttribute("shape", "square");
        }
        for(int i = 0; i < routers.size(); i++){
            var r = routers.get(i);
            int j = 0;
            for(var intf: r.intfs){
                var gedge = graph.addEdge("r%d->n%d(%d)".formatted(r.id, intf.networkId, j), "r%d".formatted(r.id), "n%d".formatted(intf.networkId));
                assert intf.cost > 0: "intf cost should > 0";
                gedge.setAttribute("label", "c%d, p%d".formatted(intf.cost, intf.id));
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
    public static String dumpGraphOspfTrans(List<Router> routers, int networkId, ConfGraph confGraph){
        Graph graph = new MultiGraph("BaseGraph");
        for(int i = 0; i < routers.size(); i++){
            graph.addNode("r%d".formatted(routers.get(i).id));
        }
        var networkIps = confGraph.getNetworks();
        for(int i = 0; i < networkId; i++){
            var n = graph.addNode("n%d".formatted(i));
            if (networkIps.containsKey(i)) {
                n.setAttribute("label", "n%d\n%s".formatted(i, networkIps.get(i).toRangeString()));
            }
            n.setAttribute("shape", "square");
        }
        for(int i = 0; i < routers.size(); i++){
            var r = routers.get(i);
            int j = 0;
            for(var intf: r.intfs){
                var gedge = graph.addEdge("r%d->n%d(%d)".formatted(r.id, intf.networkId, j), "r%d".formatted(r.id), "n%d".formatted(intf.networkId));
                assert intf.cost > 0: "intf cost should > 0";
                gedge.setAttribute("label", "c%d, p%d, a%d".formatted(intf.cost, intf.id, intf.area));
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
    public static String dumpGraphOspf(List<Router> routers, int networkId){
        Graph graph = new MultiGraph("BaseGraph");
        for(int i = 0; i < routers.size(); i++){
            graph.addNode("r%d".formatted(routers.get(i).id));
        }
        for(int i = 0; i < networkId; i++){
            var n = graph.addNode("n%d".formatted(i));
            n.setAttribute("shape", "square");
        }
        for(int i = 0; i < routers.size(); i++){
            var r = routers.get(i);
            int j = 0;
            for(var intf: r.intfs){
                var gedge = graph.addEdge("r%d->n%d(%d)".formatted(r.id, intf.networkId, j), "r%d".formatted(r.id), "n%d".formatted(intf.networkId));
                assert intf.cost > 0: "intf cost should > 0";
                gedge.setAttribute("label", "c%d, p%d".formatted(intf.cost, intf.id));
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
    public static String dumpGraphIsis(List<Router_ISIS> routers, isisRanBaseGen ran){
        Graph graph = new MultiGraph("BaseGraph");
        for(int i = 0; i < routers.size(); i++){
            var node = graph.addNode("r%d".formatted(i));
            var router = routers.get(i);
            node.setAttribute("label", "area=%d,level=%d".formatted(
                router.area, 
                router.level));

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
                gedge.setAttribute("label", "%d:%d".formatted(j, intf.cost));
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


    public static String dumpGraphOpenfabric(List<Router_ISIS> routers, openfabricRanBaseGen ran){
        Graph graph = new MultiGraph("BaseGraph");
        for(int i = 0; i < routers.size(); i++){
            var node = graph.addNode("r%d".formatted(i));
            var router = routers.get(i);
            node.setAttribute("label", "area=%d,level=%d".formatted(
                router.area,
                router.level));

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
                gedge.setAttribute("label", "%d:%d".formatted(j, intf.cost));
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

    /*
    * We will not modify List<routers> and confG
    * We will create new List<routers> and deltanodes in transGraph and a new ConfGraph
    * */
    public static Pair<transGraph, ConfGraph> transformGraph(List<Router> routers, ConfGraph old_confG, List<transRule> rules, Map<String, String> dumpInfo){
        var transG = phyTran.solve(routers, rules);
        ConfGraph new_confg = null;
        if(generate.protocol != generate.Protocol.ISIS && generate.protocol != generate.Protocol.OpenFabric){
            var b = new topoBuild();
            new_confg = b.solve(transG.getRouters());
        } //FIXME TODO ISIS
        //MULTI:
        switch (generate.protocol){
            case OSPF -> {
                var c = new ospfRanAttriGen();
                c.generate(new_confg, transG.getRouters());
            }
            case BABEL -> {
                var b = new babelRanAttriGen();
                b.generate(new_confg, transG.getRouters());
            }
            //FIXME TODO ISIS
        }
        var t = new transGraph(routers);
        switch (generate.protocol){
            case OSPF -> {ospfAttriTran.solve(old_confG, new_confg, transG.getDeltaNodes());}
            case BABEL -> {babelAttriTran.solve(old_confG, new_confg, transG.getDeltaNodes());}
        }
        if (dumpInfo != null) {
            switch (generate.protocol) {
                case OSPF -> {
                    dumpInfo.put("topoDot", dumpGraphOspfTrans(routers, transG.getNetworkId(), new_confg));
                }
                case BABEL -> {
                    dumpInfo.put("topoDot", dumpGraphBABELTrans(routers, transG.getNetworkId(), new_confg));
                }
            }
        }

        return new Pair<>(transG, new_confg);
    }

    static Intf getIntf(int routerId, int id, int area, int cost, int networkId){
        var intf = new Intf(id, routerId);
        intf.networkId = networkId;
        intf.cost = cost;
        intf.area = area;
        return intf;
    }

    public static Pair<List<Router>, ConfGraph> genInitTransGraph(int totalRouter, int areaCount, int mxDegree, int abrRatio, boolean verbose, Map<String, String> dumpInfo){
        List<Router> routers = null;
        List<Router_ISIS> routersIsis = null;
        String baseGraphStr = null;
        int networkId = 0;
        //MULTI:
        switch (generate.protocol){
            case OSPF -> {
                var ran = new ospfRanBaseGen();
                routers = ran.generate(totalRouter, areaCount, mxDegree, abrRatio);
                networkId = ran.networkId;
            }
            case BABEL ->{
                var ran = new ripRanBaseGen();
                routers = ran.generate(totalRouter, areaCount, mxDegree, abrRatio);
                //don't change this, we should give interface id
                new transGraph(routers);
                networkId = ran.networkId;
            }
            //FIXME TODO ISIS
        }
        if (verbose){
            System.out.println(baseGraphStr);
        }

        ConfGraph confg = null;
        if(generate.protocol != generate.Protocol.ISIS && generate.protocol != generate.Protocol.OpenFabric){
            var b = new topoBuild();
            confg = b.solve(routers);
        } //FIXME TODO ISIS
        //MULTI:
        switch (generate.protocol){
            case OSPF -> {
                var c = new ospfRanAttriGen();
                c.generate(confg, routers);
            }
            case BABEL -> {
                var b = new babelRanAttriGen();
                b.generate(confg, routers);
            }
            //FIXME TODO ISIS
        }

        var confgAttrStr = confg.toString();
//        if (dumpInfo != null){
//            dumpInfo.put("configGraph", confg.toDot(false));
//            dumpInfo.put("configGraphAttr", confg.toString());
//        }
        if (verbose){
            System.out.println("config graph");
            System.out.println(confgAttrStr);
        }

        if (dumpInfo != null){
            switch (generate.protocol) {
                case OSPF -> {
                    dumpInfo.put("topoDot", dumpGraphOspfTrans(routers, networkId, confg));
                }
                case BABEL -> {
                    dumpInfo.put("topoDot", dumpGraphBABELTrans(routers, networkId, confg));
                }
            }
        }
        return new Pair<>(routers, confg);
    }

    public static ConfGraph genGraph(int totalRouter, int areaCount, int mxDegree, int abrRatio, boolean verbose, ObjectNode dumpInfo){
        List<Router> routers = null;
        List<Router_ISIS> routersIsis = null;
        String baseGraphStr = null;
        //MULTI:
        switch (generate.protocol){
            case OSPF -> {
                var ran = new ospfRanBaseGen();
                routers = ran.generate(totalRouter, areaCount, mxDegree, abrRatio);
                baseGraphStr = dumpGraphOspf(routers, ran.networkId);
            }
            case RIP, BABEL -> {
                var ran = new ripRanBaseGen();
                routers = ran.generate(totalRouter, areaCount, mxDegree, abrRatio);
                baseGraphStr = dumpGraphRip(routers, ran);
            }
            case ISIS -> {
                var ran = new isisRanBaseGen();
                routersIsis = ran.generate(totalRouter, areaCount, mxDegree, abrRatio);
                baseGraphStr = dumpGraphIsis(routersIsis, ran);
            }
            case OpenFabric -> {
                var ran = new openfabricRanBaseGen();
                routersIsis = ran.generate(totalRouter, areaCount, mxDegree, abrRatio);
                baseGraphStr = dumpGraphOpenfabric(routersIsis, ran);
            }

        }
        if (dumpInfo != null) dumpInfo.put("routerGraph", TextNode.valueOf(baseGraphStr));
        if (verbose){
            System.out.println(baseGraphStr);
        }
        
        ConfGraph confg = null;
        if(generate.protocol == generate.Protocol.ISIS || generate.protocol == generate.Protocol.OpenFabric){
            var b = new topoBuild_ISIS();
            confg = b.solve(routersIsis);
        }
        else{
            var b = new topoBuild();
            confg = b.solve(routers);
        }
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
            case ISIS -> {
                var c = new isisRanAttriGen();
                c.generate(confg, routersIsis);
            }
            case OpenFabric -> {
                var c = new openfabricRanAttriGen();
                c.generate(confg, routersIsis);
            }
            case BABEL -> {
                var c = new babelRanAttriGen();
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
