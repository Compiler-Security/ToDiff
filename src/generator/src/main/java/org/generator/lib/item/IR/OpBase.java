package org.generator.lib.item.IR;

import org.generator.lib.operation.operation.OpType;
import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;

abstract public class OpBase implements Op{

    protected OpBase(){}
    protected OpBase(OpType type){
        this.type = type;
    }

    public static OpBase Of(){
        return null;
    }
    public String NAME, NAME2;

    public String getNAME() {
        return NAME;
    }

    public String getNAME2() {
        return NAME2;
    }

    public void setNAME2(String NAME2) {
        this.NAME2 = NAME2;
    }

    public org.generator.util.net.IP getIP() {
        return IP;
    }

    public void setIP(org.generator.util.net.IP IP) {
        this.IP = IP;
    }

    public org.generator.util.net.ID getID() {
        return ID;
    }

    public void setID(org.generator.util.net.ID ID) {
        this.ID = ID;
    }

    public IPRange getIPRANGE() {
        return IPRANGE;
    }

    public void setIPRANGE(IPRange IPRANGE) {
        this.IPRANGE = IPRANGE;
    }


    public IP IP;
    public ID ID;

    public IPRange IPRANGE;

    public Integer getNUM() {
        return NUM;
    }

    public void setNUM(Integer NUM) {
        this.NUM = NUM;
    }

    public Integer getNUM2() {
        return NUM2;
    }

    public void setNUM2(Integer NUM2) {
        this.NUM2 = NUM2;
    }

    public Integer getNUM3() {
        return NUM3;
    }

    public void setNUM3(Integer NUM3) {
        this.NUM3 = NUM3;
    }

    public Integer NUM, NUM2, NUM3;

    public Long getLONGNUM() {
        return LONGNUM;
    }

    public void setLONGNUM(Long LONGNUM) {
        this.LONGNUM = LONGNUM;
    }

    public Long LONGNUM;

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }


    public OpType Type() {
        return type;
    }

    public void setType(OpType type) {
        this.type = type;
    }

    private  OpType type;
}
