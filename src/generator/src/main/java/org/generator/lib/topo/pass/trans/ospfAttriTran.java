package org.generator.lib.topo.pass.trans;

import org.generator.lib.item.conf.graph.ConfGraph;

import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.ospf.OSPFAreaSum;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.topo.item.trans.transGraph.deltaNodes;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;

import java.util.HashMap;
import java.util.Map;

public class ospfAttriTran {
    public static void solve(ConfGraph oldG, ConfGraph newG, deltaNodes deltas) {
        Map<Integer, IPRange> networkIps = new HashMap<>();
        for(var s: oldG.getSwitches()){
            var intfs = oldG.getLinkedIntfsOfSwitch(s.getName());
            if (intfs.isEmpty()) continue;
            var ip = intfs.getFirst().getIp();
            networkIps.put(NodeGen.getId(s.getName()), IPRange.of(ip.getNetAddressOfIp().IDtoLong(), ip.getMask()));
        }
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
            var networkId = NodeGen.getId(s.getName());
            if (networkIps.containsKey(networkId)){
                var ipRange = networkIps.get(networkId);
                var baseNum = ipRange.getAddressOfIp().IDtoLong();
                var oldIntfs = newG.getLinkedIntfsOfSwitch(s.getName()).stream().filter(intf -> !deltas.isNewIntf(intf.getName()));
                for(var intf: newG.getLinkedIntfsOfSwitch(s.getName())){
                    if (deltas.isNewIntf(intf.getName())) {
                        //FIXME base may over max ip
                        while(true){
                            intf.setIp(IP.of(baseNum++, ipRange.getMask()));
                            if (oldIntfs.noneMatch(i -> i.getIp().equals(intf.getIp()))){
                                break;
                            }
                        }
                    }
                }
            }

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
            //TODO 7-3 remain modified interface's link's switch port name the same with oldConfG
            for(var intf: newG.getLinkedIntfsOfSwitch(s.getName())){
                if (intf == oldIntf) continue;
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
