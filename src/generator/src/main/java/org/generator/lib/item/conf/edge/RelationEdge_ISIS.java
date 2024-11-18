package org.generator.lib.item.conf.edge;

import org.generator.lib.item.conf.node.NodeType_ISIS;
import org.generator.lib.item.conf.node.AbstractNode_ISIS;
import org.generator.util.graph.AbstractEdge;

public class RelationEdge_ISIS extends AbstractEdge<AbstractNode_ISIS> {
    public RelationEdge_ISIS(AbstractNode_ISIS src, AbstractNode_ISIS dst) {
        super(src, dst);
        generateType();
    }

    public RelationEdge_ISIS(AbstractNode_ISIS src, AbstractNode_ISIS dst, EdgeType type){
        super(src, dst);
        this.type = type;
    }

    public enum EdgeType {
        INTF,
        PhyNODE,
        ISIS,
        ISISINTF,
        ISISAREA,
        ISISNetwork,
        LINK,
        ISISDAEMON,
        ISISAREASUM,
    }

    protected void generateType(){
        if (src.getNodeType() == NodeType_ISIS.Intf && dst.getNodeType() == NodeType_ISIS.Intf){
            setType(EdgeType.LINK);
            return;
        }
        switch (dst.getNodeType()){
            case Intf -> setType(EdgeType.INTF);
            case Router, Host, Switch -> setType(EdgeType.PhyNODE);
            case ISIS-> setType(EdgeType.ISIS);
            case ISISIntf  -> setType(EdgeType.ISISINTF);
            case ISISArea -> setType(EdgeType.ISISAREA);
            case ISISNet -> setType(EdgeType.ISISNetwork);
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
