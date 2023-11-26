package org.generator.topo.node;

import org.generator.topo.node.ospf.OSPFDaemon;

public enum NodeType {
    Host,
    Intf,
    Router,
    Switch,
    OSPF,
    OSPFArea,
    OSPFIntf,
    OSPFNet,
    OSPFDaemon,
    OSPFAreaSum,
    ;

    public boolean isPhyNode(){
        return (this == Switch) || (this == Router) || (this == Host);
    }

    public boolean isOSPFNode(){
        return !isPhyNode();
    }

}
