package org.generator.operation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicOp implements operation{
    public  OpType type;
    public  String template;
    public  String re;

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
    public DynamicOp(String template, OpType type){
        this.template = template;
        this.fields = new HashMap<>();
        this.type = type;
        init();
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


    public Map<String, String> fields;

//    private void init1(){
//        Pattern pattern = Pattern.compile("\\{([^{}]+)\\}");
//        Matcher matcher = pattern.matcher(template);
//        StringBuilder result = new StringBuilder();
//        while (matcher.find()){
//            String matchedText = matcher.group(1);
//            String newText = String.format("(?<%s>.*?)", matchedText);
//            matcher.appendReplacement(result, newText);
//        }
//        matcher.appendTail(result);
//
//        pattern = Pattern.compile("(\\[.*\\])");
//        re = result.toString();
//        while (true) {
//            matcher = pattern.matcher(re);
//            result.setLength(0);
//            while (matcher.find()) {
//                String matchedText = matcher.group(1);
//                matchedText = matchedText.substring(1, matchedText.length() - 1);
//                String newText = String.format("(?:%s)?", matchedText);
//                matcher.appendReplacement(result, newText);
//            }
//            matcher.appendTail(result);
//            if (re.equals(result.toString())){
//                break;
//            }else{
//                re = result.toString();
//            }
//        }
//        re = re.replaceAll("\s+", "\\\\s+");
//    }
}
