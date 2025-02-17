package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op;
import org.generator.lib.item.opg.OpArgG;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.rip.RIP;
import org.generator.lib.item.conf.node.rip.RIPIntf;
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

    public RIP getCur_ospf() {
        return cur_ospf;
    }

    public void setCur_ospf(RIP cur_ospf) {
        this.cur_ospf = cur_ospf;
    }

    public OSPFDaemon getCur_ospf_daemon() {
        return cur_ospf_daemon;
    }

    public void setCur_ospf_daemon(OSPFDaemon cur_ospf_daemon) {
        this.cur_ospf_daemon = cur_ospf_daemon;
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

    public RIPIntf getCur_ospf_intf() {
        return cur_ospf_intf;
    }

    public void setCur_ospf_intf(RIPIntf cur_ospf_intf) {
        this.cur_ospf_intf = cur_ospf_intf;
    }

    RIP cur_ospf;
    OSPFDaemon cur_ospf_daemon;
    Router cur_router;
    Intf cur_intf;
    RIPIntf cur_ospf_intf;
}
