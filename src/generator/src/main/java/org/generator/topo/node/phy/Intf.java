package org.generator.topo.node.phy;

import org.generator.topo.node.NodeType;
import org.generator.util.net.IPV4;
import org.generator.topo.node.AbstractNode;
public class Intf extends AbstractNode {
    public Intf(String name){
        setName(name);
        setNodeType(NodeType.Intf);
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public IPV4 getIp() {
        return ip;
    }

    public void setIp(IPV4 ip) {
        this.ip = ip;
    }

    boolean up;
    IPV4 ip;

    @Override
    public String getNodeAtrriStr() {
        String ip_str = "UNK";
        if (getIp() != null){
            ip_str = getIp().toString();
        }
        return String.format("{type:%s, up:%b, ip:%s}", getNodeType(), isUp(), ip_str);
    }
}
