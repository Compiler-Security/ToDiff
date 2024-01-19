package tools.frontend;

import org.generator.lib.frontend.driver.IO;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.reduction.pass.reducePass;
import org.generator.tools.frontend.OspfConfReader;
import org.generator.tools.frontend.OspfConfWriter;
import org.junit.Test;

public class IOTest {

    @Test
    public void IoTest(){
        String test_st = """
      
                                router ospf
                                area 1061954456 range 91.122.46.62/11 not-advertise  
                                area 3389220260 range 92.238.183.225/7
                         
                   
            
                """;
        var reader = new OspfConfReader();
        var opCtxG = reader.read(test_st);
        var writer = new OspfConfWriter();
        //System.out.println(writer.write(opCtxG));
        var reducer = new reducePass();
        var rCtxg = reducer.resolve(opCtxG);
        System.out.println(writer.write(rCtxg.getRemainOps()));
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
