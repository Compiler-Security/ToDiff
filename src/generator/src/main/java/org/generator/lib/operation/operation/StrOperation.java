package org.generator.lib.operation.operation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the simple implementation for operation.
 * The operation format is like area {AREA} range {RANGE}.
 * The operation support [], however, we should not use it.
 */
public class StrOperation implements OpIntf {

    public StrOperation(OpType type){
        this.setTemplate = type.getSetTemplate();
        this.args = new HashMap<>();
        this.type = type;
        this.setRe = type.getSetRe();
        this.unsetRe = type.getUnsetReS();
        this.unsetTemplateS = type.getUnsetTemplateS();
        this.unset = false;
        this.unsetIndex = -1;
    }

    private boolean decode_by_re(String st, String re){
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
    public boolean decode(String st) {
        unsetIndex = -1;
        if (decode_by_re(st, setRe)){
            setUnset(false);
            return true;
        }else {
            for(int i = 0; i < unsetRe.length; i++){
                if (decode_by_re(st, unsetRe[i])){
                    setUnset(true);
                    unsetIndex = i;
                    return true;
                }
            }
            return false;
        }
    }


    public boolean encode_by_template(StringBuilder buf, String template){
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

    static String generateRandomString(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }
    public boolean encode(StringBuilder buf, int index){
//        if (this.type == OpType.OSPFCONF){
//            var ran = new Random();
//            buf.append(generateRandomString(ran.nextInt(20) + 1));
//            return true;
//        }
        if (!unset) {
            return encode_by_template(buf, setTemplate);
        }else{
            if (index >= this.unsetTemplateS.length){
                return false;
            }
            return encode_by_template(buf, this.unsetTemplateS[index]);
        }
    }
    @Override
    public boolean encode(StringBuilder buf) {
        return encode(buf, 0);
    }

    @Override
    public String encode_to_str() {
        return encode_to_str(unsetIndex);
    }

    public String encode_to_str(int index){
        StringBuilder buf = new StringBuilder();
        encode(buf, index);
        return buf.toString();
    }


    public void setType(OpType type) {
        this.type = type;
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
    private   String setTemplate;

    private String[] unsetTemplateS;
    private   String setRe;

    private String[] unsetRe;
    private Map<String, String> args;

    public boolean isUnset() {
        return unset;
    }

    public void setUnset(boolean unset) {
        this.unset = unset;
    }

    protected boolean unset;

    public int getUnsetIndex() {
        return unsetIndex;
    }

    public void setUnsetIndex(int unsetIndex) {
        this.unsetIndex = unsetIndex;
    }

    protected int unsetIndex;

    @Override
    public String toString() {
        if (Type() == OpType.INVALID){
            return "INVALID";
        }
        return encode_to_str();
    }

    public String toString(int index){
        if (Type() == OpType.INVALID){
            return "INVALID";
        }
        return encode_to_str(index);
    }
}
