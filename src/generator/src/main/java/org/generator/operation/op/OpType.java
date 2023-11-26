package org.generator.operation.op;

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

    //=================OSPF ROUTER==================
    ROSPF("router ospf",

            """
                    ADD ospf
                    ADD ospfdaemon LINK ospf
                    """,
            "router ospf"),

    //TODO ROSPFNUM("router ospf [NUM]"),
    //TODO ROSPFVRF("router ospf vrf [NAME]"),

    RID("ospf router-id {ID}",
            """
                    SET ospf.router-id {ID}
                    """,
            "ospf router-id A.B.C.D"),
    //FIXME consistency?
    RABRTYPE("ospf abr-type {NAME}",
            """
                    IF HAS ospf, ospfintf WHERE ospf->ospfintf && ospfintf.area == 0
                    SET ospf.abr-type {NAME}
                    """,
            "ospf abr-type TYPE"),
    //FIXME consistency?
    NETAREAID("network {IP} area {ID}",
            """
                    FOR (ANY intf WHERE intf.ip in {IP})
                        IF !(HAS ospfintf WHERE intf->ospfintf)
                            ADD ospfintf
                        SET ospfintf.area {ID}
                    """,
            "network A.B.C.D/M area A.B.C.D"),
    NETAREAIDNUM("network {IP} area {IDNUM}",
            """
                    LET {ID} = IPV4({IDNUM})
                    SAME AS ABOVE
                    """,
            "network A.B.C.D/M area (0-4294967295)"),

    //FIXME consistency?
    PASSIVEINTFDEFUALT("passive-interface default",
            """
                        FOR (ANY ospfintf)
                            SET ospfintf.passive true
                    """,
            "passive-interface default"
            ),

    TIMERSTHROTTLESPF("timers throttle spf {NUM} {NUM1} {NUM2}",
            """
                        MEET has ospf
                        MEET 0<={NUM}<=600000, 0<={NUM1}<=600000, 0<={NUM2}<=600000
                        SET ospf.initdelay NUM
                        SET ospf.initholdtime NUM1
                        SET ospf.maxholdtime NUM2
                    """,
            "timers throttle spf (0-600000) (0-600000) (0-600000)"),
    //TODO max-metric...
    //TODO auto-cost it's hard to eqaul

    //=============OSPFDAEMON===================
    //TODO proactive-arp

    OSPFDAEMONGROUPBEGIN("", "", ""),
    //FIXME this instruction is not full
    CLEARIPOSPFPROCESS("clear ip ospf process", "EMPTY", "clear ip ospf process"),
    CLEARIPOSPFNEIGHBOR("clear ip ospf neighbor", "EMPTY", "clear ip ospf neighbor"),

    MAXIMUMPATHS("maximum-paths {NUM}", """
                MEET has ospfdaemon
                MEET 1<={NUM}<=64
                SET ospfdaemon.maxpaths {NUM}
            """,
            "maximum-paths (1-64)"),
    WRITEMULTIPLIER("write-multiplier {NUM}", """
                MEET has ospfdaemon
                MEET 1<={NUM}<=100
                SET ospfdaemon.writemulti {NUM}
            """,
            "write-multiplier (1-100)"),
    SOCKETBUFFERSEND("socket buffer send {NUM}", """
                MEET has ospfdaemon
                MEET 1<={NUM}<=4000000000
                SET ospfdaemon.buffersend {NUM}
            """,
            "socket buffer send (1-4000000000)"),
    SOCKETBUFFERRECV("socket buffer recv {NUM}", """
                MEET has ospfdaemon
                MEET 1<={NUM}<=4000000000
                SET ospfdaemon.bufferrecv {NUM}
            """,
            "socket buffer recv (1-4000000000)"),
    SOCKETBUFFERALL("socket buffer all {NUM}", """
                MEET has ospfdaemon
                MEET 1<={NUM}<=4000000000
                SET ospfdaemon.buffersend {NUM}
                SET ospfdaemon.bufferrecv {NUM}
            """,
            "socket buffer all (1-4000000000)"),
    NOSOCKETPERINTERFACE("no socket-per-interface", """
                MEET has ospfdaemon
                SET ospfdaemon.socketPerInterface False
            """,
            "no socket-per-interface"),

    OSPFDAEMONGROUPEND("", "", ""),
    //===================OSPF AREA=====================
    //FIXME what if we already have the same area range
    //FIXME consistency?

    OSPFAREAGROUPBEGIN("", "", ""),
    AreaRange("area {ID} range {IP}", """
                MEET HAS ospfintf WHERE ospfintf.area == 0
                MEET HAS ospf
                IF (HAS  ospfnet WHERE ospfnet.area == {ID} && ospfnet.ip in {IP})
                    IF !(HAS areaSum WHERE ospf->areaSum && areaSum.area == {ID})
                        ADD areaSum LINK ospf
                    IF !(HAS areaSumEntry WHERE areaSumEntry in areaSum && areaSumEntry.range=={IP})
                        ADD areaSumEntry TO areaSum
                        SET areaSumEntry.range {IP}
                    SET areaSumEntry.net GROUP (ANY ospfnet WHERE ospfnet.area == {ID} && ospfnet.ip in {IP})
            """,
            "area A.B.C.D range A.B.C.D/M"),
    AreaRangeAd("area {ID} range {IP} advertise", """
                #AREARANGE
                SET areaSumEntry.advertise True
            """,
            "area A.B.C.D range A.B.C.D/M advertise"),
    AreaRangeAdCost("area {ID} range {IP} advertise cost {NUM}", """
                MEET 0<={NUM}<=16777215
                #AREARANGEADVERTISE
                SET areaSumEntry.advertise True
                SET areaSumEntry.cost {NUM}
            """,
            "area A.B.C.D range A.B.C.D/M advertise cost (0-16777215)"),
    AreaRangeNoAd("area {ID} range {IP} not-advertise", """
                #AREARANGE
                SET areaSumEntry.advertise False
            """,
            "area A.B.C.D range A.B.C.D/M not-advertise"),
    AreaRangeSub("area {ID} range {IP} substitute {IP2}", """
                #AREARANGE
                SET areaSumEntry.substitute {IP2}
            """,
            "area A.B.C.D range A.B.C.D/M substitute A.B.C.D/M"),
    AreaRangeCost("area {ID} range {IP} cost {NUM}", """
                MEET 0<={NUM}<=16777215
                #AREARANGE
                SET areaSumEntry.cost {NUM}
            """,
            "area A.B.C.D range A.B.C.D/M cost (0-16777215)"),
    AreaRangeINT("area {NUM} range {IP}", """
                MEET 0<={NUM}<=16777215
                let {ID} = ID({NUM})
                #AREARANGE
            """,
            "area (0-4294967295) range A.B.C.D/M"),
    AreaRangeAdINT("area {NUM} range {IP} advertise", """
                MEET 0<={NUM}<=16777215
                let {ID} = ID({NUM})
                #AREARANGEADVERTISE
            """,
            "area (0-4294967295) range A.B.C.D/M advertise"),
    AreaRangeAdCostINT("area {NUM} range {IP} advertise cost {NUM2}", """
                MEET 0<={NUM2}<=16777215
                let {ID} = ID({NUM2})
                #AREARANGEADVERTISECOST
            """,
            "area (0-4294967295) range A.B.C.D/M advertise cost (0-16777215)"),
    AreaRangeNoAdINT("area {NUM} range {IP} not-advertise", """
                MEET 0<={NUM}<=16777215
                let {ID} = ID({NUM})
                #AREARANGENOADVERTISE
            """,
            "area (0-4294967295) range A.B.C.D/M not-advertise"),
    AreaRangeSubINT("area {NUM} range {IP} substitute {IP2}", """
                MEET 0<={NUM}<=16777215
                let {ID} = ID({NUM})
                #AREARANGESUBSTITUTE
            """,
            "area A.B.C.D range A.B.C.D/M substitute A.B.C.D/M"),
    AreaRangeCostINT("area {NUM2} range {IP} cost {NUM}", """
                MEET 0<={NUM2}<=16777215
                let {ID} = ID({NUM2})
                #AREARANGECOST
            """,
            "area (0-4294967295) range A.B.C.D/M cost (0-16777215)"),
    AreaVLink("area {ID} virtual-link {ID2}", """
                MEET HAS ospf
                IF !(HAS areaSum WHERE areaSum.area == {ID})
                    ADD areaSum LINK ospf
                SET areaSum.virtualLink {ID2}
            """,
            "area A.B.C.D virtual-link A.B.C.D"),
    AreaShortcut("area {ID} shortcut", """
                MEET HAS ospf
                IF !(HAS areaSum WHERE areaSum.area == {ID})
                    ADD areaSum LINK ospf
                SET areaSum.shortcut True
            """,
            "area A.B.C.D shortcut"),
    AreaStub("area {ID} stub", """
                MEET HAS ospf
                IF !(HAS areaSum WHERE areaSum.area == {ID})
                    ADD areaSum LINK ospf
                SET areaSum.stub True
            """,
            "area A.B.C.D stub"),
    AreaStubTotal("area {ID} stub no-summary", """
                MEET HAS ospf
                IF !(HAS areaSum WHERE areaSum.area == {ID})
                    ADD areaSum LINK ospf
                SET areaSum.stub True
                SET areaSum.nosummary True
            """,
            "area A.B.C.D stub no-summary"),
    AreaNSSA("area {ID} nssa", """
                MEET HAS ospf
                IF !(HAS areaSum WHERE areaSum.area == {ID})
                    ADD areaSum LINK ospf
                SET areaSum.nssa True
            """,
            "area A.B.C.D nssa"),

    OSPFAREAGROUPEND("", "", ""),
    INVALID(".*", "", "");

    public static boolean inPhy(OpType typ) {
        return typ.ordinal() >= NODEADD.ordinal() && typ.ordinal() <= LINKREMOVE.ordinal();
    }

    public static boolean inOSPF(OpType typ) { 
        return typ.ordinal() >= ROSPF.ordinal() && typ.ordinal() <= NETAREAIDNUM.ordinal();
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

    public String template() {
        return template;
    }

    public String Re() {
        return reMap.get(this);
    }

    private final String template;
    private final String syntax;

    private final String raw;

    OpType(String template, String syntax, String raw) {
        this.template = template;
        this.syntax = syntax;
        this.raw = raw;
    }

    private static Map<OpType, String> reMap = new HashMap<>();

    static {
        for (OpType typ : OpType.values()) {
            String st = typ.template;
            String re = st;
            do {
                re = st;
                st = st.replaceAll("\\{([^{}]+)\\}", "(?<$1>[0-9a-zA-Z.-]+)");
                //st = st.replaceAll("\\[(.*)\\]", "(?:$1)?");
            } while (!st.equals(re));
            re = re.replaceAll("\s+", "\\\\s+");
            reMap.put(typ, re);
        }
    }
}
