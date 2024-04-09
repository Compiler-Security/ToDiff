package org.generator.lib.topo.pass.attri;

import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.ospf.OSPF;
import org.generator.lib.item.conf.node.ospf.OSPFDaemon;
import org.generator.lib.item.conf.node.ospf.OSPFIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;
import org.generator.util.ran.ranHelper;

import java.util.List;

public class ranAttriGen implements genAttri {

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
            g.addNode(new OSPFDaemon(ospf_daemon_name));
            g.addOSPFRelation(ospf_name, r_name);
            g.addOSPFDaemonRelation(ospf_daemon_name, r_name);
            ospf.setRouterId(ID.of(i));
            for(int j = 0; j < r.intfs.size(); j++){
                var intf_name  = NodeGen.getIntfName(r_name, j);
                var ospf_intf_name = NodeGen.getOSPFIntfName(intf_name);
                var ospf_intf = new OSPFIntf(ospf_intf_name);
                g.addNode(ospf_intf);
                g.addOSPFIntfRelation(ospf_intf_name, intf_name);
                //TODO we should use random area
                ospf_intf.setArea(ID.of(r.intfs.get(j).area));
            }
        }
        //fill each network IP
        for(var s: g.getSwitches()){
            var baseID = ranHelper.randomID();
            //FIXME we should consider the IPRange is big enough
            var prefix = ranHelper.randomInt(10, 20);
            var ipRange = IPRange.of(baseID.toLong(), prefix);
            var baseNum = ipRange.getAddressOfIp().IDtoLong();
            for(var intf: g.<Intf>getDstsByType(s.getName(), RelationEdge.EdgeType.INTF)){
                for(var target_intf: g.<Intf>getDstsByType(intf.getName(), RelationEdge.EdgeType.LINK)){
                    target_intf.setIp(IP.of(baseNum++, prefix));
                }
            }
        }
        //TODO mutate other conf fields
    }
}
