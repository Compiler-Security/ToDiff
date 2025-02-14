package org.generator.lib.item.conf.node.isis;

import org.generator.lib.item.conf.node.NodeType_ISIS;
import org.generator.lib.item.conf.node.AbstractNode_ISIS;


public class ISISAreaSum extends AbstractNode_ISIS {

    public ISISAreaSum(String name){
        setName(name);
        setNodeType(NodeType_ISIS.ISISAreaSum);
        initFiled();
    }

    @Override
    public void initFiled() {
    }


    //    @Override
//    public String getNodeAtrriStr() {
//        new Unimplemented();
//        return "";
//    }
}
