package org.generator.lib.generator.isis.controller;

import org.generator.lib.generator.driver.generate_ISIS;
import org.generator.lib.item.IR.OpAnalysis_ISIS;
import org.generator.util.ran.ranHelper;


/**
 * if current < capacity, just add
 * if current >= capacity
 *  if current.active < capacity
 *      random move one removed and add
 *  else fail
 */
public class CapacityController_ISIS extends NormalController_ISIS {
    int cap;

    GenConfig new_config;
    CapacityController_ISIS(int cap, int rr, int ra, int ar, int aa){
        this.cap = cap;
        new_config = new GenConfig(rr, ra, ar, aa);
    }

    public static CapacityController_ISIS of(int cap, int rr, int ra, int ar, int aa){return new CapacityController_ISIS(cap, rr, ra, ar, aa);}

    public boolean canAddConfig(){
        return getActiveOpas().size() < cap;
    }

    public boolean addConfig(OpAnalysis_ISIS opa) {
        if (getOpas().size() < cap) {
            super.addConfig(opa, new_config.rr, new_config.ra, new_config.ar, new_config.aa, OpAnalysis_ISIS.STATE.ACTIVE, OpAnalysis_ISIS.STATE.ACTIVE);
            getConfigOfOpa(opa).state = OpAnalysis_ISIS.STATE.ACTIVE;
            return true;
        }
        else if (getActiveOpas().size() < cap){
            if (generate_ISIS.ran){
                delConfig(getREMOVEDOpas().get(ranHelper.randomInt(0, getREMOVEDOpas().size() - 1)));
            }else delConfig(getREMOVEDOpas().get(0));
            super.addConfig(opa, new_config.rr, new_config.ra, new_config.ar, new_config.aa, OpAnalysis_ISIS.STATE.ACTIVE, OpAnalysis_ISIS.STATE.ACTIVE);
            getConfigOfOpa(opa).state = OpAnalysis_ISIS.STATE.ACTIVE;
            return true;
        } else return false;
    }
}
