package org.generator.lib.frontend.lexical;

import java.util.HashMap;
import java.util.Map;

public class LexCtx_ISIS {
    public OpType_isis opType;

    //NUM -> "60000"
    public Map<String, String> tokenMap;

    public LexDef_isis lexDef;
    public LexCtx_ISIS(){
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
