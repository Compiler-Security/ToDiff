package operation;

import org.generator.operation.OpGen;
import org.generator.operation.OpType;
import org.generator.operation.SimpleConfReader;
import org.junit.Test;

public class OpTest {
    @Test
    public void OpGentest(){
        var op = OpGen.GenOperation(OpType.LINKUP);
        op.setNAME("r1-eth0");
        op.setNAME2("r2-eth1");
        System.out.println(op.encode_to_str());
    }

    @Test
    public void SimpleConfReaderTest(){
        var conf = """
                node  r1   add
                node r2     add
                link r1-eth0   r2-eth0    up    
                """;
        var res = new SimpleConfReader().read(conf);
        System.out.println(res);
    }

}
