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

    public enum RouterType implements StringEnum {
        LEVEL1("level-1"),
        LEVEL2("level-2-only"),
        LEVEL1_2("level-1-2");

        private final String template;
        RouterType(String template){this.template = template;}

        @Override
        public boolean match(String st) {
            return new AbstractStringEnum(template).match(st);
        }

        static public Optional<RouterType> of(String st){
            return Arrays.stream(RouterType.values())
                    .filter(x -> x.match(st))
                    .findFirst();
        }

        @Override
        public String toString() {
            return template;
        }
    }

    public RouterType getRouterType() {
        return routerType;
    }

    public void setRouterType(RouterType routerType) {
        this.routerType = routerType;
    }

    //ID RouterId;
    ISIS_STATUS status;
    NET NET;
    RouterType routerType;
    //NET

    @Override
    public void initFiled() {
        status = ISIS_STATUS.UP;
        //RouterId = ID.of(0xffffffffL);
        NET = org.generator.util.net.NET.of("ff.ffff.ffff.ffff.ffff.00");
        routerType = RouterType.LEVEL1_2;
    }
}
