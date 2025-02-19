package org.generator.lib.topo.pass.attri;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.rip.RIP;
import org.generator.lib.item.conf.node.rip.RIPIntf;
import org.generator.lib.topo.item.base.Router;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;
import org.generator.util.ran.ranHelper;

import java.util.List;

public class ripRanAttriGen implements genAttri{

    private static String generateRandomIP(int min, int max) {
        int firstOctet = ranHelper.randomInt(min, max); // 第一段在[min, max]范围内
        int secondOctet = ranHelper.randomInt(0, 255); // 第二段0-255
        int thirdOctet = ranHelper.randomInt(0, 255); // 第三段0-255
        int fourthOctet = ranHelper.randomInt(0, 255); // 第四段0-255

        return firstOctet + "." + secondOctet + "." + thirdOctet + "." + fourthOctet;
    }

    IPRange ipGen(boolean classless){
        if (classless){
            var prefix = ranHelper.randomInt(10, 20);
            var ipRange = IPRange.of(ranHelper.randomLong(0x80000000L, 0xE0000000L), prefix);
            return ipRange;
        }else{
            String ipRangeStr = "";
            switch (ranHelper.randomInt(0, 3)){
                case 0->{ ipRangeStr = generateRandomIP(1, 127) + "/8";}
                case 1->{ ipRangeStr = generateRandomIP(128, 191) + "/6";}
                case 2->{ ipRangeStr = generateRandomIP(192, 223) + "/24";}
                case 3->{ ipRangeStr = generateRandomIP(224, 239) + "/32";}
            }
            return IPRange.of(ipRangeStr);
        }
    }
    void generate_rip(RIP rip){
        if (generate.fastConvergence){
            rip.setUpdate(5);
            rip.setTimeout(10);
            rip.setGarbage(5);
        }else{
            rip.setUpdate(ranHelper.randomInt(5, 10000));
            rip.setTimeout(ranHelper.randomInt(rip.getUpdate(), 10000));
            rip.setGarbage(ranHelper.randomInt(5, 10000));
        }
        rip.setDistance(ranHelper.randomInt(1, 255));
        rip.setMetric(ranHelper.randomInt(1, 16));
    }

    void generate_rip_intf(RIPIntf rip_intf){
        rip_intf.setPassive(ranHelper.randomInt(0, 10) == 0);
        rip_intf.setPoison(ranHelper.randomInt(0, 3) == 0);
        rip_intf.setSplitHorizon(ranHelper.randomInt(0, 10) > 0);
    }
    @Override
    public void generate(ConfGraph g, List<Router> routers) {
        for(int i = 0; i < routers.size(); i++){
            var r = routers.get(i);
            var r_name = NodeGen.getRouterName(i);
            var rip_name = NodeGen.getRIPName(r_name);
            var rip = new RIP(rip_name);
            g.addNode(rip);
            g.addRIPRelation(rip_name, r_name);
            generate_rip(rip);
            for(int j = 0; j < r.intfs.size(); j++){
                var intf_name  = NodeGen.getIntfName(r_name, j);
                var rip_intf_name = NodeGen.getRIPIntfName(intf_name);
                var rip_intf = new RIPIntf(rip_intf_name);
                g.addNode(rip_intf);
                g.addRIPIntfRelation(rip_intf_name, intf_name);
                generate_rip_intf(rip_intf);
            }
        }

        //fill each network IP, and set interface protocol Type
        for(var s: g.getSwitches()){
            boolean classless = ranHelper.randomInt(0, 3) > 0;
            IPRange ipRange = ipGen(classless);
            int prefix = ipRange.getMask();
            var baseNum = ipRange.getAddressOfIp().IDtoLong();
            for(var intf: g.<Intf>getDstsByType(s.getName(), RelationEdge.EdgeType.INTF)){
                for(var target_intf: g.<Intf>getDstsByType(intf.getName(), RelationEdge.EdgeType.LINK)){
                    var rip_intf_name = NodeGen.getRIPIntfName(target_intf.getName());
                    target_intf.setIp(IP.of(baseNum++, prefix));
                    if (classless){
                        if (ranHelper.randomInt(0, 3) > 0){
                            g.getRIPIntf(rip_intf_name).setRecvVersion(RIP.RIP_VTYPE.V2);
                            g.getRIPIntf(rip_intf_name).setSendVersion(RIP.RIP_VTYPE.V2);
                        }else{
                            g.getRIPIntf(rip_intf_name).setRecvVersion(RIP.RIP_VTYPE.V12);
                            g.getRIPIntf(rip_intf_name).setSendVersion(RIP.RIP_VTYPE.V12);
                        }
                    }else{
                        g.getRIPIntf(rip_intf_name).setRecvVersion(RIP.RIP_VTYPE.V1);
                        g.getRIPIntf(rip_intf_name).setSendVersion(RIP.RIP_VTYPE.V1);
                    }
                }
            }
        }
    }
}
