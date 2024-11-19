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

        @Override
        public String toString() {
            return template.split("\\|")[0];
        }
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

    public int getLsaRefreshTime() {
        return lsaRefreshTime;
    }

    public void setLsaRefreshTime(int lsaRefreshTime) {
        this.lsaRefreshTime = lsaRefreshTime;
    }

    public int getLsaIntervalTime() {
        return lsaIntervalTime;
    }

    public void setLsaIntervalTime(int lsaIntervalTime) {
        this.lsaIntervalTime = lsaIntervalTime;
    }

    int lsaRefreshTime; //refresh timer 10
    int lsaIntervalTime; // timers throttle lsa all

    @Override
    public void initFiled() {
        status = ISIS_STATUS.UP;
        abrType = null; //abrType if it's not ABR, this field is null
        routerId = ID.of(0xffffffffL);
        initDelay = 0; //spfScheduleDelayMsecs
        minHoldTime = 50; //holdtimeMinMsecs
        maxHoldTime = 5000; //holdtimeMaxMsecs
        lsaRefreshTime = 10;//FIXME this default arg is not right
        lsaIntervalTime = 10;//FIXME this default arg is not right
    }
}
