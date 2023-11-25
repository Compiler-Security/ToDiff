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

    //===========OSPF ROUTER=============
    ROSPF("router ospf",

            """
                    SET ospf enable
                    """,
            "router ospf"),
    //FIXME current we don't support multiple instance/VRF
//    ROSPFNUM("router ospf [NUM]"),
//    ROSPFVRF("router ospf vrf [NAME]"),
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
                    FOR (ANY intf, ospfintf WHERE intf->ospfintf && intf.ip in {IP})
                        IF ospfintf.enable == true
                            SET ospfintf.area {ID}
                        ELSE
                            SET ospfintf.enable true
                            SET ospfintf.area {ID}
                    """,
            "network A.B.C.D/M area A.B.C.D"),
    NETAREAIDNUM("network {IP} area {IDNUM}",
            """
                    SAME AS ABOVE
                    """,
            "network A.B.C.D/M area (0-4294967295)"),




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
