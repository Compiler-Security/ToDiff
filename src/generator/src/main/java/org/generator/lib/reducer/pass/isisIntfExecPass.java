package org.generator.lib.reducer.pass;

import org.generator.lib.item.IR.Op;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.isis.ISISIntf;
import org.generator.util.exec.ExecStat;
import org.jetbrains.annotations.NotNull;

public class isisIntfExecPass extends baseExecPass {

    private ExecStat execISISIntfCmds(@NotNull Op op, @NotNull ConfGraph topo){
        if (cur_isis_intf == null){
            return ExecStat.MISS;
        }
        /*
         *     CIRCUITTYPE,
            CSNPINTERVAL,
            HELLOINTERVAL,
            HELLOMULTIPLIER,
            NETWORKPOINTTOPOINT,
            ISISPASSIVE,
            ISISPRIORITY,
            PSNPINTERVAL,
         */
        switch (op.Type()){
            case IPROUTERISIS ->{
                cur_isis_intf.setIprouteisis(true);
            }
            case ISISPRIORITY ->{
                if(op.getNAME().equals("level-1")){
                    cur_isis_intf.setPriorityLevel1(op.getNUM());
                }else if(op.getNAME().equals("level-2")){
                    cur_isis_intf.setPriorityLevel2(op.getNUM());
                }else if(op.getNAME().equals("")){
                    cur_isis_intf.setPriorityLevel1(op.getNUM());
                    cur_isis_intf.setPriorityLevel2(op.getNUM());
                }
                else{
                    return ExecStat.FAIL;
                }
            }

            case CIRCUITTYPE ->{        
                cur_isis_intf.setLevel(ISISIntf.ISISLEVEL.of(op.getNAME()).get());
                
            }
            
            case CSNPINTERVAL ->{
                if (op.getNAME().equals("level-1")){
                    cur_isis_intf.setCsnpIntervalLevel1(op.getNUM());
                }else if (op.getNAME().equals("level-2")){
                    cur_isis_intf.setCsnpIntervalLevel2(op.getNUM());
                }else if (op.getNAME().equals("")){
                    cur_isis_intf.setCsnpIntervalLevel1(op.getNUM());
                    cur_isis_intf.setCsnpIntervalLevel2(op.getNUM());
                }else{
                    return ExecStat.FAIL;
                }
                 
            }

            case HELLOINTERVAL ->{
                if (op.getNAME().equals("level-1")){
                    cur_isis_intf.setHelloIntervalLevel1(op.getNUM());
                }else if (op.getNAME().equals("level-2")){
                    cur_isis_intf.setHelloIntervalLevel2(op.getNUM());
                }else if(op.getNAME().equals("")){
                    cur_isis_intf.setHelloIntervalLevel1(op.getNUM());
                    cur_isis_intf.setHelloIntervalLevel2(op.getNUM());
                }
                else{
                    return ExecStat.FAIL;
                }
            }

            case PSNPINTERVAL ->{
                if (op.getNAME().equals("level-1")){
                    cur_isis_intf.setPsnpIntervalLevel1(op.getNUM());
                }else if (op.getNAME().equals("level-2")){
                    cur_isis_intf.setPsnpIntervalLevel2(op.getNUM());
                }else if(op.getNAME().equals("")){
                    cur_isis_intf.setPsnpIntervalLevel1(op.getNUM());
                    cur_isis_intf.setPsnpIntervalLevel2(op.getNUM());
                }
                else{
                    return ExecStat.FAIL;
                }
            }

            case HELLOMULTIPLIER ->{
                if (op.getNAME().equals("level-1")){
                    cur_isis_intf.setHelloMultiplierlevel1(op.getNUM());
                }else if (op.getNAME().equals("level-2")){
                    cur_isis_intf.setHelloMultiplierlevel2(op.getNUM());
                }else if(op.getNAME().equals("")){
                    cur_isis_intf.setHelloMultiplierlevel1(op.getNUM());
                    cur_isis_intf.setHelloMultiplierlevel2(op.getNUM());
                }
                else{
                    return ExecStat.FAIL;
                }
            }

            case ISISPASSIVE ->{
                cur_isis_intf.setPassive(true);
            }

            case NOHELLOPADDING ->{
                cur_isis_intf.setHelloPadding(false);
            }

            case NOTHREEWAYHANDSHAKE ->{
                cur_isis_intf.setThreeWayHandshake(false);
            }

            case NETWORKPOINTTOPOINT ->{
                cur_isis_intf.setNetType(ISISIntf.ISISNetType.POINTTOPOINT);
            }

            //FIXME it is wrong
            // case ISISMETRICLEVEL1 ->{
            //     cur_isis_intf.setMetricLevel1(op.getNUM());
            // }

            // case ISISMETRICLEVEL2 ->{
            //     cur_isis_intf.setMetricLevel2(op.getNUM());
            // }

        }
        return ExecStat.SUCC;
    }
    @Override
    ExecStat execOp(Op op, ConfGraph topo) {
        return execISISIntfCmds(op, topo);
    }

}
