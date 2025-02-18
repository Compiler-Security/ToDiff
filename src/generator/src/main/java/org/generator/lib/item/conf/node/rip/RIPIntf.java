package org.generator.lib.item.conf.node.rip;

import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;
import org.generator.util.net.ID;

import java.util.Arrays;
import java.util.Optional;

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

    public RIP.RIP_VTYPE getVersion() {
        return version;
    }

    public void setVersion(RIP.RIP_VTYPE version) {
        this.version = version;
    }

    boolean splitHorizon;

    boolean poison;

    RIP.RIP_VTYPE version;


    @Override
    public void initFiled() {
        passive = false;
        version = RIP.RIP_VTYPE.V2;
        poison = false;
        splitHorizon = true;
    }
}