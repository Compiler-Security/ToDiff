package org.generator.operation.conf;

import org.generator.operation.op.OpGen;
import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.generator.util.net.IPV4;
import org.jetbrains.annotations.NotNull;
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

    private Operation tranverseOperation(Operation op){
        if (OpType.inPhy(op.Type())) return op;
        switch (op.Type()){
            case INVALID, ROSPF,RID, NETAREAID, PASSIVEINTFDEFUALT -> {return op;}
            case NETAREAIDNUM -> {
                return changeIDNumToID(op, OpType.NETAREAID);
            }
            case AreaRangeINT -> {
                return changeIDNumToID(op, OpType.AreaRange);
            }
            case AreaRangeNoAdINT -> {
                return changeIDNumToID(op, OpType.AreaRangeNoAd);
            }
            case AreaRangeSubINT -> {
                return changeIDNumToID(op, OpType.AreaRangeSub);
            }
            case AreaRangeCostINT -> {
                return changeIDNumToID(op, OpType.AreaRangeCost);
            }
            case AreaVLink -> {
                //FIXME areavlink don't implement
                assert false: "area vlink don't implement";
            }
            case IpOspfAreaINT -> {
                return changeIDNumToID(op, OpType.IpOspfArea);
            }
            default -> {
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
                return tranverseOperation(op);
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
    public @NotNull Optional<List<Operation>> read(String st) {
        return read(new BufferedReader(new StringReader(st)));
    }

    @Override
    public @NotNull Optional<List<Operation>> read(File file) {
        try {
            return read(new BufferedReader(new FileReader(file)));
        }catch (IOException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
