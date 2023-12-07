package org.generator.topo.node.ospf;

import org.generator.topo.node.AbstractNode;
import org.generator.topo.node.NodeType;
import org.generator.util.exception.Unimplemented;
import org.generator.util.net.IPV4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OSPFAreaSum extends AbstractNode {

    public OSPFAreaSum(String name){
        setName(name);
        setNodeType(NodeType.OSPFAreaSum);
        sumEntries = new HashMap<>();
        initFiled();
    }

    public static class OSPFAreaSumEntry{
        public IPV4 getRange() {
            return range;
        }

        public void setRange(IPV4 range) {
            this.range = range;
        }

        public Set<OSPFNet> getNet() {
            return net;
        }

        public void setNet(Set<OSPFNet> net) {
            this.net = net;
        }

        public boolean isAdvertise() {
            return advertise;
        }

        public void setAdvertise(boolean advertise) {
            this.advertise = advertise;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public IPV4 getSubstitute() {
            return substitute;
        }

        public void setSubstitute(IPV4 substitute) {
            this.substitute = substitute;
        }

        IPV4 range;
        Set<OSPFNet> net;
        boolean advertise;
        int cost;
        IPV4 substitute;
    }

    public Map<String, OSPFAreaSumEntry> getSumEntries() {
        return sumEntries;
    }

    Map<String, OSPFAreaSumEntry> sumEntries;

    public IPV4 getVirtualLink() {
        return virtualLink;
    }

    public void setVirtualLink(IPV4 virtualLink) {
        this.virtualLink = virtualLink;
    }

    public boolean isShortcut() {
        return shortcut;
    }

    public void setShortcut(boolean shortcut) {
        this.shortcut = shortcut;
    }

    public boolean isStub() {
        return stub;
    }

    public void setStub(boolean stub) {
        this.stub = stub;
    }

    public boolean isNosummary() {
        return nosummary;
    }

    public void setNosummary(boolean nosummary) {
        this.nosummary = nosummary;
    }

    public boolean isNssa() {
        return nssa;
    }

    public void setNssa(boolean nssa) {
        this.nssa = nssa;
    }

    IPV4 virtualLink;
    boolean shortcut;
    boolean stub;
    boolean nosummary;
    boolean nssa;

    public IPV4 getArea() {
        return area;
    }

    public void setArea(IPV4 area) {
        this.area = area;
    }

    IPV4 area;

    @Override
    public void initFiled() {
        //TODO
    }

    @Override
    public String getNodeAtrriStr() {
        new Unimplemented();
        return "";
    }
}
