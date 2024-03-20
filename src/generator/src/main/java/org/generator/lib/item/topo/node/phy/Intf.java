package org.generator.lib.item.topo.node.phy;

import org.generator.lib.item.topo.node.NodeType;
import org.generator.util.net.IP;
import org.generator.util.net.IPBase;
import org.generator.lib.item.topo.node.AbstractNode;

import java.util.Objects;

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

    public IP getIp() {
        return ip;
    }

    public void setIp(IP ip) {
        this.ip = ip;
    }

    boolean up;
    IP ip;

    public boolean isPersudo() {
        return persudo;
    }

    public void setPersudo(boolean persudo) {
        this.persudo = persudo;
    }

    boolean persudo;
    @Override
    public void initFiled() {
        up = true;
        ip = null;
        persudo = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Intf intf = (Intf) o;
        return up == intf.up && persudo == intf.persudo && Objects.equals(ip, intf.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(up, ip, persudo);
    }

    //    @Override
//    public String getNodeAtrriStr() {
//        String ip_str = "UNK";
//        if (getIp() != null){
//            ip_str = getIp().toString();
//        }
//        return String.format("{type:%s, up:%b, ip:%s}", getNodeType(), isUp(), ip_str);
//    }
}
