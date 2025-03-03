package org.generator.lib.item.conf.node.isis;

import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.AbstractNode;


public class ISISAreaSum extends AbstractNode {

    public ISISAreaSum(String name){
        setName(name);
        setNodeType(NodeType.ISISAreaSum);
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
