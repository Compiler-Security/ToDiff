package org.generator.lib.operation.conf;

import org.generator.lib.operation.operation.Op;
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
    Optional<List<Op>> read(BufferedReader buf);
    @NotNull
    Optional<List<Op>> read(String st);
    @NotNull
    Optional<List<Op>> read(File file);
}