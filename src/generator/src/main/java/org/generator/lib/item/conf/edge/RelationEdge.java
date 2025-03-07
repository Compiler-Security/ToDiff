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
        LINK,
        //========OSPF===========
        OSPF,
        OSPFINTF,
        OSPFAREA,
        OSPFNetwork,
        OSPFDAEMON,
        OSPFAREASUM,
        //==========RIP============
        RIP,
        RIPINTF,

        //MULTI:
        //=========ISIS============
        ISIS,
        ISISINTF,
        ISISDAEMON,

        //==========OpenFabric============
        FABRIC,
        FABRICINTF,
        FABRICDAEMON
    }

    protected void generateType(){
        if (src.getNodeType() == NodeType.Intf && dst.getNodeType() == NodeType.Intf){
            setType(EdgeType.LINK);
            return;
        }
        switch (dst.getNodeType()){
            case Intf -> setType(EdgeType.INTF);
            case Router, Host, Switch -> setType(EdgeType.PhyNODE);
            case OSPF-> setType(EdgeType.OSPF);
            case OSPFIntf  -> setType(EdgeType.OSPFINTF);
            case OSPFArea -> setType(EdgeType.OSPFAREA);
            case OSPFNet -> setType(EdgeType.OSPFNetwork);
            case RIP -> setType(EdgeType.RIP);
            case RIPIntf -> setType(EdgeType.RIPINTF);
            //MULTI:
            case ISIS -> setType(EdgeType.ISIS);
            case ISISIntf -> setType(EdgeType.ISISINTF);
            case FABRIC -> setType(EdgeType.FABRIC);
            case FABRICIntf -> setType(EdgeType.FABRICINTF);
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
