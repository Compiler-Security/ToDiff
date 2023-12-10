package org.generator.operation.op;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum OpType {
    NODEADD("node {NAME} add", "", ""),
    NODEDEL("node {NAME} del", "", ""),
    NODESETOSPFUP("node {NAME} set ospf up", "", ""),
    NODESETOSPFRE("node {NAME} set ospf restart", "", ""),
    INTFUP("intf {NAME} up", "", ""),
    INTFDOWN("intf {NAME} down", "", ""),
    LINKUP("link {NAME} {NAME2} up", "", ""),
    LINKDOWN("link {NAME} {NAME2} down", "", ""),
    LINKREMOVE("link {NAME} {NAME2} remove", "", ""),

    //TODO add all no operations
    OSPFCONF("ospfconf", "", ""),
    OSPFROUTERBEGIN("", "", ""),

    //=================OSPF ROUTER==================
    ROSPF("router ospf",
            "no router ospf",
            """
                    IF !cur_router->ospf
                        ADD ospf
                        ADD ospfdaemon LINK ospf
                    SET cur_ospf ospf
                    """,
            "router ospf"),

    //TODO ROSPFNUM("router ospf [NUM]"),
    //TODO ROSPFVRF("router ospf vrf [NAME]"),

    RID("ospf router-id {ID}",
            "no ospf router-id | no ospf router-id {ID}",
            """
                    MEET cur_ospf
                    SET ospf.router-id {ID}
                    """,
            "ospf router-id A.B.C.D"),
    RABRTYPE("ospf abr-type {NAME}",
            "no ospf abr-type | no ospf abr-type {NAME}",
            """
                    IF HAS ospf, ospfintf WHERE ospf->ospfintf && ospfintf.area == 0
                    SET ospf.abr-type {NAME}
                    """,
            "ospf abr-type TYPE"),
    NETAREAID("network {IP} area {ID}",
            "no network {IP} area {ID}",
            """
                    FOR (ANY intf WHERE intf.ip in {IP})
                        IF !(HAS ospfintf WHERE intf->ospfintf && !(HAS #IpOspfArea in ospfintf))
                            ADD ospfintf
                        SET ospfintf.area {ID}
                    """,
            "network A.B.C.D/M area A.B.C.D"),
    NETAREAIDNUM("network {IP} area {IDNUM}",
            "no network {IP} area {IDNUM}",
            """
                    LET {ID} = IPV4({IDNUM})
                    #NETAREAID
                    """,
            "network A.B.C.D/M area (0-4294967295)"),

    PASSIVEINTFDEFUALT("passive-interface default",
            "no passive-interface default",
            """
                        FOR (ANY ospfintf)
                            SET ospfintf.passive true
                    """,
            "passive-interface default"
            ),

    TIMERSTHROTTLESPF("timers throttle spf {NUM} {NUM1} {NUM2}",
            "no timers throttle spf | no timers throttle spf {NUM} | no timers throttle spf {NUM} {NUM1} | no timers throttle spf {NUM} {NUM1} {NUM2}",
            """
                        MEET has ospf
                        MEET 0<={NUM}<=600000, 0<={NUM1}<=600000, 0<={NUM2}<=600000
                        SET ospf.initdelay NUM
                        SET ospf.initholdtime NUM1
                        SET ospf.maxholdtime NUM2
                    """,
            "timers throttle spf (0-600000) (0-600000) (0-600000)"),
    //TODO max-metric...
    //TODO auto-cost it's hard to equal

    OSPFROUTEREND("","",""),
    //=============OSPFDAEMON===================
    //TODO proactive-arp

    OSPFDAEMONGROUPBEGIN("", "", ""),
    //FIXME this instruction is not full
    CLEARIPOSPFPROCESS("clear ip ospf process", "EMPTY", "clear ip ospf process"),
    CLEARIPOSPFNEIGHBOR("clear ip ospf neighbor", "EMPTY", "clear ip ospf neighbor"),

    MAXIMUMPATHS("maximum-paths {NUM}",
                "no maximum-paths | no maximum-paths {NUM}",
                """
                MEET has ospfdaemon
                MEET 1<={NUM}<=64
                SET ospfdaemon.maxpaths {NUM}
            """,
            "maximum-paths (1-64)"),
    WRITEMULTIPLIER("write-multiplier {NUM}",
                "no write-multiplier | no write-multiplier {NUM}",
                """
                MEET has ospfdaemon
                MEET 1<={NUM}<=100
                SET ospfdaemon.writemulti {NUM}
            """,
            "write-multiplier (1-100)"),
    SOCKETBUFFERSEND("socket buffer send {NUM}",
                "no socket buffer send | no socket buffer send {NUM}",
                """
                MEET has ospfdaemon
                MEET 1<={NUM}<=4000000000
                SET ospfdaemon.buffersend {NUM}
            """,
            "socket buffer send (1-4000000000)"),
    SOCKETBUFFERRECV("socket buffer recv {NUM}",
                "no socket buffer recv | no socket buffer recv {NUM}",
                """
                MEET has ospfdaemon
                MEET 1<={NUM}<=4000000000
                SET ospfdaemon.bufferrecv {NUM}
            """,
            "socket buffer recv (1-4000000000)"),
    SOCKETBUFFERALL("socket buffer all {NUM}",
                "no socket buffer all | no socket buffer all {NUM}",
                """
                MEET has ospfdaemon
                MEET 1<={NUM}<=4000000000
                SET ospfdaemon.buffersend {NUM}
                SET ospfdaemon.bufferrecv {NUM}
            """,
            "socket buffer all (1-4000000000)"),
    NOSOCKETPERINTERFACE("no socket-per-interface",
                //FIXME is this has no operation?
                """
                MEET has ospfdaemon
                SET ospfdaemon.socketPerInterface False
            """,
            "no socket-per-interface"),

    OSPFDAEMONGROUPEND("", "", ""),
    //===================OSPF AREA=====================
    //FIXME what if we already have the same area range
    //FIXME consistency?

    OSPFAREAGROUPBEGIN("", "", ""),
    AreaRange("area {ID} range {IP}",
                //FIXME is this no op right?
                "no area {ID} range | no area {ID} range {IP}",
                """
                MEET HAS ospfintf WHERE ospfintf.area == 0
                MEET HAS ospf
                IF (HAS  ospfnet WHERE ospfnet.area == {ID} && ospfnet.ip in {IP} &&ospfnet.hide == False)
                    IF !(HAS areaSum WHERE ospf->areaSum && areaSum.area == {ID})
                        ADD areaSum LINK ospf
                    IF !(HAS areaSumEntry WHERE areaSumEntry in areaSum && areaSumEntry.range=={IP})
                        ADD areaSumEntry TO areaSum
                        SET areaSumEntry.range {IP}
                    SET areaSumEntry.net GROUP (ANY ospfnet WHERE ospfnet.area == {ID} && ospfnet.ip in {IP})
            """,
            "area A.B.C.D range A.B.C.D/M"),
    AreaRangeAd("area {ID} range {IP} advertise",
                //FIXME is this no op right?
                "no area {ID} range {IP} advertise",
                """
                #AREARANGE
                SET areaSumEntry.advertise True
            """,
            "area A.B.C.D range A.B.C.D/M advertise"),
    AreaRangeAdCost("area {ID} range {IP} advertise cost {NUM}",
                "no area {ID} range {IP} advertise cost | no area {ID} range {IP} advertise cost {NUM}",
                """
                MEET 0<={NUM}<=16777215
                #AREARANGEADVERTISE
                SET areaSumEntry.advertise True
                SET areaSumEntry.cost {NUM}
            """,
            "area A.B.C.D range A.B.C.D/M advertise cost (0-16777215)"),
    AreaRangeNoAd("area {ID} range {IP} not-advertise",
                "no area {ID} range {IP} not-advertise",
                """
                #AREARANGE
                SET areaSumEntry.advertise False
            """,
            "area A.B.C.D range A.B.C.D/M not-advertise"),
    AreaRangeSub("area {ID} range {IP} substitute {IP2}",
                "no area {ID} range {IP} substitute | no area {ID} range {IP} substitute {IP2}",
                """
                #AREARANGE
                SET areaSumEntry.substitute {IP2}
            """,
            "area A.B.C.D range A.B.C.D/M substitute A.B.C.D/M"),
    AreaRangeCost("area {ID} range {IP} cost {NUM}",
                "no area {ID} range {IP} cost | no area {ID} range {IP} cost {NUM}",
                """
                MEET 0<={NUM}<=16777215
                #AREARANGE
                SET areaSumEntry.cost {NUM}
            """,
            "area A.B.C.D range A.B.C.D/M cost (0-16777215)"),
    AreaRangeINT("area {IDNUM} range {IP}",
                "no area {IDNUM} range | no area {IDNUM} range {IP}",
                """
                MEET 0<={NUM}<=16777215
                let {ID} = ID({NUM})
                #AREARANGE
            """,
            "area (0-4294967295) range A.B.C.D/M"),
    AreaRangeAdINT("area {IDNUM} range {IP} advertise",
                "no area {IDNUM} range {IP} advertise",
                """
                MEET 0<={NUM}<=16777215
                let {ID} = ID({NUM})
                #AREARANGEADVERTISE
            """,
            "area (0-4294967295) range A.B.C.D/M advertise"),
    AreaRangeAdCostINT("area {IDNUM} range {IP} advertise cost {NUM}",
                "no area {IDNUM} range {IP} advertise cost | no area {IDNUM} range {IP} advertise cost {NUM}",
                """
                MEET 0<={NUM2}<=16777215
                let {ID} = ID({NUM2})
                #AREARANGEADVERTISECOST
            """,
            "area (0-4294967295) range A.B.C.D/M advertise cost (0-16777215)"),
    AreaRangeNoAdINT("area {IDNUM} range {IP} not-advertise",
                "no area {IDNUM} range {IP} not-advertise",
                """
                MEET 0<={NUM}<=16777215
                let {ID} = ID({NUM})
                #AREARANGENOADVERTISE
            """,
            "area (0-4294967295) range A.B.C.D/M not-advertise"),
    AreaRangeSubINT("area {IDNUM} range {IP} substitute {IP2}",
                "no area {IDNUM} range {IP} substitute | no area {IDNUM} range {IP} substitute {IP2}",
                """
                MEET 0<={NUM}<=16777215
                let {ID} = ID({NUM})
                #AREARANGESUBSTITUTE
            """,
            "area A.B.C.D range A.B.C.D/M substitute A.B.C.D/M"),
    AreaRangeCostINT("area {IDNUM} range {IP} cost {NUM}",
                "no area {IDNUM} range {IP} cost | no area {IDNUM} range {IP} cost {NUM}",
                """
                MEET 0<={NUM2}<=16777215
                let {ID} = ID({NUM2})
                #AREARANGECOST
            """,
            "area (0-4294967295) range A.B.C.D/M cost (0-16777215)"),
    AreaVLink("area {ID} virtual-link {ID2}",
                //FIXME is this no op right?
                "no area {ID} virtual-link ",
                """
                MEET HAS ospf
                IF !(HAS areaSum WHERE areaSum.area == {ID})
                    ADD areaSum LINK ospf
                SET areaSum.virtualLink {ID2}
            """,
            "area A.B.C.D virtual-link A.B.C.D"),
    AreaShortcut("area {ID} shortcut",
                "no area {ID} shortcut",
                """
                MEET HAS ospf
                IF !(HAS areaSum WHERE areaSum.area == {ID})
                    ADD areaSum LINK ospf
                SET areaSum.shortcut True
            """,
            "area A.B.C.D shortcut"),
    AreaStub("area {ID} stub",
                "no area {ID} stub",
                """
                MEET HAS ospf
                IF !(HAS areaSum WHERE areaSum.area == {ID})
                    ADD areaSum LINK ospf
                SET areaSum.stub True
            """,
            "area A.B.C.D stub"),
    AreaStubTotal("area {ID} stub no-summary",
                "no area {ID} stub no-summary",
                """
                MEET HAS ospf
                IF !(HAS areaSum WHERE areaSum.area == {ID})
                    ADD areaSum LINK ospf
                SET areaSum.stub True
                SET areaSum.nosummary True
            """,
            "area A.B.C.D stub no-summary"),
    AreaNSSA("area {ID} nssa",
                "no area {ID} nssa",
                """
                MEET HAS ospf
                IF !(HAS areaSum WHERE areaSum.area == {ID})
                    ADD areaSum LINK ospf
                SET areaSum.nssa True
            """,
            "area A.B.C.D nssa"),

    //TODO AREA LEFT

    OSPFAREAGROUPEND("", "", ""),

    OSPFIntfGroupBEGIN("", "", ""),
    //FIXME syntax right?
    IntfName("interface {NAME}",
            """
            MEET HAS _curRouter
            if  !HAS intf WHERE intf.name == {NAME}
                ADD intf LINK _curRouter
                SET intf.name {NAME}
                ADD ospfintf LINK intf WHERE intf.name == {NAME}
            SET _curIntf intf
            SET _curOIntf ospfintf
            """,
            ""),
    IPAddr("ip address {IP}",
            //FIXME is this no op right?
            "no ip address | no ip address {IP}",
            """
            MEET HAS _curIntf
            SET _curIntf.ip {IP}
            """,
            "ip address ADDRESS/PREFIX"),

    //NOT CONSIDER ip ospf authentication-key AUTH_KEY
    //NOT Consider ip ospf authentication message-digest
    //NOT consider ip ospf message-digest-key KEYID md5 KEY
    //NOT consider ip ospf authentication key-chain KEYCHAIN

    IpOspfArea("ip ospf area {ID}",
            //FIXME is this no op right?
            "no ip ospf area {ID}",
            """
            MEET HAS _curOIntf
            MEET HAS _curIntf
            SET _curOIntf.area {ID}
            if (!HAS ospfnet where ospfnet.ip == _curIntf.ip && ospfnet.area == {ID})
                ADD ospfnet LINK _curTopo
                SET ospfnet.ip _curIntf.ip
                SET ospfnet.area {ID}
            """,
            "ip ospf area AREA"),
    IpOspfAreaINT("ip ospf area {IDNUM}",
            "no ip ospf area {IDNUM}",
            """
            MEET 0 <= {NUM} && {NUM} <= 4294967295
            LET {ID} = ID(NUM)
            #IpOspfArea
            """,
            "ip ospf area (0-4294967295)"),
    IpOspfCost("ip ospf cost {NUM}",
            "no ip ospf cost | no ip ospf cost {NUM}",
            """
            MEET HAS _curOIntf
            MEET 1 <= {NUM} && {NUM} <= 655535
            SET _curOIntf.cost {NUM}
            """,
            "ip ospf cost (1-65535)"),
    //FIXME default value?
    IpOspfDeadInter("ip ospf dead-interval {NUM}",
            "no ip ospf dead-interval | no ip ospf dead-interval {NUM}",
            """
            MEET HAS _curOIntf
            MEET 1<= {NUM} <= 65535
            SET _curOIntf.deadInterval {NUM}
            """,
            "ip ospf dead-interval (1-65535)"),
    IpOspfDeadInterMulti("ip ospf dead-interval minimal hello-multiplier {NUM}",
            "no ip ospf dead-interval minimal hello-multiplier | no ip ospf dead-interval minimal hello-multiplier {NUM}",
            """
            MEET HAS _curOIntf
            MEET 2 <= {NUM} <= 20           
            SET _curOIntf.helloPerSec {NUM}
            SET _curOIntf.helloInterval 0
            """,
            "ip ospf dead-interval minimal hello-multiplier (2-20)"),
    //FIXME this will not work if we set IpOspfDeadInterMulti, should we consider which is in the front?
    IpOspfHelloInter("ip ospf hello-interval {NUM}",
            "no ip ospf hello-interval | no ip ospf hello-interval {NUM}",
            """
            MEET HAS _curOIntf
            MEET 1 <= {NUM} <= 65535
            MEET !(has #IpOspfDeadInterMulti in _curOIntf)
            SET _curOIntf.helloInterval {NUM}
            """,
            "ip ospf hello-interval (1-65535)"),
    IpOspfGRHelloDelay("ip ospf graceful-restart hello-delay {NUM}",
            "no ip ospf graceful-restart hello-delay | no ip ospf graceful-restart hello-delay {NUM}",
            """
            MEET HAS _curOIntf
            MEET 1<= {NUM} <= 1800
            SET _curOIntf.GRHelloDelay {NUM}
            """,
            "ip ospf graceful-restart hello-delay (1-1800)"),
    //FIXME what is nonbroadcast?
    IpOspfNet("ip ospf network {NAME}",
            //FIXME is this no op right?
            "no ip ospf network | no ip ospf network {NAME}",
            """
            MEET {NAME} == broadcast || {NAME} == non-broadcast
            SET _curOIntf.netType {NAME}
            """,
            "ip ospf network (broadcast|non-broadcast| point-to-point)"),
    IpOspfPriority("ip ospf priority {NUM}",
            "no ip ospf priority | no ip ospf priority {NUM}",
            """
            MEET {0} <= {NUM} && {NUM} <= 255
            SET _curOIntf.priority {NUM}
            """,
            "ip ospf priority (0-255)"),
    IpOspfRetransInter("ip ospf retransmit-interval {NUM}",
            "ip ospf retransmit-interval | ip ospf retransmit-interval {NUM}",
            """
            MEET 1 <= NUM && NUM <= 65535
            SET _curOIntf.retransmit-interval {NUM}
            """,
            "ip ospf retransmit-interval (1-65535)"),
    IpOspfTransDealy("ip ospf transmit-dealy {NUM}",
                "no ip ospf transmit-dealy | no ip ospf transmit-dealy {NUM}",
                """
                MEET 1 <= NUM && NUM <= 65535
                SET _curOIntf.transmit-delay {NUM}
            """,
            "ip ospf transmit-delay (1-65535)"),
    IpOspfPassive("ip ospf passive",
                "no ip ospf passive",
                """
                SET _curOIntf.passive true
            """,
            "ip ospf passive"),

//    IpOspfPrefixSupp("ip ospf prefix-suppression {ID}", """
//
//            """,
//            "ip ospf prefix-suppression [A.B.C.D]"),

    OSPFIntfGroupEND("", "", ""),
    INVALID(".*", "", "");

    public static boolean inPhy(OpType typ) {
        return typ.ordinal() >= NODEADD.ordinal() && typ.ordinal() <= LINKREMOVE.ordinal();
    }

    public static boolean inOSPF(OpType typ) { 
        return typ.ordinal() >= OSPFROUTERBEGIN.ordinal() && typ.ordinal() <= OSPFIntfGroupEND.ordinal();
    }

    public static boolean inOSPFRouterWithTopo(OpType typ) {
        return typ.ordinal() >= ROSPF.ordinal() && typ.ordinal() <= NETAREAIDNUM.ordinal();
    }

    public static boolean inOSPFDAEMON(OpType typ){
        return typ.ordinal() > OSPFDAEMONGROUPBEGIN.ordinal() && typ.ordinal() < OSPFDAEMONGROUPEND.ordinal();
    }

    public static boolean inOSPFAREA(OpType typ){
        return typ.ordinal() > OSPFAREAGROUPBEGIN.ordinal() && typ.ordinal() < OSPFAREAGROUPEND.ordinal();
    }

    public static boolean inOSPFINTF(OpType typ){
        return typ.ordinal() > OSPFIntfGroupBEGIN.ordinal() && typ.ordinal() < OSPFIntfGroupEND.ordinal();
    }
    public String template() {
        return template;
    }

    public String Re() {
        return reMap.get(this);
    }

    public String[] UnsetRe(){
        return unsetReMap.get(this);
    }
    private final String template;
    private final String syntax;

    private final String raw;

    private final int noNum;

    public String[] getUnsetTemplate() {
        return unsetTemplate;
    }

    private  final String[] unsetTemplate;

    OpType(String template, String unset, String syntax, String raw) {
        this.template = template;
        this.syntax = syntax;
        this.raw = raw;
        this.unsetTemplate = Arrays.stream(unset.split("\\|")).map(String::strip).toArray(String[]::new);
        this.noNum = this.unsetTemplate.length;
    }

    OpType(String template, String syntax, String raw){
        this.template = template;
        this.syntax = syntax;
        this.raw = raw;
        this.unsetTemplate = new String[]{};
        this.noNum = 0;
    }

    private static Map<OpType, String> reMap = new HashMap<>();
    private static Map<OpType, String[]> unsetReMap = new HashMap<>();

    private static String changeToRe(String st){
        String re = st;
        do {
            re = st;
            st = st.replaceAll("\\{([^{}]+)\\}", "(?<$1>[0-9a-zA-Z./-]+)");
            //st = st.replaceAll("\\[(.*)\\]", "(?:$1)?");
        } while (!st.equals(re));
        re = re.replaceAll("\s+", "\\\\s+");
        return re;
    }
    static {
        for (OpType typ : OpType.values()) {
            reMap.put(typ, changeToRe(typ.template));
            unsetReMap.put(typ, Arrays.stream(typ.unsetTemplate).map(OpType::changeToRe).toArray(String[]::new));
        }
    }
}
