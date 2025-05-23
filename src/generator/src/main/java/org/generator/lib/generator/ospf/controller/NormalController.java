package org.generator.lib.generator.ospf.controller;

import org.generator.lib.item.IR.OpAnalysis;

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
public  class NormalController {

    public class GenConfig{
        int ra, rr, ar, aa;
        OpAnalysis.STATE state, finalState;
        GenConfig(int rr, int ra, int ar, int aa){
            this.rr = rr;
            this.ra = ra;
            this.ar = ar;
            this.aa = aa;
            state = OpAnalysis.STATE.REMOVED;
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

        public List<OpAnalysis.STATE> getValidMoveStates(){
            List<OpAnalysis.STATE> valid_states = new ArrayList<>();
            if (state == OpAnalysis.STATE.REMOVED){
                if (this.ra > 0) valid_states.add(OpAnalysis.STATE.ACTIVE);
                if (this.rr > 0) valid_states.add(OpAnalysis.STATE.REMOVED);
            }else{
                if (this.ar > 0) valid_states.add(OpAnalysis.STATE.REMOVED);
                if (this.aa > 0) valid_states.add(OpAnalysis.STATE.ACTIVE);
            }
            return valid_states;
        }

        /**
         * Must be valid
         * @param target_state
         */
        public void moveToState(OpAnalysis.STATE target_state){
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
        public void revertToState(OpAnalysis.STATE target_state){
            switch (state){
                case REMOVED -> {
                    switch (target_state){
                        case REMOVED -> {assert  rr >= 0;}
                        case ACTIVE -> {
                            if (ra > 0) ra--;
                            if (ar == 0 && finalState == OpAnalysis.STATE.REMOVED){
                                ar++;
                            }
                        }
                    }
                }
                case ACTIVE -> {
                    switch (target_state){
                        case REMOVED -> {
                            if (ar > 0) ar--;
                            if (ra == 0 && finalState == OpAnalysis.STATE.ACTIVE) {
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
    protected HashMap<OpAnalysis, GenConfig> configMap;
    NormalController(){
        configMap = new HashMap<>();
    }
    public static NormalController of(){
        return new NormalController();
    }
    /**
     * use as update as well
     * @param opa
     */
    public boolean addConfig(OpAnalysis opa, int rr, int ra, int ar, int aa){
        configMap.put(opa, new GenConfig(rr, ra, ar, aa));
        return true;
    }

    public boolean addConfig(OpAnalysis opa, int rr, int ra, int ar, int aa, OpAnalysis.STATE cur_state, OpAnalysis.STATE final_state){
        configMap.put(opa, new GenConfig(rr, ra, ar, aa));
        configMap.get(opa).state = cur_state;
        configMap.get(opa).finalState = final_state;
        return true;
    }


    public List<OpAnalysis > getOpas(){
        return configMap.keySet().stream().toList();
    }
    public GenConfig delConfig(OpAnalysis opa){
        return configMap.remove(opa);
    }

    public OpAnalysis.STATE getConfigStateOfOpa(OpAnalysis opa){
        assert configMap.containsKey(opa);
        return configMap.get(opa).state;
    }

    public OpAnalysis.STATE getConfigFinalStateOfOpa(OpAnalysis opa){
        assert configMap.containsKey(opa);
        return configMap.get(opa).finalState;
    }

    public List<OpAnalysis.STATE> getValidMoveStatesOfOpa(OpAnalysis opa){
        return configMap.get(opa).getValidMoveStates();
    }

    public boolean canMoveStateOfOpa(OpAnalysis opa, OpAnalysis.STATE state){
        return getValidMoveStatesOfOpa(opa).contains(state);
    }

    public void moveToStateOfOpa(OpAnalysis opa, OpAnalysis.STATE target_state){
        configMap.get(opa).moveToState(target_state);
    }

    public void revertToStateOfOpa(OpAnalysis opa, OpAnalysis.STATE target_state){
        configMap.get(opa).revertToState(target_state);
    }

    public boolean canMoveOfOpa(OpAnalysis opa){
        return configMap.get(opa).canMove();
    }

    public boolean hasConfigOfOpa(OpAnalysis opa){
        return configMap.containsKey(opa);
    }

    public GenConfig getConfigOfOpa(OpAnalysis opa){
        assert configMap.containsKey(opa);
        return configMap.get(opa);
    }

    public List<OpAnalysis> getCanMoveOpas(){
        List<OpAnalysis> res = new ArrayList<>();
        for(var p: configMap.entrySet()){
            if (p.getValue().canMove()){
                res.add(p.getKey());
            }
        }
        return res;
    }

    public List<OpAnalysis> getNoMoveOpas(){
        List<OpAnalysis> res = new ArrayList<>();
        for(var p: configMap.entrySet()){
            if (!p.getValue().canMove()){
                res.add(p.getKey());
            }
        }
        return res;
    }

    public List<OpAnalysis> getActiveOpas(){
        List<OpAnalysis> res = new ArrayList<>();
        for(var p: configMap.entrySet()){
            if (p.getValue().state == OpAnalysis.STATE.ACTIVE){
                res.add(p.getKey());
            }
        }
        return res;
    }

    public List<OpAnalysis> getREMOVEDOpas(){
        List<OpAnalysis> res = new ArrayList<>();
        for(var p: configMap.entrySet()){
            if (p.getValue().state == OpAnalysis.STATE.REMOVED){
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
