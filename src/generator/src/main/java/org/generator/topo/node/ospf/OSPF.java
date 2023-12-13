package org.generator.topo.node.ospf;


import org.generator.topo.node.NodeType;
import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;
import org.generator.util.net.IPV4;
import org.generator.topo.node.AbstractNode;

import java.util.Arrays;
import java.util.Optional;

public class OSPF extends AbstractNode {
    public OSPF(String name){
        setName(name);
        setNodeType(NodeType.OSPF);
        initFiled();
    }
    public enum OSPF_STATUS{
        INIT,
        Restart,
        UP,
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
        static public Optional<ABR_TYPE> of(String st){
            return Arrays.stream(ABR_TYPE.values())
                    .filter(x -> x.match(st))
                    .findFirst();
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

    public int getInitDelay() {
        return initDelay;
    }

    public void setInitDelay(int initDelay) {
        this.initDelay = initDelay;
    }

    public int getMinHoldTime() {
        return minHoldTime;
    }

    public void setMinHoldTime(int minHoldTime) {
        this.minHoldTime = minHoldTime;
    }

    public int getMaxHoldTime() {
        return maxHoldTime;
    }

    public void setMaxHoldTime(int maxHoldTime) {
        this.maxHoldTime = maxHoldTime;
    }

    int initDelay;
    int minHoldTime;
    int maxHoldTime;

    @Override
    public void initFiled() {
        status = OSPF_STATUS.UP;
        abrType = null; //abrType if it's not ABR, this field is null
        //TODO we should set this in init
        routerId = IPV4.IDOf(0);
        initDelay = 0; //spfScheduleDelayMsecs
        minHoldTime = 50; //holdtimeMinMsecs
        maxHoldTime = 5000; //holdtimeMaxMsecs
    }


}
