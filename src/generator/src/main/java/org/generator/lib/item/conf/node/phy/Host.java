package org.generator.lib.item.conf.node.phy;

import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.lib.item.conf.node.NodeType;

public class Host extends AbstractNode {
    Host(String name){
        setName(name);
        setNodeType(NodeType.Host);
        initFiled();
    }

    @Override
    public void initFiled() {

    }

//    @Override
//    public String getNodeAtrriStr() {
//        return String.format("{type:%s}", getNodeType());
//    }
}
