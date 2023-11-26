package org.generator.topo.node;

public enum NodeType {
    Host,
    Intf,
    Router,
    Switch,
    OSPF,
    OSPFArea,
    OSPFIntf,
    OSPFNet;

    public boolean isPhyNode(){
        return (this == Switch) || (this == Router) || (this == Host);
    }

    public boolean isOSPFNode(){
        return !isPhyNode();
    }

}
