package org.generator.lib.item.conf.node.openfabric;


import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.util.net.NET;

public class FABRIC extends AbstractNode {
    public FABRIC(String name){
        setName(name);
        setNodeType(NodeType.FABRIC);
        initFiled();
    }
    public enum FABRIC_STATUS{
        INIT,
        Restart,
        UP,
        SHUTDOWN,
    }

    // public ID getRouterId() {
    //     return RouterId;
    // }

    // public void setRouterId(ID RouterId) {
    //     this.RouterId= RouterId;
    // }

    public NET getNET(){
        return NET;
    }

    public void setNET(NET NET) {
        this.NET = NET;
    }

    public FABRIC_STATUS getStatus() {
        return status;
    }

    public void setStatus(FABRIC_STATUS status) {
        this.status = status;
    }

    //ID RouterId;
    FABRIC_STATUS status;
    NET NET;

    @Override
    public void initFiled() {
        status = FABRIC_STATUS.UP;
        //RouterId = ID.of(0xffffffffL);
        NET = org.generator.util.net.NET.of("ff.ffff.ffff.ffff.ffff.00");
    }
}
