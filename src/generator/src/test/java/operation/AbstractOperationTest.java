package operation;

import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractOperationTest {
    @Test
    public void replaceAllTest(){
        String st = "area {AREA} range {RANGE} [advertise [cost {COST}]]";
        st = st.replaceAll("\\[(.*)\\]", "$1");
        System.out.println(st);
    }
    @Test
    public void regTest(){
        var d = new Operation(OpType.INVALID);
        System.out.println(d.decode("area 10 range 10.0.0.0/32 advertise cost 100"));
        System.out.println(d.IntArg("AREA"));
        System.out.println(d.encode_to_str());
    }

    @Test
    public void reTest(){
        String input = "area 10 range   10.0.0.0/32 advertise  cost 1000";

        // 使用正则表达式匹配带有命名捕获组的文本
        //String regex = "Name: (?<name>.*), Age: (?<age>.*), Country: (?<country>.*)";
        String regex = "area\s+(?<AREAID>.*?)\s+range\s+(?<RANGEID>.*?)(?:\s+advertise\s+(?:cost\s+(?<COST>.*?))?)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        var groupMap = pattern.namedGroups();
        // 创建一个 HashMap 以存储捕获组的名字和值
        HashMap<String, String> namedGroups = new HashMap<>();

        // 检查是否有匹配
        if (matcher.matches()) {
            // 遍历捕获组的名字
            for (String groupName : groupMap.keySet()) {
                // 获取捕获组的值并存储在 HashMap 中
                namedGroups.put(groupName, matcher.group(groupName));
            }

            // 输出 HashMap 中的名字和值
            for (String groupName : groupMap.keySet()) {
                System.out.println(groupName + ": " + namedGroups.get(groupName));
            }
        } else {
            System.out.println("No match found.");
        }
    }
    @Test
    public void replaceTest(){
        String input = "[34[12]]";

        // 使用正则表达式匹配价格
        String regex = "(\\[.*\\])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String matchedText = matcher.group(1); // 获取第一个捕获组的文本
            String replacement = matchedText.replace("[", "{");
            String replacement1 =replacement.replace("]", "}");
            // 替换捕获的文本并追加到结果中
            matcher.appendReplacement(result, replacement1);
        }

        // 追加剩余的文本
        matcher.appendTail(result);

        String replacedText = result.toString();
        System.out.println(replacedText);
    }

    @Test
    public void noTest(){

//        var a = Arrays.stream("123".split("\\|")).map(String::strip).toList();
//        System.out.println(a);
//        //System.out.println((String[]) Arrays.stream("123".split("\\|")).map(String::strip).toArray());
        var op = new Operation(OpType.RID);
        var c = op.decode("no ospf router-id 5.5.5.5");
        System.out.println(c);
        if (c) {
            System.out.println(op.toString());
        }
    }
}
