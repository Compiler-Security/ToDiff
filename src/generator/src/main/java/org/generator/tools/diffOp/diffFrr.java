package org.generator.tools.diffOp;

import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.ospf.OSPF;
import org.generator.lib.item.conf.node.ospf.OSPFDaemon;
import org.generator.lib.item.conf.node.ospf.OSPFIntf;
import org.generator.lib.item.conf.node.phy.Intf;

public class diffFrr {
    /**
     * if element in base, then it must in ext
     * @param base read from frr
     * @param ext from generator
     */
    public static void solve(ConfGraph base, ConfGraph ext, String router_name){
        for(var node: base.getNodes()){
            System.out.println(node.getName());
            switch (node.getNodeType()){
                case OSPF -> {
                    var ospf = (OSPF) node;
                    assert  ext.containsNode(ospf.getName());
                    var ospf_ext = (OSPF) ext.getNodeNotNull(ospf.getName());
                    //assert ospf.getRouterId().equals(ospf_ext.getRouterId());
                    assert ospf.getStatus().equals(ospf_ext.getStatus());
                    assert ospf.getInitDelay() == ospf_ext.getInitDelay();
                    assert ospf.getMinHoldTime() == ospf_ext.getMinHoldTime();
                    assert ospf.getMaxHoldTime() == ospf_ext.getMaxHoldTime();
                }
                case OSPFDaemon -> {
                    var daemon = (OSPFDaemon) node;
                    assert  ext.containsNode(daemon.getName());
                    var daemon_ext = (OSPFDaemon) ext.getNodeNotNull(daemon.getName());
                    //assert daemon_ext.getMaxPaths() == daemon.getMaxPaths();
                    //assert daemon_ext.getWritemulti() == daemon.getWritemulti();
                }
                case Intf -> {
                    var intf = (Intf) node;
                    assert  ext.containsNode(intf.getName());
                    var intf_ext = (Intf) ext.getNodeNotNull(intf.getName());
                    assert intf.isUp() == intf_ext.isUp();
                    assert intf.getIp().equals(intf_ext.getIp());
                    assert intf.isPersudo() == intf_ext.isPersudo();
                }

                case OSPFIntf -> {
                    var ospfintf = (OSPFIntf) node;
                    assert  ext.containsNode(ospfintf.getName());
                    var ospfintf_ext = (OSPFIntf) ext.getNodeNotNull(ospfintf.getName());
                    assert ospfintf.getCost() == ospfintf_ext.getCost();
                    assert ospfintf.getArea().equals(ospfintf_ext.getArea());
                    //assert ospfintf.getDeadInterval() == ospfintf_ext.getDeadInterval() : "%d:%d".formatted(ospfintf.getDeadInterval(), ospfintf_ext.getDeadInterval());
                    //assert ospfintf.getHelloInterval() == ospfintf_ext.getHelloInterval()  * 1000 / ospfintf_ext.getHelloMulti();
                    //assert ospfintf.getGRHelloDelay() == ospfintf_ext.getGRHelloDelay();
                    assert ospfintf.getPriority() == ospfintf_ext.getPriority();
                    assert ospfintf.getRetansInter() == ospfintf_ext.getRetansInter();
                    assert ospfintf.getTransDelay() == ospfintf_ext.getTransDelay();
                }
                default -> {}
            }
        }
    }
}
