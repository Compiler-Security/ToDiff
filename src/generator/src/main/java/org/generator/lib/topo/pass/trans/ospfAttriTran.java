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
        var networkIps = oldG.getNetworks();
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
            var areasums = newG.getOSPFAreaSumOfOSPF(ospf_name);
            for(var areasum: areasums){
                if (oldG.containsNode(areasum.getName())){
                    areasum.copyFrom(oldG.<OSPFAreaSum>getNodeNotNull(areasum.getName()));
                }
            }
            //newG.getOSPFAreaSumOfOSPF(ospf_name).forEach(a -> a.copyFrom(oldG.<OSPFAreaSum>getNodeNotNull(a.getName())));

            //for old interfaces, we remain networkType, ip address for all, and cost for update interfaces
            for(var intf: newG.getIntfsOfRouter(r.getName())) {
                if (deltas.isNewIntf(intf.getName())) {
                    continue;
                }
                var newOspfIntf = newG.getOSPFIntfOfIntf(intf.getName());
                newOspfIntf.setPassive(oldG.getOSPFIntf(newOspfIntf.getName()).isPassive());

            }
//                //we copy all intf, ospfintf from old;
//                assert oldG.containsNode(intf.getName()):"oldG don't have %s!".formatted(intf.getName());
//                var backIp = intf.getIp();
//                intf.copyFrom(oldG.getIntf(intf.getName()));
//                intf.setIp(backIp);
//
//                var backCost = newOspfIntf.getCost();
//                var backNetType = newOspfIntf.getNetType();
//                newOspfIntf.copyFrom(oldG.getOSPFIntfOfIntf(intf.getName()));
//                newOspfIntf.setNetType(backNetType);
//                if (deltas.isUpdateIntf(intf.getName())) {
//                    //for updateIntf, we update the new cost
//                    newOspfIntf.setCost(backCost);
//                }
//            }
        }
        //update subnets constraints
        for(var s: newG.getSwitches()){
            var networkId = NodeGen.getId(s.getName());
            if (networkIps.containsKey(networkId)){
                var ipRange = networkIps.get(networkId);
                var baseNum = ipRange.getAddressOfIp().IDtoLong();
                var oldIntfs = newG.getLinkedIntfsOfSwitch(s.getName()).stream().filter(intf -> !deltas.isNewIntf(intf.getName())).toList();
                for(var old_intf: oldIntfs){
                    old_intf.setIp(oldG.getIntf(old_intf.getName()).getIp());
                }
                for(var intf: newG.getLinkedIntfsOfSwitch(s.getName())){
                    if (deltas.isNewIntf(intf.getName())) {
                        //FIXME base may over max ip
                        while(true){
                            intf.setIp(IP.of(baseNum++, ipRange.getMask()));
                            if (oldIntfs.stream().noneMatch(i -> i.getIp().equals(intf.getIp()))){
                                break;
                            }
                        }
                    }
                }
                var intfs = newG.getLinkedIntfsOfSwitch(s.getName());
                for(var intf: newG.getLinkedIntfsOfSwitch(s.getName())){
                    assert IPRange.of(intfs.getFirst().getIp().toString()).contains(intf.getIp());
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
                //FIXME priority GRHelloDelay should be it self and passive should remain same
            }
        }
    }
}
