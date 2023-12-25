package org.generator.gen;

import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.generator.operation.opg.ParserOpGroup;
import org.generator.topo.node.NodeGen;
import org.generator.util.net.IPV4;

import java.util.Random;
import java.util.*;

public class RandomGen {

    private Stack<ParserOpGroup> opgs;

    public List<Operation> getOpsOfCtx(Operation ctx) {
        List<Operation> res = new ArrayList<>();
        for (var opg : opgs) {
            if (opg.getCtxOp().equals(ctx)) {
                res.addAll(opg.getOps());
            }
        }
        return res;
    }


    private long genRanIDNUM() {
        return ran.nextLong(4294967296L);
    }

    private IPV4 genRanID() {
        if (ran.nextInt(10) < 8)
            return IPV4.IDOf(genRanIDNUM());
        else{
            var i = new IPV4();
            i.setIsId(true);
            i.setWrong(true);
            return i;
        }
    }

    private IPV4 genRanIP() {
        if (ran.nextInt(10) < 8) {
            return IPV4.IPOf(genRanIDNUM(), ran.nextInt(32) + 1);
        }else {
            var i = new IPV4();
            i.setIsId(false);
            i.setWrong(true);
            return i;
        }
    }

    private int genRanInt(int l, int r) {
        if (ran.nextInt(10) <= 2) {
            switch (ran.nextInt(4)) {
                case 0 -> {
                    return l - 1;
                }
                case 1 -> {
                    return r + 1;
                }
                case 2 -> {
                    return 210000000;
                }
                case 3 -> {
                    return -20000000;
                }
            }
            return -1;
        } else {
            return ran.nextInt(r - l + 1) + l;
        }
    }

    private Long genRanLong(long l, long r) {
        if (ran.nextLong(10) <= 3) {
            switch (ran.nextInt(4)) {
                case 0 -> {
                    return l - 1;
                }
                case 1 -> {
                    return r + 1;
                }
                case 2 -> {
                    return 2100000000000L;
                }
                case 3 -> {
                    return -2000000000000L;
                }
            }
            return -1L;
        } else {
            return ran.nextLong(r - l + 1) + l;
        }
    }

    void setOpFiled(Operation op) {
        op.setDETAIL("123");
        op.setIP2(genRanIP());
        op.setIP(genRanIP());
        op.setIDNUM(genRanIDNUM());
        op.setID(genRanID());
        switch (op.Type()) {
            case RABRTYPE -> {
                String[] type = {"standard", "Cisco", "IBM", "shortcut", "XXXXX"};
                op.setNAME(type[ran.nextInt(5)]);
            }
            case TIMERSTHROTTLESPF -> {
                op.setNUM(genRanInt(0, 600000));
                op.setNUM2(genRanInt(0, 600000));
                op.setNUM3(genRanInt(0, 600000));
            }
            case MAXIMUMPATHS -> {
                op.setNUM(genRanInt(1, 64));
            }
            case WRITEMULTIPLIER -> {
                op.setNUM(genRanInt(1, 100));
            }
            case SOCKETBUFFERSEND, SOCKETBUFFERALL, SOCKETBUFFERRECV -> {
                op.setIDNUM(genRanLong(1, 4000000000L));
            }
            case IpOspfCost, IpOspfDeadInter, IpOspfHelloInter, IpOspfRetransInter -> {
                op.setNUM(genRanInt(1, 65535));
            }
            case IpOspfDeadInterMulti -> {
                op.setNUM(genRanInt(2, 20));
            }
            case IpOspfGRHelloDelay -> {
                op.setNUM(genRanInt(1, 1800));
            }
            case IpOspfPriority -> {
                op.setNUM(genRanInt(0, 255));
            }
            case IpOspfNet -> {
                String[] type = {"broadcast", "non-broadcast", "XXEEDDDFED"};
                op.setNAME(type[ran.nextInt(3)]);
            }
            case AreaShortcut -> {
                String[] type = {"enable", "disable", "default"};
                op.setNAME(type[ran.nextInt(3)]);
            }
        }
        if (op.Type().ordinal() >= OpType.AreaRange.ordinal() && op.Type().ordinal() <= OpType.AreaRangeCostINT.ordinal()) {
            op.setNUM(genRanInt(0, 16777215));
        }
    }

    private static final List<OpType> intfOp, OspfOp, allOp;
    static {
        intfOp = new ArrayList<>();
        OspfOp = new ArrayList<>();
        allOp = new ArrayList<>();
        for (var op_type: OpType.values()){
            if (op_type.inOSPFINTF()){intfOp.add(op_type); allOp.add(op_type);}
            else if (op_type.inOSPFAREA() || op_type.inOSPFDAEMON() || op_type.inOSPFRouterWithTopo()){
                OspfOp.add(op_type);
                allOp.add(op_type);
            }
        }
    }
    Operation genOp(boolean onlyIntf, boolean onlyOSPF){
        OpType op_type;
        if (onlyIntf){
            op_type = intfOp.get(ran.nextInt(intfOp.size()));
        }else if (onlyOSPF){
            op_type = OspfOp.get(ran.nextInt(OspfOp.size()));
        }else {
            op_type = allOp.get(ran.nextInt(allOp.size()));
        }
        var op = new Operation(op_type);
        setOpFiled(op);
        return op;
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
        //fixme we should only generate one ip address XXX at once
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
                        if (opgs.empty() || (opgs.peek().getCtxOp().Type() != OpType.OSPFCONFBEGIN || ran.nextDouble(1) > merge_ratio)){
                            var opg = new ParserOpGroup();
                            opg.setCtxOp(new Operation(OpType.OSPFCONFBEGIN));
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
            //System.out.printf("%s %d\n", opg.getCtxOp(), opg.getOps().size());
        }
        return res;
    }
}
