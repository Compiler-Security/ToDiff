package org.generator.topo.node.ospf;

import org.generator.topo.node.NodeType;
import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;
import org.generator.util.net.IPV4;
import org.generator.topo.node.AbstractNode;

import javax.swing.text.html.Option;
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

    public int getInitHoldTime() {
        return initHoldTime;
    }

    public void setInitHoldTime(int initHoldTime) {
        this.initHoldTime = initHoldTime;
    }

    public int getMaxHoldTime() {
        return maxHoldTime;
    }

    public void setMaxHoldTime(int maxHoldTime) {
        this.maxHoldTime = maxHoldTime;
    }

    int initDelay;
    int initHoldTime;
    int maxHoldTime;

    @Override
    public void initFiled() {
        status = OSPF_STATUS.UP;
        abrType = ABR_TYPE.Normal;
        routerId = IPV4.Of(0);
        //TODO
        initDelay = 10;
        initHoldTime = 10;
        maxHoldTime = 10;
    }

    @Override
    public String getNodeAtrriStr() {
        String router_id_str = "UNK";
        if (getRouterId() != null){
            router_id_str = String.format("%d", getRouterId().toInt());
        }
        return String.format("{type:%s, router_id:%s, status:%s, abr_type:%s}", getNodeType(), router_id_str, getStatus(), getAbrType());
    }

}
