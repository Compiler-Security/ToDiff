package org.generator.topo.node.ospf;

import org.generator.topo.node.AbstractNode;
import org.generator.topo.node.NodeType;

public class OSPFDaemon extends AbstractNode {
    public OSPFDaemon(String name){
        setName(name);
        setNodeType(NodeType.OSPFDaemon);
        socketPerInterface = true;
        initFiled();
    }

    public int getMaxPaths() {
        return maxPaths;
    }

    public void setMaxPaths(int maxPaths) {
        this.maxPaths = maxPaths;
    }

    int maxPaths;

    public int getWritemulti() {
        return writemulti;
    }

    public void setWritemulti(int writemulti) {
        this.writemulti = writemulti;
    }

    public long getBuffersend() {
        return buffersend;
    }

    public void setBuffersend(long buffersend) {
        this.buffersend = buffersend;
    }

    public long getBufferrecv() {
        return bufferrecv;
    }

    public void setBufferrecv(long bufferrecv) {
        this.bufferrecv = bufferrecv;
    }

    int writemulti;
    long buffersend, bufferrecv;

    public boolean isSocketPerInterface() {
        return socketPerInterface;
    }

    public void setSocketPerInterface(boolean socketPerInterface) {
        this.socketPerInterface = socketPerInterface;
    }

    boolean socketPerInterface;

    @Override
    public void initFiled() {

    }

//    @Override
//    public String getNodeAtrriStr() {
//        return "";
//    }
}
