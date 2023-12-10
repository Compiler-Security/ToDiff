package org.generator.operation.conf;

import org.generator.operation.opgexec.PhyOpgExec;
import org.generator.operation.opg.ParserOpGroup;
import org.generator.topo.graph.RelationGraph;

public class PhyConfParser {
    public static void parse(ParserOpGroup opg, RelationGraph topo){
        var exec = new PhyOpgExec();
        exec.execOps(opg, topo);
    }
}
