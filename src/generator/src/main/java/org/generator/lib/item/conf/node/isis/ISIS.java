package org.generator.lib.item.conf.node.isis;


import org.generator.lib.item.conf.node.AbstractNode_ISIS;
import org.generator.lib.item.conf.node.NodeType_ISIS;
import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;
import org.generator.util.net.ID;

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

    public ID getRouterId() {
        return routerId;
    }

    public void setRouterId(ID routerId) {
        this.routerId = routerId;
    }

    public ISIS_STATUS getStatus() {
        return status;
    }

    public void setStatus(ISIS_STATUS status) {
        this.status = status;
    }

    ID routerId;
    ISIS_STATUS status;


    //NET

    @Override
    public void initFiled() {
        status = ISIS_STATUS.UP;
        routerId = ID.of(0xffffffffL);
    }
}
