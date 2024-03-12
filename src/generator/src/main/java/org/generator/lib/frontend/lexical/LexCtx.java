package org.generator.lib.frontend.lexical;

import java.util.HashMap;
import java.util.Map;

public class LexCtx {
    public OpType opType;

    //NUM -> "60000"
    public Map<String, String> tokenMap;

    public LexDef lexDef;
    public LexCtx(){
        tokenMap = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(opType);
        builder.append(lexDef);
        builder.append(tokenMap);
        return builder.toString();
    }
}
