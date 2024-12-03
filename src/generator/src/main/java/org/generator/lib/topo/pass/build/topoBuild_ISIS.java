package org.generator.lib.topo.pass.build;

import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph_ISIS;
import org.generator.lib.item.conf.node.NodeGen_ISIS;
import org.generator.lib.item.conf.node.phy.Intf_ISIS;
import org.generator.lib.item.conf.node.phy.Switch_ISIS;
import org.generator.lib.topo.item.base.Router;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class topoBuild_ISIS {
    public ConfGraph_ISIS solve(List<Router> routers){
        ConfGraph_ISIS g = new ConfGraph_ISIS();
        Map<String, Integer> switchPort = new HashMap<>();
        for(int i = 0; i < routers.size(); i++){
            var r_name = NodeGen_ISIS.getRouterName(i);
            var r = routers.get(i);
            g.addNode(new org.generator.lib.item.conf.node.phy.Router_ISIS(r_name));
            for(int j = 0; j < r.intfs.size(); j++){
                var intf_name = NodeGen_ISIS.getIntfName(r_name, j);
                g.addNode(new Intf_ISIS(intf_name));
                g.addIntfRelation(intf_name, r_name);
                var intf = r.intfs.get(j);
                assert intf.networkId != -1;
                var s_name = NodeGen_ISIS.getSwitchName(intf.networkId);
                if (!g.containsNode(s_name)){
                    switchPort.put(s_name, 0);
                    g.addNode(new Switch_ISIS(s_name));
                }
                var s_intf_name = NodeGen_ISIS.getIntfName(s_name, switchPort.get(s_name));
                g.addNode(new Intf_ISIS(s_intf_name));
                g.addIntfRelation(s_intf_name, s_name);
                switchPort.put(s_name, switchPort.get(s_name) + 1);
                g.addIntfLink(s_intf_name, intf_name);
            }
        }
        return g;
    }
}
