package org.generator.topo.node.phy;

import org.generator.topo.node.AbstractNode;
import org.generator.topo.node.NodeType;

public class Switch extends AbstractNode {

    public Switch(String name) {
        super();
        setName(name);
        setNodeType(NodeType.Switch);
    }
}
