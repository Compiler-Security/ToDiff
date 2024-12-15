package org.generator.tools.diffTopo_ISIS;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.cli.*;
import org.generator.lib.frontend.driver.IO_ISIS;
import org.generator.lib.frontend.lexical.OpType_isis;
import org.generator.lib.generator.driver.generate_ISIS;
import org.generator.lib.item.IR.OpCtx_ISIS;
import org.generator.lib.item.IR.OpPhy_ISIS;
import org.generator.lib.item.conf.graph.ConfGraph_ISIS;
import org.generator.lib.item.conf.node.NodeGen_ISIS;
import org.generator.lib.item.opg.OpAG_ISIS;
import org.generator.lib.item.opg.OpCtxG_ISIS;
import org.generator.lib.reducer.driver.reducer_ISIS;
import org.generator.lib.reducer.pass.reducePass_ISIS;
import org.generator.lib.reducer.semantic.CtxOpDef_ISIS;
import org.generator.lib.topo.driver.topo_ISIS;
import org.generator.tools.frontend.IsisConfWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.generator.util.collections.Pair;
import org.generator.util.ran.ranHelper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class diffTopo_ISIS {

    public static List<List<OpCtx_ISIS>> ranSplitPhyConf(OpCtxG_ISIS opCtxG, int split_num){
        return ranHelper.randomSplitElemsCanEmpty(opCtxG.getOps(), split_num);
    }
    public static List<OpCtxG_ISIS> ranSplitOspfConf(OpCtxG_ISIS opCtxG, int split_num){
        var r = new reducePass_ISIS();
        var tmp = r.solve(opCtxG);
        var res = ranHelper.randomSplitElemsCanEmpty(tmp.getOps(), split_num);
        List<OpCtxG_ISIS> splits = new ArrayList<>();
        // we split opctxg to oapg, and generate leaner form of opCtxG, that is
        for(var ops: res){
            var opag = OpAG_ISIS.of(ops);
            splits.add(opag.toOpCtxGLeaner());
        }
        assert splits.size() == split_num;
        return splits;
    }
    OpCtxG_ISIS getConfOfRouter(String r_name, ConfGraph_ISIS g, boolean mutate){
        var confg = g.viewConfGraphOfRouter(r_name);
        confg.setR_name(r_name);
        if (mutate) {
            return generate_ISIS.generateEqualOfCore(generate_ISIS.generateCore(confg), false);
        }else{
            return generate_ISIS.generateCore(confg);
        }
    }

    Pair<OpCtxG_ISIS, OpCtxG_ISIS> getConfOfPhy(ConfGraph_ISIS g){
        var ori_phyg = generate_ISIS.generatePhyCore(g);
        var equal_phyg = generate_ISIS.generateEqualOfPhyCore(ori_phyg, 0.4, 1);
        //System.out.println(equal_phyg);
        return new Pair<>(ori_phyg, equal_phyg);
    }

    public void main(){
        var router_count =10;
        var confg = topo_ISIS.genGraph(router_count, 3, 4, 3, true, null);
        System.out.println("phy");
        //System.out.println(new OspfConfWriter().write(getConfOfPhy(confg)));
        for(int i = 0; i < router_count; i++){
            var r_name = NodeGen_ISIS.getRouterName(i);
            var opCtxG = getConfOfRouter(r_name, confg, false);
            System.out.println(r_name);
            System.out.println(new IsisConfWriter().write(opCtxG));
        }
    }

    /**
     *
     * @param router_count
     * @param max_step
     * @param max_step_time
     * @param round_num

     */
    public JsonNode gen(int router_count, int max_step, int max_step_time, int round_num){
        Map<String, Object> conf = new HashMap<>();
        ObjectNode dumpInfo = new ObjectMapper().createObjectNode();
        //FIXME
        //generate conf header
        conf.put("conf_name", "test%d".formatted(Instant.now().getEpochSecond()));
        List<Integer> step_nums = new ArrayList<>();
        step_nums.add(1);
        for(int i = 1; i < round_num; i++)
            step_nums.add(ranHelper.randomInt(1, max_step));
        conf.put("step_nums", step_nums);
        conf.put("round_num", round_num);
        List<String> routers_name = new ArrayList<>();
        for(int i = 0; i < router_count; i++){
            routers_name.add(NodeGen_ISIS.getRouterName(i));
        }
        conf.put("routers", routers_name);

        //generate router graph
        var confg = topo_ISIS.genGraph(router_count, topo_ISIS.areaCount, topo_ISIS.mxDegree, topo_ISIS.abrRatio, false, dumpInfo);

        //generate ospf core commands, all the round is same
        List<OpCtxG_ISIS> ospf_cores = new ArrayList<>();
        for(int i = 0; i < router_count; i++) {
            ospf_cores.add(getConfOfRouter(routers_name.get(i), confg, false));
        }

        //record each router's core commands
        if (dumpInfo != null) {
            var core_commands = dumpInfo.putObject("core_commands");
            for (int i = 0; i < router_count; i++) {
                //record all the routers' core commands
                var writer = new IsisConfWriter();
                core_commands.put(routers_name.get(i), writer.write(getConfOfRouter(routers_name.get(i), confg, false)));
            }
        }

        //generate each round's commands
        List<List<Map<String, Object>>> commands = new ArrayList<>();
        conf.put("commands", commands);
        for(int i = 0; i < round_num; i++){
            var step_num = step_nums.get(i);

            List<Map<String, Object>> steps = new ArrayList<>();
            commands.add(steps);

            //Prepare phy commands
            var phyOpgPair = getConfOfPhy(confg);
            var phyOpg =phyOpgPair.first();
            var phyEqualOpg = phyOpgPair.second();
            var stepPhyOpList = ranSplitPhyConf(phyEqualOpg, step_num - 1);

            //Prepare OSPF commands
            List<List<OpCtxG_ISIS>> split_confs = new ArrayList<>();
            //generate each router's commands
            List<OpCtxG_ISIS> opCtxGS = new ArrayList<>();
            for(int j = 0; j < router_count; j++){
                //FIXME why router 0 not mutate ?
                var opCtxG = generate_ISIS.generateEqualOfCore(ospf_cores.get(j), false);
                opCtxGS.add(opCtxG);
            }
            for(int r = 0; r < router_count; r++){
                split_confs.add(ranSplitOspfConf(opCtxGS.get(r), step_num - 1));
            }


            //we should give empty ospf conf if daemon is down
            //step 0, commands load from ospf conf, so ospfAlive is True
            List<Boolean> ospfAlive = new ArrayList<>();
            Map<String, Integer> routerNametoIdx = new HashMap<>();
            for(int r = 0; r < router_count; r++){
                ospfAlive.add(Boolean.TRUE);
                routerNametoIdx.put(routers_name.get(r), r);
            }

            for(int step = 0; step < step_num; step++){
                Map<String, Object> one_step = new HashMap<>();
                steps.add(one_step);
                one_step.put("step", step);

                //add PhyOps
                List<String> phy_ops = new ArrayList<>();
                one_step.put("phy", phy_ops);
                //FIXME
                List<OpCtx_ISIS> phyOps;
                if (step == 0){
                    phyOps = phyOpg.getOps();
                }else{
                    phyOps = stepPhyOpList.get(step - 1);
                }
                for(var op: phyOps){
                    phy_ops.add(IO_ISIS.writeOp(op));
                    //update ospfAlive
                    switch (op.getOpPhy().Type()){
                        case NODESETISISUP -> {
                            var router_name = op.getOpPhy().getNAME();
                            ospfAlive.set(routerNametoIdx.get(router_name), true);
                        }
                        case NODEDEL,NODESETISISSHUTDOWN -> {
                            var router_name = op.getOpPhy().getNAME();
                            ospfAlive.set(routerNametoIdx.get(router_name), false);
                        }
                    }
                }

                //add OSPF ops
                List<List<String>> ops = new ArrayList<>();
                for(int r = 0; r < router_count; r++){
                    //check if ospf is Alive
                    var opctxg = OpCtxG_ISIS.Of();
                    if (step == 0){
                        //in step 0, ospf is always alive
                        opctxg = ospf_cores.get(r);
                    }else {
                        if (ospfAlive.get(r)) {
                            //if router's ospf is up, we just use these commands
                            opctxg = split_confs.get(r).get(step - 1);
                        } else {
                            //else we put these commands to next steps
                            //in the last round, we ensure OSPF is up
                            var mergeCtxG = OpCtxG_ISIS.Of();
                            mergeCtxG.addOps(split_confs.get(r).get(step - 1).getOps());
                            mergeCtxG.addOps(split_confs.get(r).get(step).getOps());
                            split_confs.get(r).set(step, mergeCtxG);
                        }
                    }
                    List<String> router_commands = new ArrayList<>();
                    ops.add(router_commands);
                    var front_ctx = "";
                    //FIXME this condition we can't generate OpAnalysis without ctxOp
                    for(var op: opctxg.getOps()){
                        if (CtxOpDef_ISIS.isCtxOp(op.getOpIsis().Type())){
                            router_commands.add(IO_ISIS.writeOp(op));
                            front_ctx = router_commands.getLast();
                        }else{
                            router_commands.set(router_commands.size() - 1, router_commands.get(router_commands.size() -1) + ';' + IO_ISIS.writeOp(op));
                            //This is for ospf router-id command, it should use clear ip ospf process after this command

                            //look here:FIXME:we don't need it in ISIS
                            //FIXME we can random add some control command like clear ip ospf database like this
                            // if (op.getOpIsis().Type() == OpType_isis.RID || op.getOpIsis().Type() == OpType_isis.NORID){
                            //     router_commands.add("clear ip ospf process");
                            //     router_commands.add(front_ctx);
                            // }
                        }
                    }
                }

                one_step.put("ospf", ops);

                //add waitTime
                var waitTime = ranHelper.randomInt(1, max_step_time);
                if (step == step_num - 1){
                    waitTime = -1;
                }
                one_step.put("waitTime", waitTime);
            }
        }
        var mapper = new ObjectMapper();
        conf.put("genInfo", dumpInfo);
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
