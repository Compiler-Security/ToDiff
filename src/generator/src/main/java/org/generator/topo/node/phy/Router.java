package org.generator.topo.node.phy;

import org.generator.topo.node.phy.PhyNode;
import org.generator.topo.node.ospf.OSPF;
public class Router extends PhyNode {
    public Router(int id){
          super();
          setId(id);
          setNodeType(NodeType.Router);
    }

    public Router(int id, OSPF ospf){
        super();
        setId(id);
    }

    private String calcName(int id){
        return String.format("s%d", id);
    }
}
