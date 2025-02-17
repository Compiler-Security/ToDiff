package org.generator.lib.item.conf.node;

public enum NodeType {
    Host,
    Intf,
    Router,
    Switch,
    RIP,
    RIPIntf
    ;

    public boolean isPhyNode(){
        return (this == Switch) || (this == Router) || (this == Host);
    }

    public boolean isOSPFNode(){
        return !isPhyNode();
    }

}
