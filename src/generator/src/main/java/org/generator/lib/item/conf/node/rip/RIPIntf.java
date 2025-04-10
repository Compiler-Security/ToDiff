package org.generator.lib.item.conf.node.rip;

import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.AbstractNode;

public class RIPIntf extends AbstractNode {
    public RIPIntf(String name){
        setName(name);
        setNodeType(NodeType.RIPIntf);
        initFiled();
    }

    public boolean isPassive() {
        return passive;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    boolean passive;

    public boolean isSplitHorizon() {
        return splitHorizon;
    }

    public void setSplitHorizon(boolean splitHorizon) {
        this.splitHorizon = splitHorizon;
    }

    public boolean isPoison() {
        return poison;
    }

    public void setPoison(boolean poison) {
        this.poison = poison;
    }

    public RIP.RIP_VTYPE getRecvVersion() {
        return recvVersion;
    }

    public void setRecvVersion(RIP.RIP_VTYPE recvVersion) {
        this.recvVersion = recvVersion;
    }

    boolean splitHorizon;

    boolean poison;

    RIP.RIP_VTYPE recvVersion;

    public RIP.RIP_VTYPE getSendVersion() {
        return sendVersion;
    }

    public void setSendVersion(RIP.RIP_VTYPE sendVersion) {
        this.sendVersion = sendVersion;
    }

    RIP.RIP_VTYPE sendVersion;


    @Override
    public void initFiled() {
        passive = false;
        recvVersion = RIP.RIP_VTYPE.V2;
        poison = false;
        splitHorizon = true;
        sendVersion = RIP.RIP_VTYPE.V2;
        recvVersion = RIP.RIP_VTYPE.V12;
    }
}