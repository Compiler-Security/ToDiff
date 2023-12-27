package org.generator.lib.operation.conf;

import org.generator.lib.operation.opgexec.PhyOpgExec;
import org.generator.lib.operation.opg.ParserOpGroup;
import org.generator.lib.item.topo.graph.RelationGraph;

public class PhyConfParser {
    public static void parse(ParserOpGroup opg, RelationGraph topo){
        var exec = new PhyOpgExec();
        exec.execOps(opg, topo);
    }
}
