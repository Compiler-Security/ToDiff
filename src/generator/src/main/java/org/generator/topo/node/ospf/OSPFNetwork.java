package org.generator.topo.node.ospf;

import org.generator.topo.node.TopoNode;
import org.generator.util.net.IPV4;

public class OSPFNetwork extends TopoNode {
    public OSPFNetwork(int id) {
        setId(id);
    }

    public IPV4 getIp() {
        return ip;
    }

    public void setIp(IPV4 ip) {
        this.ip = ip;
    }

    IPV4 ip;
}
