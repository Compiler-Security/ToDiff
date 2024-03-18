package org.generator.lib.item.topo.node.ospf;

import org.generator.lib.item.topo.node.NodeType;
import org.generator.lib.item.topo.node.AbstractNode;
import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPBase;
import org.generator.util.net.IPRange;

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
        public IPRange getRange() {
            return range;
        }

        public void setRange(IPRange range) {
            this.range = range;
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

        public IP getSubstitute() {
            return substitute;
        }

        public void setSubstitute(IP substitute) {
            this.substitute = substitute;
        }

        IPRange range;
        boolean advertise;
        int cost;
        IP substitute;
    }

    public Map<String, OSPFAreaSumEntry> getSumEntries() {
        return sumEntries;
    }

    Map<String, OSPFAreaSumEntry> sumEntries;

    public ID getVirtualLink() {
        return virtualLink;
    }

    public void setVirtualLink(ID virtualLink) {
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
        Default;

        @Override
        public String toString() {
            switch (this){
                case Default -> {return "default";}
                case Enable -> {return "enable";}
                case Disable -> {return "disable";}
            }
            assert false: "should not go to here";
            return "";
        }
    };
    ID virtualLink;

    public shortCutType getShortcut() {
        return shortcut;
    }

    shortCutType shortcut;
    boolean stub;
    boolean nosummary;
    boolean nssa;

    public ID getArea() {
        return area;
    }

    public void setArea(ID area) {
        this.area = area;
    }

    ID area;

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
