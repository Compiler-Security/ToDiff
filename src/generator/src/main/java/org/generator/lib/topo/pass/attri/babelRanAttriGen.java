package org.generator.lib.topo.pass.attri;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.babel.BABEL;
import org.generator.lib.item.conf.node.babel.BABELIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.rip.RIP;
import org.generator.lib.item.conf.node.rip.RIPIntf;
import org.generator.lib.topo.item.base.Router;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;
import org.generator.util.ran.ranHelper;

import java.util.List;

public class babelRanAttriGen implements genAttri{

    void generate_babel_router(BABEL babel){
        if (generate.fastConvergence){
            babel.setResendDelay(20);
        }else{
            babel.setResendDelay(ranHelper.randomInt(20, 655340));
        }
        babel.setSmoothing(0);
    }
    void generate_babel_intf(BABELIntf babel_intf){
        //for wired network
        babel_intf.setWired(true);
        babel_intf.setNointerfering(true);
        babel_intf.setPenalty(0);

        babel_intf.setSplitHorizon(ranHelper.randomInt(0, 5) > 0);
        if (generate.fastConvergence){
            babel_intf.setHelloInterval(4000);
            babel_intf.setUpdateInterval(4000);

        }else{
            babel_intf.setHelloInterval(ranHelper.randomInt(20, 655340));
            babel_intf.setUpdateInterval(ranHelper.randomInt(20, 655340));
        }

        //FIXME we should use router's cost
        babel_intf.setRxcost(ranHelper.randomInt(1, 65534));
        babel_intf.setRttDecay(ranHelper.randomInt(1, 256));
        babel_intf.setRttMax(ranHelper.randomInt(1, 256));
        babel_intf.setRttMin(ranHelper.randomInt(1, 256));

        babel_intf.setTimeStamps(ranHelper.randomInt(0, 3) > 0);
    }
    @Override
    public void generate(ConfGraph g, List<Router> routers) {
        for (int i = 0; i < routers.size(); i++) {
            var r = routers.get(i);
            var r_name = NodeGen.getRouterName(i);
            var babel_name = NodeGen.getBABELName(r_name);
            var babel = new BABEL(babel_name);
            g.addNode(babel);
            g.addBABELRelation(babel_name, r_name);
            generate_babel_router(babel);
            for (int j = 0; j < r.intfs.size(); j++) {
                var intf_name = NodeGen.getIntfName(r_name, j);
                var babel_intf_name = NodeGen.getBABELIntfName(intf_name);
                var babel_intf = new BABELIntf(babel_intf_name);
                g.addNode(babel_intf);
                g.addBABELIntfRelation(babel_intf_name, intf_name);
                generate_babel_intf(babel_intf);
            }
        }

        for (var s : g.getSwitches()) {
            for (var intf : g.<Intf>getDstsByType(s.getName(), RelationEdge.EdgeType.INTF)) {
                var prefix = ranHelper.randomInt(10, 20);
                var ipRange = IPRange.of(ranHelper.randomLong(0x80000000L, 0xE0000000L), prefix);
                var baseNum = ipRange.getAddressOfIp().IDtoLong();
                for (var target_intf : g.<Intf>getDstsByType(intf.getName(), RelationEdge.EdgeType.LINK)) {
                    target_intf.setIp(IP.of(baseNum++, prefix));
                }
            }
        }
    }
}
