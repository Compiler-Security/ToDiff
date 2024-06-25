package org.generator.lib.topo.pass.attri;

import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.ospf.OSPF;
import org.generator.lib.item.conf.node.ospf.OSPFDaemon;
import org.generator.lib.item.conf.node.ospf.OSPFIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.util.collections.Pair;
import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;
import org.generator.util.ran.ranHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ranAttriGen implements genAttri {

    List<OSPF> ospfs;
    List<OSPFDaemon> ospfDaemons;
    Map<ID, List<OSPFIntf>> oIntfsInArea;
    Map<IPRange, List<OSPFIntf>> oIntfsInSubNet;

    ConfGraph g;

    public ranAttriGen(){
        ospfs = new ArrayList<>();
        ospfDaemons = new ArrayList<>();
        oIntfsInArea = new HashMap<>();
        oIntfsInSubNet = new HashMap<>();
    }
    @Override
    public void generate(ConfGraph g, List<Router> routers) {
        //build each router and fill area, router_id
        for(int i = 0; i < routers.size(); i++){
            var r = routers.get(i);
            var r_name = NodeGen.getRouterName(i);
            var ospf_name = NodeGen.getOSPFName(r_name);
            var ospf_daemon_name = NodeGen.getOSPFDaemonName(ospf_name);
            var ospf = new OSPF(ospf_name);
            g.addNode(ospf);
            ospfs.add(ospf);
            var ospf_daemon = new OSPFDaemon(ospf_daemon_name);
            ospfDaemons.add(ospf_daemon);
            g.addNode(ospf_daemon);
            g.addOSPFRelation(ospf_name, r_name);
            g.addOSPFDaemonRelation(ospf_name, ospf_daemon_name);
            ospf.setRouterId(ID.of(i + 1)); //router id is not allowed to 0.0.0.0
            for(int j = 0; j < r.intfs.size(); j++){
                var intf_name  = NodeGen.getIntfName(r_name, j);
                var ospf_intf_name = NodeGen.getOSPFIntfName(intf_name);
                var ospf_intf = new OSPFIntf(ospf_intf_name);
                g.addNode(ospf_intf);
                g.addOSPFIntfRelation(ospf_intf_name, intf_name);
                var area = ID.of(r.intfs.get(j).area);
                ospf_intf.setArea(area);
                if (!oIntfsInArea.containsKey(area)) {
                    oIntfsInArea.put(area, new ArrayList<>());
                }
                oIntfsInArea.get(area).add(ospf_intf);
            }
        }
        //fill each network IP
        for(var s: g.getSwitches()){
            var baseID = ranHelper.randomID();
            //FIXME we should consider the IPRange is big enough
            var prefix = ranHelper.randomInt(5, 30);
            var ipRange = IPRange.of(baseID.toLong(), prefix);
            var baseNum = ipRange.getAddressOfIp().IDtoLong();
            for(var intf: g.<Intf>getDstsByType(s.getName(), RelationEdge.EdgeType.INTF)){
                for(var target_intf: g.<Intf>getDstsByType(intf.getName(), RelationEdge.EdgeType.LINK)){
                    target_intf.setIp(IP.of(baseNum++, prefix));
                    if (!oIntfsInSubNet.containsKey(ipRange)){
                        oIntfsInSubNet.put(ipRange, new ArrayList<>());
                    }
                    var target_ospf_intf = g.getOSPFIntf(NodeGen.getOSPFIntfName(target_intf.getName()));
                    oIntfsInSubNet.get(ipRange).add(target_ospf_intf);
                }
            }
        }
        //TODO mutate other conf fields
        generatePart2(g);
    }

    void fillOspfDaemon(OSPFDaemon daemon){
        //TODO 6-25
    }

    void fillOspf(OSPF ospf){
        //TODO 6-25
        //we should know whether these filed should be the same in the same area?
    }

    void fillOSPFs(){

    }

    void fillOspfIntfs(List<OSPFIntf> ospfIntfs){
        //TODO 6-25
        //we should fill the same args for ospfIntfs in the same area

    }


    void generatePart2(ConfGraph g){
        for(var r: g.getRouters()){
            var ospf = g.getOspfOfRouter(r.getName());
            ospf.setInitDelay(0);
            ospf.setMinHoldTime(0);
            ospf.setMaxHoldTime(0);
            var ospf_daemon = g.getOSPFDaemonOfOSPF(ospf.getName());

            for(var ospf_intf: g.getOSPFIntfOfRouter(r.getName())){
                ospf_intf.setHelloMulti(10);
                ospf_intf.setDeadInterval(1);
                ospf_intf.setHelloInterval(0);
                ospf_intf.setPriority(ranHelper.randomInt(0, 255));
                //ospf_intf.setCost(ranHelper.randomInt(1, 1000));
            }
        }
    }
}
