package org.generator.lib.item.conf.node.rip;


import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;
import org.generator.util.net.ID;
import org.generator.util.net.IPRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RIP extends AbstractNode {
    public RIP(String name){
        setName(name);
        setNodeType(NodeType.RIP);
        initFiled();
    }
    public enum RIP_STATUS {
        INIT,
        Restart,
        UP,
        SHUTDOWN,
    }

    public enum RIP_VTYPE implements StringEnum{
        V1("1"),
        V2("2"),
        V12("1 2");

        private final String template;
        RIP_VTYPE(String template){
            this.template = template;
        }

        @Override
        public boolean match(String st) {
            return new AbstractStringEnum(template).match(st);
        }
        static public Optional<RIP_VTYPE> of(String st){
            return Arrays.stream(RIP_VTYPE.values())
                    .filter(x -> x.match(st))
                    .findFirst();
        }

        @Override
        public String toString() {
            return template.split("\\|")[0];
        }
    }

    public RIP_STATUS getStatus() {
        return status;
    }

    public void setStatus(RIP_STATUS status) {
        this.status = status;
    }

    RIP_STATUS status;

    public RIP_VTYPE getVersion() {
        return version;
    }

    public void setVersion(RIP_VTYPE version) {
        this.version = version;
    }

    RIP_VTYPE version;

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getGarbage() {
        return garbage;
    }

    public void setGarbage(int garbage) {
        this.garbage = garbage;
    }

    int update;
    int timeout;
    int garbage;

    public int getMetric() {
        return metric;
    }

    public void setMetric(int metric) {
        this.metric = metric;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    int metric; //refresh timer 10
    int distance; // timers throttle lsa all

    public List<IPRange> getNeighbors(){
        return neighbors;
    }

    public void addNeighbor(IPRange neighbor){
        neighbors.add(neighbor);
    }

    public void addNeighbors(List<IPRange> _neighbors){
        neighbors.addAll(_neighbors);
    }
    List<IPRange> neighbors;
    @Override
    public void initFiled() {
        status = RIP_STATUS.UP;
        version = RIP_VTYPE.V2; //we default use V2 version of RIP
        update = 30; //spfScheduleDelayMsecs
        timeout = 180; //holdtimeMinMsecs
        garbage = 120; //holdtimeMaxMsecs
        metric = 1;
        distance = 120;
        neighbors = new ArrayList<>();
    }
}