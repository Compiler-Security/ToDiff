package org.generator.lib.item.IR;

import org.generator.lib.item.lexical.LexDef;
import org.generator.lib.operation.operation.OpType;

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
    }
    public Op getOperation() {
        return operation;
    }

    public void setOperation(Op operation) {
        this.operation = operation;
    }

    private Op operation;

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
    }

    /**gen OpCtx
     * args: 1. op
     * 2. lex_idx: the lex_idx th seed*/
    public static OpCtx of(Op op, int lex_idx){
        return new OpCtx(op, OpCtx.Format.of(op.Type(), lex_idx));
    }

    /**
     * This function create the OpCtx with the first LexDef
     * @param op Operation interface
     * @return OpCtx
     */
    public  static OpCtx of(Op op){
        return of(op, 0);
    }
}
