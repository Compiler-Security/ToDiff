package org.generator.operation.conf;

import org.generator.operation.opg.PhyOpgExec;
import org.generator.operation.opg.SimpleOpGroup;
import org.generator.topo.graph.RelationGraph;

public class PhyConfParser {
    public static void parse(SimpleOpGroup opg, RelationGraph topo){
        var exec = new PhyOpgExec();
        exec.execOps(opg, topo);
    }
}
