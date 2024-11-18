package org.generator.lib.item.conf.node.phy;

import org.generator.lib.item.conf.node.AbstractNode_ISIS;
import org.generator.lib.item.conf.node.NodeType_ISIS;

public class Host_ISIS extends AbstractNode_ISIS {
    Host_ISIS(String name){
        setName(name);
        setNodeType(NodeType_ISIS.Host);
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
