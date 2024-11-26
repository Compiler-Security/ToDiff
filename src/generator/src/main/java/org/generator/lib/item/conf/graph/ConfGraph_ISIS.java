package org.generator.lib.item.conf.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.generator.lib.item.conf.edge.RelationEdge_ISIS;
import org.generator.lib.item.conf.node.AbstractNode_ISIS;
import org.generator.lib.item.conf.node.NodeGen_ISIS;
import org.generator.lib.item.conf.node.NodeType_ISIS;
import org.generator.lib.item.conf.node.isis.ISIS;
import org.generator.lib.item.conf.node.isis.ISISAreaSum;
import org.generator.lib.item.conf.node.isis.ISISDaemon;
import org.generator.lib.item.conf.node.isis.ISISIntf;
import org.generator.lib.item.conf.node.phy.Intf_ISIS;
import org.generator.lib.item.conf.node.phy.Router_ISIS;
import org.generator.lib.item.conf.node.phy.Switch_ISIS;
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
public class ConfGraph_ISIS extends AbstractRelationGraph_ISIS {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfGraph_ISIS that = (ConfGraph_ISIS) o;
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
    public ConfGraph_ISIS(){
        super();
    }
    public ConfGraph_ISIS(String r_name){
        this.r_name = r_name;
    }

    /**
     * build a new phy graph from this graph
     * @return
     */
    public ConfGraph_ISIS copyPhyGraph_ISIS(){
        var g = new ConfGraph_ISIS(r_name);
        g.addNode(new Router_ISIS(r_name));
        for(var intf: getIntfsOfRouter(r_name)){
            var intf_new = new Intf_ISIS(intf.getName());
            intf_new.setUp(intf.isUp());
            g.addNode(intf_new);
            g.addIntfRelation(intf.getName(), r_name);
        }
        return g;
    }

    public ConfGraph_ISIS viewConfGraphOfRouter(String r_name){
        var g = new ConfGraph_ISIS(r_name);
        g.addNode(getNodeNotNull(r_name));
        var ospf_name = NodeGen_ISIS.getISISName(r_name);
        var ospf_daemon_name = NodeGen_ISIS.getISISDaemonName(ospf_name);
        if (containsNode(ospf_name)){
            g.addNode(getNodeNotNull(ospf_name));
            g.addISISRelation(ospf_name, r_name);
            for(var areaSum: getISISAreaSumOfISIS(ospf_name)){
                g.addNode(areaSum);
                g.addISISAreaSumRelation(areaSum.getName(), ospf_name);
            }
        }
        if (containsNode(ospf_daemon_name)){
            g.addNode(getNodeNotNull(ospf_daemon_name));
            g.addISISDaemonRelation(ospf_daemon_name, r_name);
        }
        for(var intf: getIntfsOfRouter(r_name)){
            g.addNode(intf);
            g.addIntfRelation(intf.getName(), r_name);
            var ospf_intf_name = NodeGen_ISIS.getISISIntfName(intf.getName());
            if (containsNode(ospf_intf_name)){
                g.addNode(getNodeNotNull(ospf_intf_name));
                g.addISISIntfRelation(ospf_intf_name, intf.getName());
            }
        }
        return g;
    }

    public ObjectNode toJson(){
        var jsonNode = new ObjectMapper().createObjectNode();
        for(var node: getNodes()){
            jsonNode.set(node.getName(), node.getJsonNode());
        }
        return jsonNode;
    }

    @Override
    public String toString() {
        return toJson().toPrettyString();
    }

    private Pair<AbstractNode_ISIS, Boolean> createNode(AbstractNode_ISIS node){
        var res = addNode(node);
        assert res == ExecStat.SUCC;
        return new Pair<>(node, false);
    }
    public Pair<AbstractNode_ISIS, Boolean> getOrCrateNode(String node_name, NodeType_ISIS node_type){
        if (containsNode(node_name)){
            return new Pair<>(getNode(node_name).get(), true);
        }else{
            return createNode(NodeGen_ISIS.new_node(node_name, node_type));
        }
    }

    public <T extends AbstractNode_ISIS> Pair<T, Boolean> getOrCreateNode(String node_name, NodeType_ISIS nodeType){
        if (containsNode(node_name)){
            return new Pair<>((T)getNode(node_name).get(), true);
        }else{
            var node = NodeGen_ISIS.<T>newNode(node_name, nodeType);
            addNode(node);
            return new Pair<>(node, false);
        }
    }

    public <T extends  AbstractNode_ISIS> T getNodeNotNull(String node_name){
        return (T) getNode(node_name).get();
    }

     public <T> Set<T> getDstsByType(String nodes, RelationEdge_ISIS.EdgeType typ){
        return getEdgesByType(nodes, typ).stream().map(s->(T) s.getDst()).collect(Collectors.toSet());
     }

     public ISIS getISISOfRouter(String r_name){
    return this.<ISIS>getDstsByType(r_name, RelationEdge_ISIS.EdgeType.ISIS).stream().findFirst().get();
     }

     public Set<Intf_ISIS> getIntfsOfRouter(String r_name){
        return this.<Intf_ISIS>getDstsByType(r_name, RelationEdge_ISIS.EdgeType.INTF);
     }
     public Set<ISISIntf> getISISIntfOfRouter(String r_name){
        return this.<Intf_ISIS>getDstsByType(r_name, RelationEdge_ISIS.EdgeType.INTF).stream().map(x->this.<ISISIntf>getDstsByType(x.getName(), RelationEdge_ISIS.EdgeType.ISISINTF)).flatMap(Collection::stream).collect(Collectors.toSet());
     }

     public Set<ISISAreaSum> getISISAreaSumOfISIS(String isis_name){
        return this.<ISISAreaSum> getDstsByType(isis_name, RelationEdge_ISIS.EdgeType.ISISAREASUM);
     }
    public ISISIntf getISISIntf(String nodeName) {
        return (ISISIntf) getNode(nodeName).get();
    }

    public ISISDaemon getISISDaemonOfISIS(String ospf_name){
        return this.<ISISDaemon>getDstsByType(ospf_name, RelationEdge_ISIS.EdgeType.ISISDAEMON).stream().findFirst().get();
    }

    public Intf_ISIS getIntf(String nodeName){
        return (Intf_ISIS) getNode(nodeName).get();
    }

    public ExecStat addISISRelation(String isis_name, String phynode_name){
        var res1 = addEdge(phynode_name, isis_name, RelationEdge_ISIS.EdgeType.ISIS);
        var res2 =  addEdge(isis_name, phynode_name, RelationEdge_ISIS.EdgeType.PhyNODE);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }
    public ExecStat addISISIntfRelation(String isis_intf_name, String intf_name){
        var res1 = addEdge(isis_intf_name, intf_name, RelationEdge_ISIS.EdgeType.INTF);
        var res2 = addEdge(intf_name, isis_intf_name, RelationEdge_ISIS.EdgeType.ISISINTF);
        return ExecStat.SUCC;
    }

    public ExecStat addIntfRelation(String intfName, String routerName){
        addEdge(intfName, routerName, RelationEdge_ISIS.EdgeType.PhyNODE);
        addEdge(routerName, intfName, RelationEdge_ISIS.EdgeType.INTF);
        return ExecStat.SUCC;
    }

    public ExecStat addISISAreaRelation(String area_name, String intf_name){
        var res1 = addEdge(area_name, intf_name, RelationEdge_ISIS.EdgeType.INTF);
        var res2 = addEdge(intf_name, area_name, RelationEdge_ISIS.EdgeType.ISISAREA);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }

    public void addIntfLink(String intf1_name, String intf2_name){
        addEdge(intf1_name, intf2_name, RelationEdge_ISIS.EdgeType.LINK);
        addEdge(intf2_name, intf1_name, RelationEdge_ISIS.EdgeType.LINK);
    }

    public ExecStat addISISAreaSumRelation(String areaSum_name, String ospf_name){
        var res1 = addEdge(areaSum_name, ospf_name, RelationEdge_ISIS.EdgeType.ISIS);
        var res2 = addEdge(ospf_name, areaSum_name, RelationEdge_ISIS.EdgeType.ISISAREASUM);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }

    public ExecStat addISISDaemonRelation(String ospf_name, String ospf_daemon_name){
        var res1 = addEdge(ospf_name, ospf_daemon_name, RelationEdge_ISIS.EdgeType.ISISDAEMON);
        var res2 = addEdge(ospf_daemon_name, ospf_name, RelationEdge_ISIS.EdgeType.ISIS);
        assert res1.join(res2) == ExecStat.SUCC;
        return ExecStat.SUCC;
    }

    public boolean containsISISOfRouter(String r_name){
        return this.containsNode(NodeGen_ISIS.getISISName(r_name));
    }

    public List<Switch_ISIS> getSwitches(){
        return getNodes().stream().filter(node -> node.getNodeType() == NodeType_ISIS.Switch).map(node -> (Switch_ISIS)node).collect(Collectors.toList());
    }

    public List<Router_ISIS> getRouters(){
        return getNodesByType(NodeType_ISIS.Router);
    }

    public <T> List<T> getNodesByType(NodeType_ISIS type){
        return getNodes().stream().filter(node -> node.getNodeType().equals(type)).map(node-> (T) node).collect(Collectors.toList());
    }
    @Override
    public String toDot(boolean verbose) {
        Graph graph = new SingleGraph("RelationGraph");
        BiFunction<String, String, String> node_label_f = (String node_name, String node_attri)-> {
            if (!verbose) return node_name;
            else{
                return String.format("%s\\n[%s]", node_name, node_attri);
            }
        };
        for (var node: getNodes()){
            graph.addNode(node.getName()).setAttribute("label", node_label_f.apply(node.getName(), node.getNodeAtrriStr()));
        }

        for(var src:getNodes()){
            for(var edge: getOutEdgesOf(src)){
                var gedge = graph.addEdge(String.format("%s->%s", edge.getSrc(), edge.getDst()), edge.getSrc().toString(), edge.getDst().toString(), true);
                if (verbose){
                    gedge.setAttribute("label", edge.getType().toString());
                    gedge.setAttribute("fontsize", 8);
                }
            }
        }

        FileSinkDOT fileSinkDOT = new FileSinkDOT(true);
        StringWriter stringWriter = new StringWriter();
        try {
            fileSinkDOT.writeAll(graph, stringWriter);
        }catch (IOException e){
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    private void dfsNode(AbstractNode_ISIS node, Map<String, String> m, HashSet<AbstractNode_ISIS> visited, Predicate<AbstractNode_ISIS> filter){
        if (visited.contains(node)) return;
        visited.add(node);
        if (!filter.test(node)) return;
        m.put(node.getName(), node.getNodeAtrriStr());
        getSuccsOf(node).forEach(x -> dfsNode(x, m, visited, filter));
    }
    public void dumpOfRouter(String r_name){
        assert containsNode(r_name);
        Map<String, String> m = new TreeMap<>();
        HashSet<AbstractNode_ISIS> visited = new HashSet<>();
        dfsNode(getNode(r_name).get(), m, visited, x -> x.getName().contains(r_name));
        for (var entry: m.entrySet()){
            System.out.println(String.format("%s : %s", entry.getKey(), entry.getValue()));
        }
    }
}
