package org.generator.lib.item.conf.node.phy;

import org.generator.lib.item.conf.node.NodeType_ISIS;
import org.generator.lib.item.conf.node.AbstractNode_ISIS;

public class Switch_ISIS extends AbstractNode_ISIS {

    public Switch_ISIS(String name) {
        super();
        setName(name);
        setNodeType(NodeType_ISIS.Switch);
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
