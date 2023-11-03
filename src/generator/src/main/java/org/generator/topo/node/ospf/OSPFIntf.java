package org.generator.topo.node.ospf;

import org.generator.topo.node.ospf.OSPF;
import org.generator.topo.node.ospf.OSPFArea;
import org.generator.topo.node.phy.Intf;
import org.generator.topo.node.TopoNode;

public class OSPFIntf extends TopoNode {
    public OSPFIntf(int id, OSPF ospf){
        setId(id);
    }

    public Intf getIntf() {
        return intf;
    }

    public void setIntf(Intf intf) {
        this.intf = intf;
    }

    public int getVrf() {
        return vrf;
    }

    public void setVrf(int vrf) {
        this.vrf = vrf;
    }

    public OSPFArea getArea() {
        return area;
    }

    public void setArea(OSPFArea area) {
        this.area = area;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    Intf intf;
    int vrf;
    OSPFArea area;
    long cost;
}
