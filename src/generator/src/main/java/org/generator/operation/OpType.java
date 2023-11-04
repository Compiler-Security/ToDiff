package org.generator.operation;

public enum OpType {
    NODEADD(1, "node {NAME} add"),
    NODEDEL(2, "node {NAME} del"),
    NODESETOSPF(3, "node {NAME} set OSPF {DETAIL}"),
    INTFUP(4, "intf {NAME} up"),
    INTFDOWN(5, "intf {NAME} down"),
    LINKUP(6, "link {NAME} {NAME2} up"),
    LINKDOWN(7, "link {NAME} {NAME2} down"),
    LINKREMOVE(8, "link {NAME} {NAME2} remove"),
    INVALID(0, ".*");
    private final int num;

    public String template() {
        return template;
    }

    private final String template;
    OpType(int num, String template){
        this.num = num;
        this.template = template;
    }
}
