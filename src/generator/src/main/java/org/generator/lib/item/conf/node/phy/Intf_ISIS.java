package org.generator.lib.item.conf.node.phy;

import org.generator.lib.item.conf.node.NodeType_ISIS;
import org.generator.util.net.IP;
import org.generator.lib.item.conf.node.AbstractNode_ISIS;

public class Intf_ISIS extends AbstractNode_ISIS {
    public Intf_ISIS(String name){
        setName(name);
        setNodeType(NodeType_ISIS.Intf);
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
