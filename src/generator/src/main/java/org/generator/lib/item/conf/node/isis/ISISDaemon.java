package org.generator.lib.item.conf.node.isis;
import org.generator.lib.item.conf.node.NodeType_ISIS;

import java.util.Optional;

import org.generator.lib.item.conf.node.AbstractNode_ISIS;
import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;

import java.util.Arrays;

public class ISISDaemon extends AbstractNode_ISIS {
    /*
     * 1. overloadbitonstartup
     * 2. lspmtu
     * 3. metricstyle
     * 4. advertisehighmetrics
     * 5. setoverloadbit
     */
    public ISISDaemon(String name){
        setName(name);
        setNodeType(NodeType_ISIS.ISISDaemon);
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
    }

    //    @Override
//    public String getNodeAtrriStr() {
//        return "";
//    }
}
