package org.generator.lib.item.conf.node.ospf;

import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;
import org.generator.util.net.ID;

import java.util.Arrays;
import java.util.Optional;

public class OSPFIntf extends AbstractNode {
    public OSPFIntf(String name){
        setName(name);
        setNodeType(NodeType.OSPFIntf);
        initFiled();
    }

//    public enum NetType()
    public int getVrf() {
        return vrf;
    }

    public void setVrf(int vrf) {
        this.vrf = vrf;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }



    public boolean isPassive() {
        return passive;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    boolean passive;
    int vrf;
    int cost;

    public ID getArea() {
        return area;
    }

    public void setArea(ID area) {
        this.area = area;
    }

    ID area;

    public int getDeadInterval() {
        return deadInterval;
    }

    public void setDeadInterval(int deadInterval) {
        this.deadInterval = deadInterval;
    }

    int deadInterval;
    int helloMulti;

    public int getGRHelloDelay() {
        return GRHelloDelay;
    }

    public void setGRHelloDelay(int GRHelloDelay) {
        this.GRHelloDelay = GRHelloDelay;
    }

    int GRHelloDelay;

    public int getHelloMulti() {
        return helloMulti;
    }

    public void setHelloMulti(int helloMulti) {
        this.helloMulti = helloMulti;
    }

    public int getHelloInterval() {
        return helloInterval;
    }

    public void setHelloInterval(int helloInterval) {
        this.helloInterval = helloInterval;
    }

    int helloInterval;


    public enum OSPFNetType implements StringEnum {
        BROADCAST("broadcast"),
        POINTTOPOINT("point-to-point"),
        NONBROADCAST("non-broadcast");


        private final String template;
        OSPFNetType(String template){this.template = template;}

        @Override
        public boolean match(String st) {
            return new AbstractStringEnum(template).match(st);
        }

        static public Optional<OSPFNetType> of(String st){
            return Arrays.stream(OSPFNetType.values())
                    .filter(x -> x.match(st))
                    .findFirst();
        }

        @Override
        public String toString() {
            return template;
        }
    }

    public OSPFNetType getNetType() {
        return netType;
    }

    public void setNetType(OSPFNetType netType) {
        this.netType = netType;
    }

    OSPFNetType netType;
    @Override
    public void initFiled() {
        passive = false;
        vrf = 0;
        cost = 10;
        area = null;
        helloInterval = 10;  //timerHelloInMsecs
        deadInterval = 40; //timerDeadSecs
        retansInter = 5; //timerRetransmitSecs
        transDelay = 1; //transmitDelaySecs

        helloMulti = 1;
        GRHelloDelay = 10; //grHelloDelaySecs
        netType = OSPFNetType.BROADCAST;
        priority = 1; //priority

    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    int priority;

    public int getRetansInter() {
        return retansInter;
    }

    public void setRetansInter(int retansInter) {
        this.retansInter = retansInter;
    }

    int retansInter;

    public int getTransDelay() {
        return transDelay;
    }

    public void setTransDelay(int transDelay) {
        this.transDelay = transDelay;
    }

    int transDelay;


    //    @Override
//    public String getNodeAtrriStr() {
//        return String.format("{type:%s, area: %s, vrf:%d, cost:%d}",  getNodeType(), getArea(), getVrf(), getCost());
//    }
}
