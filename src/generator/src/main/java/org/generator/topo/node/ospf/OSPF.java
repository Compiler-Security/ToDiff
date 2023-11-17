package org.generator.topo.node.ospf;

import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;
import org.generator.util.net.IPV4;
import org.generator.topo.node.TopoNode;
public class OSPF extends TopoNode {
    public OSPF(String name){
        setName(name);
    }
    public enum OSPF_STATUS{
        UP,
        Restart,
    }

    public enum ABR_TYPE implements StringEnum{
        Normal("standard"),
        CISCO("cisco|ibm"),
        SHORTCUT("shortcut");

        private final String template;
        ABR_TYPE(String template){
            this.template = template;
        }

        @Override
        public boolean match(String st) {
            return new AbstractStringEnum(template).match(st);
        }
    }

    public IPV4 getRouterId() {
        return routerId;
    }

    public void setRouterId(IPV4 routerId) {
        this.routerId = routerId;
    }

    public OSPF_STATUS getStatus() {
        return status;
    }

    public void setStatus(OSPF_STATUS status) {
        this.status = status;
    }

    IPV4 routerId;
    OSPF_STATUS status;

    public ABR_TYPE getAbrType() {
        return abrType;
    }

    public void setAbrType(ABR_TYPE abrType) {
        this.abrType = abrType;
    }

    ABR_TYPE abrType;

}
