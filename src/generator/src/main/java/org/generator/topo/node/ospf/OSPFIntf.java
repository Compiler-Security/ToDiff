package org.generator.topo.node.ospf;

import org.generator.topo.node.ospf.OSPF;
import org.generator.topo.node.ospf.OSPFArea;
import org.generator.topo.node.phy.Intf;
import org.generator.topo.node.TopoNode;

public class OSPFIntf extends TopoNode {
    public OSPFIntf(int id, OSPF ospf){
        setId(id);
    }

    public int getVrf() {
        return vrf;
    }

    public void setVrf(int vrf) {
        this.vrf = vrf;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    int vrf;
    long cost;
}
