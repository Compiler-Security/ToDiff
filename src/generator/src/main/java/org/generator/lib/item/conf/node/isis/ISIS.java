package org.generator.lib.item.conf.node.isis;


import org.generator.lib.item.conf.node.AbstractNode_ISIS;
import org.generator.lib.item.conf.node.NodeType_ISIS;
import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;
import org.generator.util.net.ID;
import org.generator.util.net.NET;
import java.util.Arrays;
import java.util.Optional;

public class ISIS extends AbstractNode_ISIS {
    public ISIS(String name){
        setName(name);
        setNodeType(NodeType_ISIS.ISIS);
        initFiled();
    }
    public enum ISIS_STATUS{
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

    public ISIS_STATUS getStatus() {
        return status;
    }

    public void setStatus(ISIS_STATUS status) {
        this.status = status;
    }

    //ID RouterId;
    ISIS_STATUS status;
    NET NET;

    //NET

    @Override
    public void initFiled() {
        status = ISIS_STATUS.UP;
        //RouterId = ID.of(0xffffffffL);
        NET = org.generator.util.net.NET.of("ff.ffff.ffff.ffff.ffff.00");
    }
}
