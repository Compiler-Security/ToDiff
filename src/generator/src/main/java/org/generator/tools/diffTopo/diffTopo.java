package org.generator.tools.diffTopo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.cli.*;
import org.generator.lib.frontend.driver.IO;
import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpPhy;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.opg.OpAG;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.reducer.driver.reducer;
import org.generator.lib.reducer.pass.reducePass;
import org.generator.lib.reducer.semantic.CtxOpDef;
import org.generator.lib.topo.driver.topo;
import org.generator.tools.frontend.OspfConfWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.generator.util.collections.Pair;
import org.generator.util.ran.ranHelper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class diffTopo {

    public static List<List<OpCtx>> ranSplitPhyConf(OpCtxG opCtxG, int split_num){
        return ranHelper.randomSplitElemsCanEmpty(opCtxG.getOps(), split_num);
    }
    public static List<OpCtxG> ranSplitOspfConf(OpCtxG opCtxG, int split_num){
        var r = new reducePass();
        var tmp = r.solve(opCtxG);
        var res = ranHelper.randomSplitElemsCanEmpty(tmp.getOps(), split_num);
        List<OpCtxG> splits = new ArrayList<>();
        // we split opctxg to oapg, and generate leaner form of opCtxG, that is
        for(var ops: res){
            var opag = OpAG.of(ops);
            splits.add(opag.toOpCtxGLeaner());
        }
        assert splits.size() == split_num;
        return splits;
    }
    OpCtxG getConfOfRouter(String r_name, ConfGraph g, boolean mutate){
        var confg = g.viewConfGraphOfRouter(r_name);
        confg.setR_name(r_name);
        if (mutate) {
            return generate.generateEqualOfCore(generate.generateCore(confg), false);
        }else{
            return generate.generateCore(confg);
        }
    }

    Pair<OpCtxG, OpCtxG> getConfOfPhy(ConfGraph g){
        var ori_phyg = generate.generatePhyCore(g);
        var equal_phyg = generate.generateEqualOfPhyCore(ori_phyg, 0.4, 1);
        //System.out.println(equal_phyg);
        return new Pair<>(ori_phyg, equal_phyg);
    }

    public void main(){
        var router_count =10;
        var confg = topo.genGraph(router_count, 3, 4, 3, true, null);
        System.out.println("phy");
        //System.out.println(new OspfConfWriter().write(getConfOfPhy(confg)));
        for(int i = 0; i < router_count; i++){
            var r_name = NodeGen.getRouterName(i);
            var opCtxG = getConfOfRouter(r_name, confg, false);
            System.out.println(r_name);
            System.out.println(new OspfConfWriter().write(opCtxG));
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
            routers_name.add(NodeGen.getRouterName(i));
        }
        conf.put("routers", routers_name);

        //generate router graph
        var confg = topo.genGraph(router_count, topo.areaCount, topo.mxDegree, topo.abrRatio, false, dumpInfo);

        //generate ospf core commands, all the round is same
        List<OpCtxG> ospf_cores = new ArrayList<>();
        for(int i = 0; i < router_count; i++) {
            ospf_cores.add(getConfOfRouter(routers_name.get(i), confg, false));
        }

        //record each router's core commands
        if (dumpInfo != null) {
            var core_commands = dumpInfo.putObject("core_commands");
            for (int i = 0; i < router_count; i++) {
                //record all the routers' core commands
                var writer = new OspfConfWriter();
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
            List<List<OpCtxG>> split_confs = new ArrayList<>();
            //generate each router's commands
            List<OpCtxG> opCtxGS = new ArrayList<>();
            for(int j = 0; j < router_count; j++){
                //FIXME why router 0 not mutate ?
                var opCtxG = generate.generateEqualOfCore(ospf_cores.get(j), false);
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
                List<OpCtx> phyOps;
                if (step == 0){
                    phyOps = phyOpg.getOps();
                }else{
                    phyOps = stepPhyOpList.get(step - 1);
                }
                for(var op: phyOps){
                    phy_ops.add(IO.writeOp(op));
                    //update ospfAlive
                    switch (op.getOpPhy().Type()){
                        case NODESETOSPFUP -> {
                            var router_name = op.getOpPhy().getNAME();
                            ospfAlive.set(routerNametoIdx.get(router_name), true);
                        }
                        case NODEDEL,NODESETOSPFSHUTDOWN -> {
                            var router_name = op.getOpPhy().getNAME();
                            ospfAlive.set(routerNametoIdx.get(router_name), false);
                        }
                    }
                }

                //add OSPF ops
                List<List<String>> ops = new ArrayList<>();
                for(int r = 0; r < router_count; r++){
                    //check if ospf is Alive
                    var opctxg = OpCtxG.Of();
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
                            var mergeCtxG = OpCtxG.Of();
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
                        if (CtxOpDef.isCtxOp(op.getOpOspf().Type())){
                            router_commands.add(IO.writeOp(op));
                            front_ctx = router_commands.getLast();
                        }else{
                            router_commands.set(router_commands.size() - 1, router_commands.get(router_commands.size() -1) + ';' + IO.writeOp(op));
                            //This is for ospf router-id command, it should use clear ip ospf process after this command
                            //FIXME we can random add some control command like clear ip ospf database like this
                            if (op.getOpOspf().Type() == OpType.RID || op.getOpOspf().Type() == OpType.NORID){
                                router_commands.add("clear ip ospf process");
                                router_commands.add(front_ctx);
                            }
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
