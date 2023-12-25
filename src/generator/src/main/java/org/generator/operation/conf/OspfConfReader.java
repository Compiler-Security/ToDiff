package org.generator.operation.conf;

import org.generator.operation.op.OpGen;
import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.generator.util.collections.Pair;
import org.generator.util.net.IPV4;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

public class OspfConfReader implements ConfR {


    public  Operation changeIDNumToID(Operation op, OpType target_type){
        if (0 <= op.getIDNUM() && op.getIDNUM() <= 4294967295L){
            var new_op = op.cloneOfType(target_type);
            new_op.setID(IPV4.IDOf(op.getIDNUM()));
            assert new_op.getID() != null : "id should not be null";
            return new_op;
        }else{
            return new Operation(OpType.INVALID);
        }
    }

    private Operation invalid(){
        return new Operation(OpType.INVALID);
    }

    private boolean inRange(long num, long l, long r){
        return l <= num && num <= r;
    }

    private Operation checkNum(Operation op, long l, long r){
        return op.checkOrInvalid(x -> inRange(x.getNUM(), l, r));
    }

    private Operation checkLongNum(Operation op, long l, long r){
        return op.checkOrInvalid(x -> inRange(x.getIDNUM(), l, r));
    }
    private Operation checkOperation(Operation op){
        if (OpType.inPhy(op.Type())) return op;
        switch (op.Type()){
            case INVALID, ROSPF,RID, NETAREAID, PASSIVEINTFDEFUALT -> {return op;}
            case RABRTYPE -> {
                String[] type = {"standard", "Cisco", "IBM", "shortcut"};
                var name = op.getNAME();
                if (!Arrays.stream(type).anyMatch(x -> x.equals(name))){
                    return invalid();
                }
                return op;
            }
            case NETAREAIDNUM -> {
                return checkOperation(changeIDNumToID(op, OpType.NETAREAID));
            }
            case TIMERSTHROTTLESPF -> {
                return op.checkOrInvalid(x -> inRange(x.getNUM(), 0, 600000), x -> inRange(x.getNUM2(), 0, 600000), x -> inRange(x.getNUM3(), 0, 600000));
            }
            case CLEARIPOSPFPROCESS,CLEARIPOSPFNEIGHBOR -> {
                return op;
            }
            case MAXIMUMPATHS -> {
                return checkNum(op, 1, 64);
            }
            case WRITEMULTIPLIER -> {
                return checkNum(op, 1, 100);
            }
            case SOCKETBUFFERALL,SOCKETBUFFERRECV,SOCKETBUFFERSEND -> {
                return checkLongNum(op, 1, 4000000000L);
            }
            case NOSOCKETPERINTERFACE -> {return op;}
            //case AreaRangeAd,
            case AreaRange, AreaRangeNoAd,AreaRangeSub -> {return op;}
            //case AreaRangeAdCost,
                    case AreaRangeCost -> {
                return checkNum(op, 0, 16777215);
            }
            case AreaRangeINT -> {
                return checkOperation(changeIDNumToID(op, OpType.AreaRange));
            }
//            case AreaRangeAdINT -> {
//                return checkOperation(changeIDNumToID(op, OpType.AreaRangeAd));
//            }
//            case AreaRangeAdCostINT -> {
//                return checkOperation(changeIDNumToID(op, OpType.AreaRangeAdCost));
//            }
            case AreaRangeNoAdINT -> {
                return checkOperation(changeIDNumToID(op, OpType.AreaRangeNoAd));
            }
            case AreaRangeSubINT -> {
                return checkOperation(changeIDNumToID(op, OpType.AreaRangeSub));
            }
            case AreaRangeCostINT -> {
                return checkOperation(changeIDNumToID(op, OpType.AreaRangeCost));
            }
            case AreaVLink -> {
                //FIXME areavlink don't implement
                assert false: "area vlink don't implement";
            }
            case AreaShortcut -> {
                String[] type = {"enable", "disable", "default"};
                var name = op.getNAME();
                if (!Arrays.stream(type).anyMatch(x -> x.equals(name))){
                    return invalid();
                }
                return op;
            }
            case AreaStub, AreaStubTotal, AreaNSSA -> {return op;}
            case IntfName,IPAddr,IpOspfArea -> {return op;}
            case IpOspfAreaINT -> {
                return checkOperation(changeIDNumToID(op, OpType.IpOspfArea));
            }
            case IpOspfCost,IpOspfDeadInter,IpOspfHelloInter,IpOspfRetransInter,IpOspfTransDealy -> {
                return checkNum(op, 1, 65535);
            }
            case IpOspfDeadInterMulti -> {
                return checkNum(op, 2, 20);
            }
            case IpOspfGRHelloDelay -> {
                return checkNum(op, 1, 1800);
            }
            case IpOspfNet -> {
                if (op.getNAME().equals("broadcast") || op.getNAME().equals("non-broadcast")){
                    return op;
                }else{
                    return invalid();
                }
            }
            case IpOspfPriority -> {
                return checkNum(op, 0, 255);
            }
            case IpOspfPassive -> {
                return op;
            }
        };
        assert false: String.format("no check rule for %s", op.toString());
        return  null;
    }
    @Nullable
    private Operation getOperation(String op_st){
        for (var v: OpType.getMatchOps()){
            var op = OpGen.GenOperation(v);
            if (op.decode(op_st)){
                return checkOperation(op);
            }
        }
        return null;
    }
    @Override
    public Optional<List<Operation>> read(BufferedReader buf) {
        List<Operation> l = new ArrayList<>();
        try {
            String line;
            while ((line = buf.readLine()) != null) {
                line = line.strip();
                if (line.isEmpty()) continue;
                var op = getOperation(line);
                if (op == null){
                    return Optional.empty();
                }
                l.add(op);
            }
            return Optional.of(l);
        } catch (IOException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<Operation>> read(String st) {
        return read(new BufferedReader(new StringReader(st)));
    }

    @Override
    public Optional<List<Operation>> read(File file) {
        try {
            return read(new BufferedReader(new FileReader(file)));
        }catch (IOException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
