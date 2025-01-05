package org.generator.lib.frontend.lexical;

import org.generator.lib.item.conf.node.isis.ISIS;
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
    ISISROUTERBEGIN,    
    NET,
    ISTYPE,
    ISISROUTEREND,
    //================ISIS DAEMON===================
    ISISDAEMONGROUPBEGIN,
    //ATTACHEDBIT,
    // METRICSTYLE,
    ADVERTISEHIGHMETRIC,
    SETOVERLOADBIT,
    SETOVERLOADBITONSTARTUP,
    LSPMTU,
    ISISDAEMONGROUPEND,
    //===================ISIS region=====================
    ISISREGIONBEGIN,
    
    ISISREGIONEND,


    //===================ISIS INTERFACE================
    IPAddr,
    
    ISISINTFBEGIN,
    IPROUTERISIS,
    CIRCUITTYPE,
    CSNPINTERVAL,
    //HELLOPADDING,
    HELLOINTERVAL,
    HELLOMULTIPLIER,
    //ISISMETRICLEVEL1,
    //ISISMETRICLEVEL2,
    NETWORKPOINTTOPOINT,
    ISISPASSIVE,
    ISISPRIORITY,
    //THREEWAYHANDSHAKE,
    PSNPINTERVAL,
    
    ISISEND,
    //================NO ISIS =================
    NORISIS,
    NOTNET,
    
    //NOATTACHEDBIT,
    // NOMETRICSTYLE,
    NOADVERTISEHIGHMETRIC,
    NOSETOVERLOADBIT,
    NOSETOVERLOADBITONSTARTUP,
    NOLSPMTU,
    NOISTYPE,
    NOIPROUTERISIS,
    NOIPAddr,
    NOCIRCUITTYPE,
    NOCSNPINTERVAL,
    //NOHELLOPADDING,
    NOHELLOINTERVAL,
    NOHELLOMULTIPLIER,
    //NOISISMETRICLEVEL1,
    //NOISISMETRICLEVEL2,
    NONETWORKPOINTTOPOINT,
    NOISISPASSIVE,
    NOISISPRIORITY,
    //NOTHREEWAYHANDSHAKE,
    NOPSNPINTERVAL,
    

    INVALID;



    public static boolean inPhy(@NotNull OpType typ) {
        return typ.ordinal() >= NODEADD.ordinal() && typ.ordinal() <= LINKREMOVE.ordinal();
    }

    public static boolean inISIS(@NotNull OpType typ) {
        return typ.ordinal() > ISISCONF.ordinal() && typ.ordinal() < ISISEND.ordinal();
    }

    public  boolean inISISRouterWithTopo() {
        return this.ordinal() > ISISROUTERBEGIN.ordinal() && this.ordinal() < ISISROUTEREND.ordinal();
    }
    
    public boolean inISISDAEMON(){
        return this.ordinal() > ISISDAEMONGROUPBEGIN.ordinal() && this.ordinal() < ISISDAEMONGROUPEND.ordinal();
    }

    public  boolean inISISREGION(){
        return this.ordinal() > ISISREGIONBEGIN.ordinal() && this.ordinal() < ISISREGIONEND.ordinal();
    }

    public  boolean inISISINTF(){
        return this.ordinal() > ISISINTFBEGIN.ordinal() && this.ordinal() < ISISEND.ordinal();
    }

    /**
     * All unset Op
     * @return
     */
    public boolean isUnsetOp(){
        return this.ordinal() >= NORISIS.ordinal() && this.ordinal() <= NOPSNPINTERVAL.ordinal();
    }

    /**
     * All ospf set op, include ROSPF and INTFNAME
     * @return
     */
    public boolean isSetOp(){
        return this.ordinal() > ISISCONF.ordinal() && this.ordinal() < ISISEND.ordinal();
    }

    /**
     * all ops(set/unset) in router ospf, don't include router ospf, no router ospf, intf name
     * @return
     */
    public boolean isRouterOp(){
        return (this.ordinal() > ISISROUTERBEGIN.ordinal() && this.ordinal() < ISISREGIONEND.ordinal()) || (this.ordinal() >= NOTNET.ordinal() && this.ordinal() <= NOISTYPE.ordinal());
    }

    /**
     * all ops(set/unset) in interface {name}, don't include router ospf, no router ospf, intfname, include ip address
     * @return
     */
    public boolean isIntfOp(){
        //return (this.ordinal() > OSPFIntfGroupBEGIN.ordinal() && this.ordinal() < OSPFIntfGroupEND.ordinal()) || (this.ordinal() > NOAreaStub.ordinal()) || this == IPAddr || this == NOIPAddr;
        return (this.ordinal() > ISISINTFBEGIN.ordinal() && this.ordinal() < ISISEND.ordinal()) || (this.ordinal() >= NOCIRCUITTYPE.ordinal())|| this == IPAddr || this == NOIPAddr || this == IPROUTERISIS || this == NOIPROUTERISIS;
    }
}
