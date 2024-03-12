package org.generator.lib.generator.controller;

import org.generator.lib.item.IR.OpAnalysis;


/**
 * if current < capacity, just add
 * if current >= capacity
 *  if current.active < capacity
 *      random move one removed and add
 *  else fail
 */
public class CapacityController extends NormalController {
    int cap;

    GenConfig new_config;
    CapacityController(int cap, int rr, int ra, int ar, int aa){
        this.cap = cap;
        new_config = new GenConfig(rr, ra, ar, aa);
    }

    public static CapacityController of(int cap, int rr, int ra, int ar, int aa){return new CapacityController(cap, rr, ra, ar, aa);}

    public boolean canAddConfig(){
        return getActiveOpas().size() < cap;
    }

    public boolean addConfig(OpAnalysis opa) {
        if (getOpas().size() < cap) {
            super.addConfig(opa, new_config.rr, new_config.ra, new_config.ar, new_config.aa);
            return true;
        }
        else if (getActiveOpas().size() < cap){
            //TODO random remove one
            delConfig(getREMOVEDOpas().get(0));
            super.addConfig(opa, new_config.rr, new_config.ra, new_config.ar, new_config.aa);
            return true;
        } else return false;
    }
}
