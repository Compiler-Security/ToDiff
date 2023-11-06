package org.generator.topo.node.phy;

public class Host extends PhyNode{
    Host(String name){
        setName(name);
        setNodeType(NodeType.Host);
    }
}
