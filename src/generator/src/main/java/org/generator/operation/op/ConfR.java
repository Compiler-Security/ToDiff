package org.generator.operation.op;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * ConfR is read operations from conf
 * if read fail, it will return None
 * else it will return List<Operation>
 */
public interface ConfR {
    @NotNull
    Optional<List<Operation>> read(BufferedReader buf);
    @NotNull
    Optional<List<Operation>> read(String st);
    @NotNull
    Optional<List<Operation>> read(File file);
}
