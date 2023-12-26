package org.generator.lib.item.IR;

import org.generator.lib.item.lexical.LexDef;

import java.util.HashMap;
import java.util.Map;

/**
 * Op + Format
 * Format{
 *     IDISNUM
 *     lexDef
 *     byPass
 * }
 * Op operation
 * */
public class OpCtx {

    public static class Format {
        public boolean isIDISNUM() {
            return IDISNUM;
        }

        public void setIDISNUM(boolean IDISNUM) {
            this.IDISNUM = IDISNUM;
        }


        public boolean IDISNUM;

        public LexDef getLexDef() {
            return lexDef;
        }

        public void setLexDef(LexDef lexDef) {
            this.lexDef = lexDef;
        }

        public LexDef lexDef;

        public Map<String, String> getByPass() {
            return byPass;
        }

        public void addByPass(String key, String val) {
            getByPass().put(key, val);
        }

        Map<String, String> byPass;
        Format(){
            byPass = new HashMap<>();
        }
    }
    public Op getOperation() {
        return operation;
    }

    public void setOperation(Op operation) {
        this.operation = operation;
    }

    Op operation;

    public Format getFormmat() {
        return format;
    }

    public void setFormmat(Format format) {
        this.format = format;
    }

    public Format format;
    public OpCtx(Op operation){
        format = new Format();
        this.operation = operation;
    }
    public OpCtx(){
        format = new Format();
    }

}
