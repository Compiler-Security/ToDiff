package org.generator.lib.frontend.lexical;

import org.jetbrains.annotations.NotNull;

public enum OpType {
    NODEADD,
    NODEDEL,
    NODESETOSPFUP,
    NODESETOSPFRE,
    NODESETOSPFSHUTDOWN,

    INTFUP,
    INTFDOWN,
    LINKADD,
    LINKDOWN,
    LINKREMOVE,


    //Don't change this!
    OSPFCONF,

    ROSPF,

    //TODO ROSPFNUM,
    //TODO ROSPFVRF,

    IntfName,
    OSPFCONFEND,

    //=================OSPF ROUTER==================

    OSPFROUTERBEGIN,
    RID,
    RABRTYPE,
    NETAREAID,

    PASSIVEINTFDEFUALT,

    TIMERSTHROTTLESPF,
    //TODO max-metric...
    //TODO auto-cost it's hard to equal

    OSPFROUTEREND,
    //=============OSPFDAEMON===================
    //TODO proactive-arp
    CLEARIPOSPFPROCESS,
    CLEARIPOSPFNEIGHBOR,
    OSPFDAEMONGROUPBEGIN,
    //FIXME this instruction's ctx is OSPFCONF
    MAXIMUMPATHS,
    WRITEMULTIPLIER,
    SOCKETBUFFERSEND,
    SOCKETBUFFERRECV,
    SOCKETBUFFERALL,
    NOSOCKETPERINTERFACE,

    OSPFDAEMONGROUPEND,
    //===================OSPF AREA=====================
    //FIXME what if we already have the same area range

    OSPFAREAGROUPBEGIN,

    //FIXME IP equal is prefix ==, mask & ip ==
    AreaRange,
    AreaRangeNoAd,
    AreaRangeSub,
    AreaRangeCost,

    //FIXME area can have multiple virtual-link
    //FIXME areaVLINK
    //AreaVLink,

    AreaShortcut,
    AreaStub,
    AreaStubTotal,

    AreaNSSA,
    //TODO AREA LEFT

    OSPFAREAGROUPEND,




    //FIXME we can set multiple ip to one interface, so here we should only generate one
    IPAddr,

    OSPFIntfGroupBEGIN,
    //NOT CONSIDER ip ospf authentication-key AUTH_KEY
    //NOT Consider ip ospf authentication message-digest
    //NOT consider ip ospf message-digest-key KEYID md5 KEY
    //NOT consider ip ospf authentication key-chain KEYCHAIN

    IpOspfArea,
    IpOspfCost,
    IpOspfDeadInter,
    IpOspfDeadInterMulti,
    IpOspfHelloInter,
    IpOspfGRHelloDelay,
    //FIXME what is nonbroadcast?
    IpOspfNet,
    IpOspfPriority,
    IpOspfRetransInter,
    IpOspfTransDealy,
    IpOspfPassive,
//    TODO IpOspfPrefixSupp,

    OSPFIntfGroupEND,
    INVALID,
    NOROSPF,
    NORID,
    NORABRTYPE,
    NONETAREAID,
    NOPASSIVEINTFDEFUALT,
    NOTIMERSTHROTTLESPF,
    NOCLEARIPOSPFPROCESS,
    NOCLEARIPOSPFNEIGHBOR,
    NOMAXIMUMPATHS,
    NOWRITEMULTIPLIER,
    NOSOCKETBUFFERSEND,
    NOSOCKETBUFFERRECV,
    NOSOCKETBUFFERALL,
    NONOSOCKETPERINTERFACE,
    NOAreaRange,
    NOAreaRangeNoAd,
    NOAreaRangeSub,
    NOAreaRangeCost,
    NOAreaVLink,
    NOAreaShortcut,
    NOAreaStub,
    //FIXME simple fix of NOAreaStubTotal
    //NOAreaStubTotal,
    NOAreaNSSA,
    NOIPAddr,
    NOIpOspfArea,
    NOIpOspfCost,
    NOIpOspfDeadInter,
    NOIpOspfDeadInterMulti,
    NOIpOspfHelloInter,
    NOIpOspfGRHelloDelay,
    NOIpOspfNet,
    NOIpOspfPriority,
    NOIpOspfRetransInter,
    NOIpOspfTransDealy,
    NOIpOspfPassive;

    public static boolean inPhy(@NotNull OpType typ) {
        return typ.ordinal() >= NODEADD.ordinal() && typ.ordinal() <= LINKREMOVE.ordinal();
    }

    public static boolean inOSPF(@NotNull OpType typ) {
        return typ.ordinal() > OSPFCONF.ordinal() && typ.ordinal() < OSPFIntfGroupEND.ordinal();
    }

    public  boolean inOSPFRouterWithTopo() {
        return this.ordinal() > OSPFROUTERBEGIN.ordinal() && this.ordinal() < OSPFROUTEREND.ordinal();
    }

    public boolean inOSPFDAEMON(){
        return this.ordinal() > OSPFDAEMONGROUPBEGIN.ordinal() && this.ordinal() < OSPFDAEMONGROUPEND.ordinal();
    }

    public  boolean inOSPFAREA(){
        return this.ordinal() > OSPFAREAGROUPBEGIN.ordinal() && this.ordinal() < OSPFAREAGROUPEND.ordinal();
    }

    public  boolean inOSPFINTF(){
        return this.ordinal() > OSPFIntfGroupBEGIN.ordinal() && this.ordinal() < OSPFIntfGroupEND.ordinal();
    }

    /**
     * All unset Op
     * @return
     */
    public boolean isUnsetOp(){
        return this.ordinal() >= NOROSPF.ordinal() && this.ordinal() <= NOIpOspfPassive.ordinal();
    }

    /**
     * All ospf set op, include ROSPF and INTFNAME
     * @return
     */
    public boolean isSetOp(){
        return this.ordinal() > OSPFCONF.ordinal() && this.ordinal() < OSPFIntfGroupEND.ordinal();
    }

    /**
     * all ops(set/unset) in router ospf, don't include router ospf, no router ospf, intf name
     * @return
     */
    public boolean isRouterOp(){
        return (this.ordinal() > OSPFROUTERBEGIN.ordinal() && this.ordinal() < OSPFAREAGROUPEND.ordinal()) || (this.ordinal() >= NORID.ordinal() && this.ordinal() <= NOAreaNSSA.ordinal());
    }

    /**
     * all ops(set/unset) in interface {name}, don't include router ospf, no router ospf, intfname, include ip address
     * @return
     */
    public boolean isIntfOp(){
        return (this.ordinal() > OSPFIntfGroupBEGIN.ordinal() && this.ordinal() < OSPFIntfGroupEND.ordinal()) || (this.ordinal() > NOAreaStub.ordinal()) || this == IPAddr || this == NOIPAddr;
    }
}
