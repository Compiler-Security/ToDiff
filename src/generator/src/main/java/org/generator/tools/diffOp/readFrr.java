package org.generator.tools.diffOp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.ospf.OSPF;
import org.generator.lib.item.conf.node.ospf.OSPFDaemon;
import org.generator.lib.item.conf.node.phy.Router;
import org.generator.util.net.ID;
import org.generator.util.net.IP;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class readFrr {

    HashMap<String, Object> read_from_json(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            HashMap<String, Object> data = mapper.readValue(new File(file_path), HashMap.class);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void read_daemon(ConfGraph confGraph, HashMap<String, Object> j){
        HashMap<String, Object> d = null;
        if (j.containsKey("ospf-daemon")){
            d = (HashMap<String, Object>) j.get("ospf-daemon");
        }else return;
        var router_node_name = router_name;
        var ospf_node_name = NodeGen.getOSPFName(router_name);
        var ospf_daemon_name = NodeGen.getOSPFDaemonName(ospf_node_name);
        confGraph.addNode(NodeGen.newNode(ospf_node_name, NodeType.OSPF));
        confGraph.addOSPFRelation(ospf_node_name, router_node_name);
        confGraph.addNode(NodeGen.newNode(ospf_daemon_name, NodeType.OSPFDaemon));
        confGraph.addOSPFDaemonRelation(ospf_node_name, ospf_daemon_name);
        var ospf = (OSPF) confGraph.getNodeNotNull(ospf_node_name);
        var daemon = (OSPFDaemon) confGraph.getNodeNotNull(ospf_daemon_name);
        {
            ospf.setRouterId(ID.of((String)d.get("routerId")));
            ospf.setStatus(OSPF.OSPF_STATUS.UP);
            //TODO ABRTYPE
            ospf.setInitDelay((int)d.get("spfScheduleDelayMsecs"));
            ospf.setMinHoldTime((int)d.get("holdtimeMinMsecs"));
            ospf.setMaxHoldTime((int)d.get("holdtimeMaxMsecs"));
        }
        {
            daemon.setMaxPaths((int)d.get("maximumPaths"));
            daemon.setWritemulti((int)d.get("writeMultiplier"));
            //TODO socketPerInterface
            //TODO buffersend
            //TODO bufferrecv
        }
        //TODO current all the r1-ospf-area's information can't be compared
    }

    void read_intf(ConfGraph confGraph, HashMap<String, Object> j, String intf_name){
        var intf_node_name = intf_name;
        confGraph.addNode(NodeGen.newNode(intf_node_name, NodeType.Intf));
        confGraph.addIntfRelation(intf_node_name, router_name);
        var intf_ospf_node_name = NodeGen.getOSPFIntfName(intf_node_name);
        confGraph.addNode(NodeGen.newNode(intf_ospf_node_name, NodeType.OSPFIntf));
        confGraph.addOSPFIntfRelation(intf_ospf_node_name, intf_node_name);
        var intf = confGraph.getIntf(intf_node_name);
        var ospf_intf = confGraph.getOSPFIntf(intf_ospf_node_name);
        {
            intf.setUp(true);
            intf.setIp(IP.of("%s/%d".formatted((String)j.get("ipAddress"), (int)j.get("ipAddressPrefixlen"))));
            intf.setPersudo(false);
        }
        {
            //TODO set Passive
            ospf_intf.setVrf(0);
            ospf_intf.setCost((int) j.get("cost"));
            ospf_intf.setArea(ID.of((String) j.get("area")));
            ospf_intf.setDeadInterval((int) j.get("timerDeadSecs"));
            ospf_intf.setHelloInterval((int) j.get("timerMsecs"));
            ospf_intf.setGRHelloDelay((int) j.get("grHelloDelaySecs"));
            ospf_intf.setPriority((int) j.get("priority"));
            ospf_intf.setRetansInter((int) j.get("timerRetransmitSecs"));
            ospf_intf.setTransDelay((int) j.get("transmitDelaySecs"));
        }
    }
    public ConfGraph solve(String file_path, String router_name){
        this.file_path = file_path;
        this.router_name = router_name;
        var j = (HashMap)read_from_json().get(router_name);
        var confGraph = new ConfGraph();
        confGraph.setR_name(router_name);
        confGraph.addNode(new Router(router_name));
        read_daemon(confGraph, j);
        if (j.containsKey("ospf-intfs")){
            var k = (HashMap) ((HashMap)j.get("ospf-intfs")).get("interfaces");
            for(var item:k.keySet()){
                read_intf(confGraph, (HashMap<String, Object>) k.get(item), (String)item);
            }
        }
        return confGraph;
    }

    String file_path, router_name;
}
