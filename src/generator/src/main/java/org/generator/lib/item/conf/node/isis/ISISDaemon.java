package org.generator.lib.item.conf.node.isis;
import org.generator.lib.item.conf.node.NodeType;

import java.util.Optional;

import org.generator.lib.item.conf.node.AbstractNode;
import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;

import java.util.Arrays;

public class ISISDaemon extends AbstractNode {
    /*
     * 1. overloadbitonstartup
     * 2. lspmtu
     * 3. metricstyle
     * 4. advertisehighmetrics
     * 5. setoverloadbit
     */
    public ISISDaemon(String name){
        setName(name);
        setNodeType(NodeType.ISISDaemon);
        initFiled();
    }

    boolean setoverloadbit;

    public boolean isSetoverloadbit() {
        return setoverloadbit;
    }

    public void setSetoverloadbit(boolean setoverloadbit) {
        this.setoverloadbit = setoverloadbit;
    }

    int overloadbitonstartup;

    public int getOverloadbitonstartup() {
        return overloadbitonstartup;
    }

    public void setOverloadbitonstartup(int overloadbitonstartup) {
        this.overloadbitonstartup = overloadbitonstartup;
    }

    int lspmtu;

    public int getLspmtu() {
        return lspmtu;
    }

    public void setLspmtu(int lspmtu) {
        this.lspmtu = lspmtu;
    }

    int LspgenintervalLevel1;
    int LspgenintervalLevel2;

    public int getLspgenintervalLevel1() {
        return LspgenintervalLevel1;
    }

    public void setLspgenintervalLevel1(int lspgenintervalLevel1) {
        LspgenintervalLevel1 = lspgenintervalLevel1;
    }

    public int getLspgenintervalLevel2() {
        return LspgenintervalLevel2;
    }

    public void setLspgenintervalLevel2(int lspgenintervalLevel2) {
        LspgenintervalLevel2 = lspgenintervalLevel2;
    }

    int spfintervalLevel1;
    int spfintervalLevel2;

    public int getSpfintervalLevel1() {
        return spfintervalLevel1;
    }

    public void setSpfintervalLevel1(int spfintervalLevel1) {
        this.spfintervalLevel1 = spfintervalLevel1;
    }

    public int getSpfintervalLevel2() {
        return spfintervalLevel2;
    }

    public void setSpfintervalLevel2(int spfintervalLevel2) {
        this.spfintervalLevel2 = spfintervalLevel2;
    }



    // public enum metricstyle implements StringEnum{
    //     NARROW("narrow"),
    //     TRANSITION("transition"),
    //     WIDE("wide");

    //     private final String template;
    //     metricstyle(String template){
    //         this.template = template;
    //     }

    //     @Override
    //     public boolean match(String st) {
    //         return new AbstractStringEnum(template).match(st);
    //     }

    //     static public Optional<metricstyle> of(String st){
    //         return Arrays.stream(metricstyle.values())
    //                 .filter(x -> x.match(st))
    //                 .findFirst();
    //     }

    //     @Override
    //     public String toString() {
    //         return template;
    //     }
    // }

    // metricstyle metricStyle;

    // public metricstyle getMetricStyle() {
    //     return metricStyle;
    // }

    // public void setMetricStyle(metricstyle metricStyle) {
    //     this.metricStyle = metricStyle;
    // }

    boolean advertisehighmetrics;

    public boolean isAdvertisehighmetrics() {
        return advertisehighmetrics;
    }

    public void setAdvertisehighmetrics(boolean advertisehighmetrics) {
        this.advertisehighmetrics = advertisehighmetrics;
    }

    @Override
    public void initFiled() {
        setoverloadbit = false;
        overloadbitonstartup = 0;
        lspmtu = 1492; 
        //metricStyle = metricstyle.WIDE;
        advertisehighmetrics = false;
        LspgenintervalLevel1 = 30;
        LspgenintervalLevel2 = 30;
        spfintervalLevel1 = 1;
        spfintervalLevel2 = 1;
    }

    //    @Override
//    public String getNodeAtrriStr() {
//        return "";
//    }
}
