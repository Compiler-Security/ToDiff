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
    NODESETBABELUP,
    NODESETBABELSHUTDOWN,
    NODESETBABELRE,
    NODESETFABRICUP,
    NODESETFABRICRE,
    NODESETFABRICSHUTDOWN,

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

    //====================BABEL=========================
    RBABEL,
    //BABELDI,
    BNETWORKINTF,
    BREDISTRIBUTE,
    BRESENDDELAY,
    BSOMMOTHING,

    BWIRE,
    //currently we set this command in babel for safety, it should be in zebra
    IPAddr6,
    BSPLITHORIZON,
    BHELLOINTERVAL,
    BUPDATEINTERVAL,
    BCHANELNOINTEFERING,
    BRXCOST,
    BRTTDECAY,
    BRTTMIN,
    BRTTMAX,
    BPENALTY,
    BENABLETIMESTAMP,


    NORBABEL,
    //NOBABELDI,
    NOBNETWORKINTF,
    NOBREDISTRIBUTE,
    NOBRESENDDELAY,
    NOBSOMMOTHING,

    NOBWIRE,
    NOIPAddr6,
    NOBSPLITHORIZON,
    NOBHELLOINTERVAL,
    NOBUPDATEINTERVAL,
    NOBCHANELNOINTEFERING,
    NOBRXCOST,
    NOBRTTDECAY,
    NOBRTTMIN,
    NOBRTTMAX,
    NOBPENALTY,
    NOBENABLETIMESTAMP,
    //====================OpenFabric=========================
    RFABRIC,
    //--------------ROUTER------------------
    //NET


    //--------------DAEMON----------------
    FABRICSETOVERLOADBIT,
    FABRICTIER,
    FABRICLSPGENINTERVAL,
    FABRICSPFINTERVAL,
    //--------------INTERFACE----------------
    IPROUTERFABRIC,
    FABRICCSNPINTERVAL,
    FABRICPSNPINTERVAL,
    FABRICHELLOINTERVAL,
    FABRICHELLOMULTIPLIER,
    FABRICPASSIVE,

    //--------------UNSET------------------
    NORFABRIC,
    NOFABRICSETOVERLOADBIT,
    NOFABRICTIER,

    NOFABRICLSPGENINTERVAL,
    NOFABRICSPFINTERVAL,

    NOIPROUTERFABRIC,
    NOFABRICCSNPINTERVAL,
    NOFABRICPSNPINTERVAL,
    NOFABRICHELLOINTERVAL,
    NOFABRICHELLOMULTIPLIER,
    NOFABRICPASSIVE,
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
    
    //=======OpenFabric Function============

    public  boolean inOpenFabricRouterWithTopo() {
        return this == NET;
    }

    public boolean inOpenFabricDAEMON(){
        return this.ordinal() >= FABRICSETOVERLOADBIT.ordinal() && this.ordinal() <= FABRICSPFINTERVAL.ordinal();
    }

    public  boolean inOpenFabricINTF(){
        return this.ordinal() >= IPROUTERFABRIC.ordinal() && this.ordinal() <= FABRICPASSIVE.ordinal();
    }


    //==========COMMON Function======================
    public boolean isZEBRAUnsetOp(){
        return this == NOIPAddr;
    }

    public boolean isOSPFUnsetOp(){
        return this.ordinal() >= NOROSPF.ordinal() && this.ordinal() <= NOIpOspfPassive.ordinal() || this == NOIPAddr;
    }

    public boolean isRIPUnsetOp(){
        return (this.ordinal() >= NORRIP.ordinal() && this.ordinal() <= NOIPSPLITHORIZION.ordinal()) || this==NOIPAddr;
    }

    public boolean isISISUnsetOp(){
        return (this.ordinal() >= NORISIS.ordinal() && this.ordinal() <= NOPSNPINTERVAL.ordinal()) || this==NOIPAddr;
    }

    public boolean isBABELUnsetOp(){
        return (this.ordinal() >= NORBABEL.ordinal() && this.ordinal() <= NOBENABLETIMESTAMP.ordinal()) || this==NOIPAddr;
    }

    public boolean isFABRICUnsetOp(){
        return this.ordinal() >= NORFABRIC.ordinal() && this.ordinal() <= NOFABRICPASSIVE.ordinal() || this == NOIPAddr || this == NOTNET;
    }
    /**
     * All unset Op
     * @return
     */
    public boolean isUnsetOp(){
        return  isZEBRAUnsetOp() || isOSPFUnsetOp() || isRIPUnsetOp() || isISISUnsetOp() || isBABELUnsetOp() || isFABRICUnsetOp();
    }

    //-------------------------------------------------
    public boolean isZEBRASetOp(){
        return this.ordinal() >= IntfName.ordinal() && this.ordinal() <= IPAddr.ordinal();
    }

    public boolean isOSPFSetOp(){
        return this.ordinal() >= ROSPF.ordinal() && this.ordinal() <= IpOspfPassive.ordinal() || this== IntfName || this == IPAddr;
    }

    public boolean isRIPSetOp(){
        return (this.ordinal() >= RRIP.ordinal() && this.ordinal() <= IPSPLITHORIZION.ordinal()) || this== IntfName || this == IPAddr;
    }

    public boolean isISISSetOp(){
        return (this.ordinal() >= RISIS.ordinal() && this.ordinal() <= PSNPINTERVAL.ordinal())|| this== IntfName || this == IPAddr;
    }

    public boolean isBABELSetOp(){
        return (this.ordinal() >= RBABEL.ordinal() && this.ordinal() <= BENABLETIMESTAMP.ordinal()) || this==IntfName || this == IPAddr;
    }


    public boolean isFABRICSetOp(){
        return this.ordinal() >= RFABRIC.ordinal() && this.ordinal() <= FABRICPASSIVE.ordinal() || this == IntfName || this == IPAddr || this == NET;
    }
    /**
     * All set op, include router XXX, interface name
     * @return
     */
    public boolean isSetOp(){
        return isZEBRASetOp() || isOSPFSetOp() || isRIPSetOp() || isISISSetOp() || isBABELSetOp() || isFABRICSetOp();
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

    public boolean isBABELRouterOp(){
        return (this.ordinal() >= BNETWORKINTF.ordinal() && this.ordinal() <= BSOMMOTHING.ordinal()) || (this.ordinal() >= NOBNETWORKINTF.ordinal() && this.ordinal() <= NOBSOMMOTHING.ordinal());
    }

    public boolean isFABRICRouterOp(){
        return (this.ordinal() >= FABRICSETOVERLOADBIT.ordinal() && this.ordinal() <= FABRICSPFINTERVAL.ordinal()) || (this.ordinal() >= NOFABRICSETOVERLOADBIT.ordinal() && this.ordinal() <= NOFABRICSPFINTERVAL.ordinal()) || this == NET || this == NOTNET;
    }
    /**
     * all ops(set/unset) in router XXX, don't include router XXX, no router XXX, intf name
     * @return
     */
    public boolean isRouterOp(){
        return isZEBRARouterOp() || isOSPFRouterOp() || isRIPRouterOp() || isISISRouterOp() || isBABELRouterOp() || isFABRICRouterOp();
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
        return (this.ordinal() >= IPROUTERISIS.ordinal() && this.ordinal() <= PSNPINTERVAL.ordinal()) || (this.ordinal() >= NOCIRCUITTYPE.ordinal() && this.ordinal() <= NOPSNPINTERVAL.ordinal())|| this == IPAddr || this == NOIPAddr || this == IPROUTERISIS || this == NOIPROUTERISIS;
    }

    public boolean isBABELIntfOp(){
        return (this.ordinal() >= BWIRE.ordinal() && this.ordinal() <= BENABLETIMESTAMP.ordinal()) || (this.ordinal() >= NOBWIRE.ordinal() && this.ordinal() <= NOBENABLETIMESTAMP.ordinal());
    }

    public boolean isFABRICIntfOp(){
        return (this.ordinal() >= IPROUTERFABRIC.ordinal() && this.ordinal() <= FABRICPASSIVE.ordinal()) || (this.ordinal() >= NOIPROUTERFABRIC.ordinal() && this.ordinal() <= NOFABRICPASSIVE.ordinal()) || this == IPAddr || this == NOIPAddr || this == IPROUTERFABRIC || this == NOIPROUTERFABRIC;
    }
    /**
     * all ops(set/unset) in interface {name}, don't include router ospf, no router ospf, intfname, include ip address
     * @return
     */
    public boolean isIntfOp(){
        return isZEBRAIntfOp() || isOSPFIntfOp() || isRIPIntfOp() || isISISIntfOp() || isBABELIntfOp() || isFABRICIntfOp();
    }

    public static Set<OpType> OSPFOps, RIPOps, ISISOps, BABELOps, FABRICOps;
    public static List<OpType> OSPFIntfSetOps, OSPFRouterSetOps, RIPRouterSetOps, RIPIntfSetOps, ISISRouterSetOps, ISISIntfSetOps, BABELRouterSetOps, BABELIntfSetOps, FABRICRouterSetOps, FABRICIntfSetOps;
    //MULTI:
    static{
        OSPFOps = new HashSet<OpType>(Arrays.asList(ROSPF, NOROSPF));
        RIPOps = new HashSet<OpType>(Arrays.asList(RRIP, NORRIP));
        ISISOps = new HashSet<OpType>(Arrays.asList(RISIS, NORISIS));
        BABELOps = new HashSet<OpType>(Arrays.asList(RBABEL, NORBABEL));
        FABRICOps = new HashSet<OpType>(Arrays.asList(RFABRIC, NORFABRIC));
        OSPFIntfSetOps = new ArrayList<>();
        OSPFRouterSetOps = new ArrayList<>();
        RIPIntfSetOps = new ArrayList<>();
        RIPRouterSetOps = new ArrayList<>();
        ISISRouterSetOps = new ArrayList<>();
        ISISIntfSetOps = new ArrayList<>();
        BABELRouterSetOps = new ArrayList<>();
        BABELIntfSetOps = new ArrayList<>();
        FABRICRouterSetOps = new ArrayList<>();
        FABRICIntfSetOps = new ArrayList<>();
        for (var op: OpType.values()){
            if (op.isZEBRASetOp() || op.isZEBRAUnsetOp() || op.isOSPFSetOp() || op.isOSPFUnsetOp()) OSPFOps.add(op);
            if (op.isZEBRASetOp() || op.isZEBRAUnsetOp() || op.isRIPSetOp() || op.isRIPUnsetOp()) RIPOps.add(op);
            if (op.isZEBRASetOp() || op.isZEBRAUnsetOp() || op.isISISSetOp() || op.isISISUnsetOp()) ISISOps.add(op);
            if (op.isZEBRASetOp() || op.isZEBRAUnsetOp() || op.isBABELSetOp() || op.isBABELUnsetOp())  BABELOps.add(op);
            if (op.isZEBRASetOp() || op.isZEBRAUnsetOp() || op.isFABRICSetOp() || op.isFABRICUnsetOp()) FABRICOps.add(op);
            if (op.isOSPFIntfOp() && op.isSetOp()) OSPFIntfSetOps.add(op);
            if (op.isOSPFRouterOp()  && op.isSetOp()) OSPFRouterSetOps.add(op);
            if (op.isRIPIntfOp()  && op.isSetOp()) RIPIntfSetOps.add(op);
            if (op.isRIPRouterOp()  && op.isSetOp()) RIPRouterSetOps.add(op);
            if (op.isISISIntfOp() && op.isSetOp()) ISISIntfSetOps.add(op);
            if (op.isISISRouterOp() && op.isSetOp()) ISISRouterSetOps.add(op);
            if (op.isBABELIntfOp() && op.isSetOp()) BABELIntfSetOps.add(op);
            if (op.isBABELRouterOp() && op.isSetOp()) BABELRouterSetOps.add(op);
            if (op.isFABRICIntfOp() && op.isSetOp()) FABRICIntfSetOps.add(op);
            if (op.isFABRICRouterOp() && op.isSetOp()) FABRICRouterSetOps.add(op);
        }
    }

    //not include IPAddr, IntfName
    public static List<OpType> getIntfSetOps(){
        switch (generate.protocol){
            case RIP : return RIPIntfSetOps;
            case OSPF : return OSPFIntfSetOps;
            case ISIS : return ISISIntfSetOps;
            case BABEL: return BABELIntfSetOps;
            case OpenFabric: return FABRICIntfSetOps;
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
            case BABEL: return BABELRouterSetOps;
            case OpenFabric: return FABRICRouterSetOps;
        }
        assert false;
        return null;
    }

    public static Set<OpType> getOpsOfProtocol(generate.Protocol protocol){
        switch (protocol){
            case OSPF: return OSPFOps;
            case RIP: return RIPOps;
            case ISIS: return ISISOps;
            case BABEL: return BABELOps;
            case OpenFabric: return FABRICOps;
        }
        assert false;
        return null;
    }

    public static Set<OpType> getAllOps(){
        return getOpsOfProtocol(generate.protocol);
    }
}
