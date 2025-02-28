package org.generator.lib.item.IR;

import org.generator.lib.frontend.driver.IO;
import org.generator.lib.item.opg.OpOspfG;
import org.generator.lib.frontend.lexical.OpType;

import java.util.Objects;

public class OpOspf extends OpBase{
    OpOspf(OpType type) {
        super(type);
    }
    /**
     * create the OpOspf with OpType type
     * @param type OpOspf's type
     * @return OpOspf with OpType type
     */
    public static OpOspf of(OpType type){
        return new OpOspf(type);
    }

    /**
     * create the default OpOspf, whose type is invalid
     * @return default OpOspf
     */
    public static OpOspf of(){
        return new OpOspf(OpType.INVALID);
    }

//    public OpOspf getCtxOp() {
//        return ctxOp;
//    }

//    public void setCtxOp(OpOspf ctxOp) {
//        this.ctxOp = ctxOp;
//    }

    public OpOspfG getOpg() {
        return opg;
    }

    public void setOpg(OpOspfG opg) {
        this.opg = opg;
    }

    /**
     * ctxOp of this op, this should equal to opg's ctxOp
     */
//    private OpOspf ctxOp;

    /**
     * this op's Opg
     */
    private OpOspfG opg;


    /**
     * two OpOspf equal iff type1==type2 && args1 equal args2 <br>
     * args1 equal args2 iff any arg in args1 union args2, args1[arg] equal args2[arg]
     * @return is_equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpOspf opOspf = (OpOspf) o;
        if (!super.equals(o)) return false;
        return true;
        //return argEqual(getCtxOp(), opOspf.getCtxOp());
    }

    /**
     * if OpCtx, print OpCtx <br>
     * if OpCtx null, choose the first LexDef <br>
     * No print ctxOp <br>
     */
    @Override
    public String toString() {
        if (getOpCtx() != null){
            return IO.writeOp(getOpCtx());
        }else {
            return IO.writeOp(OpCtx.of(this, 0));
        }
    }

    /**
     * two equal OpOspf have same hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    /**
     * It's a deep copy, we deep copy all the Op fields and deep OpCtx
     *@param
     *
     */
    public OpOspf copy(){
        var newOp = new OpOspf(Type());
        if (ID != null) newOp.ID = ID.copy();
        if (IP != null) newOp.IP = IP.copy();
        if (IPRANGE != null) newOp.IPRANGE = IPRANGE.copy();
        if (NET != null) newOp.NET = NET.copy();
        newOp.NAME = NAME;
        newOp.NAME2 = NAME2;
        newOp.NUM = NUM;
        newOp.NUM2 = NUM2;
        newOp.NUM3 = NUM3;
        newOp.LONGNUM = LONGNUM;
        newOp.setOpCtx(getOpCtx().copy(newOp));
        return newOp;
    }
}
