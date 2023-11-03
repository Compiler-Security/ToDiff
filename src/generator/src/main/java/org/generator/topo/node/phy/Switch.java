package org.generator.topo.node.phy;

import org.generator.topo.node.TopoNode;

public class Switch extends PhyNode {

    public Switch(int id) {
        super();
        setId(id);
        setNodeType(NodeType.Switch);
    }
}
