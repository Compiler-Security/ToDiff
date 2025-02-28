package org.generator.lib.item.conf.node.phy;

import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.AbstractNode;

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
