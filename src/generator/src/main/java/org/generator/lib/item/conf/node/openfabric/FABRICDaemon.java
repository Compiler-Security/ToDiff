package org.generator.lib.item.conf.node.openfabric;
import org.generator.lib.item.conf.node.NodeType;

import java.util.Optional;

import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;

import java.util.Arrays;

public class FABRICDaemon extends AbstractNode {
    /*
     * 1. overloadbitonstartup
     * 2. lspmtu
     * 3. metricstyle
     * 4. advertisehighmetrics
     * 5. setoverloadbit
     */
    public FABRICDaemon(String name){
        setName(name);
        setNodeType(NodeType.FABRICDaemon);
        initFiled();
    }

    boolean setoverloadbit;

    public boolean isSetoverloadbit() {
        return setoverloadbit;
    }

    public void setSetoverloadbit(boolean setoverloadbit) {
        this.setoverloadbit = setoverloadbit;
    }



    int Lspgeninterval;


    public int getLspgeninterval() {
        return Lspgeninterval;
    }

    public void setLspgeninterval(int lspgeninterval) {
        Lspgeninterval = lspgeninterval;
    }


    int spfinterval;


    public int getSpfinterval() {
        return spfinterval;
    }

    public void setSpfinterval(int spfinterval) {
        this.spfinterval = spfinterval;
    }

    int tier;

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }


    @Override
    public void initFiled() {
        setoverloadbit = false;
        Lspgeninterval = 30;
        spfinterval = 1;
        tier = 2;
    }

    //    @Override
//    public String getNodeAtrriStr() {
//        return "";
//    }
}
