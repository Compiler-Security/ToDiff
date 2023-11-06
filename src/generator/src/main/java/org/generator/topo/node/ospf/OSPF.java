package org.generator.topo.node.ospf;

import org.generator.util.net.IPV4;
import org.generator.topo.node.TopoNode;
public class OSPF extends TopoNode {
    public OSPF(String name){
        setName(name);
    }
    public enum OSPF_STATUS{
        UP,
        Restart,
    }

    public IPV4 getRouterId() {
        return routerId;
    }

    public void setRouterId(IPV4 routerId) {
        this.routerId = routerId;
    }

    public OSPF_STATUS getStatus() {
        return status;
    }

    public void setStatus(OSPF_STATUS status) {
        this.status = status;
    }

    IPV4 routerId;
    OSPF_STATUS status;
}
