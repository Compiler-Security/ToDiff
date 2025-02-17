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
    RefreshTimer,
    TimersLsaThrottle,
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

    //FIXME SOCKETBUFFERALL
    //SOCKETBUFFERALL,
    NoSOCKETPERINTERFACE,

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
    IpOspfTransDelay,
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
    //FIXME SOCKETBUFFERALL
    //NOSOCKETBUFFERALL,
    SOCKETPERINTERFACE,
    NOAreaRange,
    NOAreaRangeNoAd,
    //FIXME NoAreaRangeSub
    //NOAreaRangeSub,
    NOAreaRangeCost,
    NOAreaVLink,
    NOAreaShortcut,
    NOAreaStub,
    //FIXME simple fix of NOAreaStubTotal
    //NOAreaStubTotal,
    NORefreshTimer,
    NOTimersLsaThrottle,
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
    NOIpOspfTransDelay,
    NOIpOspfPassive,

    //====================RIP======================
    RRIP,

    NETWORKN,
    NETWORKI,
    NEIGHBOR,
    VERSION,
    DEFAULTMETRIC,
    DISTANCE,
    TIMERSBASIC,
    PASSIVEINTFDEFAULT,
    PASSIVEINTFNAME,

    IPSPLITPOISION,
    IPSPLITHORIZION,


    NORRIP,

    NONETWORKN,
    NONETWORKI,
    NONEIGHBOR,
    NOVERSION,
    NODEFAULTMETRIC,
    NODISTANCE,
    NOPASSIVEINTFDEFAULT,
    NOPASSIVEINTFNAME,
    NOTIMERSBASIC,

    NOIPSPLITPOISION,
    NOIPSPLITHORIZION;

    //=======OSPF Function============
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


    //==========COMMON Function======================
    private boolean isOSPFUnsetOp(){
        return this.ordinal() >= NOROSPF.ordinal() && this.ordinal() <= NOIpOspfPassive.ordinal();
    }

    private boolean isRIPUnsetOp(){
        return this.ordinal() >= NORRIP.ordinal() && this.ordinal() <= NOIPSPLITHORIZION.ordinal();
    }

    /**
     * All unset Op
     * @return
     */
    public boolean isUnsetOp(){
        return  isOSPFUnsetOp() || isRIPUnsetOp();
    }

    //-------------------------------------------------
    private boolean isOSPFSetOp(){
        return this.ordinal() > OSPFCONF.ordinal() && this.ordinal() < OSPFIntfGroupEND.ordinal();
    }

    private boolean isRIPSetOp(){
        return this.ordinal() >= RRIP.ordinal() && this.ordinal() <= IPSPLITHORIZION.ordinal();
    }

    /**
     * All set op, include router XXX, interface name
     * @return
     */
    public boolean isSetOp(){
        return isOSPFSetOp() || isRIPSetOp();
    }

    //-----------------------------------------------------
    private boolean isOSPFRouterOp(){
        return (this.ordinal() > OSPFROUTERBEGIN.ordinal() && this.ordinal() < OSPFAREAGROUPEND.ordinal()) || (this.ordinal() >= NORID.ordinal() && this.ordinal() <= NOAreaNSSA.ordinal());
    }

    private boolean isRIPRouterOp(){
        return (this.ordinal() >= NETWORKN.ordinal() && this.ordinal() <= PASSIVEINTFNAME.ordinal()) || (this.ordinal() >= NONETWORKN.ordinal() && this.ordinal() <= NOTIMERSBASIC.ordinal());
    }
    /**
     * all ops(set/unset) in router XXX, don't include router XXX, no router XXX, intf name
     * @return
     */
    public boolean isRouterOp(){
        return isOSPFRouterOp() || isRIPRouterOp();
    }

    //------------------------------------------------------
    private boolean isOSPFIntfOp(){
        return (this.ordinal() > OSPFIntfGroupBEGIN.ordinal() && this.ordinal() < OSPFIntfGroupEND.ordinal()) || (this.ordinal() > NOAreaStub.ordinal()) || this == IPAddr || this == NOIPAddr;
    }

    private boolean isRIPIntfOp(){
        return (this.ordinal() >= IPSPLITPOISION.ordinal() && this.ordinal() <= IPSPLITHORIZION.ordinal()) || (this.ordinal() >= NOIPSPLITPOISION.ordinal() && this.ordinal() <= NOIPSPLITHORIZION.ordinal()) ;
    }
    /**
     * all ops(set/unset) in interface {name}, don't include router ospf, no router ospf, intfname, include ip address
     * @return
     */
    public boolean isIntfOp(){
        return isOSPFIntfOp() || isRIPIntfOp();
    }
}
