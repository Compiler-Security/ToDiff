package org.generator.lib.topo.pass.attri;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.openfabric.FABRIC;
import org.generator.lib.item.conf.node.openfabric.FABRICDaemon;
import org.generator.lib.item.conf.node.openfabric.FABRICIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.topo.item.base.Router_ISIS;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;
import org.generator.util.net.NET;
import org.generator.util.ran.ranHelper;

import java.util.*;

public class openfabricRanAttriGen implements genAttri_ISIS {

    List<FABRICDaemon> openfabric_daemons;
    List<FABRIC> openfabrics;
    Map<IPRange, List<FABRICIntf>> networkToFABRICIntfs;

    Map<Integer, String> areaToAreaId; // 存储区域到区域ID的映射
    Map<Integer, Integer> areaSystemIdCounter; // 存储每个区域的系统ID计数器



    private void generate_FABRIC(FABRIC openfabric){
        //self, we don't think about status of openfabric
        //FIXME for testing we must choose some proper value such as 0 etc.

        // ================look here================
        //it doesn't need in the ISIS

    }

    public void generate_FABRICS(){
        for(var openfabric: openfabrics){
            generate_FABRIC(openfabric);
        }
    }
    public void generate_FABRIC_Daemons(){
        for(var daemon: openfabric_daemons){
            generate_FABRIC_Daemon(daemon);
        }
    }
    private void generate_FABRIC_Daemon(FABRICDaemon daemon){
        //all attribute is random
        //FIXME for testing we must choose some important value such as 1, 65535 etc.
        daemon.setSetoverloadbit(ranHelper.randomInt(0, 1) == 0);
        daemon.setTier(ranHelper.randomInt(0, 14));
        if(generate.fastConvergence){
            daemon.setLspgeninterval(30);
        }
        else{
            daemon.setLspgeninterval(ranHelper.randomInt(1, 120));
        }

        if(generate.fastConvergence){
            daemon.setSpfinterval(1);
        }
        else{
            daemon.setSpfinterval(ranHelper.randomInt(1, 120));
        }
       
    }

    private void generate_FABRIC_Intf_by_same_network(List<FABRICIntf> openfabricIntfs){
        //network
        //FIXME for testing we must choose some important value such as 1, 65535 etc.
        var psnpInterval = ranHelper.randomInt(1, 120);
        var helloInterval = ranHelper.randomInt(1, 600);
        var csnpInterval = ranHelper.randomInt(1, 600);
        var helloMultiplier = ranHelper.randomInt(2, 100);
        if (generate.fastConvergence){
            helloInterval = 1;
            helloMultiplier = 2;
            // psnpInterval = 1;
            // csnpInterval = 1;
        }

        //set all intfs
        for(var openfabricIntf: openfabricIntfs){
            //self
            //FIXME for testing this ratio should be considered
            openfabricIntf.setPassive(ranHelper.randomInt(0, 10) == 0);
            openfabricIntf.setIproutefabric(true);
            openfabricIntf.setHelloInterval(helloInterval);
            openfabricIntf.setHelloMultiplier(helloMultiplier);
            openfabricIntf.setPsnpInterval(psnpInterval);
            openfabricIntf.setCsnpInterval(csnpInterval);
            //FIXME we need consider the metric
        }
        //we should ensure one openfabricIntf priority > 0
    }
    public void generate_FABRIC_Intfs(){
        for(var openfabricintfs: networkToFABRICIntfs.values()){
            generate_FABRIC_Intf_by_same_network(openfabricintfs);
        }
    }
    @Override
    public void generate(ConfGraph g, List<Router_ISIS> routers) {
        openfabric_daemons = new ArrayList<>();
        networkToFABRICIntfs = new HashMap<>();
        openfabrics = new ArrayList<>();
        areaToAreaId = new HashMap<>();
        areaSystemIdCounter = new HashMap<>();
        // generate the unique area ID for each area
        for (Router_ISIS r : routers) {
            if (!areaToAreaId.containsKey(r.area)) {
                // generate areaId
                String areaId = String.format("49.%04d", r.area);
                areaToAreaId.put(r.area, areaId);
                // initialize the systemId counter
                areaSystemIdCounter.put(r.area, 1); 
            }
        }
        //build each router and fill area, router_id
        for(int i = 0; i < routers.size(); i++){
            var r = routers.get(i);
            var r_name = NodeGen.getRouterName(i);
            var openfabric_name = NodeGen.getOpenFabricName(r_name);
            var openfabric_daemon_name = NodeGen.getOpenFabricDaemonName(openfabric_name);
            var openfabric = new FABRIC(openfabric_name);

            // generate areaId and systemId
            String areaId = areaToAreaId.get(r.area);
            int systemIdCount = areaSystemIdCounter.get(r.area);
            String systemId = String.format("%04d.%04d.%04d", 
                systemIdCount / 10000,
                (systemIdCount % 10000) / 100,
                systemIdCount % 100);
            areaSystemIdCounter.put(r.area, systemIdCount + 1);

            // generate NET
            NET net = NET.of(areaId + "." + systemId + ".00");
            openfabric.setNET(net);

            // set router type
            // if(r.level == 0){
            //     openfabric.setRouterType(FABRIC.RouterType.LEVEL1);
            // }else if(r.level == 1){
            //     openfabric.setRouterType(FABRIC.RouterType.LEVEL2);
            // }else if(r.level == 2){
            //     openfabric.setRouterType(FABRIC.RouterType.LEVEL1_2);
            // }
            // else{
            //     throw new RuntimeException("level should be 0, 1, 2");
            // }
            g.addNode(openfabric);
            openfabrics.add(openfabric);
            var openfabric_daemon = new FABRICDaemon(openfabric_daemon_name);
            openfabric_daemons.add(openfabric_daemon);
            g.addNode(openfabric_daemon);
            g.addOpenFabricRelation(openfabric_name, r_name);
            g.addOpenFabricDaemonRelation(openfabric_daemon_name, r_name);
            //openfabric.setRouterId(ID.of(i + 1)); //router id is not allowed to 0.0.0.0
            for(int j = 0; j < r.intfs.size(); j++){
                var intf_name  = NodeGen.getIntfName(r_name, j);
                var openfabric_intf_name = NodeGen.getOpenFabricIntfName(intf_name);
                var openfabric_intf = new FABRICIntf(openfabric_intf_name);
                g.addNode(openfabric_intf);
                g.addOpenFabricIntfRelation(openfabric_intf_name, intf_name);
                //set circuit type for each interface
                // if(r.level == 0){
                //     openfabric_intf.setLevel(FABRICIntf.FABRICLEVEL.LEVEL1);
                // }
                // else if(r.level == 1){
                //     openfabric_intf.setLevel(FABRICIntf.FABRICLEVEL.LEVEL2);
                // }
                // //if the router is level-1-2, the cuicuit type can be random
                // else if(r.level == 2){
                //     openfabric_intf.setLevel(ranHelper.randomElemOfList(Arrays.asList(FABRICIntf.FABRICLEVEL.values())));
                // }
                // else{
                //     throw new RuntimeException("level should be 0, 1, 2");
                // }

                //TODO we should use random area
                //var area_id = ID.of(r.intfs.get(j).area);
                //openfabric_intf.setArea(ID.of(r.intfs.get(j).area));
                //openfabric_intf.setCost(r.intfs.get(j).cost);


            }
            
        }



        //fill each network IP
        for(var s: g.getSwitches()){
            //FIXME we should consider the IPRange is big enough
            var prefix = ranHelper.randomInt(10, 20);
            var ipRange = IPRange.of(ranHelper.randomLong(0x80000000L, 0xE0000000L), prefix);
            networkToFABRICIntfs.put(ipRange, new ArrayList<>());
            var baseNum = ipRange.getAddressOfIp().IDtoLong();
            for(var intf: g.<Intf>getDstsByType(s.getName(), RelationEdge.EdgeType.INTF)){
                for(var target_intf: g.<Intf>getDstsByType(intf.getName(), RelationEdge.EdgeType.LINK)){
                    var openfabric_intf_name = NodeGen.getOpenFabricIntfName(target_intf.getName());
                    networkToFABRICIntfs.get(ipRange).add(g.getOpenFabricIntf(openfabric_intf_name));
                    target_intf.setIp(IP.of(baseNum++, prefix));
                }
            }
        }
        //TODO mutate other conf fields
        generate_FABRIC_Daemons();
        generate_FABRIC_Intfs();
        generate_FABRICS();
        //System.out.println(g.toDot(false));
    }
}
