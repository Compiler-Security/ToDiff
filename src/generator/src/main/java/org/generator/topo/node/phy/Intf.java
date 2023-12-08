package org.generator.topo.node.phy;

import org.generator.topo.node.NodeType;
import org.generator.util.net.IPV4;
import org.generator.topo.node.AbstractNode;
public class Intf extends AbstractNode {
    public Intf(String name){
        setName(name);
        setNodeType(NodeType.Intf);
        initFiled();
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

    public boolean isPersudo() {
        return persudo;
    }

    public void setPersudo(boolean persudo) {
        this.persudo = persudo;
    }

    boolean persudo;
    @Override
    public void initFiled() {

    }

    @Override
    public String getNodeAtrriStr() {
        String ip_str = "UNK";
        if (getIp() != null){
            ip_str = getIp().toString();
        }
        return String.format("{type:%s, up:%b, ip:%s}", getNodeType(), isUp(), ip_str);
    }
}
