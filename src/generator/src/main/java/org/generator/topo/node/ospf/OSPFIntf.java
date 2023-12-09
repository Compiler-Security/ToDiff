package org.generator.topo.node.ospf;

import org.generator.topo.node.AbstractNode;
import org.generator.topo.node.NodeType;
import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;
import org.generator.util.net.IPV4;

import java.util.Arrays;
import java.util.Optional;

public class OSPFIntf extends AbstractNode {
    public OSPFIntf(String name){
        setName(name);
        setNodeType(NodeType.OSPFIntf);

        passive = false;
    }

//    public enum NetType()
    public int getVrf() {
        return vrf;
    }

    public void setVrf(int vrf) {
        this.vrf = vrf;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
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
    long cost;

    public IPV4 getArea() {
        return area;
    }

    public void setArea(IPV4 area) {
        this.area = area;
    }

    IPV4 area;

    public int getDeadInterval() {
        return deadInterval;
    }

    public void setDeadInterval(int deadInterval) {
        this.deadInterval = deadInterval;
    }

    int deadInterval;
    int helloPerSec;

    public int getGRHelloDelay() {
        return GRHelloDelay;
    }

    public void setGRHelloDelay(int GRHelloDelay) {
        this.GRHelloDelay = GRHelloDelay;
    }

    int GRHelloDelay;

    public int getHelloPerSec() {
        return helloPerSec;
    }

    public void setHelloPerSec(int helloPerSec) {
        this.helloPerSec = helloPerSec;
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
        //TODO
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

    @Override
    public String getNodeAtrriStr() {
        return String.format("{type:%s, area: %s, vrf:%d, cost:%d}",  getNodeType(), getArea(), getVrf(), getCost());
    }
}
