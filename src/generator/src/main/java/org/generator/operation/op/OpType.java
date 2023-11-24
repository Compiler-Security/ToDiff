package org.generator.operation.op;

public enum OpType {
    NODEADD( "node {NAME} add"),
    NODEDEL("node {NAME} del"),
    NODESETOSPFUP( "node {NAME} set ospf up"),
    NODESETOSPFRE("node {NAME} set ospf restart"),
    INTFUP( "intf {NAME} up"),
    INTFDOWN( "intf {NAME} down"),
    LINKUP("link {NAME} {NAME2} up"),
    LINKDOWN( "link {NAME} {NAME2} down"),
    LINKREMOVE("link {NAME} {NAME2} remove"),
    ROSPF("router ospf"),
    //FIXME current we don't support multiple instance/VRF
//    ROSPFNUM("router ospf [NUM]"),
//    ROSPFVRF("router ospf vrf [NAME]"),
    RID("ospf router-id {ID}"),
    RABRTYPE("ospf abr-type {NAME}"),
    NETAREAID("network {IP} area {ID}"),
    NETAREAIDNUM("network {IP} area {IDNUM}"),
    INVALID( ".*");

    public static boolean inPhy(OpType typ){
        return typ.ordinal() >= NODEADD.ordinal() && typ.ordinal() <= LINKREMOVE.ordinal();
    }

    public static boolean inOSPF(OpType typ){
        return typ.ordinal() >= ROSPF.ordinal() && typ.ordinal() <= NETAREAIDNUM.ordinal();
    }

    public static boolean inOSPFRouterWithTopo(OpType typ){
        return typ.ordinal() >= ROSPF.ordinal() && typ.ordinal() <= NETAREAIDNUM.ordinal();
    }
    public String template() {
        return template;
    }

    private final String template;
    OpType(String template){
        this.template = template;
    }
}
