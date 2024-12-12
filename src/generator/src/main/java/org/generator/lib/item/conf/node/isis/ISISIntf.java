package org.generator.lib.item.conf.node.isis;

import org.generator.lib.item.conf.node.NodeType_ISIS;
import org.generator.lib.item.conf.node.AbstractNode_ISIS;
import org.generator.util.collections.AbstractStringEnum;
import org.generator.util.collections.StringEnum;

import java.util.Arrays;
import java.util.Optional;

public class ISISIntf extends AbstractNode_ISIS {
    /*
     * element
     * 1. vrf
     * 2. passive
     * 3. netType
     * 4. level
     * 5. helloIntervalLevel1
     * 6. helloIntervalLevel2
     * 7. priorityLevel1
     * 8. priorityLevel2
     * 9. csnpIntervalLevel1
     * 10. csnpIntervalLevel2
     * 11. psnpIntervalLevel1
     * 12. psnpIntervalLevel2
     * 13. helloMultiplierlevel1
     * 14. helloMultiplierlevel2
     * 15. metricLevel1
     * 16. metricLevel2
     */
    public ISISIntf(String name){
        setName(name);
        setNodeType(NodeType_ISIS.ISISIntf);
        initFiled();
    }

//    public enum NetType()
    public int getVrf() {
        return vrf;
    }

    public void setVrf(int vrf) {
        this.vrf = vrf;
    }




    public boolean isPassive() {
        return passive;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    boolean passive;
    int vrf;



    public enum ISISNetType implements StringEnum {
        BROADCAST("broadcast"),
        POINTTOPOINT("point-to-point");


        private final String template;
        ISISNetType(String template){this.template = template;}

        @Override
        public boolean match(String st) {
            return new AbstractStringEnum(template).match(st);
        }

        static public Optional<ISISNetType> of(String st){
            return Arrays.stream(ISISNetType.values())
                    .filter(x -> x.match(st))
                    .findFirst();
        }

        @Override
        public String toString() {
            return template;
        }
    }


    public ISISNetType getNetType() {
        return netType;
    }

    public void setNetType(ISISNetType netType) {
        this.netType = netType;
    }

    ISISNetType netType;

    public enum ISISLEVEL implements StringEnum {
        LEVEL1("level-1"),
        LEVEL2("level-2"),
        LEVEL1_2("level-1-2");

        private final String template;
        ISISLEVEL(String template){this.template = template;}

        @Override
        public boolean match(String st) {
            return new AbstractStringEnum(template).match(st);
        }

        static public Optional<ISISLEVEL> of(String st){
            return Arrays.stream(ISISLEVEL.values())
                    .filter(x -> x.match(st))
                    .findFirst();
        }

        @Override
        public String toString() {
            return template;
        }
    }

    public ISISLEVEL getLevel() {
        return level;
    }

    public void setLevel(ISISLEVEL level) {
        this.level = level;
    }

    ISISLEVEL level;


    @Override
    public void initFiled() {
        passive = false;
        vrf = 0;
        // FIXME: helloIntervalLevel1 and helloIntervalLevel2 should be set to what?
        helloIntervalLevel1 = 10;  //timerHelloInMsecs
        helloIntervalLevel2 = 10;  //timerHelloInMsecs

        netType = ISISNetType.BROADCAST;
        level = ISISLEVEL.LEVEL1;
        priorityLevel1 = 1; //prioritylevel1
        priorityLevel2 = 1; //prioritylevel2

        csnpIntervalLevel1 = 10; //timerCsnpInMsecs
        csnpIntervalLevel2 = 10; //timerCsnpInMsecs
        psnpIntervalLevel1 = 10; //timerPsnpInMsecs
        psnpIntervalLevel2 = 10; //timerPsnpInMsecs

        helloMultiplierlevel1 = 3; //helloMultiplierLevel1
        helloMultiplierlevel2 = 3; //helloMultiplierLevel2

        metricLevel1 = 10; //metricLevel1
        metricLevel2 = 10; //metricLevel2
    }

    // Add separate hello intervals and priorities for level-1 and level-2
    int helloIntervalLevel1;
    int helloIntervalLevel2;
    int priorityLevel1;
    int priorityLevel2;

    // Getters and setters for level-1 hello interval
    public int getHelloIntervalLevel1() {
        return helloIntervalLevel1;
    }

    public void setHelloIntervalLevel1(int helloIntervalLevel1) {
        this.helloIntervalLevel1 = helloIntervalLevel1;
    }

    // Getters and setters for level-2 hello interval
    public int getHelloIntervalLevel2() {
        return helloIntervalLevel2;
    }

    public void setHelloIntervalLevel2(int helloIntervalLevel2) {
        this.helloIntervalLevel2 = helloIntervalLevel2;
    }

    // Getters and setters for level-1 priority
    public int getPriorityLevel1() {
        return priorityLevel1;
    }

    public void setPriorityLevel1(int priorityLevel1) {
        this.priorityLevel1 = priorityLevel1;
    }

    // Getters and setters for level-2 priority
    public int getPriorityLevel2() {
        return priorityLevel2;
    }

    public void setPriorityLevel2(int priorityLevel2) {
        this.priorityLevel2 = priorityLevel2;
    }

    // Method to get hello interval based on level
    public int getHelloInterval(ISISLEVEL level) {
        switch (level) {
            case LEVEL1:
                return helloIntervalLevel1;
            case LEVEL2:
                return helloIntervalLevel2;
            default:
                throw new IllegalArgumentException("Invalid level");
        }
    }

    // Method to get priority based on level
    public int getPriority(ISISLEVEL level) {
        switch (level) {
            case LEVEL1:
                return priorityLevel1;
            case LEVEL2:
                return priorityLevel2;
            default:
                throw new IllegalArgumentException("Invalid level");
        }
    }

    int csnpIntervalLevel1;
    int csnpIntervalLevel2;
    int psnpIntervalLevel1;
    int psnpIntervalLevel2;

    public int getCsnpIntervalLevel1() {
        return csnpIntervalLevel1;
    }

    public void setCsnpIntervalLevel1(int csnpIntervalLevel1) {
        this.csnpIntervalLevel1 = csnpIntervalLevel1;
    }

    public int getCsnpIntervalLevel2() {
        return csnpIntervalLevel2;
    }

    public void setCsnpIntervalLevel2(int csnpIntervalLevel2) {
        this.csnpIntervalLevel2 = csnpIntervalLevel2;
    }

    public int getPsnpIntervalLevel1() {
        return psnpIntervalLevel1;
    }

    public void setPsnpIntervalLevel1(int psnpIntervalLevel1) {
        this.psnpIntervalLevel1 = psnpIntervalLevel1;
    }

    public int getPsnpIntervalLevel2() {
        return psnpIntervalLevel2;
    }

    public void setPsnpIntervalLevel2(int psnpIntervalLevel2) {
        this.psnpIntervalLevel2 = psnpIntervalLevel2;
    }

    public int getCsnpInterval(ISISLEVEL level) {
        switch (level) {
            case LEVEL1:
                return csnpIntervalLevel1;
            case LEVEL2:
                return csnpIntervalLevel2;
            default:
                throw new IllegalArgumentException("Invalid level");
        }
    }

    public int getPsnpInterval(ISISLEVEL level) {
        switch (level) {
            case LEVEL1:
                return psnpIntervalLevel1;
            case LEVEL2:
                return psnpIntervalLevel2;
            default:
                throw new IllegalArgumentException("Invalid level");
        }
    }

    int helloMultiplierlevel1;
    int helloMultiplierlevel2;

    public int getHelloMultiplierlevel1() {
        return helloMultiplierlevel1;
    }

    public void setHelloMultiplierlevel1(int helloMultiplierlevel1) {
        this.helloMultiplierlevel1 = helloMultiplierlevel1;
    }

    public int getHelloMultiplierlevel2() {
        return helloMultiplierlevel2;
    }

    public void setHelloMultiplierlevel2(int helloMultiplierlevel2) {
        this.helloMultiplierlevel2 = helloMultiplierlevel2;
    }

    public int getHelloMulti(ISISLEVEL level) {
        switch (level) {
            case LEVEL1:
                return helloMultiplierlevel1;
            case LEVEL2:
                return helloMultiplierlevel2;
            default:
                throw new IllegalArgumentException("Invalid level");
        }
    }

    int metricLevel1;
    int metricLevel2;

    public int getMetricLevel1() {
        return metricLevel1;
    }

    public void setMetricLevel1(int metricLevel1) {
        this.metricLevel1 = metricLevel1;
    }

    public int getMetricLevel2() {
        return metricLevel2;
    }

    public void setMetricLevel2(int metricLevel2) {
        this.metricLevel2 = metricLevel2;
    }

    public int getMetric(ISISLEVEL level) {
        switch (level) {
            case LEVEL1:
                return metricLevel1;
            case LEVEL2:
                return metricLevel2;
            default:
                throw new IllegalArgumentException("Invalid level");
        }
    }




    //    @Override
//    public String getNodeAtrriStr() {
//        return String.format("{type:%s, area: %s, vrf:%d, cost:%d}",  getNodeType(), getArea(), getVrf(), getCost());
//    }
}
