package org.generator.tools.diffPath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.generator.lib.frontend.driver.IO;
import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.reducer.semantic.CtxOpDef;
import org.generator.lib.topo.driver.topo;
import org.generator.lib.topo.item.base.Router;
import org.generator.lib.topo.pass.trans.phyTran;
import org.generator.util.collections.Pair;
import org.generator.util.ran.ranHelper;

import java.time.Instant;
import java.util.*;

public class diffPath {
    static List<String> dumpOspfCommands(OpCtxG opctxg){
        List<String> router_commands = new ArrayList<>();
        var front_ctx = "";
        for(var op: opctxg.getOps()){
            if (CtxOpDef.isCtxOp(op.getOpOspf().Type())){
                router_commands.add(IO.writeOp(op));
                front_ctx = router_commands.getLast();
            }else{
                //append op to last line of router_commands with split symbol `;`
                router_commands.set(router_commands.size() - 1, router_commands.get(router_commands.size() -1) + ';' + IO.writeOp(op));
            }
        }
        return router_commands;
    }
    static Map<String, Object> getNextStepCommands(ConfGraph old_confg, ConfGraph new_confg) {
        OpCtxG phy_ops;
        if (old_confg == null){
            phy_ops = generate.generatePhyCore(new_confg);
        }else {
            var old_phyOps = generate.generatePhyCore(old_confg);
            var new_phyOps = generate.generatePhyCore(new_confg);
            phy_ops = generate.generateDiffPhyOp(old_phyOps, new_phyOps);
        }
        Map<String, List<String>> ospf_ops = new HashMap<>();
        for(var new_r: new_confg.getRouters()){
            if (old_confg != null && old_confg.containsNode(new_r.getName())){
                var old_rConfg = old_confg.viewConfGraphOfRouter(new_r.getName());
                old_rConfg.setR_name(new_r.getName());
                var new_rConfg = new_confg.viewConfGraphOfRouter(new_r.getName());
                new_rConfg.setR_name(new_r.getName());
                var old_ospfOps = generate.generateCore(old_rConfg, false);
                var new_ospfOps = generate.generateCore(new_rConfg, false);
                ospf_ops.put(new_r.getName(), dumpOspfCommands(generate.generateDiffProtoOp(old_ospfOps, new_ospfOps)));
            }else{
                var new_rConfg = new_confg.viewConfGraphOfRouter(new_r.getName());
                new_rConfg.setR_name(new_r.getName());
                var new_ospfOps = generate.generateCore(new_rConfg, false);
                ospf_ops.put(new_r.getName(), dumpOspfCommands(new_ospfOps));
            }
        }
        Map<String, Object> res = new HashMap<>();
        res.put("phy", phy_ops.getOps().stream().map(IO::writeOp).toList());
        //FIXME 7-15 multiple protocols
        res.put("ospf", ospf_ops);
        res.put("routers", new_confg.getRouters().stream().map(r -> r.getName()).sorted().toList());
        return res;
    }

    static int generateSteps(List<Map<String, Object>> steps, List<Router> routers, ConfGraph confg, int max_step, int max_step_time, boolean init){
        steps.add(getNextStepCommands(null, confg));
        if (init){
            steps.getFirst().put("waitTime", -1);
            return 1;
        }else{
            //FIXME 7-15 we should use transform engine
            steps.getFirst().put("waitTime", 2);
            for(int i = 0; i < 1; i++){
                //FIXME dumpInfo every step
                var res = topo.transformGraph(routers, confg, new ArrayList<>(List.of(phyTran.transRule.addSubGraph)), null);
                steps.add(getNextStepCommands(confg, res.second()));
                steps.getLast().put("waitTime", -1);
            }
            return 2;
        }
    }
    public static JsonNode gen(int router_count, int max_step, int max_step_time, int round_num){
        Map<String, Object> conf = new HashMap<>();
        //ObjectNode dumpInfo = new ObjectMapper().createObjectNode();
        conf.put("conf_name", "test%d".formatted(Instant.now().getEpochSecond()));
        List<Integer> step_nums = new ArrayList<>();
        conf.put("step_nums", step_nums);
        conf.put("round_num", round_num);
        var res = topo.genInitTransGraph(router_count, topo.areaCount, topo.mxDegree, topo.abrRatio, false, null);
        var confg = res.second();
        var routers = res.first();
        List<List<Map<String, Object>>> commands = new ArrayList<>();
        conf.put("commands", commands);
        for(int i = 0; i < round_num; i++) {
            commands.add(new ArrayList<>());
            var step_num = generateSteps(commands.getLast(), routers, confg, max_step, max_step_time, i == 0);
            step_nums.add(step_num);
        }


        var mapper = new ObjectMapper();
        //conf.put("genInfo", dumpInfo);
        try {
            var json = mapper.valueToTree(conf);
            return json;
        }catch (Exception e) {
            e.printStackTrace();
        }
        assert false:"gen wrong";
        return null;
    }
}
