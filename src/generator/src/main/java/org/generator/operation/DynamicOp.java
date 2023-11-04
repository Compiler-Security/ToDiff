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
public class DynamicOp implements operation{

    public DynamicOp(String template, OpType type){
        this.template = template;
        this.fields = new HashMap<>();
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
                fields.put(groupName, matcher.group(groupName));
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
            String rep = fields.get(matcher.group(1));
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
    public OpType getType() {
        return type;
    }

    @Override
    public Map<String, String> getFields() {
        return fields;
    }

    public  OpType type;
    public  String template;
    public  String re;
    public Map<String, String> fields;
}
