package org.generator.lib.generator.phy.controller;

import org.generator.lib.frontend.lexical.OpType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NormalController {

    public enum CType{
        NODE,
        INTF,
        LINK,
        OSPF
    }

    public CType getcType() {
        return cType;
    }

    public void setcType(CType cType) {
        this.cType = cType;
    }

    public OpType getCurType() {
        return curType;
    }

    public OpType getFinalType() {
        return finalType;
    }

    public void setFinalType(OpType finalType) {
        this.finalType = finalType;
    }

    public OpType finalType;
    CType cType;
    List<Integer> counter;
    Map<Integer, OpType> intToType;

    Map<OpType, Integer> typeToInt;

    public boolean equalName(String name){
        return (this.name == null && name == null) || (this.name != null && this.name.matches(name));
    }

    public boolean partialEqualName2(String name2){
        return (name2 == null) || (this.name2 != null && this.name2.matches(name2));
    }
    public NormalController(List<Integer> counter, List<OpType> opTypes, String name, String name2, OpType curType, CType cType){
        this.counter = new ArrayList<>();
        this.counter.addAll(counter);
        intToType = new HashMap<>();
        typeToInt = new HashMap<>();
        finalType = curType;
        this.name = name;
        this.name2 = name2;
        this.curType = curType;
        this.cType = cType;
        for(var i = 0; i < opTypes.size(); i++){
            intToType.put(i, opTypes.get(i));
            typeToInt.put(opTypes.get(i), i);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    String name, name2;
    OpType curType;
    public int getCounterOfType(OpType opType){
        return counter.get(typeToInt.get(opType));
    }

    public List<OpType> getAllTypes(){
        return typeToInt.keySet().stream().toList();
    }
    public void setCurType(OpType opType){
        curType = opType;
    }

    public void deltaTypeNum(OpType type, int deltaNum){
        if (type == null) return;
        int idx = typeToInt.get(type);
        counter.set(idx, counter.get(idx) + deltaNum);
    }
    public void deltaAllTypeNum(int deltaNum){
        for(int i = 0; i < counter.size(); i++){
            counter.set(i, counter.get(i) + deltaNum);
        }
    }
    public List<OpType> getPossibleTypes(){
        var l = typeToInt.keySet().stream().filter(typ -> (getCounterOfType(typ) > 1 && typ == getFinalType()) || (getCounterOfType(typ) > 0 && typ != getFinalType())).collect(Collectors.toList());
        if (!l.isEmpty()){
            if (cType == CType.LINK){
                if (getCounterOfType(OpType.LINKADD) == 1){
                    return l.stream().filter(x -> x != OpType.LINKREMOVE).collect(Collectors.toList());
                }
            }
            return l;
        }
        if (getCounterOfType(finalType) > 0) return Stream.of(finalType).toList();
        else return new ArrayList<>();
    }

    public void consumeOneType(OpType opType){
        assert getCounterOfType(opType) > 0;
        counter.set(typeToInt.get(opType), getCounterOfType(opType) - 1);
    }

    static public NormalController getNodeCatg(int add, int del, String name, OpType type, CType cType){
        return new NormalController(List.of(add, del), List.of(OpType.NODEADD, OpType.NODEDEL), name, null, type, cType);
    }

    static public NormalController getIntfCatg(int up, int down, String name, OpType type, CType cType){
        return new NormalController(List.of(up, down), List.of(OpType.INTFUP, OpType.INTFDOWN), name, null, type, cType);
    }

    static public NormalController getLinkCatg(int add, int down, int remove, String name, String name2, OpType type, CType cType){
        return  new NormalController(List.of(add, down, remove), List.of(OpType.LINKADD, OpType.LINKDOWN, OpType.LINKREMOVE), name, name2, type, cType);
    }

    static public NormalController getOSPFCatg(int up, int re, int shutDown, String name, OpType type, CType cType){
        return new NormalController(List.of(up, re, shutDown), List.of(OpType.NODESETOSPFUP, OpType.NODESETOSPFRE, OpType.NODESETOSPFSHUTDOWN), name, null, type, cType);
    }


    @Override
    public String toString() {
        return "NormalController{" +
                "cType=" + cType +
                ", counter=" + counter +
                ", name='" + name + '\'' +
                ", name2='" + name2 + '\'' +
                ", curType=" + curType +
                '}';
    }
}
