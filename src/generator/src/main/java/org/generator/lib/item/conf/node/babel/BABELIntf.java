package org.generator.lib.item.conf.node.babel;


import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.lib.item.conf.node.NodeType;

public class BABELIntf extends AbstractNode {
    public BABELIntf(String name){
        setName(name);
        setNodeType(NodeType.BABELIntf);
        initFiled();
    }

    public boolean isWired() {
        return wired;
    }

    public void setWired(boolean wired) {
        this.wired = wired;
    }

    public boolean isSplitHorizon() {
        return splitHorizon;
    }

    public void setSplitHorizon(boolean splitHorizon) {
        this.splitHorizon = splitHorizon;
    }

    public int getHelloInterval() {
        return helloInterval;
    }

    public void setHelloInterval(int helloInterval) {
        this.helloInterval = helloInterval;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public boolean isNointerfering() {
        return nointerfering;
    }

    public void setNointerfering(boolean nointerfering) {
        this.nointerfering = nointerfering;
    }

    public int getRxcost() {
        return rxcost;
    }

    public void setRxcost(int rxcost) {
        this.rxcost = rxcost;
    }

    public int getRttDecay() {
        return rttDecay;
    }

    public void setRttDecay(int rttDecay) {
        this.rttDecay = rttDecay;
    }

    public int getRttMin() {
        return rttMin;
    }

    public void setRttMin(int rttMin) {
        this.rttMin = rttMin;
    }

    public int getRttMax() {
        return rttMax;
    }

    public void setRttMax(int rttMax) {
        this.rttMax = rttMax;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public boolean isTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(boolean timeStamps) {
        this.timeStamps = timeStamps;
    }

    boolean wired;
    boolean splitHorizon;
    int helloInterval;
    int updateInterval;
    boolean nointerfering;
    int rxcost;
    int rttDecay;
    int rttMin;
    int rttMax;
    int penalty;
    boolean timeStamps;
    @Override
    public void initFiled() {
        wired = true;
        splitHorizon = true;
        helloInterval = 4000;
        updateInterval = 16000;
        nointerfering = true;
        rxcost = 96;
        rttDecay = 42;
        rttMin = 10000;
        rttMax = 120000;
        penalty = 150;
        timeStamps = false;
    }
}