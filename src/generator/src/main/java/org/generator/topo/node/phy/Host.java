package org.generator.topo.node.phy;

import org.generator.topo.node.AbstractNode;
import org.generator.topo.node.NodeType;

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
