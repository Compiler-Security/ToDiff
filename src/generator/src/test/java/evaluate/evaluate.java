package evaluate;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.reducer.driver.reducer;
import org.generator.tools.diffOp.genOps;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class evaluate {

    double genInstruction(int inst_num){
        var genOp = new genOps();
        var ori = genOp.genRandomEva(inst_num, 0.2, 0.6, 4, 0.2f, 1, "r1");
        var core = reducer.reduceToCore(ori);
        long startTime = System.currentTimeMillis();
        var gen = generate.generateEqualOfCore(core, true);
        long endTime = System.currentTimeMillis();
        System.out.println(gen.getOps().size());
        double elapsedTimeInSeconds = (endTime - startTime) / 1000.0;
        return elapsedTimeInSeconds;
    }
    /*
    X-axis command num
    Y-axis time
     */
    @Test
    public void evaCommandNumTime(){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        Integer i = 1;
        while (i < 2000){
            if (i <= 100) i += 2;
            else if (i > 500) i += 50;
            else i += 10;
            var res = genInstruction(i);
            System.out.println("=====%d=======".formatted(i));
            resultNode.put(i.toString(), res);
        }
        var writer = new ObjectMapper().writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(new File("/home/binshui/result/numTime.json"), resultNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
