package org.generator.lib.item.conf.node.phy;

import org.generator.lib.item.conf.node.NodeType_ISIS;
import org.generator.lib.item.conf.node.AbstractNode_ISIS;

public class Router_ISIS extends AbstractNode_ISIS {
    public Router_ISIS(String name){
          super();
          setName(name);
          setNodeType(NodeType_ISIS.Router);
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
