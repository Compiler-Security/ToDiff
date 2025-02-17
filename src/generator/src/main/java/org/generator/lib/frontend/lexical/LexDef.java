package org.generator.lib.frontend.lexical;

import org.generator.util.collections.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.generator.lib.frontend.lexical.OpType.*;

/** one template -> {template x re x args x argsRange}*/
public class LexDef {

    public LexDef(){}
    public LexDef(String re, String template, List<String> args, Map<String, Object> argsRange){
        this.Re = re;
        this.Template = template;
        this.Args = args;
        this.ArgsRange = argsRange;
    }
    private static final Map<OpType, List<LexDef>> preprocess;
    static {
        var lexical_seed = new Object[][]{
                {NODEADD, "node {NAME} add"},
                {NODEDEL, "node {NAME} del"},
                {NODESETOSPFUP, "node {NAME} set ospf up"},
                {NODESETOSPFRE, "node {NAME} set ospf restart"},
                {INTFUP, "intf {NAME} up"},
                {INTFDOWN, "intf {NAME} down"},
                {LINKADD, "link {NAME} {NAME2} add"},
                {LINKDOWN, "link {NAME} {NAME2} down"},
                {LINKREMOVE, "link {NAME} {NAME2} remove"},
                {NODESETOSPFUP, "node {NAME} set OSPF up"},
                {NODESETOSPFSHUTDOWN, "node {NAME} set OSPF down"},
                {NODESETOSPFRE, "node {NAME} set OSPF restart"},
                {CONF, "ROSPFCONF"},

                {RRIP, "router rip"},
                {IntfName, "interface {NAME}|int {NAME}"},

                {NETWORKN, "network {IPRANGE}"},
                {NETWORKI, "network {NAME}"},
                {NEIGHBOR, "neighbor {ID}"},
                {VERSION, "version {NAME}"},
                {DEFAULTMETRIC, "default-metric {NUM(1-16)}"},
                {DISTANCE, "distance {NUM(1-255)}"},
                {TIMERSBASIC, "timers basic {NUM(5-2147483647)}, {NUM2(5-2147483647)}, {NUM3(5-2147483647)}"},
                {PASSIVEINTFDEFAULT, "passive-interface default"},
                {PASSIVEINTFNAME, "passive-interface {NAME}"},

                //default is no ip rip split-horizon, so the no command is ip rip split-horizon
                {IPSPLITHORIZION, "no ip rip split-horizon"},
                {IPSPLITPOISION, "ip rip split-horizon poisoned-reverse"},
                {IPAddr, "ip address {IP}"},


                {NORRIP, "no router rip"},
//                {NOIntfName, "no interface {NAME}|no int {NAME}"},

                {NONETWORKN, "no network {IPRANGE}"},
                {NONETWORKI, "no network {NAME}"},
                {NONEIGHBOR, "no neighbor {ID}"},
                {NOVERSION, "no version|no version {NUM(1-2)}"},
                {NODEFAULTMETRIC, "no default-metric|no default-metric {NUM(1-16)}"},
                {NODISTANCE, "no distance |no distance {NUM(1-255)}"},
                {NOPASSIVEINTFDEFAULT, "no passive-interface default"},
                {NOPASSIVEINTFNAME, "no passive-interface {NAME}"},
                {NOTIMERSBASIC, "no timers basic | no timers basic {NUM(1-30)} | no timers basic {NUM(1-30)}, {NUM2(1-30)} | no timers basic {NUM(1-30)}, {NUM2(1-30)}, {NUM3(1-30)}"},

                {NOIPSPLITPOISION, "no ip rip split-horizon poisoned-reverse"},
                {NOIPSPLITHORIZION, "ip split-horizon"},
                {NOIPAddr, "no ip address | no ip address {IP}"},


                //INVALID will not to match, it can read/write invalid str to bypass [NAME]
                {INVALID, "{NAME}"},
        };


        preprocess = new HashMap<>();
        for(var item: lexical_seed){
            OpType opType = (OpType) item[0];
            String seeds = (String) item[1];
            var seedArray = Arrays.stream(seeds.split("\\|")).map(String::strip).toArray(String[]::new);
            preprocess.put(opType, new ArrayList<>());
            for(var seed: seedArray){
                preprocess.get(opType).add(seedToLexDef(seed));
            }
        }
    }

    private static LexDef seedToLexDef(String st){
        Map<String, Object> argsRange = new HashMap<>();
        List<String> args = new ArrayList<>();

        String regex = "\\{(\\w+)(?:\\(([\\w\\-|,]+)\\))?\\}";
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
                    var tmp = Arrays.stream(value.split(",")).toList();
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

        return new LexDef(re, template, args, argsRange);
    }

    private static  String removeRangeStr(String st){
        return st.replaceAll("\\([\\w\\-,]+\\)", "");
    }

    public static List<LexDef> getLexDef(OpType opType){
        assert preprocess.containsKey(opType): opType;
        return preprocess.get(opType);
    }

    public static int getLexDefNum(OpType opType){
        return preprocess.get(opType).size();
    }

    /**
     * INVALID will not to match
     * @return OpType to match
     */
    public static List<OpType> getOpTypesToMatch(){
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
