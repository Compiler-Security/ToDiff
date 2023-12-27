package org.generator.lib.item.topo.node.ospf;

import org.generator.lib.item.topo.node.AbstractNode;
import org.generator.lib.item.topo.node.NodeType;
import org.generator.util.net.IPBase;

public class OSPFNet extends AbstractNode {
    public OSPFNet(String name) {
        setName(name);
        setNodeType(NodeType.OSPFNet);
        hide = false;
        ip = null;
        initFiled();
    }

    public IPBase getIp() {
        return ip;
    }

    public void setIp(IPBase ip) {
        this.ip = ip;
    }

    IPBase ip;

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    boolean hide;

    @Override
    public void initFiled() {

    }

//    @Override
//    public String getNodeAtrriStr() {
//        String ip_str = "UNK";
//        if (getIp() != null){
//            ip_str = getIp().toString();
//        }
//        return String.format("{type:%s, ip:%s}", getNodeType(), ip_str);
//    }
}
