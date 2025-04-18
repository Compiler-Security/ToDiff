package org.generator.lib.item.conf.node.babel;


import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.lib.item.conf.node.NodeType;

public class BABEL extends AbstractNode {
    public BABEL(String name){
        setName(name);
        setNodeType(NodeType.BABEL);
        initFiled();
    }
    int resendDelay;
    int smoothing;
    public int getResendDelay() {
        return resendDelay;
    }

    public void setResendDelay(int resendDelay) {
        this.resendDelay = resendDelay;
    }

    public int getSmoothing() {
        return smoothing;
    }

    public void setSmoothing(int smoothing) {
        this.smoothing = smoothing;
    }

    @Override
    public void initFiled() {
        resendDelay = 2000;
        smoothing = 4;
    }
}