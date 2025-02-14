package org.generator.lib.item.IR;

import org.generator.lib.frontend.lexical.OpType_isis;
import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;
import org.generator.util.net.NET;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

abstract public class OpBase_ISIS implements Op_ISIS{

    protected OpBase_ISIS(){}
    protected OpBase_ISIS(OpType_isis type){
        this.type = type;
    }

    public static OpBase_ISIS Of(){
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

    public void setNET(NET NET) {
        this.NET = NET;
    }

    public NET getNET() {
        return NET;
    }

    public IP IP;
    public ID ID;
    public NET NET;
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


    public OpType_isis Type() {
        return type;
    }

    public void setType(OpType_isis type) {
        this.type = type;
    }

    private  OpType_isis type;


    /**
     *  arg1 equals arg2 iff arg1==arg2==null || arg1 != null && arg2 != null && arg1.equals(arg2)
     * @param arg1 can be null
     * @param arg2 can be null
     * @return is equal
     * @param <T> extends object
     */
    protected  <T> boolean argEqual(@Nullable T arg1, @Nullable T arg2){
        return Objects.equals(arg1, arg2);
    }

    /**
     * args1 equal args2 iff any arg in args1 union args2, args1[arg] equal args2[arg]
     * @param op
     * @return if all args equal
     */
    public boolean ArgsEqual(Op_ISIS op){
        return argEqual(getNAME(), op.getNAME())
                && argEqual(getNAME2(), op.getNAME2())
                && argEqual(getID(), op.getID())
                && argEqual(getIP(), op.getIP())
                && argEqual(getIPRANGE(), op.getIPRANGE())
                && argEqual(getNUM(), op.getNUM())
                && argEqual(getNUM2(), op.getNUM2())
                && argEqual(getNUM3(), op.getNUM3())
                && argEqual(getLONGNUM(), op.getLONGNUM())
                && argEqual(getNET(), op.getNET());  
    }

    /**
        two OpBase equal iff type1==type2 && args1 equal args2
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpBase_ISIS opBase = (OpBase_ISIS) o;
        return type == opBase.type && Objects.equals(NAME, opBase.NAME) && Objects.equals(NAME2, opBase.NAME2) && Objects.equals(IP, opBase.IP) && Objects.equals(ID, opBase.ID) && Objects.equals(IPRANGE, opBase.IPRANGE) && Objects.equals(NUM, opBase.NUM) && Objects.equals(NUM2, opBase.NUM2) && Objects.equals(NUM3, opBase.NUM3) && Objects.equals(LONGNUM, opBase.LONGNUM)&& Objects.equals(NET, opBase.NET);
    }

    /**
     * two HashCode of OpBase equal iff opBase equal
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(NAME, NAME2, IP, ID, IPRANGE, NET, NUM, NUM2, NUM3, LONGNUM, type);
    }

    public OpCtx_ISIS getOpCtx() {
        return opCtx;
    }

    public void setOpCtx(OpCtx_ISIS opCtx) {
        this.opCtx = opCtx;
    }

    /**
     * this op's opCtx
     */
    private OpCtx_ISIS opCtx;

}
