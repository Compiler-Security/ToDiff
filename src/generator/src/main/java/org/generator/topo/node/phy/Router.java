package org.generator.topo.node.phy;

import org.generator.topo.node.ospf.OSPF;
public class Router extends PhyNode {
    public Router(String name){
          super();
          setName(name);
          setNodeType(NodeType.Router);
    }


    private String calcName(int id){
        return String.format("s%d", id);
    }
}
