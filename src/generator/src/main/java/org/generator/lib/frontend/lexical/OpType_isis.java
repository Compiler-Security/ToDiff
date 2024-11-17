package org.generator.lib.frontend.lexical;

import org.jetbrains.annotations.NotNull;

public enum OpType_isis {
    NODEADD,
    NODEDEL,
    NODESETISISUP,
    NODESETISISRE,
    NODESETISISSHUTDOWN,

    INTFUP,
    INTFDOWN,
    LINKADD,
    LINKDOWN,
    LINKREMOVE,


    //Don't change this!
    ISISCONF,

    RISIS,
    IntfName,
    //=================ISIS ROUTER==================

    NET,
    //===================ISIS region=====================
    ISTYPE,


    //===================ISIS INTERFACE================
    IPROUTERISIS,
    CIRCUITTYPE,
    CSNPINTERVAL,
    HELLOPADDING,
    

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
