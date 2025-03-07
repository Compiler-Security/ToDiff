package org.generator.lib.item.conf.node.openfabric;

import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.AbstractNode;

public class FABRICIntf extends AbstractNode{
    /*
     * element

     */
    public FABRICIntf(String name){
        setName(name);
        setNodeType(NodeType.FABRICIntf);
        initFiled();
    }

//    public enum NetType()
    public int getVrf() {
        return vrf;
    }

    public void setVrf(int vrf) {
        this.vrf = vrf;
    }

    boolean iproutefabric;
    public boolean isIproutefabric() {
        return iproutefabric;
    }

    public void setIproutefabric(boolean iproutefabric) {
        this.iproutefabric = iproutefabric;
    }




    public boolean isPassive() {
        return passive;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    boolean passive;
    int vrf;

    @Override
    public void initFiled() {
        passive = false;
        vrf = 0;
        // FIXME: helloIntervalLevel1 and helloIntervalLevel2 should be set to what?
        helloInterval = 3;  //timerHelloInMsecs
        iproutefabric = false;
        csnpInterval = 10; //timerCsnpInMsecs 
        psnpInterval = 2; //timerPsnpInMsecs
        helloMultiplier = 10; //helloMultiplierLevel1

    }

    // Add separate hello intervals and priorities for level-1 and level-2
    int helloInterval;


    // Getters and setters for level-1 hello interval
    public int getHelloInterval() {
        return helloInterval;
    }

    public void setHelloInterval(int helloInterval) {
        this.helloInterval = helloInterval;
    }

    int csnpInterval;
    int psnpInterval;

    public int getCsnpInterval() {
        return csnpInterval;
    }

    public void setCsnpInterval(int csnpInterval) {
        this.csnpInterval = csnpInterval;
    }


    public int getPsnpInterval() {
        return psnpInterval;
    }

    public void setPsnpInterval(int psnpInterval) {
        this.psnpInterval = psnpInterval;
    }


    int helloMultiplier;


    public int getHelloMultiplier() {
        return helloMultiplier;
    }

    public void setHelloMultiplier(int helloMultiplier) {
        this.helloMultiplier = helloMultiplier;
    }


    


    //    @Override
//    public String getNodeAtrriStr() {
//        return String.format("{type:%s, area: %s, vrf:%d, cost:%d}",  getNodeType(), getArea(), getVrf(), getCost());
//    }
}
