package graph;

import org.generator.operation.op.SimpleConfReader;
import org.generator.operation.opg.OpgExec;
import org.generator.operation.opg.SimpleOpGroup;
import org.generator.topo.graph.RelationGraph;
import org.junit.Test;

import java.util.Optional;

public class RelationGraphIntfExecTest {
    @Test
    public void phyExecSimpleTest(){
        var reader = new SimpleConfReader();
        String test_st = """
                node r1 add
                node s1 add
                node r1 set ospf up
                link r1-eth0 s1-eth0 up
                """;
        var ops = reader.read(test_st).get();
        //ops.forEach(op -> {assert op.Type() != OpType.INVALID : String.format("stmt error %s", op.toString());});
        var opg = new SimpleOpGroup(ops);
        System.out.println(opg);
        RelationGraph topo = new RelationGraph();
        var opgexec = new OpgExec();
        opgexec.ExecOpGroup(opg, topo, Optional.empty());
        System.out.println(topo);
    }
}
