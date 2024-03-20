package org.generator.lib.item.topo.node.phy;

import org.generator.lib.item.topo.node.NodeType;
import org.generator.lib.item.topo.node.AbstractNode;

public class Router extends AbstractNode {
    public Router(String name){
          super();
          setName(name);
          setNodeType(NodeType.Router);
          initFiled();
    }


    private String calcName(int id){
        return String.format("s%d", id);
    }

    @Override
    public void initFiled() {

    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    //    @Override
//    public String getNodeAtrriStr() {
//        return String.format("{type:%s}", getNodeType());
//    }
}
