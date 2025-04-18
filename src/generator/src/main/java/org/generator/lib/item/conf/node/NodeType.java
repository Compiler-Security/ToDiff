package org.generator.lib.item.conf.node;

public enum NodeType {
    Host,
    Intf,
    Router,
    Switch,
    //========OSPF==========
    OSPF,
    OSPFArea,
    OSPFIntf,
    OSPFNet,
    OSPFDaemon,
    OSPFAreaSum,

    //========RIP=============
    RIP,
    RIPIntf,
    //MULTI:
    //=========ISIS============
    ISIS,
    ISISIntf,
    ISISDaemon,
    ISISAreaSum,

    //=========BABEL===========
    BABEL,
    BABELIntf,
    ;

    public boolean isPhyNode(){
        return (this == Switch) || (this == Router) || (this == Host);
    }

    public boolean isOSPFNode(){
        return !isPhyNode();
    }

}
