package graph;

import org.generator.operation.op.OpType;
import org.generator.operation.op.SimpleConfReader;
import org.generator.operation.opg.OpgExec;
import org.generator.operation.opg.SimpleOpGroup;
import org.generator.topo.Topo;
import org.generator.topo.graph.AbstractTopoGraph;
import org.junit.Test;

public class TopoExecTest {
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
        Topo topo = new AbstractTopoGraph();
        OpgExec.ExecOpGroup(opg, topo);
        System.out.println(topo);
    }
}
