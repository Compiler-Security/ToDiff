package org.generator.lib.item.conf.edge;

import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.util.graph.AbstractEdge;

public class RelationEdge extends AbstractEdge<AbstractNode> {
    public RelationEdge(AbstractNode src, AbstractNode dst) {
        super(src, dst);
        generateType();
    }

    public RelationEdge(AbstractNode src, AbstractNode dst, EdgeType type){
        super(src, dst);
        this.type = type;
    }

    public enum EdgeType {
        INTF,
        PhyNODE,
        OSPF,
        OSPFINTF,
        OSPFAREA,
        OSPFNetwork,
        LINK,
        OSPFDAEMON,
        OSPFAREASUM,
    }

    protected void generateType(){
        if (src.getNodeType() == NodeType.Intf && dst.getNodeType() == NodeType.Intf){
            setType(EdgeType.LINK);
            return;
        }
        switch (dst.getNodeType()){
            case Intf -> setType(EdgeType.INTF);
            case Router, Host, Switch -> setType(EdgeType.PhyNODE);
            case RIP -> setType(EdgeType.OSPF);
            case RIPIntf -> setType(EdgeType.OSPFINTF);
            case OSPFArea -> setType(EdgeType.OSPFAREA);
            case OSPFNet -> setType(EdgeType.OSPFNetwork);
            default -> throw new IllegalStateException("Unexpected value: " + dst);
        }
    }
    public EdgeType getType() {
        return type;
    }

    public void setType(EdgeType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%s has %s %s", src.toString(), getType().toString(), dst.toString());
    }

    EdgeType type;
}
