package org.generator.topo.node.ospf;

import org.generator.util.net.IPV4;
import org.generator.topo.node.TopoNode;
public class OSPFArea extends TopoNode {
    public OSPFArea(int id) {
        setId(id);
    }

    IPV4 area;
}
