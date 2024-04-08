package org.generator.lib.item.conf.node.phy;

import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.AbstractNode;

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


    //    @Override
//    public String getNodeAtrriStr() {
//        return String.format("{type:%s}", getNodeType());
//    }
}
