package org.generator.lib.item.conf.node.rip;


import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;
import org.generator.util.net.ID;

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

    public List<ID> getNeighbors(){
        return neighbors;
    }

    public void addNeighbor(ID neighbor){
        neighbors.add(neighbor);
    }

    public void addNeighbors(List<ID> _neighbors){
        neighbors.addAll(_neighbors);
    }
    List<ID> neighbors;
    @Override
    public void initFiled() {
        status = RIP_STATUS.UP;
        update = 30; //spfScheduleDelayMsecs
        timeout = 180; //holdtimeMinMsecs
        garbage = 120; //holdtimeMaxMsecs
        metric = 1;
        distance = 120;
        neighbors = new ArrayList<org.generator.util.net.ID>();
    }
}