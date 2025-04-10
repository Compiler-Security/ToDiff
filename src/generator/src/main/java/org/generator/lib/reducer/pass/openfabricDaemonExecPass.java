package org.generator.lib.reducer.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.item.IR.Op;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.NodeType;
import org.generator.lib.item.conf.node.openfabric.FABRIC;
import org.generator.lib.item.conf.node.openfabric.FABRICDaemon;
import org.generator.util.exec.ExecStat;
import org.generator.util.net.ID;
import org.generator.util.net.IPRange;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

public class openfabricDaemonExecPass extends baseExecPass{
    public ExecStat execOpenFabricAttriCmds(@NotNull Op op, @NotNull ConfGraph topo) {
        if (op.Type() != OpType.RFABRIC && (cur_router ==null || cur_openfabric == null)){
            return ExecStat.FAIL;
        }
        var cur_rname = cur_router.getName();
        switch (op.Type()) {
            case NET -> {
                cur_openfabric.setNET(op.getNET());
                return ExecStat.SUCC;
            }

            default -> {
                return ExecStat.MISS;
            }

        }
        //assert false:"should not goto here %s".formatted(op.Type());
        //return ExecStat.FAIL;
    }

    public ExecStat execOpenFabricDaemonAttriCmds(@NotNull Op op, @NotNull ConfGraph topo) {
        if (cur_openfabric_daemon == null){
            return ExecStat.MISS;
        }
        var openfabric_daemon = cur_openfabric_daemon;
        /*METRICSTYLE,
        ADVERTISEHIGHMETRIC,
        SETOVERLOADBIT,
        SETOVERLOADBITONSTARTUP,
        LSPMTU, */
        switch (op.Type()){
        

            case FABRICSETOVERLOADBIT -> {
                openfabric_daemon.setSetoverloadbit(true);
                return ExecStat.SUCC;
            }


            case FABRICTIER -> {
                openfabric_daemon.setTier(op.getNUM());
                return ExecStat.SUCC;
            }

            case FABRICLSPGENINTERVAL -> {
                openfabric_daemon.setLspgeninterval(op.getNUM());
                return ExecStat.SUCC;
            }

            case FABRICSPFINTERVAL -> {
                openfabric_daemon.setSpfinterval(op.getNUM());
                return ExecStat.SUCC;
            }
            
        }
        assert false:String.format("should not go to here %s", op.toString());
        return ExecStat.FAIL;
    }



    private ExecStat execOpenFabricOp(@NotNull Op op, ConfGraph topo) {
        if (op.Type().inOpenFabricRouterWithTopo()) {
            return execOpenFabricAttriCmds(op, topo);
        }else if (op.Type().inOpenFabricDAEMON()){
            return execOpenFabricDaemonAttriCmds(op, topo);
        }
        // else if (op.Type().inISISREGION()){
        //     return execOSPFAreaCmds(op, topo);
        // }
        return ExecStat.MISS;
    }
    @Override
    ExecStat execOp(Op op, ConfGraph topo) {
        return execOpenFabricOp(op, topo);
    }
}
