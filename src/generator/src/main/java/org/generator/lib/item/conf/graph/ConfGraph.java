package org.generator.lib.item.conf.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.babel.BABEL;
import org.generator.lib.item.conf.node.babel.BABELIntf;
import org.generator.lib.item.conf.node.ospf.OSPF;
import org.generator.lib.item.conf.node.ospf.OSPFAreaSum;
import org.generator.lib.item.conf.node.ospf.OSPFDaemon;
import org.generator.lib.item.conf.node.ospf.OSPFIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.phy.Router;
import org.generator.lib.item.conf.node.phy.Switch;
import org.generator.lib.item.conf.node.rip.RIP;
import org.generator.lib.item.conf.node.rip.RIPIntf;
import org.generator.lib.item.conf.node.isis.ISIS;
import org.generator.lib.item.conf.node.isis.ISISDaemon;
import org.generator.lib.item.conf.node.isis.ISISIntf;
import org.generator.lib.item.conf.node.openfabric.FABRIC;
import org.generator.lib.item.conf.node.openfabric.FABRICDaemon;
import org.generator.lib.item.conf.node.openfabric.FABRICIntf;
import org.generator.util.collections.Pair;
import org.generator.util.exec.ExecStat;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkDOT;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//TODO clean this code
public class ConfGraph extends AbstractRelationGraph {

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ConfGraph that = (ConfGraph) o;
        return that.toJson().equals(this.toJson());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toJson().hashCode());
    }

    public String getR_name() {
        return r_name;
    }

    public void setR_name(String r_name) {
        this.r_name = r_name;
    }

    String r_name;

    public ConfGraph() {
        super();
    }

    public ConfGraph(String r_name) {
        this.r_name = r_name;
    }

    /**
     * build a new phy graph from this graph
     * 
     * @return
     */
    public ConfGraph copyPhyGraph() {
        var g = new ConfGraph(r_name);
        g.addNode(new Router(r_name));
        for (var intf : getIntfsOfRouter(r_name)) {
            var intf_new = new Intf(intf.getName());
            intf_new.setUp(intf.isUp());
            g.addNode(intf_new);
            g.addIntfRelation(intf.getName(), r_name);
        }
        return g;
    }

    // MULTI:
    private ConfGraph viewConfGraphOfRouterOSPF(String r_name) {
        var g = new ConfGraph(r_name);
        g.addNode(getNodeNotNull(r_name));
        var ospf_name = NodeGen.getOSPFName(r_name);
        var ospf_daemon_name = NodeGen.getOSPFDaemonName(ospf_name);
        if (containsNode(ospf_name)) {
            g.addNode(getNodeNotNull(ospf_name));
            g.addOSPFRelation(ospf_name, r_name);
            for (var areaSum : getOSPFAreaSumOfOSPF(ospf_name)) {
                g.addNode(areaSum);
                g.addOSPFAreaSumRelation(areaSum.getName(), ospf_name);
            }
        }
        if (containsNode(ospf_daemon_name)) {
            g.addNode(getNodeNotNull(ospf_daemon_name));
            g.addOSPFDaemonRelation(ospf_daemon_name, r_name);
        }
        for (var intf : getIntfsOfRouter(r_name)) {
            g.addNode(intf);
            g.addIntfRelation(intf.getName(), r_name);
            var ospf_intf_name = NodeGen.getOSPFIntfName(intf.getName());
            if (containsNode(ospf_intf_name)) {
                g.addNode(getNodeNotNull(ospf_intf_name));
                g.addOSPFIntfRelation(ospf_intf_name, intf.getName());
            }
        }
        return g;
    }

    private ConfGraph viewConfGraphOfRouterRIP(String r_name) {
        var g = new ConfGraph(r_name);
        g.addNode(getNodeNotNull(r_name));
        var rip_name = NodeGen.getRIPName(r_name);
        if (containsNode(rip_name)) {
            g.addNode(getNodeNotNull(rip_name));
            g.addRIPRelation(rip_name, r_name);
        }
        for (var intf : getIntfsOfRouter(r_name)) {
            g.addNode(intf);
            g.addIntfRelation(intf.getName(), r_name);
            var rip_intf_name = NodeGen.getRIPIntfName(intf.getName());
            if (containsNode(rip_intf_name)) {
                g.addNode(getNodeNotNull(rip_intf_name));
                g.addRIPIntfRelation(rip_intf_name, intf.getName());
            }
        }
        return g;
    }

    private ConfGraph viewConfGraphOfRouterISIS(String r_name) {
        var g = new ConfGraph(r_name);
        g.addNode(getNodeNotNull(r_name));
        var isis_name = NodeGen.getISISName(r_name);
        var isis_daemon_name = NodeGen.getISISDaemonName(isis_name);
        if (containsNode(isis_name)) {
            g.addNode(getNodeNotNull(isis_name));
            g.addISISRelation(isis_name, r_name);
        }
        if (containsNode(isis_daemon_name)) {
            g.addNode(getNodeNotNull(isis_daemon_name));
            g.addISISDaemonRelation(isis_daemon_name, r_name);
        }
        for (var intf : getIntfsOfRouter(r_name)) {
            g.addNode(intf);
            g.addIntfRelation(intf.getName(), r_name);
            var isis_intf_name = NodeGen.getISISIntfName(intf.getName());
            if (containsNode(isis_intf_name)) {
                g.addNode(getNodeNotNull(isis_intf_name));
                g.addISISIntfRelation(isis_intf_name, intf.getName());
            }
        }
        return g;
    }

    private ConfGraph viewConfGraphOfRouterBABEL(String r_name) {
        var g = new ConfGraph(r_name);
        g.addNode(getNodeNotNull(r_name));
        var babel_name = NodeGen.getBABELName(r_name);
        if (containsNode(babel_name)) {
            g.addNode(getNodeNotNull(babel_name));
            g.addBABELRelation(babel_name, r_name);
        }
        for (var intf : getIntfsOfRouter(r_name)) {
            g.addNode(intf);
            g.addIntfRelation(intf.getName(), r_name);
            var babel_intf_name = NodeGen.getBABELIntfName(intf.getName());
            if (containsNode(babel_intf_name)) {
                g.addNode(getNodeNotNull(babel_intf_name));
                g.addBABELIntfRelation(babel_intf_name, intf.getName());
            }
        }
        return g;
    }


    private ConfGraph viewConfGraphOfRouterOpenFabric(String r_name) {
        var g = new ConfGraph(r_name);
        g.addNode(getNodeNotNull(r_name));
        var openfabric_name = NodeGen.getOpenFabricName(r_name);
        var openfabric_daemon_name = NodeGen.getOpenFabricDaemonName(openfabric_name);
        if (containsNode(openfabric_name)) {
            g.addNode(getNodeNotNull(openfabric_name));
            g.addOpenFabricRelation(openfabric_name, r_name);
        }
        if (containsNode(openfabric_daemon_name)) {
            g.addNode(getNodeNotNull(openfabric_daemon_name));
            g.addOpenFabricDaemonRelation(openfabric_daemon_name, r_name);
        }
        for (var intf : getIntfsOfRouter(r_name)) {
            g.addNode(intf);
            g.addIntfRelation(intf.getName(), r_name);
            var openfabric_intf_name = NodeGen.getOpenFabricIntfName(intf.getName());
            if (containsNode(openfabric_intf_name)) {
                g.addNode(getNodeNotNull(openfabric_intf_name));
                g.addOpenFabricIntfRelation(openfabric_intf_name, intf.getName());
            }
        }
        return g;
    }


    public ConfGraph viewConfGraphOfRouter(String r_name) {
        switch (generate.protocol) {
            case OSPF -> {
                return viewConfGraphOfRouterOSPF(r_name);
            }
            case RIP -> {
                return viewConfGraphOfRouterRIP(r_name);
            }
            case ISIS -> {
                return viewConfGraphOfRouterISIS(r_name);
            }
            case OpenFabric -> {
                return viewConfGraphOfRouterOpenFabric(r_name);
            }
            case BABEL -> {
                return viewConfGraphOfRouterBABEL(r_name);
            }

            //MULTI:
        }
        assert false;
        return null;
    }

    public ObjectNode toJson() {
        var jsonNode = new ObjectMapper().createObjectNode();
        for (var node : getNodes()) {
            jsonNode.set(node.getName(), node.getJsonNode());
        }
        return jsonNode;
    }

    @Override
    public String toString() {
        return toJson().toPrettyString();
    }

    private Pair<AbstractNode, Boolean> createNode(AbstractNode node) {
        var res = addNode(node);
        assert res == ExecStat.SUCC;
        return new Pair<>(node, false);
    }

    public Pair<AbstractNode, Boolean> getOrCrateNode(String node_name, NodeType node_type) {
        if (containsNode(node_name)) {
            return new Pair<>(getNode(node_name).get(), true);
        } else {
            return createNode(NodeGen.new_node(node_name, node_type));
        }
    }

    public <T extends AbstractNode> Pair<T, Boolean> getOrCreateNode(String node_name, NodeType nodeType) {
        if (containsNode(node_name)) {
            return new Pair<>((T) getNode(node_name).get(), true);
        } else {
            var node = NodeGen.<T>newNode(node_name, nodeType);
            addNode(node);
            return new Pair<>(node, false);
        }
    }

    // =================GET==========================
    public <T> List<T> getNodesByType(NodeType type) {
        return getNodes().stream().filter(node -> node.getNodeType().equals(type)).map(node -> (T) node)
                .collect(Collectors.toList());
    }

    public <T extends AbstractNode> T getNodeNotNull(String node_name) {
        return (T) getNode(node_name).get();
    }

    public <T> Set<T> getDstsByType(String nodes, RelationEdge.EdgeType typ) {
        return getEdgesByType(nodes, typ).stream().map(s -> (T) s.getDst()).collect(Collectors.toSet());
    }

    public List<Switch> getSwitches() {
        return getNodes().stream().filter(node -> node.getNodeType() == NodeType.Switch).map(node -> (Switch) node)
                .collect(Collectors.toList());
    }

    public List<Router> getRouters() {
        return getNodesByType(NodeType.Router);
    }

    public Set<Intf> getIntfsOfRouter(String r_name) {
        return this.<Intf>getDstsByType(r_name, RelationEdge.EdgeType.INTF);
    }

    public Intf getIntf(String nodeName) {
        return (Intf) getNode(nodeName).get();
    }

    // ------------------OSPF----------------------------
    public OSPF getOspfOfRouter(String r_name) {
        return this.<OSPF>getDstsByType(r_name, RelationEdge.EdgeType.OSPF).stream().findFirst().get();
    }

    public Set<OSPFIntf> getOSPFIntfOfRouter(String r_name) {
        return this.<Intf>getDstsByType(r_name, RelationEdge.EdgeType.INTF).stream()
                .map(x -> this.<OSPFIntf>getDstsByType(x.getName(), RelationEdge.EdgeType.OSPFINTF))
                .flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public Set<OSPFAreaSum> getOSPFAreaSumOfOSPF(String ospf_name) {
        return this.<OSPFAreaSum>getDstsByType(ospf_name, RelationEdge.EdgeType.OSPFAREASUM);
    }

    public OSPFIntf getOSPFIntf(String nodeName) {
        return (OSPFIntf) getNode(nodeName).get();
    }

    public OSPFDaemon getOSPFDaemonOfOSPF(String ospf_name) {
        return this.<OSPFDaemon>getDstsByType(ospf_name, RelationEdge.EdgeType.OSPFDAEMON).stream().findFirst().get();
    }

    // ------------------RIP---------------------------------
    public RIP getRipOfRouter(String r_name) {
        return this.<RIP>getDstsByType(r_name, RelationEdge.EdgeType.RIP).stream().findFirst().get();
    }

    public Set<RIPIntf> getRIPIntfOfRouter(String r_name) {
        return this.<Intf>getDstsByType(r_name, RelationEdge.EdgeType.INTF).stream()
                .map(x -> this.<RIPIntf>getDstsByType(x.getName(), RelationEdge.EdgeType.RIPINTF))
                .flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public RIPIntf getRIPIntf(String nodeName) {
        return (RIPIntf) getNode(nodeName).get();
    }

    // MULTI:
    // ------------------ISIS----------------------------
    public ISIS getISISOfRouter(String r_name) {
        return this.<ISIS>getDstsByType(r_name, RelationEdge.EdgeType.ISIS).stream().findFirst().get();
    }

    public Set<ISISIntf> getISISIntfOfRouter(String r_name) {
        return this.<Intf>getDstsByType(r_name, RelationEdge.EdgeType.INTF).stream()
                .map(x -> this.<ISISIntf>getDstsByType(x.getName(), RelationEdge.EdgeType.ISISINTF))
                .flatMap(Collection::stream).collect(Collectors.toSet());
    }


    public ISISIntf getISISIntf(String nodeName) {
        return (ISISIntf) getNode(nodeName).get();
    }

    public ISISDaemon getISISDaemonOfISIS(String isis_name) {
        return this.<ISISDaemon>getDstsByType(isis_name, RelationEdge.EdgeType.ISISDAEMON).stream().findFirst()
                .get();
    }

    // ------------------BABEL---------------------------------
    public BABEL getBABELOfRouter(String r_name) {
        return this.<BABEL>getDstsByType(r_name, RelationEdge.EdgeType.BABEL).stream().findFirst().get();
    }

    public Set<BABELIntf> getBABELIntfOfRouter(String r_name) {
        return this.<Intf>getDstsByType(r_name, RelationEdge.EdgeType.INTF).stream()
                .map(x -> this.<BABELIntf>getDstsByType(x.getName(), RelationEdge.EdgeType.BABELINTF))
                .flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public BABELIntf getBABELIntf(String nodeName) {
        return (BABELIntf) getNode(nodeName).get();
    }

    // ------------------OpenFabric----------------------------

    public FABRIC getOpenFabricOfRouter(String r_name) {
        return this.<FABRIC>getDstsByType(r_name, RelationEdge.EdgeType.FABRIC).stream().findFirst().get();
    }

    public Set<FABRICIntf> getOpenFabricIntfOfRouter(String r_name) {
        return this.<Intf>getDstsByType(r_name, RelationEdge.EdgeType.INTF).stream()
                .map(x -> this.<FABRICIntf>getDstsByType(x.getName(), RelationEdge.EdgeType.FABRICINTF))
                .flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public FABRICIntf getOpenFabricIntf(String nodeName) {
        return (FABRICIntf) getNode(nodeName).get();
    }

    public FABRICDaemon getOpenFabricDaemonOfOpenFabric(String openfabric_name) {
        return this.<FABRICDaemon>getDstsByType(openfabric_name, RelationEdge.EdgeType.FABRICDAEMON).stream().findFirst()
                .get();
    }

    // =================ADD==========================
    public void addIntfLink(String intf1_name, String intf2_name) {
        addEdge(intf1_name, intf2_name, RelationEdge.EdgeType.LINK);
        addEdge(intf2_name, intf1_name, RelationEdge.EdgeType.LINK);
    }

    public ExecStat addIntfRelation(String intfName, String routerName) {
        addEdge(intfName, routerName, RelationEdge.EdgeType.PhyNODE);
        addEdge(routerName, intfName, RelationEdge.EdgeType.INTF);
        return ExecStat.SUCC;
    }

    // ------------------OSPF-------------------------
    public ExecStat addOSPFRelation(String ospf_name, String phynode_name) {
        var res1 = addEdge(phynode_name, ospf_name, RelationEdge.EdgeType.OSPF);
        var res2 = addEdge(ospf_name, phynode_name, RelationEdge.EdgeType.PhyNODE);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }

    public ExecStat addOSPFIntfRelation(String ospf_intf_name, String intf_name) {
        var res1 = addEdge(ospf_intf_name, intf_name, RelationEdge.EdgeType.INTF);
        var res2 = addEdge(intf_name, ospf_intf_name, RelationEdge.EdgeType.OSPFINTF);
        return ExecStat.SUCC;
    }

    public ExecStat addOSPFAreaRelation(String area_name, String intf_name) {
        var res1 = addEdge(area_name, intf_name, RelationEdge.EdgeType.INTF);
        var res2 = addEdge(intf_name, area_name, RelationEdge.EdgeType.OSPFAREA);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }

    public ExecStat addOSPFAreaSumRelation(String areaSum_name, String ospf_name) {
        var res1 = addEdge(areaSum_name, ospf_name, RelationEdge.EdgeType.OSPF);
        var res2 = addEdge(ospf_name, areaSum_name, RelationEdge.EdgeType.OSPFAREASUM);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }

    public ExecStat addOSPFDaemonRelation(String ospf_name, String ospf_daemon_name) {
        var res1 = addEdge(ospf_name, ospf_daemon_name, RelationEdge.EdgeType.OSPFDAEMON);
        var res2 = addEdge(ospf_daemon_name, ospf_name, RelationEdge.EdgeType.OSPF);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }

    // -----------------RIP--------------------
    public ExecStat addRIPRelation(String rip_name, String phynode_name) {
        var res1 = addEdge(phynode_name, rip_name, RelationEdge.EdgeType.RIP);
        var res2 = addEdge(rip_name, phynode_name, RelationEdge.EdgeType.PhyNODE);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }

    public ExecStat addRIPIntfRelation(String rip_intf_name, String intf_name) {
        var res1 = addEdge(rip_intf_name, intf_name, RelationEdge.EdgeType.INTF);
        var res2 = addEdge(intf_name, rip_intf_name, RelationEdge.EdgeType.RIPINTF);
        return ExecStat.SUCC;
    }

    // MULTI:

    // ----------------ISIS-------------------

    public ExecStat addISISRelation(String isis_name, String phynode_name){
        var res1 = addEdge(phynode_name, isis_name, RelationEdge.EdgeType.ISIS);
        var res2 =  addEdge(isis_name, phynode_name, RelationEdge.EdgeType.PhyNODE);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }
    public ExecStat addISISIntfRelation(String isis_intf_name, String intf_name){
        var res1 = addEdge(isis_intf_name, intf_name, RelationEdge.EdgeType.INTF);
        var res2 = addEdge(intf_name, isis_intf_name, RelationEdge.EdgeType.ISISINTF);
        return ExecStat.SUCC;
    }

    public ExecStat addISISDaemonRelation(String isis_name, String isis_daemon_name){
        var res1 = addEdge(isis_name, isis_daemon_name, RelationEdge.EdgeType.ISISDAEMON);
        var res2 = addEdge(isis_daemon_name, isis_name, RelationEdge.EdgeType.ISIS);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }

    // -----------------BABEL--------------------
    public ExecStat addBABELRelation(String babel_name, String phynode_name) {
        var res1 = addEdge(phynode_name, babel_name, RelationEdge.EdgeType.BABEL);
        var res2 = addEdge(babel_name, phynode_name, RelationEdge.EdgeType.PhyNODE);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }

    public ExecStat addBABELIntfRelation(String babel_intf_name, String intf_name) {
        var res1 = addEdge(babel_intf_name, intf_name, RelationEdge.EdgeType.INTF);
        var res2 = addEdge(intf_name, babel_intf_name, RelationEdge.EdgeType.BABELINTF);
        return ExecStat.SUCC;
    }

    // ----------------OpenFabric-------------------


    public ExecStat addOpenFabricRelation(String openfabric_name, String phynode_name){
        var res1 = addEdge(phynode_name, openfabric_name, RelationEdge.EdgeType.FABRIC);
        var res2 =  addEdge(openfabric_name, phynode_name, RelationEdge.EdgeType.PhyNODE);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }

    public ExecStat addOpenFabricIntfRelation(String openfabric_intf_name, String intf_name){
        var res1 = addEdge(openfabric_intf_name, intf_name, RelationEdge.EdgeType.INTF);
        var res2 = addEdge(intf_name, openfabric_intf_name, RelationEdge.EdgeType.FABRICINTF);
        return ExecStat.SUCC;
    }

    public ExecStat addOpenFabricDaemonRelation(String openfabric_name, String openfabric_daemon_name){
        var res1 = addEdge(openfabric_name, openfabric_daemon_name, RelationEdge.EdgeType.FABRICDAEMON);
        var res2 = addEdge(openfabric_daemon_name, openfabric_name, RelationEdge.EdgeType.FABRIC);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }


    //Multi:
    public boolean containsOSPFOfRouter(String r_name) {
        return this.containsNode(NodeGen.getOSPFName(r_name));
    }

    public boolean containsRIPOfRouter(String r_name) {
        return this.containsNode(NodeGen.getRIPName(r_name));
    }

    public boolean containsISISOfRouter(String r_name){
        return this.containsNode(NodeGen.getISISName(r_name));
    }


    public boolean containsBABELOfRouter(String r_name) {
        return this.containsNode(NodeGen.getBABELName(r_name));
    }
    public boolean containsOpenFabricOfRouter(String r_name){
        return this.containsNode(NodeGen.getOpenFabricName(r_name));
    }

    @Override
    public String toDot(boolean verbose) {
        Graph graph = new SingleGraph("RelationGraph");
        BiFunction<String, String, String> node_label_f = (String node_name, String node_attri) -> {
            if (!verbose)
                return node_name;
            else {
                return String.format("%s\\n[%s]", node_name, node_attri);
            }
        };
        for (var node : getNodes()) {
            graph.addNode(node.getName()).setAttribute("label",
                    node_label_f.apply(node.getName(), node.getNodeAtrriStr()));
        }

        for (var src : getNodes()) {
            for (var edge : getOutEdgesOf(src)) {
                var gedge = graph.addEdge(String.format("%s->%s", edge.getSrc(), edge.getDst()),
                        edge.getSrc().toString(), edge.getDst().toString(), true);
                if (verbose) {
                    gedge.setAttribute("label", edge.getType().toString());
                    gedge.setAttribute("fontsize", 8);
                }
            }
        }

        FileSinkDOT fileSinkDOT = new FileSinkDOT(true);
        StringWriter stringWriter = new StringWriter();
        try {
            fileSinkDOT.writeAll(graph, stringWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    private void dfsNode(AbstractNode node, Map<String, String> m, HashSet<AbstractNode> visited,
            Predicate<AbstractNode> filter) {
        if (visited.contains(node))
            return;
        visited.add(node);
        if (!filter.test(node))
            return;
        m.put(node.getName(), node.getNodeAtrriStr());
        getSuccsOf(node).forEach(x -> dfsNode(x, m, visited, filter));
    }

    public void dumpOfRouter(String r_name) {
        assert containsNode(r_name);
        Map<String, String> m = new TreeMap<>();
        HashSet<AbstractNode> visited = new HashSet<>();
        dfsNode(getNode(r_name).get(), m, visited, x -> x.getName().contains(r_name));
        for (var entry : m.entrySet()) {
            System.out.println(String.format("%s : %s", entry.getKey(), entry.getValue()));
        }
    }
}
