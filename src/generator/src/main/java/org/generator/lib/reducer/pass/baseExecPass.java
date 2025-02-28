package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op;
import org.generator.lib.item.conf.node.rip.RIP;
import org.generator.lib.item.conf.node.rip.RIPIntf;
import org.generator.lib.item.opg.OpArgG;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.isis.ISIS;
import org.generator.lib.item.conf.node.isis.ISISDaemon;
import org.generator.lib.item.conf.node.isis.ISISIntf;
import org.generator.lib.item.conf.node.ospf.OSPF;
import org.generator.lib.item.conf.node.ospf.OSPFDaemon;
import org.generator.lib.item.conf.node.ospf.OSPFIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.phy.Router;
import org.generator.util.collections.Pair;
import org.generator.util.exec.ExecStat;

import java.util.ArrayList;
import java.util.List;

public abstract class baseExecPass {
    baseExecPass(){
        cur_intf = null;
        cur_ospf = null;
        cur_router = null;
        cur_ospf_intf = null;
        cur_ospf_daemon = null;
        cur_rip = null;
        cur_rip_intf = null;
        cur_isis = null;
        cur_isis_daemon = null;
        cur_isis_intf = null;
        
    }
    abstract ExecStat execOp(Op op, ConfGraph topo);
    public List<Pair<Op, ExecStat>> execOps(OpArgG opg, ConfGraph topo){
        List<Pair<Op, ExecStat>> l = new ArrayList<>();
        for(var op: opg.getOps()){
            var res = execOp(op, topo);
            l.add(new Pair<>(op, res));
        }
        return l;
    }

    public Router getCur_router() {
        return cur_router;
    }

    public void setCur_router(Router cur_router) {
        this.cur_router = cur_router;
    }

    public Intf getCur_intf() {
        return cur_intf;
    }

    public void setCur_intf(Intf cur_intf) {
        this.cur_intf = cur_intf;
    }

    //=============OSPF==============================
    public OSPF getCur_ospf() {
        return cur_ospf;
    }

    public void setCur_ospf(OSPF cur_ospf) {
        this.cur_ospf = cur_ospf;
    }

    public OSPFDaemon getCur_ospf_daemon() {
        return cur_ospf_daemon;
    }

    public void setCur_ospf_daemon(OSPFDaemon cur_ospf_daemon) {
        this.cur_ospf_daemon = cur_ospf_daemon;
    }

    public OSPFIntf getCur_ospf_intf() {
        return cur_ospf_intf;
    }

    public void setCur_ospf_intf(OSPFIntf cur_ospf_intf) {
        this.cur_ospf_intf = cur_ospf_intf;
    }

    //==============RIP===============================
    public RIP getCur_rip() {return cur_rip;}

    public void setCur_rip(RIP cur_rip) {this.cur_rip = cur_rip;}

    public RIPIntf getCur_rip_intf() {return cur_rip_intf;}

    public void setCur_rip_intf(RIPIntf cur_rip_intf) {this.cur_rip_intf = cur_rip_intf;}

    //==============IS-IS=============================
    public ISIS getCur_isis() {
        return cur_isis;
    }

    public void setCur_isis(ISIS cur_isis) {
        this.cur_isis = cur_isis;
    }

    public ISISDaemon getCur_isis_daemon() {
        return cur_isis_daemon;
    }

    public void setCur_isis_daemon(ISISDaemon cur_isis_daemon) {
        this.cur_isis_daemon = cur_isis_daemon;
    }

    public ISISIntf getCur_isis_intf() {
        return cur_isis_intf;
    }

    public void setCur_isis_intf(ISISIntf cur_isis_intf) {
        this.cur_isis_intf = cur_isis_intf;
    }

    Router cur_router;
    Intf cur_intf;
    //==========OSPF============
    OSPF cur_ospf;
    OSPFDaemon cur_ospf_daemon;
    OSPFIntf cur_ospf_intf;
    //==========RIP=============
    RIP cur_rip;
    RIPIntf cur_rip_intf;
    //==========ISIS============
    ISIS cur_isis;
    ISISDaemon cur_isis_daemon;
    ISISIntf cur_isis_intf;
}
