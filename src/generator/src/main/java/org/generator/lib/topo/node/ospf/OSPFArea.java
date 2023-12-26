package org.generator.lib.topo.node.ospf;

import org.generator.lib.topo.node.NodeType;
import org.generator.util.net.IPBase;
import org.generator.lib.topo.node.AbstractNode;
public class OSPFArea extends AbstractNode {
    public OSPFArea(String name) {
        setName(name);
        setNodeType(NodeType.OSPFArea);
        initFiled();
    }

    public IPBase getArea() {
        return area;
    }

    public void setArea(IPBase area) {
        this.area = area;
    }

    IPBase area;

    @Override
    public void initFiled() {
        area = null;
    }

//    @Override
//    public String getNodeAtrriStr() {
//        String area_str = "UNK";
//        if (getArea() != null){
//            area_str = String.format("%d", getArea().IDtoInt());
//        }
//        return String.format("{type:%s, area:%s}", getNodeType(), area_str);
//    }
}
