package org.generator.topo.node.ospf;

import org.generator.topo.node.AbstractNode;
import org.generator.topo.node.NodeType;
import org.generator.util.net.IPV4;

public class OSPFNet extends AbstractNode {
    public OSPFNet(String name) {
        setName(name);
        setNodeType(NodeType.OSPFNet);
    }

    public IPV4 getIp() {
        return ip;
    }

    public void setIp(IPV4 ip) {
        this.ip = ip;
    }

    IPV4 ip;

    @Override
    public String getNodeAtrriStr() {
        String ip_str = "UNK";
        if (getIp() != null){
            ip_str = getIp().toString();
        }
        return String.format("{type:%s, ip:%s}", getNodeType(), ip_str);
    }
}
