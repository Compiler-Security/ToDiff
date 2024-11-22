package org.generator.lib.frontend.pass;

import org.generator.lib.frontend.lexical.LexCtx_ISIS;
import org.generator.lib.frontend.lexical.LexDef_isis;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrToLexPass_ISIS {
    private static Map<String, String> decode_by_re(String st_op, String re){
        HashMap<String, String> args = new HashMap<>();
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(st_op);
        if (matcher.matches()){
            for(String groupName: pattern.namedGroups().keySet()){
                args.put(groupName, matcher.group(groupName));
            }
            return args;
        }else return null;
    }
    /** this will parse st_op to LexCtx, if fail return null*/
    @Nullable public LexCtx_ISIS solve(String st_op){
        var lexCtx = new LexCtx_ISIS();
        for(var opType: LexDef_isis.getOpTypesToMatch()){
            for(var lexDef: LexDef_isis.getLexDef(opType)){
                var tokenMap = decode_by_re(st_op, lexDef.Re);
                if (tokenMap != null){
                    lexCtx.tokenMap = tokenMap;
                    lexCtx.opType = opType;
                    lexCtx.lexDef = lexDef;
                    return lexCtx;
                }
            }
        }
        return null;
    }

}
