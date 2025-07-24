package org.generator.lib.topo.pass.trans;

import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.ospf.OSPFAreaSum;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.topo.item.trans.transGraph;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;

public class babelAttriTran {
    public static void solve(ConfGraph oldG, ConfGraph newG, transGraph.deltaNodes deltas) {
        var networkIps = oldG.getNetworks();
        //copy attributes from oldG
        for (var r: newG.getRouters()){
            if (deltas.isNewRouter(r.getName())){
                continue;
            }

            //copy BABEL
            newG.getBABELOfRouter(r.getName()).copyFrom(oldG.getBABELOfRouter(r.getName()));

            //copy BABEL Intf
            //for old interfaces, we copy all attributes for them ,the cost of old and new should be the same
            //for update interfaces, we copy all except the cost for them
            for(var intf: newG.getIntfsOfRouter(r.getName())) {
                if (deltas.isNewIntf(intf.getName())) {
                    continue;
                }
                var new_babel_intf = newG.getBABELIntfOfIntf(intf.getName());
                var old_babel_intf = oldG.getBABELIntfOfIntf(intf.getName());
                if (deltas.isUpdateIntf(intf.getName())){
                    var rxcost = new_babel_intf.getRxcost();
                    new_babel_intf.copyFrom(old_babel_intf);
                    new_babel_intf.setRxcost(rxcost);
                }else{
                    assert new_babel_intf.getRxcost() == old_babel_intf.getRxcost();
                    new_babel_intf.copyFrom(old_babel_intf);
                }
            }
        }
        //update subnets IP
        for(var s: newG.getSwitches()){
            var intfs = newG.getLinkedIntfsOfSwitch(s.getName());
            for(var intf: intfs){
                assert IPRange.of(intfs.getFirst().getIp().toString()).contains(intf.getIp());
            }
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
                intfs = newG.getLinkedIntfsOfSwitch(s.getName());
                for(var intf: newG.getLinkedIntfsOfSwitch(s.getName())){
                    assert IPRange.of(intfs.getFirst().getIp().toString()).contains(intf.getIp());
                }
            }
        }
    }
}
