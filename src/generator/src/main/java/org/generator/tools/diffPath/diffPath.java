package org.generator.tools.diffPath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.generator.lib.frontend.driver.IO;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.ospf.OSPF;
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

    static boolean isProtoUp(ConfGraph cur_confg, String r_name){
        assert cur_confg.containsNode(r_name);
        switch (generate.protocol){
            case OSPF-> {
                return cur_confg.getOspfOfRouter(r_name).getStatus() == OSPF.OSPF_STATUS.UP;
            }
        }
        assert false;
        return false;
    }

    /**
     * This function is to write phy commands and protocol commands to change cur_confg to target_confg
     * @param cur_confg
     * @param target_confg
     * @param cur_phyOps
     * @param cur_protoConfs
     * @return
     */
    static Map<String, Object> writeTransCommands(ConfGraph cur_confg, ConfGraph target_confg, OpCtxG cur_phyOps, Map<String, OpCtxG> cur_protoConfs, List<String> cur_init_conf_routers) {
        OpCtxG phy_ops;
        if (cur_confg == null){
            phy_ops = generate.generatePhyCore(target_confg);
        }else {
            var new_phyOps = generate.generatePhyCore(target_confg);
            phy_ops = generate.generateDiffPhyOp(cur_phyOps, new_phyOps);
        }
        cur_phyOps.addOps(phy_ops.getOps());
        Map<String, List<String>> ospf_ops_str = new HashMap<>();
        for(var r_name: target_confg.getRouterNames()){
            //if protocol daemon is not up, then we should not generate protocol commands
            if (!isProtoUp(target_confg, r_name)){continue;}

            var r_confg = target_confg.viewConfGraphOfRouter(r_name);
            r_confg.setR_name(r_name);
            var target_ospfOps = generate.generateCore(r_confg, false);

            if (!cur_protoConfs.containsKey(r_name)){
                cur_protoConfs.put(r_name, OpCtxG.Of());
                cur_init_conf_routers.add(r_name);
            }
            var add_ospfOps = generate.generateDiffProtoOp(cur_protoConfs.get(r_name),  target_ospfOps);

            ospf_ops_str.put(r_name, dumpOspfCommands(add_ospfOps));
            cur_protoConfs.get(r_name).addOps(add_ospfOps.getOps());
        }
        Map<String, Object> res = new HashMap<>();
        res.put("phy", phy_ops.getOps().stream().map(IO::writeOp).toList());
        //FIXME 7-15 multiple protocols
        res.put("ospf", ospf_ops_str);

        res.put("routers", target_confg.getRouters().stream().map(r -> r.getName()).sorted().toList());
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
     * @param cur_init_conf_routers if router a's conf in cur_ospfconf is clear or is not in cur_ospfconf, then router's name should be in the list
     * @param cur_phyOps aggregate version
     * @param cur_ospfConf  aggregate version
     */
    static void genStep(List<Map<String, Object>> steps, ConfGraph old_confg, ConfGraph new_confg, transGraph.deltaNodes deltas, int waitTime, OpCtxG cur_phyOps, Map<String, OpCtxG> cur_ospfConf, List<String> cur_init_conf_routers){
        steps.add(writeTransCommands(old_confg, new_confg, cur_phyOps, cur_ospfConf, cur_init_conf_routers));

        steps.getLast().put("waitTime", waitTime);

        //after this step, all routers in initConf is init, so clean it
        steps.getLast().put("initConf", new ArrayList<>(cur_init_conf_routers));
        cur_init_conf_routers.clear();

        List<String> compareNet = new ArrayList<>();
        steps.getLast().put("compareNet", compareNet);
        writeComparableNet(new_confg, deltas, compareNet);

        List<String> compareIntf = new ArrayList<>();
        steps.getLast().put("compareIntf", compareIntf);
        writeComparableIntf(new_confg, deltas, compareIntf);
    }

    static int genRound(List<Map<String, Object>> steps, List<Router> routers, ConfGraph confg, int max_step, int max_step_time, Map<String, Map<String, String>> info_one_round, Map<String, String> info_step_0){
        var cur_phy_ops = OpCtxG.Of();
        //proto confs will be keeped and updated all the time, except use initConf to override the conf
        //if cur_proto_confs key is changed(remove or add router conf), then router should be in initConf
        Map<String, OpCtxG> cur_proto_confs = new HashMap<>();
        List<String> cur_init_conf_routers = new ArrayList<>();

        int cur_step_num = 1;
        genStep(steps, null, confg, new transGraph.deltaNodes(), cur_step_num == max_step ? -1:2, cur_phy_ops, cur_proto_confs, cur_init_conf_routers);
        info_one_round.put("step0", info_step_0);

        //FIXME 7-18 we should use transform engine
        for(int i = 0; i < 1; i++){
            //FIXME dumpInfo every step
            Map<String, String> info_step = new HashMap<>();
            var res = topo.transformGraph(routers, confg, new ArrayList<>(List.of(phyTran.transRule.addSubGraph)), info_step);
            genStep(steps, confg, res.second(), res.first().getDeltaNodes(), -1, cur_phy_ops, cur_proto_confs, cur_init_conf_routers);
            info_one_round.put("step%d".formatted(i + 1), info_step);
            cur_step_num++;
        }

        return cur_step_num;
    }

    public static JsonNode gen(int router_count, int max_step, int max_step_time, int round_num){
        Map<String, Object> conf = new HashMap<>();
        conf.put("conf_name", "test%d".formatted(Instant.now().getEpochSecond()));
        conf.put("conf_type", "diffPath");
        List<Integer> step_nums = new ArrayList<>();
        conf.put("step_nums", step_nums);
        conf.put("round_num", round_num);

        Map<String, String> info_step_0 = new HashMap<>();
        var res = topo.genInitTransGraph(router_count, topo.areaCount, topo.mxDegree, topo.abrRatio, false, info_step_0);
        var confg = res.second();
        var routers = res.first();

        conf.put("routers", confg.getRouters().stream().map(r -> r.getName()).sorted().toList());
        List<List<Map<String, Object>>> commands = new ArrayList<>();
        Map<String, Map<String, Map<String, String>>> info = new HashMap<>();
        conf.put("commands", commands);
        conf.put("info", info);

        for(int i = 0; i < round_num; i++) {
            commands.add(new ArrayList<>());
            Map<String, Map<String, String>> info_one_round = new HashMap<>();
            info.put("round%d".formatted(i), info_one_round);

            var step_num = genRound(commands.getLast(), routers, confg, i + 1, max_step_time, info_one_round, info_step_0);

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
