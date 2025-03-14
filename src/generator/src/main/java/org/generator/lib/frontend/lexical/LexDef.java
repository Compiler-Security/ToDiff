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
                //MULTI:
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
                {NODESETRIPUP, "node {NAME} set RIP up"},
                {NODESETRIPSHUTDOWN, "node {NAME} set RIP down"},
                {NODESETRIPRE, "node {NAME} set RIP restart"},
                {NODESETISISUP, "node {NAME} set ISIS up"},
                {NODESETISISSHUTDOWN, "node {NAME} set ISIS down"},
                {NODESETISISRE, "node {NAME} set ISIS restart"},
                {NODESETBABELUP, "node {NAME} set BABEL up"},
                {NODESETBABELSHUTDOWN, "node {NAME} set BABEL down"},
                {NODESETBABELRE, "node {NAME} set BABEL restart"},
                //{OSPFCONF, "ROSPFCONF"},

                {ROSPF, "router ospf"},
                {IntfName, "interface {NAME}|int {NAME}"},
                {RID, "ospf router-id {ID}"},
                {RABRTYPE, "ospf abr-type {NAME(standard,shortcut,ibm,cisco)}"},
                {NETAREAID, "network {IPRANGE} area {ID(NUM)}"},
                {PASSIVEINTFDEFUALT, "passive-interface default"},
                {TIMERSTHROTTLESPF, "timers throttle spf {NUM(0-600000)} {NUM2(0-600000)} {NUM3(0-600000)}"},
                //{CLEARIPOSPFPROCESS, "clear ip ospf process"},
                //{CLEARIPOSPFNEIGHBOR, "clear ip ospf neighbor"},
                {MAXIMUMPATHS, "maximum-paths {NUM(1-64)}"},
                {WRITEMULTIPLIER, "write-multiplier {NUM(1-100)}"},
                {SOCKETBUFFERSEND, "socket buffer send {LONGNUM(1-4000000000)}"},
                {SOCKETBUFFERRECV, "socket buffer recv {LONGNUM(1-4000000000)}"},
                //FIXME SOCKETBUFFERALL
                //{SOCKETBUFFERALL, "socket buffer all {LONGNUM(1-4000000000)}"},
                {NoSOCKETPERINTERFACE, "no socket-per-interface"},

                {AreaRange, "area {ID(NUM)} range {IPRANGE}|area {ID(NUM)} range {IPRANGE} advertise"},
                {AreaRangeNoAd, "area {ID(NUM)} range {IPRANGE} not-advertise"},
                {AreaRangeSub, "area {ID(NUM)} range {IPRANGE} substitute {IP}"},
                {AreaRangeCost, "area {ID(NUM)} range {IPRANGE} cost {NUM(0-16777215)}|area {ID(NUM)} range {IPRANGE} advertise cost {NUM(0-16777215)}"},
                //{AreaVLink, "area {ID(NUM)} virtual-link {ID2}"},
                {AreaShortcut, "area {ID(NUM)} shortcut {NAME(enable,disable,default)}"},
                {AreaStub, "area {ID(NUM)} stub"},
                {AreaStubTotal, "area {ID(NUM)} stub no-summary"},
                {AreaNSSA, "area {ID(NUM)} nssa"},
                {RefreshTimer, "refresh timer {NUM(10-1800)}"},
                {TimersLsaThrottle, "timers throttle lsa all {NUM(0-5000)}"},
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
                {IpOspfTransDelay, "ip ospf transmit-delay {NUM(1-65535)}"},
                {IpOspfPassive, "ip ospf passive"},


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

                {RRIP, "router rip"},

                {NETWORKN, "network {NAME}"},
                {NETWORKI, "network {IPRANGE}"},
                {NEIGHBOR, "neighbor {ID}"},
                {VERSION, "version {NUM(1-2)}"},
                {DEFAULTMETRIC, "default-metric {NUM(1-16)}"},
                {DISTANCE, "distance {NUM(1-255)}"},
                {TIMERSBASIC, "timers basic {NUM(5-2147483647)} {NUM2(5-2147483647)} {NUM3(5-2147483647)}"},
                {PASSIVEINTFDEFAULT, "passive-interface default"},
                {PASSIVEINTFNAME, "passive-interface {NAME}"},

                //default is no ip rip split-horizon, so the no command is ip rip split-horizon
                {IPSPLITHORIZION, "no ip rip split-horizon"},
                {IPSENDVERSION1, "ip rip send version 1"},
                {IPSENDVERSION2, "ip rip send version 2"},
                {IPSENDVERSION12, "ip rip send version 1 2"},
                {IPRECVVERSION1, "ip rip receive version 1"},
                {IPRECVVERSION2, "ip rip receive version 2"},
                {IPRECVVERSION12, "ip rip receive version 1 2"},
                {IPSPLITPOISION, "ip rip split-horizon poisoned-reverse"},


                {NORRIP, "no router rip"},

                {NONETWORKN, "no network {NAME}"},
                {NONETWORKI, "no network {IPRANGE}"},
                {NONEIGHBOR, "no neighbor {ID}"},
                {NOVERSION, "no version|no version {NUM(1-2)}"},
                {NODEFAULTMETRIC, "no default-metric|no default-metric {NUM(1-16)}"},
                {NODISTANCE, "no distance |no distance {NUM(1-255)}"},
                {NOPASSIVEINTFDEFAULT, "no passive-interface default"},
                {NOPASSIVEINTFNAME, "no passive-interface {NAME}"},
                {NOTIMERSBASIC, "no timers basic |no timers basic {NUM(5-2147483647)} {NUM2(5-2147483647)} {NUM3(5-2147483647)}"},

                {NOIPSPLITPOISION, "no ip rip split-horizon poisoned-reverse"},
                {NOIPSENDVERSION1, "no ip rip send version 1"},
                {NOIPSENDVERSION2, "no ip rip send version 2"},
                {NOIPSENDVERSION12, "no ip rip send version 1 2"},
                {NOIPRECVVERSION1, "no ip rip receive version 1"},
                {NOIPRECVVERSION2, "no ip rip receive version 2"},
                {NOIPRECVVERSION12, "no ip rip receive version 1 2"},
                {NOIPSPLITHORIZION, "ip split-horizon"},


                //IS-IS
                {RISIS, "router isis 1"},
                {IntfName, "interface {NAME}|int {NAME}"},
                //----route----
                {NET, "net {NET}"},
                
                
                //FIXME: we need "ATTACHEDBIT"!
                //{ATTACHEDBIT, "attached-bit {NAME(receive ignore,send)}"},
                //{METRICSTYLE, "metric-style {NAME(narrow,transition,wide)}"},
                {ADVERTISEHIGHMETRIC, "advertise-high-metrics"},
                {SETOVERLOADBIT, "set-overload-bit"},
                {SETOVERLOADBITONSTARTUP, "set-overload-bit on-startup {NUM(0-86400)}"},
                {LSPMTU, "lsp-mtu {NUM(128-4352)}"},
                {LSPGENINTERVAL, "lsp-gen-interval {NAME(level-1,level-2,_)} {NUM(1-120)}"},
                {SPFINTERVAL, "spf-interval {NAME(level-1,level-2,_)} {NUM(1-120)}"},
                // ---region----
                {ISTYPE, "is-type {NAME(level-1,level-1-2,level-2-only)}"},

                // -----interface----
                {IPROUTERISIS, "ip router isis 1"},
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
                {NOLSPGENINTERVAL, "no lsp-gen-interval {NAME(level-1,level-2,_)} {NUM(1-120)}"},
                {NOSPFINTERVAL, "no spf-interval {NAME(level-1,level-2,_)} {NUM(1-120)}"},
                {NOISTYPE, "no is-type {NAME(level-1,level-1-2,level-2-only)}"},
                {NOIPROUTERISIS, "no ip router isis 1"},
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

                {RBABEL, "router babel"},
                //FIXME is there babel id?
                //{BABELDI, "babel diversity"},
                {BNETWORKINTF, "network {NAME}"},
                {BWIRE, "babel {NAME(wired,wireless)}"},
                {BSPLITHORIZON, "no babel split-horizon"},
                {BHELLOINTERVAL, "babel hello-interval {NUM(20-655340)}"},
                {BUPDATEINTERVAL, "babel update-interval {NUM(20-655340)}"},
                {BCHANELNOINTEFERING, "babel channel {NAME(interfering,noninterfering)}"},
                {BRXCOST, "babel rxcost {NUM(1-65534)}"},
                {BRTTDECAY, "babel rtt-decay {NUM(1-256)}"},
                {BRTTMIN, "babel rtt-min {NUM(1-65535)}"},
                {BRTTMAX, "babel rtt-max {NUM(1-65536)}"},
                {BPENALTY, "babel max-rtt-penalty {NUM(0-65535)}"},
                {BENABLETIMESTAMP, "babel enable-timestamps"},
                {BRESENDDELAY, "babel resend-delay {NUM(20-655340)}"},
                {BSOMMOTHING, "babel smoothing-half-life {NUM(0-65534)}"},

                {NORBABEL, "no router babel"},
                //{NOBABELDI, "no babel diversity"},
                {NOBNETWORKINTF, "no network {NAME}"},
                {NOBWIRE, "no babel {NAME(wired,wireless)}"},
                {NOBSPLITHORIZON, "babel split-horizon"},
                //FIXME Current implementation is wrong as we should ignore the arguments
                {NOBHELLOINTERVAL, "no babel hello-interval {NUM(20-655340)}"},
                {NOBUPDATEINTERVAL, "no babel update-interval {NUM(20-655340)}"},
                {NOBCHANELNOINTEFERING, "no babel channel {NAME(interfering,noninterfering)}"},
                {NOBRXCOST, "no babel rxcost {NUM(1-65534)}"},
                {NOBRTTDECAY, "no babel rtt-decay {NUM(1-256)}"},
                {NOBRTTMIN, "no babel rtt-min {NUM(1-65535)}"},
                {NOBRTTMAX, "no babel rtt-max {NUM(1-65536)}"},
                {NOBPENALTY, "no babel max-rtt-penalty {NUM(0-65535)}"},
                {NOBENABLETIMESTAMP, "no babel enable-timestamps"},
                {NOBRESENDDELAY, "no babel resend-delay {NUM(20-655340)}"},
                {NOBSOMMOTHING, "no babel smoothing-half-life {NUM(0-65534)}"},
                //MULTI:
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
                    var tmp = Arrays.stream(value.split(","))
                         .map(s -> s.equals("_") ? "" : s)  // convert "_" to ""
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
