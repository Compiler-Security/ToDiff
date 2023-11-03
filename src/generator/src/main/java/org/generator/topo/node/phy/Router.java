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
        this.ospf = ospf;
    }

    private String calcName(int id){
        return String.format("s%d", id);
    }

    public OSPF getOspf() {
        return ospf;
    }

    public void setOspf(OSPF ospf) {
        this.ospf = ospf;
    }

    public boolean hasOspf(){
        return ospf != null;
    }
    private OSPF ospf;
}
