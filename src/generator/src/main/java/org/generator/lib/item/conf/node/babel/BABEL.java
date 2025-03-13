package org.generator.lib.item.conf.node.babel;


import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.lib.item.conf.node.NodeType;

public class BABEL extends AbstractNode {
    public BABEL(String name){
        setName(name);
        setNodeType(NodeType.BABEL);
        initFiled();
    }
    @Override
    public void initFiled() {}
}