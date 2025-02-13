package org.generator.lib.generator.isis.controller;

import org.generator.lib.item.IR.OpAnalysis_ISIS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Config gives the limit of generation of operation
 * It contains a state machine tracks the given operation
 * The state machine is
 *
 * (selfLoop)REMOVED <-> ACTIVE(selfLoop)
 *
 * transition     target_op target STATE
 * REMOVED->ACTIVE | ACTIVE
 * REMOVED->REMOVED   | REMOVED
 * ACTIVE->REMOVED | REMOVED
 * ACTIVE->ACTIVE | ACTIVE
 */
public  class NormalController_ISIS {

    public class GenConfig{
        int ra, rr, ar, aa;
        OpAnalysis_ISIS.STATE state, finalState;
        GenConfig(int rr, int ra, int ar, int aa){
            this.rr = rr;
            this.ra = ra;
            this.ar = ar;
            this.aa = aa;
            state = OpAnalysis_ISIS.STATE.REMOVED;
        }

        @Override
        public String toString() {
            return "\nGenConfig{" +
                    "ra=" + ra +
                    ", rr=" + rr +
                    ", ar=" + ar +
                    ", aa=" + aa +
                    ", state=" + state +
                    ", final_state=" + finalState +
                    "}\n";

        }

        public List<OpAnalysis_ISIS.STATE> getValidMoveStates(){
            List<OpAnalysis_ISIS.STATE> valid_states = new ArrayList<>();
            if (state == OpAnalysis_ISIS.STATE.REMOVED){
                if (this.ra > 0) valid_states.add(OpAnalysis_ISIS.STATE.ACTIVE);
                if (this.rr > 0) valid_states.add(OpAnalysis_ISIS.STATE.REMOVED);
            }else{
                if (this.ar > 0) valid_states.add(OpAnalysis_ISIS.STATE.REMOVED);
                if (this.aa > 0) valid_states.add(OpAnalysis_ISIS.STATE.ACTIVE);
            }
            return valid_states;
        }

        /**
         * Must be valid
         * @param target_state
         */
        public void moveToState(OpAnalysis_ISIS.STATE target_state){
            switch (state){
                case REMOVED -> {
                    switch (target_state){
                        case REMOVED -> {rr--; assert  rr >= 0;}
                        case ACTIVE -> {ra--; assert  ra >= 0;}
                    }
                }
                case ACTIVE -> {
                    switch (target_state){
                        case REMOVED -> {ar--; assert ar >= 0;}
                        case ACTIVE -> {aa--; assert  aa >= 0;}
                    }
                }
            }
            state = target_state;
        }

        /**
         * state A -> state B (A != B)
         * set cur_state = state B
         * B -> A ++
         * @param target_state
         */
        public void revertToState(OpAnalysis_ISIS.STATE target_state){
            switch (state){
                case REMOVED -> {
                    switch (target_state){
                        case REMOVED -> {assert  rr >= 0;}
                        case ACTIVE -> {
                            if (ra > 0) ra--;
                            if (ar == 0 && finalState == OpAnalysis_ISIS.STATE.REMOVED){
                                ar++;
                            }
                        }
                    }
                }
                case ACTIVE -> {
                    switch (target_state){
                        case REMOVED -> {
                            if (ar > 0) ar--;
                            if (ra == 0 && finalState == OpAnalysis_ISIS.STATE.ACTIVE) {
                                ra++;
                            }
                        }
                        case ACTIVE -> {assert  aa >= 0;}
                    }
                }
            }
            state = target_state;
        }

        public boolean canMove(){
            return !getValidMoveStates().isEmpty();
        }

        public  GenConfig copy(){
            return new GenConfig(rr, ra, ar, aa);
        }
    }
    protected HashMap<OpAnalysis_ISIS, GenConfig> configMap;
    NormalController_ISIS(){
        configMap = new HashMap<>();
    }
    public static NormalController_ISIS of(){
        return new NormalController_ISIS();
    }
    /**
     * use as update as well
     * @param opa
     */
    public boolean addConfig(OpAnalysis_ISIS opa, int rr, int ra, int ar, int aa){
        configMap.put(opa, new GenConfig(rr, ra, ar, aa));
        return true;
    }

    public boolean addConfig(OpAnalysis_ISIS opa, int rr, int ra, int ar, int aa, OpAnalysis_ISIS.STATE cur_state, OpAnalysis_ISIS.STATE final_state){
        configMap.put(opa, new GenConfig(rr, ra, ar, aa));
        configMap.get(opa).state = cur_state;
        configMap.get(opa).finalState = final_state;
        return true;
    }


    public List<OpAnalysis_ISIS > getOpas(){
        return configMap.keySet().stream().toList();
    }
    public GenConfig delConfig(OpAnalysis_ISIS opa){
        return configMap.remove(opa);
    }

    public OpAnalysis_ISIS.STATE getConfigStateOfOpa(OpAnalysis_ISIS opa){
        assert configMap.containsKey(opa);
        return configMap.get(opa).state;
    }

    public OpAnalysis_ISIS.STATE getConfigFinalStateOfOpa(OpAnalysis_ISIS opa){
        assert configMap.containsKey(opa);
        return configMap.get(opa).finalState;
    }

    public List<OpAnalysis_ISIS.STATE> getValidMoveStatesOfOpa(OpAnalysis_ISIS opa){
        return configMap.get(opa).getValidMoveStates();
    }

    public boolean canMoveStateOfOpa(OpAnalysis_ISIS opa, OpAnalysis_ISIS.STATE state){
        return getValidMoveStatesOfOpa(opa).contains(state);
    }

    public void moveToStateOfOpa(OpAnalysis_ISIS opa, OpAnalysis_ISIS.STATE target_state){
        configMap.get(opa).moveToState(target_state);
    }

    public void revertToStateOfOpa(OpAnalysis_ISIS opa, OpAnalysis_ISIS.STATE target_state){
        configMap.get(opa).revertToState(target_state);
    }

    public boolean canMoveOfOpa(OpAnalysis_ISIS opa){
        return configMap.get(opa).canMove();
    }

    public boolean hasConfigOfOpa(OpAnalysis_ISIS opa){
        return configMap.containsKey(opa);
    }

    public GenConfig getConfigOfOpa(OpAnalysis_ISIS opa){
        assert configMap.containsKey(opa);
        return configMap.get(opa);
    }

    public List<OpAnalysis_ISIS> getCanMoveOpas(){
        List<OpAnalysis_ISIS> res = new ArrayList<>();
        for(var p: configMap.entrySet()){
            if (p.getValue().canMove()){
                res.add(p.getKey());
            }
        }
        return res;
    }

    public List<OpAnalysis_ISIS> getNoMoveOpas(){
        List<OpAnalysis_ISIS> res = new ArrayList<>();
        for(var p: configMap.entrySet()){
            if (!p.getValue().canMove()){
                res.add(p.getKey());
            }
        }
        return res;
    }

    public List<OpAnalysis_ISIS> getActiveOpas(){
        List<OpAnalysis_ISIS> res = new ArrayList<>();
        for(var p: configMap.entrySet()){
            if (p.getValue().state == OpAnalysis_ISIS.STATE.ACTIVE){
                res.add(p.getKey());
            }
        }
        return res;
    }

    public List<OpAnalysis_ISIS> getREMOVEDOpas(){
        List<OpAnalysis_ISIS> res = new ArrayList<>();
        for(var p: configMap.entrySet()){
            if (p.getValue().state == OpAnalysis_ISIS.STATE.REMOVED){
                res.add(p.getKey());
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return "NormalController{" +
                "configMap=" + configMap +
                '}';
    }
}
