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
    private static final Map<OpType, List<LexDef>> preprocess;
    /**
     * type:
     * 1.NAME
     * 2.NAME2
     * 3.NUM
     * 4.ID
     * 5.IPRANGE
     * 6.LONGNUM
     * 7.IP
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

                {RISIS, "router isis {NAME}"},
                {IntfName, "interface {NAME}|int {NAME}"},
                // route
                //FIXME NUM or IP or ID
                {NET, "NET {NUM}"},


                
                // region
                {ISTYPE, "is-type {NAME(level-1,level-1-2,level-2-only)}"},

                // interface
                {IPROUTERISIS, "ip router isis {NAME}"},
                {CIRCUITTYPE, "isis circuit-type {NAME(level-1,level-1-2,level-2)}"},
                {CSNPINTERVAL, "isis csnp-interval {NUM(1-600)} {NAME(level-1,level-2)}"},
                {HELLOPADDING, "isis hello padding"},
                



                //minimal args is the first LexDef of no operation
                {NOROSPF, "no router ospf"},
                //FIXME this is the simple fix of no ospf router-id, because no ospf router-id {ID} , id should be same for unset
                //we don't support this in this generator
                {NORID, "no ospf router-id"},
                {NORABRTYPE, "no ospf abr-type {NAME(standard,shortcut,ibm,cisco)}"},
                {NONETAREAID, "no network {IPRANGE} area {ID}"},
                {NOPASSIVEINTFDEFUALT, "no passive-interface default"},
                {NOTIMERSTHROTTLESPF, "no timers throttle spf|no timers throttle spf {NUM(0-600000)} {NUM2(0-600000)} {NUM3(0-600000)}"},
                {NOMAXIMUMPATHS, "no maximum-paths|no maximum-paths {NUM(1-64)}"},
                {NOWRITEMULTIPLIER, "no write-multiplier {NUM(1-100)}"},
                {NOSOCKETBUFFERSEND, "no socket buffer send|no socket buffer send {LONGNUM(1-4000000000)}"},
                {NOSOCKETBUFFERRECV, "no socket buffer recv|no socket buffer recv {LONGNUM(1-4000000000)}"},
                //SOCKETBUFFERALL
                //{NOSOCKETBUFFERALL, "no socket buffer all|no socket buffer all {LONGNUM(1-4000000000)}"},
                {SOCKETPERINTERFACE, "socket-per-interface"},
                {NOAreaRange, "no area {ID(NUM)} range {IPRANGE}|no area {ID(NUM)} range {IPRANGE} advertise"},
                {NOAreaRangeNoAd, "no area {ID(NUM)} range {IPRANGE} not-advertise"},
                //{NOAreaRangeSub, "no area {ID(NUM)} range {IPRANGE} substitute {IP}"},
                //FIXME no ...cost only should be added once we fix frr bug
                {NOAreaRangeCost, "no area {ID(NUM)} range {IPRANGE} cost {NUM(0-16777215)}|no area {ID(NUM)} range {IPRANGE} advertise cost {NUM(0-16777215)}"},
                {NOAreaVLink, "no area {ID(NUM)} virtual-link {ID2}"},
                //FIXME default should be added once we fix frr bug
                {NOAreaShortcut, "no area {ID(NUM)} shortcut {NAME(enable,disable)}"},
                {NOAreaStub, "no area {ID(NUM)} stub"},

                //FIXME simple fix of NOAreaStubTotal
                //{NOAreaStubTotal, "no area {ID(NUM)} stub no-summary"},
                {NOAreaNSSA, "no area {ID(NUM)} nssa"},
                {NORefreshTimer,  "no refresh timer {NUM(10-1800)}"},
                {NOTimersLsaThrottle, "no timers throttle lsa all {NUM(0-5000)}"},
                {NOIPAddr, "no ip address {IP}"},
                {NOIpOspfArea, "no ip ospf area | no ip ospf area {ID(NUM)}"},
                {NOIpOspfCost, "no ip ospf cost | no ip ospf cost {NUM(1-65535)}"},
                {NOIpOspfDeadInter, "no ip ospf dead-interval | no ip ospf dead-interval {NUM(1-65535)}"},
                {NOIpOspfDeadInterMulti, "no ip ospf dead-interval minimal hello-multiplier | no ip ospf dead-interval minimal hello-multiplier {NUM(2-20)}"},
                {NOIpOspfHelloInter, "no ip ospf hello-interval | no ip ospf hello-interval {NUM(1-65535)}"},
                {NOIpOspfGRHelloDelay, "no ip ospf graceful-restart hello-delay | no ip ospf graceful-restart hello-delay {NUM(1-1800)}"},
                {NOIpOspfNet, "no ip ospf network | no ip ospf network {NAME(broadcast,non-broadcast)}"},
                {NOIpOspfPriority, "no ip ospf priority | no ip ospf priority {NUM(0-255)}"},
                {NOIpOspfRetransInter, "no ip ospf retransmit-interval | no ip ospf retransmit-interval {NUM(1-65535)}"},
                {NOIpOspfTransDelay, "no ip ospf transmit-delay | no ip ospf transmit-delay {NUM(1-65535)}"},
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
