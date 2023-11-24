package org.generator.topo.node.ospf;

import org.generator.topo.node.AbstractNode;
import org.generator.util.net.IPV4;

public class OSPFNet extends AbstractNode {
    public OSPFNet(String name) {
        setName(name);
    }

    public IPV4 getIp() {
        return ip;
    }

    public void setIp(IPV4 ip) {
        this.ip = ip;
    }

    IPV4 ip;
}
