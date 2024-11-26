package org.generator.lib.item.IR;

import org.generator.lib.frontend.driver.IO_ISIS;
import org.generator.lib.item.opg.OpIsisG;
import org.generator.lib.frontend.lexical.OpType_isis;

import java.util.Objects;

public class OpIsis extends OpBase_ISIS{
    OpIsis(OpType_isis type) {
        super(type);
    }
    /**
     * create the OpOspf with OpType type
     * @param type OpOspf's type
     * @return OpOspf with OpType type
     */
    public static OpIsis of(OpType_isis type){
        return new OpIsis(type);
    }

    /**
     * create the default OpIsis, whose type is invalid
     * @return default OpIsis
     */
    public static OpIsis of(){
        return new OpIsis(OpType_isis.INVALID);
    }

//    public OpOspf getCtxOp() {
//        return ctxOp;
//    }

//    public void setCtxOp(OpOspf ctxOp) {
//        this.ctxOp = ctxOp;
//    }

    public OpIsisG getOpg() {
        return opg;
    }

    public void setOpg(OpIsisG opg) {
        this.opg = opg;
    }

    /**
     * ctxOp of this op, this should equal to opg's ctxOp
     */
//    private OpOspf ctxOp;

    /**
     * this op's Opg
     */
    private OpIsisG opg;


    /**
     * two OpIsis equal iff type1==type2 && args1 equal args2 <br>
     * args1 equal args2 iff any arg in args1 union args2, args1[arg] equal args2[arg]
     * @return is_equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpIsis opIsis = (OpIsis) o;
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
            return IO_ISIS.writeOp(getOpCtx());
        }else {
            return IO_ISIS.writeOp(OpCtx_ISIS.of(this, 0));
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
    public OpIsis copy(){
        var newOp = new OpIsis(Type());
        if (ID != null) newOp.ID = ID.copy();
        if (IP != null) newOp.IP = IP.copy();
        if (IPRANGE != null) newOp.IPRANGE = IPRANGE.copy();
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
