package org.generator.lib.generator.phy.controller;

import org.generator.lib.frontend.lexical.OpType_isis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NormalController_ISIS {

    public enum CType{
        NODE,
        INTF,
        LINK,
        ISIS
    }

    public CType getcType() {
        return cType;
    }

    public void setcType(CType cType) {
        this.cType = cType;
    }

    public OpType_isis getCurType() {
        return curType;
    }

    public OpType_isis getFinalType() {
        return finalType;
    }

    public void setFinalType(OpType_isis finalType) {
        this.finalType = finalType;
    }

    public OpType_isis finalType;
    CType cType;
    List<Integer> counter;
    Map<Integer, OpType_isis> intToType;

    Map<OpType_isis, Integer> typeToInt;

    public boolean equalName(String name){
        return (this.name == null && name == null) || (this.name != null && this.name.matches(name));
    }

    public boolean partialEqualName2(String name2){
        return (name2 == null) || (this.name2 != null && this.name2.matches(name2));
    }
    public NormalController_ISIS(List<Integer> counter, List<OpType_isis> opTypes, String name, String name2, OpType_isis curType, CType cType){
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
    OpType_isis curType;
    public int getCounterOfType(OpType_isis opType){
        return counter.get(typeToInt.get(opType));
    }

    public List<OpType_isis> getAllTypes(){
        return typeToInt.keySet().stream().toList();
    }
    public void setCurType(OpType_isis opType){
        curType = opType;
    }

    public void deltaTypeNum(OpType_isis type, int deltaNum){
        if (type == null) return;
        int idx = typeToInt.get(type);
        counter.set(idx, counter.get(idx) + deltaNum);
    }
    public void deltaAllTypeNum(int deltaNum){
        for(int i = 0; i < counter.size(); i++){
            counter.set(i, counter.get(i) + deltaNum);
        }
    }
    public List<OpType_isis> getPossibleTypes(){
        if (getCounterOfType(finalType) == 1){
            var otherTypes = typeToInt.keySet().stream().filter(typ -> getCounterOfType(typ) > 0 && typ != getFinalType()).collect(Collectors.toList());
            if (!otherTypes.isEmpty()) return otherTypes;
            else return Stream.of(finalType).toList();
        }else{
            return typeToInt.keySet().stream().filter(typ -> getCounterOfType(typ) > 0).collect(Collectors.toList());
        }
    }

    public void consumeOneType(OpType_isis opType){
        assert getCounterOfType(opType) > 0;
        counter.set(typeToInt.get(opType), getCounterOfType(opType) - 1);
    }

    static public NormalController_ISIS getNodeCatg(int add, int del, String name, OpType_isis type, CType cType){
        return new NormalController_ISIS(List.of(add, del), List.of(OpType_isis.NODEADD, OpType_isis.NODEDEL), name, null, type, cType);
    }

    static public NormalController_ISIS getIntfCatg(int up, int down, String name, OpType_isis type, CType cType){
        return new NormalController_ISIS(List.of(up, down), List.of(OpType_isis.INTFUP, OpType_isis.INTFDOWN), name, null, type, cType);
    }

    static public NormalController_ISIS getLinkCatg(int add, int down, int remove, String name, String name2, OpType_isis type, CType cType){
        return  new NormalController_ISIS(List.of(add, down, remove), List.of(OpType_isis.LINKADD, OpType_isis.LINKDOWN, OpType_isis.LINKREMOVE), name, name2, type, cType);
    }

    static public NormalController_ISIS getISISCatg(int up, int re, int shutDown, String name, OpType_isis type, CType cType){
        return new NormalController_ISIS(List.of(up, re, shutDown), List.of(OpType_isis.NODESETISISUP, OpType_isis.NODESETISISRE, OpType_isis.NODESETISISSHUTDOWN), name, null, type, cType);
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
