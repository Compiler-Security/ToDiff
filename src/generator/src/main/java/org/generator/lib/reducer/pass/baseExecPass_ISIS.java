package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op_ISIS;
import org.generator.lib.item.opg.OpArgG_ISIS;
import org.generator.lib.item.conf.graph.ConfGraph_ISIS;
import org.generator.lib.item.conf.node.isis.ISIS;
import org.generator.lib.item.conf.node.isis.ISISDaemon;
import org.generator.lib.item.conf.node.isis.ISISIntf;
import org.generator.lib.item.conf.node.phy.Intf_ISIS;
import org.generator.lib.item.conf.node.phy.Router_ISIS;
import org.generator.util.collections.Pair;
import org.generator.util.exec.ExecStat;

import java.util.ArrayList;
import java.util.List;

public abstract class baseExecPass_ISIS {
    baseExecPass_ISIS(){
        cur_intf = null;
        cur_isis = null;
        cur_router = null;
        cur_isis_intf = null;
        cur_isis_daemon = null;
    }
    abstract ExecStat execOp(Op_ISIS op, ConfGraph_ISIS topo);
    public List<Pair<Op_ISIS, ExecStat>> execOps(OpArgG_ISIS opg, ConfGraph_ISIS topo){
        List<Pair<Op_ISIS, ExecStat>> l = new ArrayList<>();
        for(var op: opg.getOps()){
            var res = execOp(op, topo);
            l.add(new Pair<>(op, res));
        }
        return l;
    }

    public ISIS getCur_ospf() {
        return cur_isis;
    }

    public void setCur_ospf(ISIS cur_isis) {
        this.cur_isis = cur_isis;
    }

    public ISISDaemon getCur_ospf_daemon() {
        return cur_isis_daemon;
    }

    public void setCur_ospf_daemon(ISISDaemon cur_isis_daemon) {
        this.cur_isis_daemon = cur_isis_daemon;
    }

    public Router_ISIS getCur_router() {
        return cur_router;
    }

    public void setCur_router(Router_ISIS cur_router) {
        this.cur_router = cur_router;
    }

    public Intf_ISIS getCur_intf() {
        return cur_intf;
    }

    public void setCur_intf(Intf_ISIS cur_intf) {
        this.cur_intf = cur_intf;
    }

    public ISISIntf getCur_isis_intf() {
        return cur_isis_intf;
    }

    public void setCur_ospf_intf(ISISIntf cur_isis_intf) {
        this.cur_isis_intf = cur_isis_intf;
    }

    ISIS cur_isis;
    ISISDaemon cur_isis_daemon;
    Router_ISIS cur_router;
    Intf_ISIS cur_intf;
    ISISIntf cur_isis_intf;
}
