package org.generator.topo.node.ospf;

import org.generator.topo.node.NodeType;
import org.generator.util.net.IPV4;
import org.generator.topo.node.AbstractNode;
public class OSPFArea extends AbstractNode {
    public OSPFArea(String name) {
        setName(name);
        setNodeType(NodeType.OSPFArea);
        initFiled();
    }

    public IPV4 getArea() {
        return area;
    }

    public void setArea(IPV4 area) {
        this.area = area;
    }

    IPV4 area;

    @Override
    public void initFiled() {
        //TODO
    }

    @Override
    public String getNodeAtrriStr() {
        String area_str = "UNK";
        if (getArea() != null){
            area_str = String.format("%d", getArea().toInt());
        }
        return String.format("{type:%s, area:%s}", getNodeType(), area_str);
    }
}
