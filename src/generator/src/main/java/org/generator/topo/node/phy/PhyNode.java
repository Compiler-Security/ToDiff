package org.generator.topo.node.phy;

import org.generator.topo.node.phy.Intf;
import org.generator.topo.node.TopoNode;
import java.util.ArrayList;
import java.util.List;

public class PhyNode extends TopoNode {

    public enum NodeType{
        Router,
        Host,
        Switch,
    }


    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    private NodeType nodeType;

}
