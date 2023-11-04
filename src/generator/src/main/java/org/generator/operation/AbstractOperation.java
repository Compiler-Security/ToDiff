package org.generator.operation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the simple implementation for operation.
 * The operation format is like area {AREA} range {RANGE}.
 * The operation support [], however, we should not use it.
 */
public abstract class AbstractOperation implements op {

    public AbstractOperation(String template, OpType type){
        this.template = template;
        this.args = new HashMap<>();
        this.type = type;
        init();
    }
    private  void init(){
        String st = template;
        re = template;
        do {
            re = st;
            st = st.replaceAll("\\{([^{}]+)\\}", "(?<$1>.*?)");
            st = st.replaceAll("\\[(.*)\\]", "(?:$1)?");
        } while (!st.equals(re));
        re = re.replaceAll("\s+", "\\\\s+");
    }
    @Override
    public boolean decode(String st) {
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(st);
        if (matcher.matches()){
            for(String groupName: pattern.namedGroups().keySet()){
                args.put(groupName, matcher.group(groupName));
            }
            return true;
        }else return false;
    }

    @Override
    public void encode(StringBuilder buf) {
        //{}
        Pattern pattern = Pattern.compile("\\{([^{}]+)\\}");
        Matcher matcher = pattern.matcher(template);
        while(matcher.find()){
            String rep = args.get(matcher.group(1));
            assert  rep != null;
            matcher.appendReplacement(buf, rep);
        }
        matcher.appendTail(buf);
    }

    @Override
    public String encode_to_str() {
        StringBuilder buf = new StringBuilder();
        encode(buf);
        return buf.toString();
    }

    @Override
    public OpType Type() {
        return type;
    }

    @Override
    public Map<String, String> Args() {
        return args;
    }

    @Override
    public void putArgs(Map<String, String> args) {
        this.args = args;
    }

    @Override
    public void putArg(String field_name, String val) {
        assert val != null;
        args.put(field_name, val);
    }

    protected   OpType type;
    protected   String template;
    protected   String re;
    protected Map<String, String> args;

    @Override
    public String toString() {
        if (Type() == OpType.INVALID){
            return "INVALID";
        }
        return encode_to_str();
    }
}
