package org.generator.lib.frontend.pass;

import org.generator.lib.item.lexical.LexCtx;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexToStrPass {
    private static boolean encode_by_template(StringBuilder buf, String template, Map<String, String> args){
        Pattern pattern = Pattern.compile("\\{([^{}]+)\\}");
        Matcher matcher = pattern.matcher(template);
        while(matcher.find()){
            String rep = args.get(matcher.group(1));
            if (rep == null) return false;
            matcher.appendReplacement(buf, rep);
        }
        matcher.appendTail(buf);
        return true;
    }

    /** this solve will always return the string of operation, if the operation is not right, we will assert false*/
    @NotNull public String solve(LexCtx ctx){
        StringBuilder builder = new StringBuilder();
        var res = encode_by_template(builder, ctx.lexDef.Template, ctx.tokenMap);
        assert res: String.format("when lowering in lexicalPass, UpperRes ctx not right {%s}", ctx);
        return builder.toString();
    }
}
