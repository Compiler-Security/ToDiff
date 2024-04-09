package org.generator.lib.item.conf.node.phy;

import org.generator.lib.item.conf.node.NodeType;
import org.generator.util.net.IP;
import org.generator.lib.item.conf.node.AbstractNode;

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


    //    @Override
//    public String getNodeAtrriStr() {
//        String ip_str = "UNK";
//        if (getIp() != null){
//            ip_str = getIp().toString();
//        }
//        return String.format("{type:%s, up:%b, ip:%s}", getNodeType(), isUp(), ip_str);
//    }
}
