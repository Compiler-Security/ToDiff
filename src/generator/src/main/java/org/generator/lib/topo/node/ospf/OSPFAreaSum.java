package org.generator.lib.topo.node.ospf;

import org.generator.lib.topo.node.AbstractNode;
import org.generator.lib.topo.node.NodeType;
import org.generator.util.net.IPBase;

import java.util.HashMap;
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
        public IPBase getRange() {
            return range;
        }

        public void setRange(IPBase range) {
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

        public IPBase getSubstitute() {
            return substitute;
        }

        public void setSubstitute(IPBase substitute) {
            this.substitute = substitute;
        }

        IPBase range;
        Set<OSPFNet> net;
        boolean advertise;
        int cost;
        IPBase substitute;
    }

    public Map<String, OSPFAreaSumEntry> getSumEntries() {
        return sumEntries;
    }

    Map<String, OSPFAreaSumEntry> sumEntries;

    public IPBase getVirtualLink() {
        return virtualLink;
    }

    public void setVirtualLink(IPBase virtualLink) {
        this.virtualLink = virtualLink;
    }


    public void setShortcut(shortCutType shortcut) {
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

    public enum shortCutType{
        Enable,
        Disable,
        Default
    };
    IPBase virtualLink;

    public shortCutType getShortcut() {
        return shortcut;
    }

    shortCutType shortcut;
    boolean stub;
    boolean nosummary;
    boolean nssa;

    public IPBase getArea() {
        return area;
    }

    public void setArea(IPBase area) {
        this.area = area;
    }

    IPBase area;

    @Override
    public void initFiled() {
        shortcut = shortCutType.Default;
        stub = false;
        nosummary = false;
        nssa = false;
        virtualLink = null;
        sumEntries = new HashMap<>();
        area = null;
    }

//    @Override
//    public String getNodeAtrriStr() {
//        new Unimplemented();
//        return "";
//    }
}
