package org.generator.operation.conf;

import org.generator.operation.op.OpGen;
import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ConfReader implements ConfR {


    private  Operation changeIDNumToID(Operation op, OpType target_type){
        if (0 <= op.getIDNUM() && op.getIDNUM() <= 4294967295L){
            var new_op = op.cloneOfType(target_type);
            return new_op;
        }else{
            return new Operation(OpType.INVALID);
        }
    }
    private Operation checkOperation(Operation op){
        switch (op.Type()){
            case RABRTYPE -> {
                String[] type = {"standard", "Cisco", "IBM", "shortcut"};
                var name = op.getNAME();
                if (!Arrays.stream(type).anyMatch(x -> x.equals(name))){
                    return new Operation(OpType.INVALID);
                }
            }
            case NETAREAIDNUM -> {
                return checkOperation(changeIDNumToID(op, OpType.NETAREAID));
            }
            case TIMERSTHROTTLESPF -> {
                int[] array = {op.getNUM(), op.getNUM2(), op.getNUM3()};
                var mx = Arrays.stream(array).max().getAsInt();
                var mi = Arrays.stream(array).min().getAsInt();
                if (mi < 0 || mx > 600000){
                    return new Operation(OpType.INVALID);
                }
            }
            case MAXIMUMPATHS -> {
                if (op.getNUM() < 1 || op.getNUM() > 64){
                    return new Operation(OpType.INVALID);
                }
            }
            case WRITEMULTIPLIER -> {
                if (op.getNUM() < 1 || op.getNUM() > 100){
                    return new Operation(OpType.INVALID);
                }
            }
            case AreaRangeINT -> {
                return checkOperation(changeIDNumToID(op, OpType.AreaRange));
            }
            //TODO
        };
        return op;
    }
    @Nullable
    private Operation getOperation(String op_st){
        for (var v: OpType.values()){
            var op = OpGen.GenOperation(v);
            if (op.decode(op_st)){
                return op;
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
