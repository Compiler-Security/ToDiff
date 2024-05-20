package org.generator.tools.diffTopo;

import org.generator.lib.frontend.driver.IO;
import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpOspf;
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
    public static List<OpCtxG> ranSplitConf(OpCtxG opCtxG, int split_num){
        var r = new reducePass();
        var tmp = r.solve(opCtxG);
        var res = ranHelper.randomSplitElemsCanEmpty(tmp.getOps(), split_num);
        List<OpCtxG> splits = new ArrayList<>();
        for(var ops: res){
            var opag = OpAG.of(ops);
            splits.add(opag.toOpCtxGLeaner());
        }
        assert splits.size() == split_num;
        return splits;
    }
    OpCtxG getConfOfRouter(String r_name, ConfGraph g){
        var confg = g.viewConfGraphOfRouter(r_name);
        confg.setR_name(r_name);
        var opCtxG = generate.generateEqualOfCore(generate.generateCore(confg), 1);
        System.out.println(opCtxG);
        return opCtxG;
    }

    Pair<OpCtxG, OpCtxG> getConfOfPhy(ConfGraph g){
        var ori_phyg = generate.generatePhyCore(g);
        var equal_phyg = generate.generateEqualOfPhyCore(ori_phyg, 0.4, 1);
        return new Pair<>(ori_phyg, equal_phyg);
    }

    public void main(){
        var router_count =3;
        var confg = topo.genGraph(router_count, 3, 4, 3, true);
        System.out.println("phy");
        //System.out.println(new OspfConfWriter().write(getConfOfPhy(confg)));
        for(int i = 0; i < router_count; i++){
            var r_name = NodeGen.getRouterName(i);
            var opCtxG = getConfOfRouter(r_name, confg);
            System.out.println(r_name);
            System.out.println(new OspfConfWriter().write(opCtxG));
        }
    }

    public void gen(int router_count, int max_step, int max_step_time, int round_num){
        Map<String, Object> conf = new HashMap<>();
        //FIXME
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
        var confg = topo.genGraph(router_count, 3, 4, 3, true);

        List<OpCtxG> opCtxGS = new ArrayList<>();
        for(int i = 0; i < router_count; i++){
            opCtxGS.add(getConfOfRouter(routers_name.get(i), confg));
        }

        List<List<Map<String, Object>>> commands = new ArrayList<>();
        conf.put("commands", commands);
        for(int i = 0; i < round_num; i++){
            var step_num = step_nums.get(i);
            List<List<OpCtxG>> split_confs = new ArrayList<>();
            for(int r = 0; r < router_count; r++){
                split_confs.add(ranSplitConf(opCtxGS.get(r), step_num));
            }
            List<Map<String, Object>> steps = new ArrayList<>();
            commands.add(steps);

            var phyOpgPair = getConfOfPhy(confg);
            var phyOpg =phyOpgPair.first();
            var phyEqualOpg = phyOpgPair.second();
            var stepPhyOpList = ranSplitPhyConf(phyEqualOpg, step_num - 1);

            for(int step = 0; step < step_num; step++){
                Map<String, Object> one_step = new HashMap<>();
                steps.add(one_step);
                one_step.put("step", step);

                List<List<String>> ops = new ArrayList<>();
                for(int r = 0; r < router_count; r++){
                    var opctxg = split_confs.get(r).get(step);
                    List<String> a = new ArrayList<>();
                    ops.add(a);
                    var front_ctx = "";
                    //FIXME this condition we can't generate OpAnalysis without ctxOp
                    for(var op: opctxg.getOps()){
                        if (CtxOpDef.isCtxOp(op.getOpOspf().Type())){
                            a.add(IO.writeOp(op));
                            front_ctx = a.getLast();
                        }else{
                            a.set(a.size() - 1, a.get(a.size() -1) + ';' + IO.writeOp(op));
                            //This is for ospf router-id command, it should use clear ip ospf process after this command
                            //FIXME we can random add some control command like clear ip ospf database like this
                            if (op.getOpOspf().Type() == OpType.RID || op.getOpOspf().Type() == OpType.NORID){
                                a.add("clear ip ospf process");
                                a.add(front_ctx);
                            }
                        }
                    }
                }

                one_step.put("ospf", ops);
                List<String> phy_ops = new ArrayList<>();
                one_step.put("phy", phy_ops);
                //FIXME
                if (step == 0){
                    for(var op: phyOpg.getOps()){
                        phy_ops.add(IO.writeOp(op));
                    }
                }else{
                    for(var op: stepPhyOpList.get(step - 1)){
                        phy_ops.add(IO.writeOp(op));
                    }
                }

                var waitTime = ranHelper.randomInt(1, max_step_time);
                if (step == step_num - 1){
                    waitTime = -1;
                }
                one_step.put("waitTime", waitTime);
            }
        }
        var mapper = new ObjectMapper();
        try {
            var json = mapper.valueToTree(conf);

            System.out.println(json.toPrettyString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
