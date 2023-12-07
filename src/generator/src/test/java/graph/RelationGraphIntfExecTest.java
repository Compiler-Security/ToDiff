package graph;

import org.generator.operation.conf.SimpleConfReader;
import org.generator.operation.opg.OSPFOpgExec;
import org.generator.operation.opg.SimpleOpGroup;
import org.generator.topo.graph.RelationGraph;
import org.jetbrains.annotations.NotNull;
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
        var opg = new SimpleOpGroup(ops, Optional.empty());
        System.out.println(opg);
        RelationGraph topo = new RelationGraph();
        var opgexec = new OSPFOpgExec();
        opgexec.ExecOpGroup(opg, topo);
        System.out.println(topo);
    }

    @Test
    public void dotTest(){
        var reader = new SimpleConfReader();
        String test_st = """
                node r1 add
                node s1 add
                node r1 set ospf up
                link r1-eth0 s1-eth0 up
                """;
        var ops = reader.read(test_st).get();
        //ops.forEach(op -> {assert op.Type() != OpType.INVALID : String.format("stmt error %s", op.toString());});
        var opg = new SimpleOpGroup(ops, Optional.empty());
        System.out.println(opg);
        RelationGraph topo = new RelationGraph();
        var opgexec = new OSPFOpgExec();
        opgexec.ExecOpGroup(opg, topo);
        System.out.println(topo);
        System.out.println(topo.toDot(true));
    }


    private void run_cmd_str(String cmd, RelationGraph topo, @NotNull Optional<String> target){
        var reader = new SimpleConfReader();
        var ops = reader.read(cmd).get();
        //ops.forEach(op -> {assert op.Type() != OpType.INVALID : String.format("stmt error %s", op.toString());});
        var opg = new SimpleOpGroup(ops, target);
        var opgexec = new OSPFOpgExec();
        opgexec.ExecOpGroup(opg, topo);
    }

    private RelationGraph getBaseTopo(){
        String test_st = """
                node r1 add
                node s1 add
                node r1 set ospf up
                link r1-eth0 s1-eth0 up
                """;
        RelationGraph topo = new RelationGraph();
        run_cmd_str(test_st, topo, Optional.empty());
        return topo;
    }
    @Test
    public void routerOperationTest(){
        String test_st = """
                router ospf
                ospf router-id 0.0.0.1
                """;
        var topo = getBaseTopo();
        run_cmd_str(test_st, topo, Optional.of("r1"));
        System.out.println(topo);
        System.out.println(topo.toDot(true));
    }
}
