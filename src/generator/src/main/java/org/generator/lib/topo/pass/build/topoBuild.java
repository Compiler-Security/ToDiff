package org.generator.lib.topo.pass.build;

import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.phy.Switch;
import org.generator.lib.topo.item.base.Router;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class topoBuild {
    public ConfGraph solve(List<Router> routers){
        ConfGraph g = new ConfGraph();
        Map<String, Integer> switchPort = new HashMap<>();
        for(int i = 0; i < routers.size(); i++){
            var r_name = NodeGen.getRouterName(i);
            var r = routers.get(i);
            g.addNode(new org.generator.lib.item.conf.node.phy.Router(r_name));
            for(int j = 0; j < r.intfs.size(); j++){
                var intf_name = NodeGen.getIntfName(r_name, j);
                g.addNode(new Intf(intf_name));
                g.addIntfRelation(intf_name, r_name);
                var intf = r.intfs.get(j);
                assert intf.networkId != -1;
                var s_name = NodeGen.getSwitchName(intf.networkId);
                if (!g.containsNode(s_name)){
                    switchPort.put(s_name, 0);
                    g.addNode(new Switch(s_name));
                }
                var s_intf_name = NodeGen.getIntfName(s_name, switchPort.get(s_name));
                g.addNode(new Intf(s_intf_name));
                g.addIntfRelation(s_intf_name, s_name);
                switchPort.put(s_name, switchPort.get(s_name) + 1);
                g.addIntfLink(s_intf_name, intf_name);
            }
        }
        return g;
    }
}
