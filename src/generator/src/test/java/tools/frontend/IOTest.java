package tools.frontend;

import org.generator.lib.frontend.driver.IO;
import org.generator.lib.generator.controller.CapacityController;
import org.generator.lib.generator.controller.NormalController;
import org.generator.lib.generator.pass.genPass;
import org.generator.lib.item.IR.OpOspf;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.reducer.pass.reducePass;
import org.generator.tools.frontend.OspfConfReader;
import org.generator.tools.frontend.OspfConfWriter;
import org.junit.Test;

public class IOTest {

    @Test
    public void Part2Test(){
        String test_st = """
                                router ospf
                                int r1-eth0
                                router ospf     
                                area 1061954456 range 91.122.46.62/11 not-advertise  
                                area 3389220260 range 92.238.183.225/7      
                """;
        var reader = new OspfConfReader();
        var opCtxG = reader.read(test_st);
        var writer = new OspfConfWriter();
        //System.out.println(writer.write(opCtxG));
        var reducer = new reducePass();
        var opas = reducer.resolve(opCtxG).setActiveView().getOps();
        var normal_controller = NormalController.of();
        for(var opa: opas){
            normal_controller.addConfig(opa, 1, 2, 1, 1);
        }
        var tmp_controller = CapacityController.of(6, 0, 0, 1, 0);
        var gen_opag = genPass.solve(normal_controller, tmp_controller);
        gen_opag = reducePass.expandOpAG(gen_opag);
        var print_ctx = OpCtxG.Of();
        gen_opag.getOps().forEach(opa -> print_ctx.addOp(opa.getOp().getOpCtx()));
        System.out.println(writer.write(print_ctx));
    }
    @Test
    public void IoTest(){
        String test_st = """
                                router ospf     
                                area 1061954456 range 91.122.46.62/11 not-advertise  
                                area 1061954456 range 91.122.46.62/11 not-advertise  
                         
            ·
                """;
        var reader = new OspfConfReader();
        var opCtxG = reader.read(test_st);
        var writer = new OspfConfWriter();
        //System.out.println(writer.write(opCtxG));
        var reducer = new reducePass();
        var rCtxg = reducer.resolve(opCtxG);
        rCtxg.reduce();
        //var rCtxg = reducePass.expandOpAG(reducer.resolve(opCtxG));
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
