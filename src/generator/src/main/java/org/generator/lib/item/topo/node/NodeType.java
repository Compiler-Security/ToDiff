package org.generator.lib.item.topo.node;

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
