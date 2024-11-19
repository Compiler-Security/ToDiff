package org.generator.lib.item.conf.node.ospf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;

import java.util.HashMap;
import java.util.Map;

public class ISISAreaSum extends AbstractNode {

    public ISISAreaSum(String name){
        setName(name);
        setNodeType(NodeType.OSPFAreaSum);
        sumEntries = new HashMap<>();
        initFiled();
    }

    public static class OSPFAreaSumEntry{
        public OSPFAreaSumEntry(){
        }
        void initField(){
            //TODO other fields
            setAdvertise(true);
        }
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

        public ObjectNode getJsonNode() {
            ObjectNode jsonObject = new ObjectMapper().createObjectNode();
            Class<?> clazz = this.getClass();
            for(var field : clazz.getDeclaredFields()){
                var key = field.getName();
                if (key.equals("$assertionsDisabled")) continue;
                field.setAccessible(true);
                try {
                    var val = field.get(this);
                    jsonObject.put(key, String.format("%s", val));
                }catch (Exception e){
                    assert false: e;
                }
            }
            return jsonObject;
        }
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

    /**
     * We override this Node because we should print entrySums
     * @return
     */
    @Override
    public ObjectNode getJsonNode() {
        var jsNode = super.getJsonNode();
        var sums = new ObjectMapper().createObjectNode();
        for(var entry: sumEntries.values()){
            sums.set(entry.getRange().toString(), entry.getJsonNode());
        }
        jsNode.set("sumEntries", sums);
        return jsNode;
    }

    //    @Override
//    public String getNodeAtrriStr() {
//        new Unimplemented();
//        return "";
//    }
}
