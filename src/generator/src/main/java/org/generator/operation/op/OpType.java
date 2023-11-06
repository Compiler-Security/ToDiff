package org.generator.operation.op;

public enum OpType {
    NODEADD( "node {NAME} add"),
    NODEDEL("node {NAME} del"),
    NODESETOSPFUP( "node {NAME} set OSPF up"),
    NODESETOSPFRE("node {NAME} set OSPF restart"),
    INTFUP( "intf {NAME} up"),
    INTFDOWN( "intf {NAME} down"),
    LINKUP("link {NAME} {NAME2} up"),
    LINKDOWN( "link {NAME} {NAME2} down"),
    LINKREMOVE("link {NAME} {NAME2} remove"),
    INVALID( ".*");

    public static boolean inPhy(OpType typ){
        return typ.ordinal() >= NODEADD.ordinal() && typ.ordinal() <= LINKREMOVE.ordinal();
    }
    public String template() {
        return template;
    }

    private final String template;
    OpType(String template){
        this.template = template;
    }
}
