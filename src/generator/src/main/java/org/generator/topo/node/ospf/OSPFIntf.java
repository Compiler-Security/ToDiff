package org.generator.topo.node.ospf;

import org.generator.topo.node.AbstractNode;
import org.generator.topo.node.NodeType;

public class OSPFIntf extends AbstractNode {
    public OSPFIntf(String name){
        setName(name);
        setNodeType(NodeType.OSPFIntf);
    }

    public int getVrf() {
        return vrf;
    }

    public void setVrf(int vrf) {
        this.vrf = vrf;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    int vrf;
    long cost;

    @Override
    public String getNodeAtrriStr() {
        return String.format("{type:%s, vrf:%d, cost:%d}", getNodeType(), getVrf(), getCost());
    }
}
