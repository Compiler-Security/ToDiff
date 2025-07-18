package org.generator.tools.diffPath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.generator.lib.frontend.driver.IO;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.lib.reducer.semantic.CtxOpDef;
import org.generator.lib.topo.driver.topo;
import org.generator.lib.topo.item.base.Router;
import org.generator.lib.topo.item.trans.transGraph;
import org.generator.lib.topo.pass.trans.phyTran;

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
    static Map<String, Object> writeCommands(ConfGraph old_confg, ConfGraph new_confg, OpCtxG cur_phyOps, Map<String, OpCtxG> cur_ospfConf) {
        OpCtxG phy_ops;
        if (old_confg == null){
            phy_ops = generate.generatePhyCore(new_confg);
        }else {
            var new_phyOps = generate.generatePhyCore(new_confg);
            phy_ops = generate.generateDiffPhyOp(cur_phyOps, new_phyOps);
        }
        cur_phyOps.addOps(phy_ops.getOps());
        Map<String, List<String>> ospf_ops_str = new HashMap<>();
        for(var new_r: new_confg.getRouters()){
            if (old_confg != null && old_confg.containsNode(new_r.getName())){
                var new_rConfg = new_confg.viewConfGraphOfRouter(new_r.getName());
                new_rConfg.setR_name(new_r.getName());
                var old_ospfOps = cur_ospfConf.get(new_r.getName());
                var new_ospfOps = generate.generateCore(new_rConfg, false);
                var add_ospfOps = generate.generateDiffProtoOp(old_ospfOps, new_ospfOps);
                ospf_ops_str.put(new_r.getName(), dumpOspfCommands(add_ospfOps));
                cur_ospfConf.get(new_r.getName()).addOps(add_ospfOps.getOps());
            }else{
                var new_rConfg = new_confg.viewConfGraphOfRouter(new_r.getName());
                new_rConfg.setR_name(new_r.getName());
                var new_ospfOps = generate.generateCore(new_rConfg, false);
                ospf_ops_str.put(new_r.getName(), dumpOspfCommands(new_ospfOps));
                cur_ospfConf.put(new_r.getName(), new_ospfOps);
            }
        }
        Map<String, Object> res = new HashMap<>();
        res.put("phy", phy_ops.getOps().stream().map(IO::writeOp).toList());
        //FIXME 7-15 multiple protocols
        res.put("ospf", ospf_ops_str);
        res.put("routers", new_confg.getRouters().stream().map(r -> r.getName()).sorted().toList());
        return res;
    }

    static void writeComparableNet(ConfGraph new_confg, transGraph.deltaNodes deltas, List<String> compareNet){
        var networkIp = new_confg.getNetworks();
        for(var s: new_confg.getSwitches()){
            var comparable = true;
            for(var intf: new_confg.getLinkedIntfsOfSwitch(s.getName())){
                if (deltas.isNewIntf(intf.getName()) || deltas.isUpdateIntf(intf.getName())){
                    comparable = false;
                    break;
                }
            }
            if (comparable){
                compareNet.add(networkIp.get(NodeGen.getId(s.getName())).toRangeString());
            }
        }
    }

    static void writeComparableIntf(ConfGraph new_confg, transGraph.deltaNodes deltas, List<String> compareIntf){
        for(var r: new_confg.getRouters()){
            for(var intf: new_confg.getIntfsOfRouter(r.getName())){
                if (!(deltas.isNewIntf(intf.getName()) || deltas.isUpdateIntf(intf.getName()))){
                    compareIntf.add(intf.getName());
                }
            }
        }
    }

    /**
     * Each writeStep will wirte phy and ospf commands to generate the new Confgraph, update cur_phyOps and cur_ospfConf to the aggregate version
     * For routers delete, the conf is remained, and will be restored once use OSPF UP
     * @param steps
     * @param old_confg
     * @param new_confg
     * @param deltas
     * @param waitTime
     * @param init_conf if router a's conf in cur_ospfconf is clear or is not in cur_ospfconf, then init_conf should be set to true
     * @param cur_phyOps aggregate version
     * @param cur_ospfConf  aggregate version
     */
    static void writeStep(List<Map<String, Object>> steps, ConfGraph old_confg, ConfGraph new_confg, transGraph.deltaNodes deltas, int waitTime, boolean init_conf, OpCtxG cur_phyOps, Map<String, OpCtxG> cur_ospfConf){
        steps.add(writeCommands(old_confg, new_confg, cur_phyOps, cur_ospfConf));

        steps.getLast().put("waitTime", waitTime);
        steps.getLast().put("initConf", init_conf);

        List<String> compareNet = new ArrayList<>();
        steps.getLast().put("compareNet", compareNet);
        writeComparableNet(new_confg, deltas, compareNet);

        List<String> compareIntf = new ArrayList<>();
        steps.getLast().put("compareIntf", compareIntf);
        writeComparableIntf(new_confg, deltas, compareIntf);
    }

    static int generateSteps(List<Map<String, Object>> steps, List<Router> routers, ConfGraph confg, int max_step, int max_step_time, boolean init, Map<String, Map<String, String>> info_round, Map<String, String> initInfo){
        var phy_ops = OpCtxG.Of();
        Map<String, OpCtxG> ospf_confs = new HashMap<>();
        if (init){
            //step 0
            writeStep(steps, null, confg, new transGraph.deltaNodes(), -1, true, phy_ops, ospf_confs);
            info_round.put("step0", initInfo);
            return 1;
        }else{
            //step 0
            writeStep(steps, null, confg, new transGraph.deltaNodes(), 2,  true, phy_ops, ospf_confs);
            info_round.put("step0", initInfo);
            //FIXME 7-15 we should use transform engine
            for(int i = 0; i < 1; i++){
                //FIXME dumpInfo every step
                Map<String, String> info_step = new HashMap<>();
                var res = topo.transformGraph(routers, confg, new ArrayList<>(List.of(phyTran.transRule.addSubGraph)), info_step);
                writeStep(steps, confg, res.second(), res.first().getDeltaNodes(), -1, false, phy_ops, ospf_confs);
                info_round.put("step%d".formatted(i + 1), info_step);
            }
            return 2;
        }
    }

    public static JsonNode gen(int router_count, int max_step, int max_step_time, int round_num){
        Map<String, Object> conf = new HashMap<>();
        conf.put("conf_name", "test%d".formatted(Instant.now().getEpochSecond()));
        conf.put("conf_type", "diffPath");
        List<Integer> step_nums = new ArrayList<>();
        conf.put("step_nums", step_nums);
        conf.put("round_num", round_num);
        Map<String, String> dumpInfo = new HashMap<>();
        var res = topo.genInitTransGraph(router_count, topo.areaCount, topo.mxDegree, topo.abrRatio, false, dumpInfo);
        var confg = res.second();
        var routers = res.first();
        conf.put("routers", confg.getRouters().stream().map(r -> r.getName()).sorted().toList());
        List<List<Map<String, Object>>> commands = new ArrayList<>();
        Map<String, Map<String, Map<String, String>>> info = new HashMap<>();
        conf.put("commands", commands);
        conf.put("info", info);
        for(int i = 0; i < round_num; i++) {
            commands.add(new ArrayList<>());
            Map<String, Map<String, String>> info_round = new HashMap<>();
            info.put("r%d".formatted(i), info_round);

            var step_num = generateSteps(commands.getLast(), routers, confg, max_step, max_step_time, i == 0, info_round, dumpInfo);

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
