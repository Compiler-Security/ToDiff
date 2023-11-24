package org.generator.topo.node.phy;

import org.generator.topo.node.AbstractNode;
import org.generator.topo.node.NodeType;

public class Host extends AbstractNode {
    Host(String name){
        setName(name);
        setNodeType(NodeType.Host);
    }
}
