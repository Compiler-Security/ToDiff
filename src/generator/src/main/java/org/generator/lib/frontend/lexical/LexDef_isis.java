package org.generator.lib.frontend.lexical;

import org.generator.util.collections.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.generator.lib.frontend.lexical.OpType_isis.*;

/** one template -> {template x re x args x argsRange}*/
public class LexDef_isis {

    public LexDef_isis(){}
    public LexDef_isis(String re, String template, List<String> args, Map<String, Object> argsRange){
        this.Re = re;
        this.Template = template;
        this.Args = args;
        this.ArgsRange = argsRange;
    }
    private static final Map<OpType_isis, List<LexDef_isis>> preprocess;
    /**
     * type:
     * 1.NAME
     * 2.NAME2
     * 3.NUM
     * 4.ID
     * 5.IPRANGE
     * 6.LONGNUM
     * 7.IP
     * 8.NET
     */
    static {
        var lexical_seed = new Object[][]{
                {NODEADD, "node {NAME} add"},
                {NODEDEL, "node {NAME} del"},
                {NODESETISISUP, "node {NAME} set isis up"},
                {NODESETISISRE, "node {NAME} set isis restart"},
                {INTFUP, "intf {NAME} up"},
                {INTFDOWN, "intf {NAME} down"},
                {LINKADD, "link {NAME} {NAME2} add"},
                {LINKDOWN, "link {NAME} {NAME2} down"},
                {LINKREMOVE, "link {NAME} {NAME2} remove"},
                {NODESETISISUP, "node {NAME} set ISIS up"},
                {NODESETISISSHUTDOWN, "node {NAME} set ISIS down"},
                {NODESETISISRE, "node {NAME} set ISIS restart"},
                {ISISCONF, "RISISCONF"},

                {RISIS, "router isis 1"},
                {IntfName, "interface {NAME}|int {NAME}"},
                // route
                {NET, "net {NET}"},
                
                
                //FIXME: we need "ATTACHEDBIT"!
                //{ATTACHEDBIT, "attached-bit {NAME(receive ignore,send)}"},
                //{METRICSTYLE, "metric-style {NAME(narrow,transition,wide)}"},
                {ADVERTISEHIGHMETRIC, "advertise-high-metrics"},
                {SETOVERLOADBIT, "set-overload-bit"},
                {SETOVERLOADBITONSTARTUP, "set-overload-bit on-startup {NUM(0-86400)}"},
                {LSPMTU, "lsp-mtu {NUM(128-4352)}"},
                // region
                {ISTYPE, "is-type {NAME(level-1,level-1-2,level-2-only)}"},

                // interface
                {IPROUTERISIS, "ip router isis 1"},
                {IPAddr, "ip address {IP}"},
                {CIRCUITTYPE, "isis circuit-type {NAME(level-1,level-1-2,level-2)}"},
                {CSNPINTERVAL, "isis csnp-interval {NUM(1-600)} {NAME(level-1,level-2,_)}"},
                {HELLOPADDING, "isis hello padding"},
                {HELLOINTERVAL, "isis hello-interval {NAME(level-1,level-2,_)} {NUM(1-600)} "},
                {HELLOMULTIPLIER, "isis hello-multiplier {NAME(level-1,level-2,_)} {NUM(2-100)}"},
                //{ISISMETRICLEVEL1, "isis metric level-1 {NUM(0-255)}"},
                //{ISISMETRICLEVEL2, "isis metric level-2 {NUM(0-16777215)}"},
                {NETWORKPOINTTOPOINT, "isis network point-to-point"},
                {ISISPASSIVE, "isis passive"},
                {ISISPRIORITY, "isis priority {NUM(0-127)} {NAME(level-1,level-2,_)}"},
                {PSNPINTERVAL, "isis psnp-interval {NUM(1-120)} {NAME(level-1,level-2,_)}"},
                {THREEWAYHANDSHAKE, "isis three-way-handshake"},

                // no isis
                {NORISIS, "no router isis 1"},
                {NOTNET, "no net {NET}"},
                //FIXME: we need "NOATTACHEDBIT"!
                //{NOATTACHEDBIT, "no attached-bit {NAME(receive ignore,send)}"},
                //{NOMETRICSTYLE, "no metric-style {NAME(narrow,transition,wide)}"},
                {NOADVERTISEHIGHMETRIC, "no advertise-high-metrics"},
                {NOSETOVERLOADBIT, "no set-overload-bit"},
                {NOSETOVERLOADBITONSTARTUP, "no set-overload-bit on-startup {NUM(0-86400)}"},
                {NOLSPMTU, "no lsp-mtu {NUM(128-4352)}"},
        
                {NOISTYPE, "no is-type {NAME(level-1,level-1-2,level-2-only)}"},
                {NOIPROUTERISIS, "no ip router isis 1"},
                {NOIPAddr, "no ip address {IP}"},
                {NOCIRCUITTYPE, "no isis circuit-type {NAME(level-1,level-1-2,level-2)}"},
                {NOCSNPINTERVAL, "no isis csnp-interval {NUM(1-600)} {NAME(level-1,level-2,_)}"},
                {NOHELLOPADDING, "no isis hello padding"},
                {NOHELLOINTERVAL, "no isis hello-interval {NAME(level-1,level-2,_)} {NUM(1-600)} "},
                {NOHELLOMULTIPLIER, "no isis hello-multiplier {NAME(level-1,level-2,_)} {NUM(2-100)}"},
                //{NOISISMETRICLEVEL1, "no isis metric level-1 {NUM(0-255)}"},
                //{NOISISMETRICLEVEL2, "no isis metric level-2 {NUM(0-16777215)}"},
                {NONETWORKPOINTTOPOINT, "no isis network point-to-point"},
                {NOISISPASSIVE, "no isis passive"},
                {NOISISPRIORITY, "no isis priority {NUM(0-127)} {NAME(level-1,level-2,_)}"},
                {NOPSNPINTERVAL, "no isis psnp-interval {NUM(1-120)} {NAME(level-1,level-2,_)}"},
                {NOTHREEWAYHANDSHAKE, "no isis three-way-handshake"},
                

              

                //INVALID will not to match, it can read/write invalid str to bypass [NAME]
                {INVALID, "{NAME}"},
        };


        preprocess = new HashMap<>();
        for(var item: lexical_seed){
            OpType_isis opType = (OpType_isis) item[0];
            String seeds = (String) item[1];
            var seedArray = Arrays.stream(seeds.split("\\|")).map(String::strip).toArray(String[]::new);
            preprocess.put(opType, new ArrayList<>());
            for(var seed: seedArray){
                preprocess.get(opType).add(seedToLexDef(seed));
            }
        }
    }

    private static LexDef_isis seedToLexDef(String st){
        Map<String, Object> argsRange = new HashMap<>();
        List<String> args = new ArrayList<>();

        String regex = "\\{(\\w+)(?:\\(([\\w\\-|,_]+)\\))?\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(st);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String name = matcher.group(1);
            String value = matcher.group(2);
            if (value != null) {
                if (value.contains("NUM")){
                    argsRange.put(name, true);
                }
                if (value.contains(",")){
                    var tmp = Arrays.stream(value.split(","))
                         .map(s -> s.equals("_") ? "" : s)  // 将 _ 转换为空字符串
                         .toList();
                    argsRange.put(name, tmp);
                }else if (value.contains("-")){
                    var tmp = Arrays.stream(value.split("-")).map(Long::valueOf).toList();
                    argsRange.put(name, new Pair<>(tmp.get(0), tmp.get(1)));
                }
            }
            args.add(name);
            matcher.appendReplacement(result, "(?<" + name + ">[0-9a-zA-Z./-]+)");
        }
        matcher.appendTail(result);

        var re = result.toString();
        re = re.replaceAll(" +", "\\\\s+");
        var template = removeRangeStr(st);

        return new LexDef_isis(re, template, args, argsRange);
    }

    private static  String removeRangeStr(String st){
        return st.replaceAll("\\([\\w\\-,]+\\)", "");
    }

    public static List<LexDef_isis> getLexDef(OpType_isis opType){
        assert preprocess.containsKey(opType): opType;
        return preprocess.get(opType);
    }

    public static int getLexDefNum(OpType_isis opType){
        return preprocess.get(opType).size();
    }

    /**
     * INVALID will not to match
     * @return OpType to match
     */
    public static List<OpType_isis> getOpTypesToMatch(){
        return preprocess.keySet().stream().filter(x -> x != INVALID).toList();
    }
    public String Template;
    public String Re;
    public List<String> Args;
    public Map<String, Object> ArgsRange;

    public Pair<Long, Long> getNumRange(String field){
        return (Pair<Long, Long>) ArgsRange.getOrDefault(field, null);
    }

    public List<String> getStrListRange(String field){
        return (List<String>) ArgsRange.getOrDefault(field, null);
    }

    public Boolean getBoolRange(String field){
        return (Boolean) ArgsRange.getOrDefault(field, null);
    }

    @Override
    public String toString() {
        return Template + " , " +
                Re + " , "+
                Args + " , " +
                ArgsRange;
    }
}
