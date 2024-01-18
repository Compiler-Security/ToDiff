package tools.frontend;

import org.generator.lib.frontend.driver.IO;
import org.generator.lib.item.IR.OpOspf;
import org.generator.tools.frontend.OspfConfReader;
import org.generator.tools.frontend.OspfConfWriter;
import org.junit.Test;

public class IOTest {

    @Test
    public void IoTest(){
        String test_st = """
                router ospf
                ospf router-id 0.0.0.1
                network 10.0.0.5/30 area 0
                network 10.0.0.0/10 area 2
                network 10.0.0.0/10 area 3
                area 2.0.0.0 range 9.0.0.0/20
                area 3 range 9.0.0.0/20
                write-multiplier 95

                interface r1-eth0
                ip address 10.0.0.0/10
                ip ospf area 3
                ip ospf cost 50
                no ip ospf area
                
                ip address 11.0.0.0/10
                ip ospf cost 100

                interface r1-eth1
                ip address 10.0.0.5/30
                ip ospf cost 300
                
                """;
        var reader = new OspfConfReader();
        var opCtxG = reader.read(test_st);
        var writer = new OspfConfWriter();
        System.out.println(writer.write(opCtxG));
    }
    @Test
    public void OpOspfTest(){
        var op1 = OpOspf.of();
        var op2 = OpOspf.of();
        var opCtx1 = IO.readOp("ip address 11.0.0.0/10", op1);
        var opCtx2 = IO.readOp("ip address 11.0.0.0/10", op2);
        assert opCtx1.getOperation().equals(opCtx2.getOperation());
        assert opCtx1.getOperation().hashCode() == opCtx2.getOperation().hashCode();
    }
}
