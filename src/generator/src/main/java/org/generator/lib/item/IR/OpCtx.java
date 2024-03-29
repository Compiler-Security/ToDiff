package org.generator.lib.item.IR;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.frontend.lexical.OpType;

import java.util.HashMap;
import java.util.Map;

/**
 * OpCtx := Op + Format
 * Format := LexDef(must) + byPass(must, default empty) + IDISNUM(default false)
 * */
public class OpCtx {

    public static class Format {
        public boolean isIDISNUM() {
            return IDISNUM;
        }

        public void setIDISNUM(boolean IDISNUM) {
            this.IDISNUM = IDISNUM;
        }


        public LexDef getLexDef() {
            return lexDef;
        }

        public void setLexDef(LexDef lexDef) {
            this.lexDef = lexDef;
        }



        public Map<String, String> getByPass() {
            return byPass;
        }

        public void addByPass(String key, String val) {
            getByPass().put(key, val);
        }

        public boolean IDISNUM;
        public LexDef lexDef;
        Map<String, String> byPass;

        Format(LexDef lexDef){
            this.IDISNUM = false;
            this.lexDef = lexDef;
            byPass = new HashMap<>();
        }
        public static Format of(OpType type, int lex_idx){
            return new Format(LexDef.getLexDef(type).get(lex_idx));
        }
        public Format copy(){
            var format = new Format(lexDef);
            format.setIDISNUM(isIDISNUM());
            for(var key: byPass.keySet()){
                format.addByPass(key, byPass.get(key));
            }
            return format;
        }
    }
    public Op getOperation() {
        return operation;
    }


    private final Op operation;

    public Format getFormmat() {
        return format;
    }

    public void setFormmat(Format format) {
        this.format = format;
    }

    public Format format;

    private OpCtx(Op operation, Format format){
        this.operation = operation;
        this.format = format;
        operation.setOpCtx(this);
    }

    /**gen OpCtx
     * @param op
     * @param lex_idx the lex_idx th LexDef*/
    public static OpCtx of(Op op, int lex_idx){
        return new OpCtx(op, OpCtx.Format.of(op.Type(), lex_idx));
    }

    /**
     * create new OpCtx of OpOspf
     * @param type
     * @param lex_idx
     * @return
     */
    public static OpCtx of(OpType type, int lex_idx){
        return of(new OpOspf(type), lex_idx);
    }

    /**
     * This function create the OpCtx with init format of first LexDef
     * @param op Operation interface
     * @return OpCtx
     */
    public  static OpCtx of(Op op){
        return of(op, 0);
    }

    /**
     * This function create the OpCtx with format
     * @param op
     * @param format
     * @return
     */
    public static OpCtx of(Op op, Format format){return  new OpCtx(op, format);}

    public OpCtx copy(Op op){
        var opCtx = OpCtx.of(op);
        opCtx.format = format.copy();
        return opCtx;
    }

    /**
     *
     * @return
     */
    public OpOspf getOpOspf(){
        return (OpOspf) getOperation();
    }

    public OpPhy getOpPhy(){
        return (OpPhy) getOperation();
    }
}
