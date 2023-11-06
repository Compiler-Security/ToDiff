package org.generator.topo.edge;

import org.generator.topo.node.TopoNode;
import org.generator.topo.node.ospf.OSPF;
import org.generator.topo.node.ospf.OSPFArea;
import org.generator.topo.node.ospf.OSPFIntf;
import org.generator.topo.node.ospf.OSPFNetwork;
import org.generator.topo.node.phy.Intf;
import org.generator.topo.node.phy.PhyNode;
import org.generator.util.graph.AbstractEdge;
import org.generator.util.graph.Edge;

public class RelationEdge extends AbstractEdge<TopoNode> {
    public RelationEdge(TopoNode src, TopoNode dst) {
        super(src, dst);
        generateType();
    }

    public RelationEdge(TopoNode src, TopoNode dst, EdgeType type){
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
        LINK
    }

    protected void generateType(){
        if (src instanceof Intf && dst instanceof  Intf){
            setType(EdgeType.LINK);
            return;
        }
        switch (dst){
            case Intf ignored -> setType(EdgeType.INTF);
            case PhyNode ignored -> setType(EdgeType.PhyNODE);
            case OSPF ignored -> setType(EdgeType.OSPF);
            case OSPFIntf ignored -> setType(EdgeType.OSPFINTF);
            case OSPFArea ignored -> setType(EdgeType.OSPFAREA);
            case OSPFNetwork ignored -> setType(EdgeType.OSPFNetwork);
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
