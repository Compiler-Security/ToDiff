package org.generator.lib.item.topo.node.phy;

import org.generator.lib.item.topo.node.NodeType;
import org.generator.lib.item.topo.node.AbstractNode;

public class Switch extends AbstractNode {

    public Switch(String name) {
        super();
        setName(name);
        setNodeType(NodeType.Switch);
    }

    @Override
    public void initFiled() {
        initFiled();
    }

//    @Override
//    public String getNodeAtrriStr() {
//        return String.format("{type:%s}", getNodeType());
//    }
}
