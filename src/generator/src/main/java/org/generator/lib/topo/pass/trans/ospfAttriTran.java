package org.generator.lib.topo.pass.trans;

import org.generator.lib.item.conf.graph.ConfGraph;

import org.generator.lib.item.conf.node.ospf.OSPFAreaSum;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.topo.item.trans.transGraph.deltaNodes;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;

public class ospfAttriTran {
    public static void solve(ConfGraph oldG, ConfGraph newG, deltaNodes deltas) {
        //copy attributes from oldG
        for (var r: newG.getRouters()){
            if (deltas.isNewRouter(r.getName())){
                continue;
            }
            //oldG.getOspfOfRouter(r.getName());
            //copy OSPF
            newG.getOspfOfRouter(r.getName()).copyFrom(oldG.getOspfOfRouter(r.getName()));
            var ospf_name = newG.getOspfOfRouter(r.getName()).getName();
            //copy daemon
            newG.getOSPFDaemonOfOSPF(ospf_name).copyFrom(oldG.getOSPFDaemonOfOSPF(ospf_name));
            //copy areas
            newG.getOSPFAreaSumOfOSPF(ospf_name).forEach(a -> a.copyFrom(oldG.<OSPFAreaSum>getNodeNotNull(a.getName())));

            //for interfaces, we remain networkType for all, and cost for update interfaces
            for(var intf: newG.getIntfsOfRouter(r.getName())){
                if (deltas.isNewIntf(intf.getName())){continue;};
                //we copy all intf, ospfintf from old
                //FIXME 7-1
                if (!oldG.containsNode(intf.getName())){continue;}
                assert oldG.containsNode(intf.getName()):"oldG don't have %s!".formatted(intf.getName());
                intf.copyFrom(oldG.getIntf(intf.getName()));
                var newOspfIntf = newG.getOSPFIntfOfIntf(intf.getName());
                var backCost = newOspfIntf.getCost();
                var backNetType = newOspfIntf.getNetType();
                newOspfIntf.copyFrom(oldG.getOSPFIntfOfIntf(intf.getName()));
                newOspfIntf.setNetType(backNetType);
                if (deltas.isUpdateIntf(intf.getName())) {
                    //for updateIntf, we update the new cost
                    newOspfIntf.setCost(backCost);
                }
            }
        }
        //update subnets constraints
        for(var s: newG.getSwitches()){
            Intf oldIntf = null, newIntf = null;
            for(var intf: newG.getLinkedIntfsOfSwitch(s.getName())){
                if (deltas.isNewIntf(intf.getName())) {
                    newIntf = intf;
                }else{
                    oldIntf = intf;
                }
            }
            //if all is new then continue
            if (oldIntf == null) continue;
            //if all is old except cost continue
            if (newIntf == null) continue;
            var oldOspfIntf = newG.getOSPFIntfOfIntf(oldIntf.getName());
            var ipRange = IPRange.of(oldIntf.getIp().getNetAddressOfIp().IDtoLong(), oldIntf.getIp().getMask());
            var baseNum = ipRange.getAddressOfIp().IDtoLong();
            for(var intf: newG.getLinkedIntfsOfSwitch(s.getName())){
                if (intf == oldIntf) continue;
                intf.setIp(IP.of(baseNum++, ipRange.getMask()));
                var ospfIntf = newG.getOSPFIntfOfIntf(intf.getName());
                ospfIntf.setDeadInterval(oldOspfIntf.getDeadInterval());
                ospfIntf.setTransDelay(oldOspfIntf.getTransDelay());
                ospfIntf.setHelloInterval(oldOspfIntf.getHelloInterval());
                ospfIntf.setGRHelloDelay(oldOspfIntf.getGRHelloDelay());
                ospfIntf.setHelloMulti(oldOspfIntf.getHelloMulti());
                ospfIntf.setRetansInter(oldOspfIntf.getRetansInter());
            }
        }
    }
}
