package org.generator.gen;

import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.generator.operation.opg.ParserOpGroup;
import org.generator.topo.node.NodeGen;
import org.generator.util.collections.Pair;
import org.generator.util.exception.Unimplemented;
import java.util.Random;
import javax.swing.text.html.parser.Parser;
import java.util.*;

public class RandomGen {

    private Stack<ParserOpGroup> opgs;

    public List<Operation> getOpsOfCtx(Operation ctx){
        List<Operation> res = new ArrayList<>();
        for(var opg: opgs){
            if (opg.getCtxOp().equals(ctx)){
                res.addAll(opg.getOps());
            }
        }
        return  res;
    }

    Operation genOp(boolean onlyIntf, boolean onlyOSPF){
        if (onlyIntf){
            var op = new Operation(OpType.IpOspfCost);
            op.setNUM(10);
            return op;
        }else if (onlyOSPF){
            return new Operation(OpType.RID);
        }else {
            return new Operation(OpType.PASSIVEINTFDEFUALT);
        }
    }
    void addOp(ParserOpGroup opg){
        if (opg.getCtxOp().Type() == OpType.IntfName){
            opg.addOp(genOp(true, false));
        }else if (opg.getCtxOp().Type() == OpType.ROSPF){
            opg.addOp(genOp(false, true));
        }else {
            opg.addOp(genOp(false, false));
        }
    }

    private Random ran;

    private int ospf_total_num , ospf_instnum, intf_total_num, intf_instnum, interface_num, other_total_num, other_instnum, rest_num, total_num;

    private float no_ratio;
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
    public ParserOpGroup genRandom(int inst_num, double router_ospf_ratio, double intf_ratio, int interface_num, float no_ratio, double merge_ratio, String r_name){
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
        while(total_num < inst_num){
            if (rest_num > 0){
                addOp(opgs.peek());
                rest_num -= 1;
                total_num += 1;
            }else{
                switch (selectCtx()){
                    case 0 -> {
                        if (opgs.empty() || (opgs.peek().getCtxOp().Type() != OpType.ROSPF || ran.nextDouble(1) > merge_ratio) ) {
                            var opg = new ParserOpGroup();
                            opg.setCtxOp(new Operation(OpType.ROSPF));
                            opgs.push(opg);
                        }
                    }
                    case 1 -> {
                        var opg = new ParserOpGroup();
                        var intf = new Operation(OpType.IntfName);
                        intf.setNAME(NodeGen.getIntfName(r_name, ran.nextInt(interface_num)));
                        if (opgs.empty() || (!intf.getNAME().equals(opgs.peek().getCtxOp().getNAME()) || ran.nextDouble(1) > merge_ratio)){
                            opg.setCtxOp(intf);
                            opgs.push(opg);
                        }
                    }
                    case 2 -> {
                        if (opgs.empty() || (opgs.peek().getCtxOp().Type() != OpType.OSPFCONF || ran.nextDouble(1) > merge_ratio)){
                            var opg = new ParserOpGroup();
                            opg.setCtxOp(new Operation(OpType.OSPFCONF));
                            opgs.push(opg);
                        }
                    }
                }
                continue;
            }
        }
        var res = new ParserOpGroup();
        for(var opg:opgs){
            res.addOp(opg.getCtxOp());
            res.addOps(opg.getOps());
            System.out.printf("%s %d\n", opg.getCtxOp(), opg.getOps().size());
        }
        return res;
    }
}
