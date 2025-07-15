package tools.diffPath;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.generator.lib.frontend.driver.IO;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.topo.driver.topo;
import org.generator.lib.topo.item.base.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.lib.topo.pass.attri.ospfRanAttriGen;
import org.generator.lib.topo.pass.build.topoBuild;
import org.generator.lib.topo.pass.trans.phyTran;
import org.generator.util.collections.Pair;
import org.generator.util.net.IP;
import org.generator.util.net.IPBase;
import org.generator.util.ran.ranHelper;
import org.junit.Test;

import java.time.Instant;
import java.util.*;

public class diffPathTest {

    Intf getIntf(int routerId, int id, int area, int cost, int networkId){
        var intf = new Intf(id, routerId);
        intf.networkId = networkId;
        intf.cost = cost;
        intf.area = area;
        return intf;
    }
    Pair<List<Router>, ConfGraph> getDelNodeTestRouters(){
        List<Router> routers = new ArrayList<>();
        for(int i = 0; i < 5; i++) routers.add(new Router(i));
        for(int i = 0; i < 3; i++) {
            routers.get(0).intfs.add(getIntf(0, i, 0, i + 1, i));
        }
        routers.get(1).intfs.add(getIntf(1, 0, 0, 4, 0));
        routers.get(2).intfs.add(getIntf(2, 0, 0, 5, 0));
        routers.get(3).intfs.add(getIntf(3, 0, 0, 6, 1));
        routers.get(4).intfs.add(getIntf(4, 0, 0, 7, 2));
        var b = new topoBuild();
        var confg = b.solve(routers);
        var c = new ospfRanAttriGen();
        c.generate(confg, routers);
        return new Pair<>(routers, confg);
    }
    @Test public void test_delNode() {
        //var tmp = topo.genInitTransGraph(3, 2, 2, 1, false, null);
        var tmp = getDelNodeTestRouters();
        var old_routers = tmp.first();
        var old_confg = tmp.second();
        var tmp1 = topo.transformGraph(old_routers, old_confg, new ArrayList<>(List.of(phyTran.transRule.equalDealNode)), null);
        var new_confg = tmp1.second();
        var old_phyOps = generate.generatePhyCore(old_confg);
        var new_phyOps = generate.generatePhyCore(new_confg);
        System.out.println(generate.generateDiffPhyOp(old_phyOps, new_phyOps));
        var new_routers = tmp1.first().getRouters();
        for(var old_r:old_confg.getRouters()) {
            System.out.println(old_r.getName());
            if (new_confg.containsNode(old_r.getName())){
                var old_rConfg = old_confg.viewConfGraphOfRouter(old_r.getName());
                old_rConfg.setR_name(old_r.getName());
                var new_rConfg = new_confg.viewConfGraphOfRouter(old_r.getName());
                new_rConfg.setR_name(old_r.getName());
                var old_ospfOps = generate.generateCore(old_rConfg, false);
                var new_ospfOps = generate.generateCore(new_rConfg, false);
                //System.out.println(old_ospfOps);
                //System.out.println(new_ospfOps);
                System.out.println(generate.generateDiffProtoOp(old_ospfOps, new_ospfOps));
                break;
            }
        }
    }

    @Test public void test_addSubGraph() {
        //var tmp = topo.genInitTransGraph(3, 2, 2, 1, false, null);
        var tmp = getDelNodeTestRouters();
        var old_routers = tmp.first();
        var old_confg = tmp.second();
        var tmp1 = topo.transformGraph(old_routers, old_confg, new ArrayList<>(List.of(phyTran.transRule.addSubGraph)), null);
        var new_confg = tmp1.second();
        var old_phyOps = generate.generatePhyCore(old_confg);
        var new_phyOps = generate.generatePhyCore(new_confg);
        System.out.println(generate.generateDiffPhyOp(old_phyOps, new_phyOps));
//        var new_routers = tmp1.first().getRouters();
//        for(var old_r:old_confg.getRouters()) {
//            System.out.println(old_r.getName());
//            if (new_confg.containsNode(old_r.getName())){
//                var old_rConfg = old_confg.viewConfGraphOfRouter(old_r.getName());
//                old_rConfg.setR_name(old_r.getName());
//                var new_rConfg = new_confg.viewConfGraphOfRouter(old_r.getName());
//                new_rConfg.setR_name(old_r.getName());
//                var old_ospfOps = generate.generateCore(old_rConfg, false);
//                var new_ospfOps = generate.generateCore(new_rConfg, false);
//                //System.out.println(old_ospfOps);
//                //System.out.println(new_ospfOps);
//                System.out.println(generate.generateDiffProtoOp(old_ospfOps, new_ospfOps));
//                break;
//            }
//        }
    }

    @Test
    public void generate_testcase(){
        Map<String, Object> conf = new HashMap<>();
        ObjectNode dumpInfo = new ObjectMapper().createObjectNode();
        var round_num = 2;
        conf.put("conf_name", "test%d".formatted(Instant.now().getEpochSecond()));
        List<Integer> step_nums = new ArrayList<>();
        step_nums.add(1);
        for(int i = 1; i < round_num; i++)
            step_nums.add(2);
        conf.put("step_nums", step_nums);
        conf.put("round_num", round_num);
        List<String> routers_name = new ArrayList<>();
        var router_count = 5;
        for(int i = 0; i < router_count; i++){
            routers_name.add(NodeGen.getRouterName(i));
        }
        conf.put("routers", routers_name);
        var tmp = getDelNodeTestRouters();
        var old_routers = tmp.first();
        var old_confg = tmp.second();
        List<List<Map<String, Object>>> commands = new ArrayList<>();
        conf.put("commands", commands);

            List<Map<String, Object>> steps = new ArrayList<>();
            List<Map<String, Object>> steps2 = new ArrayList<>();
            commands.add(steps);
            commands.add(steps2);
            Map<String, Object> one_step = new HashMap<>();
            steps.add(one_step);
            steps2.add(one_step);
            one_step.put("step", 0);
            var old_phyOps = generate.generatePhyCore(old_confg);
            List<String> phy_ops = new ArrayList<>();
            one_step.put("phy", phy_ops);
            for(var op: old_phyOps){
                phy_ops.add(IO.writeOp(op));
            }
            List<List<String>> ops = new ArrayList<>();
            one_step.put("ospf", ops);
            one_step.put("waitTime", -1);
            for(var old_r_name: routers_name) {
                var old_rConfg = old_confg.viewConfGraphOfRouter(old_r_name);
                old_rConfg.setR_name(old_r_name);
                var opctxg = generate.generateCore(old_rConfg, false);
                List<String> router_commands = new ArrayList<>();
                ops.add(router_commands);
                for(var op: opctxg.getOps()){
                    router_commands.add(IO.writeOp(op));
                }
            }

            Map<String, Object> two_step = new HashMap<>();
            steps2.add(two_step);
            two_step.put("step", 2);
            var tmp1 = topo.transformGraph(old_routers, old_confg, new ArrayList<>(List.of(phyTran.transRule.equalDealNode)), null);
            var new_confg = tmp1.second();
            var new_phyOps = generate.generatePhyCore(new_confg);
            var new_phyOps_delta = generate.generateDiffPhyOp(old_phyOps, new_phyOps);
            List<String> phy_ops1 = new ArrayList<>();
            two_step.put("phy", phy_ops1);
            //System.out.println(new_phyOps_delta);
            for(var op: new_phyOps_delta){
                phy_ops1.add(IO.writeOp(op));
            }
            List<List<String>> ops1 = new ArrayList<>();
            two_step.put("ospf", ops1);
            for(var old_r_name:routers_name) {
                if (new_confg.containsNode(old_r_name)){
                    var old_rConfg = old_confg.viewConfGraphOfRouter(old_r_name);
                    old_rConfg.setR_name(old_r_name);
                    var new_rConfg = new_confg.viewConfGraphOfRouter(old_r_name);
                    new_rConfg.setR_name(old_r_name);
                    var old_ospfOps = generate.generateCore(old_rConfg, false);
                    var new_ospfOps = generate.generateCore(new_rConfg, false);
                    List<String> router_commands = new ArrayList<>();
                    ops1.add(router_commands);
                    var opctxg = generate.generateDiffProtoOp(old_ospfOps, new_ospfOps);
                    for(var op: opctxg.getOps()){
                        router_commands.add(IO.writeOp(op));
                    }
                }else{
                    ops1.add(new ArrayList<>());

                }
            }
        two_step.put("waitTime", -1);
        var mapper = new ObjectMapper();
        try {
            var json = mapper.valueToTree(conf);
            var writer = new ObjectMapper().writer(new DefaultPrettyPrinter());
            System.out.println(writer.writeValueAsString(json));
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
