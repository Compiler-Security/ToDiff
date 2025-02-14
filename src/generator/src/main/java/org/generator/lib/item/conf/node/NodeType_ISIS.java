package org.generator.lib.item.conf.node;

public enum NodeType_ISIS {
    Host,
    Intf,
    Router,
    Switch,
    ISIS,
    ISISArea,
    ISISIntf,
    ISISNet,
    ISISDaemon,
    ISISAreaSum,
    ;

    public boolean isPhyNode(){
        return (this == Switch) || (this == Router) || (this == Host);
    }

    public boolean isISISNode(){
        return !isPhyNode();
    }

}
