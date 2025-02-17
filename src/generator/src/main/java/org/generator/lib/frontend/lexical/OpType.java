package org.generator.lib.frontend.lexical;

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
    CONF,
    RRIP,
    IntfName,

    NETWORKN,
    NETWORKI,
    NEIGHBOR,
    VERSION,
    DEFAULTMETRIC,
    DISTANCE,
    TIMERSBASIC,
    PASSIVEINTFDEFAULT,
    PASSIVEINTFNAME,

    IPAddr,
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

    NOIPAddr,
    NOIPSPLITPOISION,
    NOIPSPLITHORIZION,


    INVALID;

//    public  boolean inOSPFRouterWithTopo() {
//        return this.ordinal() > OSPFROUTERBEGIN.ordinal() && this.ordinal() < OSPFROUTEREND.ordinal();
//    }
//
//    public boolean inOSPFDAEMON(){
//        return this.ordinal() > OSPFDAEMONGROUPBEGIN.ordinal() && this.ordinal() < OSPFDAEMONGROUPEND.ordinal();
//    }
//
//    public  boolean inOSPFAREA(){
//        return this.ordinal() > OSPFAREAGROUPBEGIN.ordinal() && this.ordinal() < OSPFAREAGROUPEND.ordinal();
//    }
//
//    public  boolean inOSPFINTF(){
//        return this.ordinal() > OSPFIntfGroupBEGIN.ordinal() && this.ordinal() < OSPFIntfGroupEND.ordinal();
//    }

    /**
     * All unset Op
     * @return
     */
    public boolean isUnsetOp(){
        return this.ordinal() >= NORRIP.ordinal() && this.ordinal() <= NOIPSPLITHORIZION.ordinal();
    }

    /**
     * All ospf set op, include ROSPF and INTFNAME
     * @return
     */
    public boolean isSetOp(){
        return this.ordinal() >= RRIP.ordinal() && this.ordinal() <= IPSPLITHORIZION.ordinal();
    }

    /**
     * all ops(set/unset) in router ospf, don't include router ospf, no router ospf, intf name
     * @return
     */
    public boolean isRouterOp(){
        return (this.ordinal() >= NETWORKN.ordinal() && this.ordinal() <= PASSIVEINTFNAME.ordinal()) || (this.ordinal() >= NONETWORKN.ordinal() && this.ordinal() <= NOTIMERSBASIC.ordinal());
    }

    /**
     * all ops(set/unset) in interface {name}, don't include router ospf, no router ospf, intfname, include ip address
     * @return
     */
    public boolean isIntfOp(){
        return (this.ordinal() >= IPAddr.ordinal() && this.ordinal() <= IPSPLITHORIZION.ordinal()) || (this.ordinal() >= NOIPAddr.ordinal() && this.ordinal() <= NOIPSPLITHORIZION.ordinal()) ;
    }
}
