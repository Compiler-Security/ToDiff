package org.generator.lib.frontend.lexical;

import org.generator.lib.operation.operation.OpType;
import org.generator.util.collections.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.generator.lib.operation.operation.OpType.*;

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
                {LINKUP, "link {NAME} {NAME2} up"},
                {LINKDOWN, "link {NAME} {NAME2} down"},
                {LINKREMOVE, "link {NAME} {NAME2} remove"},
                {OSPFCONFBEGIN, "ROSPFCONF"},

                {ROSPF, "router ospf"},
                {IntfName, "interface {NAME}|int {NAME}"},
                {RID, "ospf router-id {ID}"},
                {RABRTYPE, "ospf abr-type {NAME(standard,shortcut,ibm,cisco)}"},
                {NETAREAID, "network {IPRANGE} area {ID(NUM)}"},
                {PASSIVEINTFDEFUALT, "passive-interface default"},
                {TIMERSTHROTTLESPF, "timers throttle spf {NUM(0-600000)} {NUM1(0-600000)} {NUM2(0-600000)}"},
                {CLEARIPOSPFPROCESS, "clear ip ospf process"},
                {CLEARIPOSPFNEIGHBOR, "clear ip ospf neighbor"},
                {MAXIMUMPATHS, "maximum-paths {NUM(1-64)}"},
                {WRITEMULTIPLIER, "write-multiplier {NUM(1-100)}"},
                {SOCKETBUFFERSEND, "socket buffer send {LONGNUM(1-4000000000)}"},
                {SOCKETBUFFERRECV, "socket buffer recv {LONGNUM(1-4000000000)}"},
                {SOCKETBUFFERALL, "socket buffer all {LONGNUM(1-4000000000)}"},
                {NOSOCKETPERINTERFACE, "no socket-per-interface"},
                {AreaRange, "area {ID(NUM)} range {IPRANGE}|area {ID(NUM)} range {IPRANGE} advertise"},
                {AreaRangeNoAd, "area {ID(NUM)} range {IPRANGE} not-advertise"},
                {AreaRangeSub, "area {ID(NUM)} range {IPRANGE} substitute {IP}"},
                {AreaRangeCost, "area {ID(NUM)} range {IPRANGE} cost {NUM(0-16777215)}|area {ID(NUM)} range {IPRANGE} advertise cost {NUM(0-16777215)}"},
                {AreaVLink, "area {ID(NUM)} virtual-link {ID2}"},
                {AreaShortcut, "area {ID(NUM)} shortcut {NAME(enable,disable,default)}"},
                {AreaStub, "area {ID(NUM)} stub"},
                {AreaStubTotal, "area {ID(NUM)} stub no-summary"},
                {AreaNSSA, "area {ID(NUM)} nssa"},
                {IPAddr, "ip address {IP}"},
                {IpOspfArea, "ip ospf area {ID(NUM)}"},
                {IpOspfCost, "ip ospf cost {NUM(1-65535)}"},
                {IpOspfDeadInter, "ip ospf dead-interval {NUM(1-65535)}"},
                {IpOspfDeadInterMulti, "ip ospf dead-interval minimal hello-multiplier {NUM(2-20)}"},
                {IpOspfHelloInter, "ip ospf hello-interval {NUM(1-65535)}"},
                {IpOspfGRHelloDelay, "ip ospf graceful-restart hello-delay {NUM(1-1800)}"},
                {IpOspfNet, "ip ospf network {NAME(broadcast,non-broadcast)}"},
                {IpOspfPriority, "ip ospf priority {NUM(0-255)}"},
                {IpOspfRetransInter, "ip ospf retransmit-interval {NUM(1-65535)}"},
                {IpOspfTransDealy, "ip ospf transmit-dealy {NUM(1-65535)}"},
                {IpOspfPassive, "ip ospf passive"},


                //minimal args is the first LexDef of no operation
                {NOROSPF, "no router ospf"},
                {NORID, "no ospf router-id|no ospf router-id {ID}"},
                {NORABRTYPE, "no ospf abr-type|no ospf abr-type {NAME(standard,shortcut,ibm,cisco)}"},
                {NONETAREAID, "no network {IPRANGE} area {ID}"},
                {NOPASSIVEINTFDEFUALT, "no passive-interface default"},
                {NOTIMERSTHROTTLESPF, "no timers throttle spf|no timers throttle spf {NUM(0-600000)} {NUM1(0-600000)} {NUM2(0-600000)}"},
                {NOMAXIMUMPATHS, "no maximum-paths|no maximum-paths {NUM(1-64)}"},
                {NOWRITEMULTIPLIER, "no write-multiplier|no write-multiplier {NUM(1-100)}"},
                {NOSOCKETBUFFERSEND, "no socket buffer send|no socket buffer send {LONGNUM(1-4000000000)}"},
                {NOSOCKETBUFFERRECV, "no socket buffer recv|no socket buffer recv {LONGNUM(1-4000000000)}"},
                {NOSOCKETBUFFERALL, "no socket buffer all|no socket buffer all {LONGNUM(1-4000000000)}"},
                {NONOSOCKETPERINTERFACE, "no socket-per-interface"},
                {NOAreaRange, "no area {ID(NUM)} range {IPRANGE}|no area {ID(NUM)} range {IPRANGE} advertise"},
                {NOAreaRangeNoAd, "no area {ID(NUM)} range {IPRANGE} not-advertise"},
                {NOAreaRangeSub, "no area {ID(NUM)} range {IPRANGE} substitute {IP}"},
                //FIXME is this no lexical right?
                {NOAreaRangeCost, "no area {ID(NUM)} range {IPRANGE} cost|no area {ID(NUM)} range {IPRANGE} cost {NUM(0-16777215)}|no area {ID(NUM)} range {IPRANGE} advertise cost {NUM(0-16777215)}"},
                {NOAreaVLink, "no area {ID(NUM)} virtual-link {ID2}"},
                {NOAreaShortcut, "no area {ID(NUM)} shortcut {NAME(enable,disable,default)}"},
                {NOAreaStub, "no area {ID(NUM)} stub"},
                {NOAreaStubTotal, "no area {ID(NUM)} stub no-summary"},
                {NOAreaNSSA, "no area {ID(NUM)} nssa"},
                {NOIPAddr, "no ip address {IP}"},
                {NOIpOspfArea, "no ip ospf area {ID(NUM)}"},
                {NOIpOspfCost, "no ip ospf cost | no ip ospf cost {NUM(1-65535)}"},
                {NOIpOspfDeadInter, "no ip ospf dead-interval | no ip ospf dead-interval {NUM(1-65535)}"},
                {NOIpOspfDeadInterMulti, "no ip ospf dead-interval minimal hello-multiplier | no ip ospf dead-interval minimal hello-multiplier {NUM(2-20)}"},
                {NOIpOspfHelloInter, "no ip ospf hello-interval | no ip ospf hello-interval {NUMNUM(1-65535)}"},
                {NOIpOspfGRHelloDelay, "no ip ospf graceful-restart hello-delay | no ip ospf graceful-restart hello-delay {NUM(1-1800)}"},
                {NOIpOspfNet, "no ip ospf network | no ip ospf network {NAME(broadcast,non-broadcast)}"},
                {NOIpOspfPriority, "no ip ospf priority | no ip ospf priority {NUM(0-255)}"},
                {NOIpOspfRetransInter, "ip ospf retransmit-interval | ip ospf retransmit-interval {NUM(1-65535)}"},
                {NOIpOspfTransDealy, "no ip ospf transmit-dealy | no ip ospf transmit-dealy {NUM(1-65535)}"},
                {NOIpOspfPassive, "no ip ospf passive"},


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
