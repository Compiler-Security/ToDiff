package org.generator.topo.node.ospf;

import org.generator.util.net.IPV4;
import org.generator.topo.node.TopoNode;
public class OSPFArea extends TopoNode {
    public OSPFArea(String name) {
        setName(name);
    }

    public IPV4 getArea() {
        return area;
    }

    public void setArea(IPV4 area) {
        this.area = area;
    }

    IPV4 area;
}
