package org.generator.operation.opg;

import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.generator.topo.graph.RelationGraph;
import org.generator.topo.node.ospf.OSPF;
import org.generator.topo.node.ospf.OSPFDaemon;
import org.generator.topo.node.ospf.OSPFIntf;
import org.generator.topo.node.phy.Intf;
import org.generator.topo.node.phy.Router;
import org.generator.util.collections.Pair;
import org.generator.util.exec.ExecStat;

import java.util.ArrayList;
import java.util.List;

public abstract class OpgExec {
    OpgExec(){
        cur_intf = null;
        cur_ospf = null;
        cur_router = null;
        cur_ospf_intf = null;
        cur_ospf_daemon = null;
    }
    abstract ExecStat execOp(Operation op, RelationGraph topo);
    public List<Pair<Operation, ExecStat>> execOps(OpGroup opg, RelationGraph topo){
        List<Pair<Operation, ExecStat>> l = new ArrayList<>();
        assert opg.getOps().stream().allMatch(x -> OpType.inPhy(x.Type())) : "there is op not in phyOpGroup";
        for(var op: opg.getOps()){
            var res = execOp(op, topo);
            l.add(new Pair<>(op, res));
        }
        return l;
    }

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

    public OSPFIntf getCur_ospf_intf() {
        return cur_ospf_intf;
    }

    public void setCur_ospf_intf(OSPFIntf cur_ospf_intf) {
        this.cur_ospf_intf = cur_ospf_intf;
    }

    OSPF cur_ospf;
    OSPFDaemon cur_ospf_daemon;
    Router cur_router;
    Intf cur_intf;
    OSPFIntf cur_ospf_intf;
}
