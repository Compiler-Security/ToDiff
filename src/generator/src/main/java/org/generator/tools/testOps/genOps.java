package org.generator.tools.testOps;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.generator.pass.actionRulePass;
import org.generator.lib.generator.pass.genOpPass;
import org.generator.lib.item.IR.OpAnalysis;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.item.topo.node.NodeGen;
import org.generator.lib.reducer.semantic.UnsetRedexDef;
import org.generator.tools.frontend.ConfReader;
import org.generator.tools.frontend.OspfConfWriter;
import org.generator.util.collections.Pair;
import org.generator.util.net.ID;
import org.generator.util.ran.ranHelper;

import java.util.*;

public class genOps {
    private Stack<OpCtxG> opgs;
    private static final List<OpType> intfOp, OspfOp, allOp;
    static {
        intfOp = new ArrayList<>();
        OspfOp = new ArrayList<>();
        allOp = new ArrayList<>();
        for (var op_type: OpType.values()){
            if (op_type == OpType.AreaVLink) continue;
            if (op_type == OpType.NETAREAID) continue;
            if (op_type == OpType.IpOspfArea) continue;
            if (op_type == OpType.IPAddr) continue;
            if (op_type.inOSPFINTF()){intfOp.add(op_type); allOp.add(op_type);}
            else if (op_type.inOSPFAREA() || op_type.inOSPFDAEMON() || op_type.inOSPFRouterWithTopo()){
                OspfOp.add(op_type);
                allOp.add(op_type);
            }
        }
    }

    private static  int getRanIntNum(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof Pair<?,?> p){
            return ranHelper.randomInt((int)((Long) p.first() + 0), (int) ((Long) p.second() + 0));
        }else return ranHelper.randomInt(0, 2100000000);
    }

    private static  int getRanNoIntNum(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof Pair<?,?> p) {
            if (ranHelper.randomInt(0, 1) == 0) {
                return ranHelper.randomInt(-2100000000, (int) ((Long) p.first() - 1));
            } else return ranHelper.randomInt((int) ((Long) p.second() + 1), 2100000000);
        }else return ranHelper.randomInt(0, 2100000000);
    }

    private static long getRanLongNum(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof Pair<?,?> p){
            return ranHelper.randomLong((long) p.first(), (long) p.second());
        }else return ranHelper.randomLong(0, 0xfffffffffL);
    }

    private static long getRanNoLongNum(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof Pair<?,?> p){
            if (ranHelper.randomInt(0, 1) == 0) {
                return ranHelper.randomLong(-210000000000L, (long) p.first() - 1);
            } else return ranHelper.randomLong((long) p.second() + 1, 5L * (long)p.second());
        }else return ranHelper.randomLong(0, 0xfffffffffL);
    }

    private static String getRanName(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof List<?> p && !p.isEmpty()){
            return (String) p.get(ranHelper.randomInt(0, p.size() - 1));
        }else return ranHelper.randomStr();
    }

    private static String getRanNoName(Map<String, Object> argRange, String field){
        return ranHelper.randomStr();
    }

    private static  String getRanNoIp(){
        //TODO we can do it better
        StringBuilder b = new StringBuilder();
        b.append(ranHelper.randomInt(-500, -1));
        for(int i = 0; i < 3; i++){
            b.append('.');
            b.append(ranHelper.randomInt(-500, -1));
        }
        return b.toString();
    }

    private static void mutate(OpOspf new_op){
        //TODO we can use other type of op to override
        var args =  new_op.getOpCtx().getFormmat().getLexDef().Args;
        var argsRange = new_op.getOpCtx().getFormmat().getLexDef().ArgsRange;
        for(var arg: args){
                switch (arg){
                    case "ID" -> {new_op.setID(ranHelper.randomID());}
                    case "ID2" -> {new_op.setID(ranHelper.randomID());}
                    case "IPRANGE" -> {new_op.setIPRANGE(ranHelper.randomIpRange());}
                    case "IP" -> {new_op.setIP(ranHelper.randomIP());}
                    case "NUM" -> {new_op.setNUM(getRanIntNum(argsRange, "NUM"));}
                    case "NUM2" -> {new_op.setNUM2(getRanIntNum(argsRange, "NUM2"));}
                    case "NUM3" -> {new_op.setNUM3(getRanIntNum(argsRange, "NUM3"));}
                    case "LONGNUM" -> {new_op.setLONGNUM(getRanLongNum(argsRange, "LONGNUM"));}
                    case "NAME" -> {new_op.setNAME(getRanName(argsRange, "NAME"));}
                    case "NAME2" -> {new_op.setNAME(getRanName(argsRange, "NAME2"));}
                    //case "NUM" -> {new_op.setNUM(argsRange.get("NUM"));}
                    default -> {
                        assert false : "mutate TODO %s".formatted(arg);
                    }
                }
        }
    }

    private static void broken(OpOspf new_op){
        var args =  new_op.getOpCtx().getFormmat().getLexDef().Args;
        var argsRange = new_op.getOpCtx().getFormmat().getLexDef().ArgsRange;
        if (args.isEmpty()) {
            new_op.getOpCtx().setFormmat(OpCtx.Format.of(OpType.INVALID, 0));
            new_op.setNAME(ranHelper.randomStr());
        }
        for(var arg: new_op.getOpCtx().getFormmat().getLexDef().Args) {
            switch (arg){
                case "ID","IPRANGE","IP" -> {new_op.getOpCtx().getFormmat().addByPass(arg, getRanNoIp());}
                case "NUM" -> {new_op.setNUM(getRanNoIntNum(argsRange, "NUM"));}
                case "NUM2" -> {new_op.setNUM2(getRanNoIntNum(argsRange, "NUM2"));}
                case "NUM3" -> {new_op.setNUM3(getRanNoIntNum(argsRange, "NUM3"));}
                case "LONGNUM" -> {new_op.setLONGNUM(getRanNoLongNum(argsRange, "LONGNUM"));}
                case "NAME" -> {new_op.setNAME(getRanNoName(argsRange, "NAME"));}
                case "NAME2" -> {new_op.setNAME(getRanNoName(argsRange, "NAME2"));}
                //case "NUM" -> {new_op.setNUM(argsRange.get("NUM"));}
                default -> {
                    assert false : "mutate TODO";
                }
            }
        }
    }

    private static OpOspf unset(OpOspf op){
        return actionRulePass.unset(OpAnalysis.of(op)).getOp();
    }

    OpCtx genOp(OpType op_type){
        return genOpPass.genRanOpOfType(op_type);
    }

    OpCtx genRanOpByControl(boolean onlyIntf, boolean onlyOSPF){
        OpType op_type;
        if (onlyIntf){
            op_type = intfOp.get(ran.nextInt(intfOp.size()));
        }else if (onlyOSPF){
            op_type = OspfOp.get(ran.nextInt(OspfOp.size()));
        }else {
            op_type = allOp.get(ran.nextInt(allOp.size()));
        }
        return genOp(op_type);
    }

    void addOp(OpCtxG opg){
        if (all){
            var op = genRanOpByControl(false, false);
            if (ranHelper.randomInt(1, 5) < 2){
                broken((OpOspf) op.getOperation());
            }else{
                var unset_op = unset((OpOspf) op.getOperation());
                if (unset_op != null) op = unset_op.getOpCtx();
            }
            opg.addOp(op);
            return;
        }
        if (opg.getOps().get(0).getOperation().Type() == OpType.IntfName){
            opg.addOp(genRanOpByControl(true, false));
        }else if (opg.getOps().get(0).getOperation().Type() == OpType.ROSPF){
            opg.addOp(genRanOpByControl(false, true));
        }else {
            opg.addOp(genRanOpByControl(false, false));
        }
    }

    private Random ran;

    private int ospf_total_num , ospf_instnum, intf_total_num, intf_instnum, interface_num, other_total_num, other_instnum, rest_num, total_num;

    private float no_ratio;

    OpType getCtxOpType(OpCtxG opCtxG){
        return opCtxG.getOps().get(0).getOperation().Type();
    }
    //ospf 0 intf 1 other 2
    private int selectCtx(){
        var idx = ran.nextInt(ospf_instnum + intf_instnum + other_instnum);
        if (ospf_instnum + intf_instnum <= idx && other_instnum > 0){
            rest_num = ran.nextInt(Math.min(other_total_num / 5 + 1, other_instnum)) + 1;
            other_instnum -= rest_num;
            return 2;
        }else if (ospf_instnum <= idx && intf_instnum > 0){
            rest_num = ran.nextInt(Math.min(intf_instnum, intf_total_num/ (interface_num + 1) + 1)) + 1;
            intf_instnum -= rest_num;
            return 1;
        }else if (ospf_instnum > 0){
            rest_num = ran.nextInt(Math.min(ospf_total_num / 5 + 1, ospf_instnum)) + 1;
            ospf_instnum -= rest_num;
            return 0;
        }else assert false :"one ctx num should > 0";
        return -1;
    }

    private OpOspf addOp(OpCtxG opCtxG, OpType typ){
        var opCtx = genOpPass.genRanOpOfType(typ);
        opCtxG.addOp(opCtx);
        return opCtx.getOpOspf();
    }
    boolean all;
    public OpCtxG genRandom(int inst_num, double router_ospf_ratio, double intf_ratio, int interface_num, float no_ratio, double merge_ratio, String r_name){
        ran = new Random();
        this.no_ratio = no_ratio;
        ospf_instnum = (int) (inst_num * router_ospf_ratio);
        ospf_total_num = ospf_instnum;
        intf_instnum = (int) (inst_num * intf_ratio);
        intf_total_num = intf_instnum;
        other_instnum = inst_num - ospf_instnum - intf_instnum;
        other_total_num = other_instnum;
        this.interface_num = interface_num;
        opgs = new Stack<>();
        total_num = 0;
        rest_num = 0;
        all = false;
        //fixme we should only generate one ip address XXX at once
        var opg1 = OpCtxG.Of();
        opg1.addOp(genOp(OpType.ROSPF));
        opgs.push(opg1);
        while(total_num < inst_num){
            if (rest_num > 0){
                addOp(opgs.peek());
                rest_num -= 1;
                total_num += 1;
            }else{
                switch (selectCtx()) {
                    case 0 -> {
                        if (opgs.empty() || (getCtxOpType(opgs.peek()) != OpType.ROSPF || ran.nextDouble(1) > merge_ratio)) {
                            var opg = OpCtxG.Of();
                            opg.addOp(genOp(OpType.ROSPF));
                            opgs.push(opg);
                        }
                        all = false;
                    }
                    case 1 -> {
                        var opg = OpCtxG.Of();
                        var intf = genOp(OpType.IntfName);
                        intf.getOperation().setNAME(NodeGen.getIntfName(r_name, ran.nextInt(interface_num)));
                        if (opgs.empty() || (!intf.getOperation().getNAME().equals(opgs.peek().getOps().get(0).getOperation().getNAME()) || ran.nextDouble(1) > merge_ratio)) {
                            opg.addOp(intf);
                            opgs.push(opg);
                        }
                        all = false;
                    }
                    case 2 -> {
                        all = true;
                    }
                }
                continue;
            }
        }
        var res = OpCtxG.Of();
        for(var opg:opgs){
            res.addOps(opg.getOps());
            //System.out.printf("%s %d\n", opg.getCtxOp(), opg.getOps().size());
        }
        for(int i = 0; i < interface_num; i++){
            var intf = addOp(res, OpType.IntfName);
            intf.setNAME(NodeGen.getIntfName(r_name, i));
            var ip = addOp(res, OpType.IPAddr);
            var area = addOp(res, OpType.IpOspfArea);
            area.setID(ID.of(ranHelper.randomInt(0, 3)));
        }
        return res;
    }
}

