package org.generator.lib.frontend.pass;

import org.generator.lib.frontend.lexical.LexCtx;
import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.driver.generate;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrToLexPass {
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
    @Nullable public LexCtx solve(String st_op){
        var lexCtx = new LexCtx();
        for(var opType: OpType.getAllOps()){
            for(var lexDef: LexDef.getLexDef(opType)){
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
