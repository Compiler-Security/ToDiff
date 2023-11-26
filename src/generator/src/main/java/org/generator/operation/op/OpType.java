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
                    ADD ospfdaemon
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
                        MEET 0<=NUM<=600000, 0<=NUM1<=600000, 0<=NUM2<=600000
                        SET ospf.initdelay NUM
                        SET ospf.initholdtime NUM1
                        SET ospf.maxholdtime NUM2
                    """,
            "timers throttle spf (0-600000) (0-600000) (0-600000)"),
    //TODO max-metric...
    //TODO auto-cost it's hard to eqaul

    //=============OSPFDAEMON===================
    //TODO proactive-arp


    //FIXME this instruction is not full
    CLEARIPOSPFPROCESS("clear ip ospf process", "EMPTY", "clear ip ospf process"),
    CLEARIPOSPFNEIGHBOR("clear ip ospf neighbor", "EMPTY", "clear ip ospf neighbor"),

    MAXIMUMPATHS("maximum-paths {NUM}", """
                MEET 1<=NUM<=64
                SET ospfdaemon.maxpaths {NUM}
            """,
            "maximum-paths (1-64)"),
    WRITEMULTIPLIER("write-multiplier {NUM}", """
                MEET 1<=NUM<=100
                SET ospfdaemon.writemulti {NUM}
            """,
            "write-multiplier (1-100)"),
    SOCKETBUFFERSEND("socket buffer send {NUM}", """
                MEET 1<=NUM<=4000000000
                SET ospfdaemon.buffersend {NUM}
            """,
            "socket buffer send (1-4000000000)"),
    SOCKETBUFFERRECV("socket buffer recv {NUM}", """
                MEET 1<=NUM<=4000000000
                SET ospfdaemon.bufferrecv {NUM}
            """,
            "socket buffer recv (1-4000000000)"),
    SOCKETBUFFERALL("socket buffer all {NUM}", """
                MEET 1<=NUM<=4000000000
                SET ospfdaemon.buffersend {NUM}
                SET ospfdaemon.bufferrecv {NUM}
            """,
            "socket buffer all (1-4000000000)"),
    NOSOCKETPERINTERFACE("no socket-per-interface", """
                SET ospfdaemon.socket-per-interface False
            """,
            "no socket-per-interface"),

    //===================OSPF AREA=====================
    //FIXME what if we already have the same area range
    //FIXME consistency?

    AREARANGE("area {ID} range {IP}", """
                MEET HAS ospfintf WHERE ospfintf.area == 0
                IF (HAS  ospfnet WHERE ospfnet.area == {ID} && ospfnet.ip in {IP})
                    IF !(HAS areaSum WHERE areaSum.area == {ID})
                        ADD areaSum LINK ospf
                    IF !(HAS areaSumEntry WHERE areaSumEntry in areaSum && areaSumEntry.range=={IP})
                        ADD areaSumEntry TO areaSum
                        SET areaSumEntry.range {IP}
                    SET areaSumEntry.net GROUP (ANY ospfnet WHERE ospfnet.area == {ID} && ospfnet.ip in {IP})
            """,
            "area A.B.C.D range A.B.C.D/M"),
    AREARANGEADVERTISE("area {ID} range {IP} advertise", """
                #AREARANGE
                SET areaSumEntry.advertise True
            """,
            "area A.B.C.D range A.B.C.D/M advertise"),
    AREARANGEADVERTISECOST("area {ID} range {IP} advertise cost {NUM}", """
                MEET 0<=NUM<=16777215
                #AREARANGEADVERTISE
                SET areaSumEntry.cost {NUM}
            """,
            "area A.B.C.D range A.B.C.D/M advertise cost (0-16777215)"),
    AREARANGENOADVERTISE("area {ID} range {IP} not-advertise", """
                #AREARANGE
                SET areaSumEntry.advertise False
            """,
            "area A.B.C.D range A.B.C.D/M not-advertise"),
    AREARANGESUBSTITUTE("area {ID} range {IP} substitute {IP2}", """
                #AREARANGE
                SET areaSumEntry.substitute {IP2}
            """,
            "area A.B.C.D range A.B.C.D/M substitute A.B.C.D/M"),
    AREARANGECOST("area {ID} range {IP} cost {NUM}", """
                MEET 0<=NUM <=16777215
                #AREARANGE
                SET areaSumEntry.cost {NUM}
            """,
            "area A.B.C.D range A.B.C.D/M cost (0-16777215)"),

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
