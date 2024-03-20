package org.generator.lib.item.topo.node.ospf;

import org.generator.lib.item.topo.node.NodeType;
import org.generator.lib.item.topo.node.AbstractNode;

import java.util.Objects;

public class OSPFDaemon extends AbstractNode {
    public OSPFDaemon(String name){
        setName(name);
        setNodeType(NodeType.OSPFDaemon);
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
        maxPaths = 64;
        writemulti = 20;
        socketPerInterface = true;
        buffersend = 8 * 1024 * 1024;
        bufferrecv = 8 * 1024 * 1024;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OSPFDaemon that = (OSPFDaemon) o;
        return maxPaths == that.maxPaths && writemulti == that.writemulti && buffersend == that.buffersend && bufferrecv == that.bufferrecv && socketPerInterface == that.socketPerInterface;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxPaths, writemulti, buffersend, bufferrecv, socketPerInterface);
    }

    //    @Override
//    public String getNodeAtrriStr() {
//        return "";
//    }
}
