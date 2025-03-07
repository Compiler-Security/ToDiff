package org.generator.lib.frontend.lexical;

import org.generator.lib.generator.driver.generate;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public enum OpType {
    //=============PHY=====================
    NODEADD,
    NODEDEL,
    NODESETOSPFUP,
    NODESETOSPFRE,
    NODESETOSPFSHUTDOWN,
    NODESETRIPUP,
    NODESETRIPRE,
    NODESETRIPSHUTDOWN,
    NODESETISISUP,
    NODESETISISRE,
    NODESETISISSHUTDOWN,

    INTFUP,
    INTFDOWN,
    LINKADD,
    LINKDOWN,
    LINKREMOVE,

    //==============ZEBRA===================
    IntfName,
    IPAddr,
    NOIPAddr,

    //==============OSPF====================
    ROSPF,
    //--------------ROUTER------------------
    RID,
    RABRTYPE,
    NETAREAID,
    PASSIVEINTFDEFUALT,
    TIMERSTHROTTLESPF,
    //TODO max-metric...
    //TODO auto-cost it's hard to equal
    RefreshTimer,
    TimersLsaThrottle,
    //--------------DAEMON------------------
    //TODO proactive-arp
    //CLEARIPOSPFPROCESS,
    //CLEARIPOSPFNEIGHBOR,
    //FIXME this instruction's ctx is OSPFCONF
    MAXIMUMPATHS,
    WRITEMULTIPLIER,
    SOCKETBUFFERSEND,
    SOCKETBUFFERRECV,
    //FIXME SOCKETBUFFERALL
    //SOCKETBUFFERALL,
    NoSOCKETPERINTERFACE,
    //---------------AREA---------------------
    //FIXME what if we already have the same area range
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
    //--------------INTF----------------------
    //FIXME we can set multiple ip to one interface, so here we should only generate one
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
    //---------------UNSET----------------------
    NOROSPF,
    NORID,
    NORABRTYPE,
    NONETAREAID,
    NOPASSIVEINTFDEFUALT,
    NOTIMERSTHROTTLESPF,
    //NOCLEARIPOSPFPROCESS,
    //NOCLEARIPOSPFNEIGHBOR,
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
    //--------------------ROUTER-------------------
    NETWORKN,
    NETWORKI,
    NEIGHBOR,
    VERSION,
    DEFAULTMETRIC,
    DISTANCE,
    TIMERSBASIC,
    PASSIVEINTFDEFAULT,
    PASSIVEINTFNAME,
    //---------------------INTF---------------------
    IPSPLITPOISION,
    IPSENDVERSION1,
    IPSENDVERSION2,
    IPSENDVERSION12,
    IPRECVVERSION1,
    IPRECVVERSION2,
    IPRECVVERSION12,
    IPSPLITHORIZION,
    //---------------------UNSET--------------------

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
    NOIPSENDVERSION1,
    NOIPSENDVERSION2,
    NOIPSENDVERSION12,
    NOIPRECVVERSION1,
    NOIPRECVVERSION2,
    NOIPRECVVERSION12,
    NOIPSPLITHORIZION,
    //MULTI:

    //====================IS-IS=========================
    RISIS,
    //--------------ROUTER------------------
    //ISISROUTERBEGIN,    
    NET,
    ISTYPE,
    //ISISROUTEREND,
    //--------------DAEMON----------------
    //ISISDAEMONGROUPBEGIN,
    //ATTACHEDBIT,
    // METRICSTYLE,
    ADVERTISEHIGHMETRIC,
    SETOVERLOADBIT,
    SETOVERLOADBITONSTARTUP,
    LSPMTU,
    LSPGENINTERVAL,
    SPFINTERVAL,
    //ISISDAEMONGROUPEND,
    //--------------REGION------------------
    //ISISREGIONBEGIN,
    
    //ISISREGIONEND,

    //--------------INTERFACE----------------
    //ISISINTFBEGIN,
    IPROUTERISIS,
    CIRCUITTYPE,
    CSNPINTERVAL,
    NOHELLOPADDING,
    HELLOINTERVAL,
    HELLOMULTIPLIER,
    //ISISMETRICLEVEL1,
    //ISISMETRICLEVEL2,
    NETWORKPOINTTOPOINT,
    ISISPASSIVE,
    ISISPRIORITY,
    NOTHREEWAYHANDSHAKE,
    PSNPINTERVAL,

    //ISISEND,

    //--------------UNSET------------------
    NORISIS,
    NOTNET,
    
    //NOATTACHEDBIT,
    // NOMETRICSTYLE,
    NOADVERTISEHIGHMETRIC,
    NOSETOVERLOADBIT,
    NOSETOVERLOADBITONSTARTUP,
    NOLSPMTU,
    NOLSPGENINTERVAL,
    NOSPFINTERVAL,
    NOISTYPE,
    NOIPROUTERISIS,
    NOCIRCUITTYPE,
    NOCSNPINTERVAL,
    HELLOPADDING,
    NOHELLOINTERVAL,
    NOHELLOMULTIPLIER,
    //NOISISMETRICLEVEL1,
    //NOISISMETRICLEVEL2,
    NONETWORKPOINTTOPOINT,
    NOISISPASSIVE,
    NOISISPRIORITY,
    THREEWAYHANDSHAKE,
    NOPSNPINTERVAL,

    //============================EIGRP====================
    //----------------------------router-------------------
    REIGRP,
    ERID,
    EPASSIVEINTFNAME,
    ETIMERSACTIVE,
    ETIMERSACTIVEDISABLE,
    EVARIANCE,
    EMAXIMUMPATHS,
    EMETRIC,
    ENETWORKI,
    ENEIGHBOR,
    EDELAY,
    EBANDWIDTH,
    EHELLOINTERVAL,
    EHOLDTIME,
    ESUMMARY,

    NOREIGRP,
    NOERID,
    NOEPASSIVEINTFNAME,
    NOETIMERSACTIVE,
    NOEVARIANCE,
    NOEMAXIMUMPATHS,
    NOEMETRIC,
    NOENETWORKI,
    NOENEIGHBOR,
    NOEDELAY,
    NOEBANDWIDTH,
    NOEHELLOINTERVAL,
    NOEHOLDTIME,
    NOESUMMARY,


    //============================INVALID=========================
    INVALID;

    //=======OSPF Function============
    public static boolean inPhy(@NotNull OpType typ) {
        return typ.ordinal() >= NODEADD.ordinal() && typ.ordinal() <= LINKREMOVE.ordinal();
    }

//    public static boolean inOSPF(@NotNull OpType typ) {
//        return typ.ordinal() > OSPFCONF.ordinal() && typ.ordinal() < OSPFIntfGroupEND.ordinal();
//    }

    public  boolean inOSPFRouterWithTopo() {
        return this.ordinal() >= RID.ordinal() && this.ordinal() <= TimersLsaThrottle.ordinal();
    }

    public boolean inOSPFDAEMON(){
        return this.ordinal() >= MAXIMUMPATHS.ordinal() && this.ordinal() <= NoSOCKETPERINTERFACE.ordinal();
    }

    public  boolean inOSPFAREA(){
        return this.ordinal() >= AreaRange.ordinal() && this.ordinal() <= AreaNSSA.ordinal();
    }

    public  boolean inOSPFINTF(){
        return this.ordinal() >= IpOspfArea.ordinal() && this.ordinal() <= IpOspfPassive.ordinal();
    }


    //=======ISIS Function============
    public  boolean inISISRouterWithTopo() {
        return this.ordinal() >= NET.ordinal() && this.ordinal() <=ISTYPE.ordinal();
    }
    
    public boolean inISISDAEMON(){
        return this.ordinal() >=ADVERTISEHIGHMETRIC.ordinal() && this.ordinal() <= SPFINTERVAL.ordinal();
    }

    public  boolean inISISREGION(){
        return false;
    }

    public  boolean inISISINTF(){
        return this.ordinal() >= IPROUTERISIS.ordinal() && this.ordinal() <= PSNPINTERVAL.ordinal();
    }
    
    //==========COMMON Function======================
    public boolean isZEBRAUnsetOp(){
        return this == NOIPAddr;
    }

    public boolean isOSPFUnsetOp(){
        return this.ordinal() >= NOROSPF.ordinal() && this.ordinal() <= NOIpOspfPassive.ordinal();
    }

    public boolean isRIPUnsetOp(){
        return (this.ordinal() >= NORRIP.ordinal() && this.ordinal() <= NOIPSPLITHORIZION.ordinal()) || this==NOIPAddr;
    }

    public boolean isISISUnsetOp(){
        return (this.ordinal() >= NORISIS.ordinal() && this.ordinal() <= NOPSNPINTERVAL.ordinal()) || this==NOIPAddr;
    }
    /**
     * All unset Op
     * @return
     */
    public boolean isUnsetOp(){
        return  isZEBRAUnsetOp() || isOSPFUnsetOp() || isRIPUnsetOp() || isISISUnsetOp();
    }

    //-------------------------------------------------
    public boolean isZEBRASetOp(){
        return this.ordinal() >= IntfName.ordinal() && this.ordinal() <= IPAddr.ordinal();
    }

    public boolean isOSPFSetOp(){
        return this.ordinal() >= ROSPF.ordinal() && this.ordinal() <= IpOspfPassive.ordinal();
    }

    public boolean isRIPSetOp(){
        return (this.ordinal() >= RRIP.ordinal() && this.ordinal() <= IPSPLITHORIZION.ordinal()) || this== IntfName || this == IPAddr;
    }

    public boolean isISISSetOp(){
        return (this.ordinal() >= RISIS.ordinal() && this.ordinal() <= PSNPINTERVAL.ordinal())|| this== IntfName || this == IPAddr;
    }

    /**
     * All set op, include router XXX, interface name
     * @return
     */
    public boolean isSetOp(){
        return isZEBRASetOp() || isOSPFSetOp() || isRIPSetOp() || isISISSetOp();
    }

    //-----------------------------------------------------
    public boolean isZEBRARouterOp(){
        return false;
    }

    public boolean isOSPFRouterOp(){
        return (this.ordinal() >= RID.ordinal() && this.ordinal() <= AreaNSSA.ordinal()) || (this.ordinal() >= NORID.ordinal() && this.ordinal() <= NOAreaNSSA.ordinal());
    }

    private boolean isRIPRouterOp(){
        return (this.ordinal() >= NETWORKN.ordinal() && this.ordinal() <= PASSIVEINTFNAME.ordinal()) || (this.ordinal() >= NONETWORKN.ordinal() && this.ordinal() <= NOTIMERSBASIC.ordinal());
    }

    public boolean isISISRouterOp(){
        return (this.ordinal() >= NET.ordinal() && this.ordinal() <= SPFINTERVAL.ordinal()) || (this.ordinal() >= NOTNET.ordinal() && this.ordinal() <= NOISTYPE.ordinal());
    }
    /**
     * all ops(set/unset) in router XXX, don't include router XXX, no router XXX, intf name
     * @return
     */
    public boolean isRouterOp(){
        return isZEBRARouterOp() || isOSPFRouterOp() || isRIPRouterOp() || isISISRouterOp();
    }

    //------------------------------------------------------
    public boolean isZEBRAIntfOp(){
        return this == IPAddr || this == NOIPAddr;
    }

    public boolean isOSPFIntfOp(){
        return (this.ordinal() >= IpOspfArea.ordinal() && this.ordinal() <= IpOspfPassive.ordinal()) || (this.ordinal() >= NOIpOspfArea.ordinal() && this.ordinal() <= NOIpOspfPassive.ordinal());
    }

    public boolean isRIPIntfOp(){
        return (this.ordinal() >= IPSPLITPOISION.ordinal() && this.ordinal() <= IPSPLITHORIZION.ordinal()) || (this.ordinal() >= NOIPSPLITPOISION.ordinal() && this.ordinal() <= NOIPSPLITHORIZION.ordinal()) || this==IPAddr;
    }

    public boolean isISISIntfOp(){
        return (this.ordinal() >= IPROUTERISIS.ordinal() && this.ordinal() <= PSNPINTERVAL.ordinal()) || (this.ordinal() >= NOCIRCUITTYPE.ordinal())|| this == IPAddr || this == NOIPAddr || this == IPROUTERISIS || this == NOIPROUTERISIS;
    }
    /**
     * all ops(set/unset) in interface {name}, don't include router ospf, no router ospf, intfname, include ip address
     * @return
     */
    public boolean isIntfOp(){
        return isZEBRAIntfOp() || isOSPFIntfOp() || isRIPIntfOp() || isISISIntfOp();
    }

    public static Set<OpType> OSPFOps, RIPOps, ISISOps;
    public static List<OpType> OSPFIntfSetOps, OSPFRouterSetOps, RIPRouterSetOps, RIPIntfSetOps, ISISRouterSetOps, ISISIntfSetOps;
    //MULTI:
    static{
        OSPFOps = new HashSet<OpType>(Arrays.asList(ROSPF, NOROSPF));
        RIPOps = new HashSet<OpType>(Arrays.asList(RRIP, NORRIP));
        ISISOps = new HashSet<OpType>(Arrays.asList(RISIS, NORISIS));
        OSPFIntfSetOps = new ArrayList<>();
        OSPFRouterSetOps = new ArrayList<>();
        RIPIntfSetOps = new ArrayList<>();
        RIPRouterSetOps = new ArrayList<>();
        ISISRouterSetOps = new ArrayList<>();
        ISISIntfSetOps = new ArrayList<>();
        for (var op: OpType.values()){
            if (op.isZEBRASetOp() || op.isZEBRAUnsetOp() || op.isOSPFSetOp() || op.isOSPFUnsetOp()) OSPFOps.add(op);
            if (op.isZEBRASetOp() || op.isZEBRAUnsetOp() || op.isRIPSetOp() || op.isRIPUnsetOp()) RIPOps.add(op);
            if (op.isZEBRASetOp() || op.isZEBRAUnsetOp() || op.isISISSetOp() || op.isISISUnsetOp()) ISISOps.add(op);
            if (op.isOSPFIntfOp() && op.isSetOp()) OSPFIntfSetOps.add(op);
            if (op.isOSPFRouterOp()  && op.isSetOp()) OSPFRouterSetOps.add(op);
            if (op.isRIPIntfOp()  && op.isSetOp()) RIPIntfSetOps.add(op);
            if (op.isRIPRouterOp()  && op.isSetOp()) RIPRouterSetOps.add(op);
            if (op.isISISIntfOp() && op.isSetOp()) ISISIntfSetOps.add(op);
            if (op.isISISRouterOp() && op.isSetOp()) ISISRouterSetOps.add(op);
        }
    }

    //not include IPAddr, IntfName
    public static List<OpType> getIntfSetOps(){
        switch (generate.protocol){
            case RIP : return RIPIntfSetOps;
            case OSPF : return OSPFIntfSetOps;
            case ISIS : return ISISIntfSetOps;
        }
        assert false;
        return null;
    }

    //not include RXXX, NORXXX
    public static List<OpType> getRouterSetOps(){
        switch (generate.protocol){
            case RIP: return RIPRouterSetOps;
            case OSPF: return OSPFRouterSetOps;
            case ISIS: return ISISRouterSetOps;
        }
        assert false;
        return null;
    }

    public static Set<OpType> getOpsOfProtocol(generate.Protocol protocol){
        switch (protocol){
            case OSPF: return OSPFOps;
            case RIP: return RIPOps;
            case ISIS: return ISISOps;
        }
        assert false;
        return null;
    }

    public static Set<OpType> getAllOps(){
        return getOpsOfProtocol(generate.protocol);
    }
}
