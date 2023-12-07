package org.generator.operation.conf;

import org.generator.operation.conf.ConfR;
import org.generator.operation.op.OpGen;
import org.generator.operation.op.OpType;
import org.generator.operation.op.Operation;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleConfReader implements ConfR {
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
