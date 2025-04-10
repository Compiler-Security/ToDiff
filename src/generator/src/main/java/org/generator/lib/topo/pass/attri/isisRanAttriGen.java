package org.generator.lib.topo.pass.attri;

import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.isis.ISIS;
import org.generator.lib.item.conf.node.isis.ISISDaemon;
import org.generator.lib.item.conf.node.isis.ISISIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.topo.item.base.Router_ISIS;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;
import org.generator.util.net.NET;
import org.generator.util.ran.ranHelper;

import java.util.*;

public class isisRanAttriGen implements genAttri_ISIS {

    List<ISISDaemon> isis_daemons;
    List<ISIS> isiss;
    Map<IPRange, List<ISISIntf>> networkToISISIntfs;

    Map<Integer, String> areaToAreaId; // 存储区域到区域ID的映射

    // class AreaAttri {
    //     public AreaAttri(ID area_id){
    //         isiss = new HashSet<>();
    //         intfs = new HashSet<>();
    //         this.area_id = area_id;
    //     }
    //     public Set<ISIS> isiss;
    //     public Set<Intf_ISIS> intfs;
    //     public ID area_id;
    //     public boolean stub;
    //     public boolean noSummary;
    //     public boolean nssa;

    //     @Override
    //     public boolean equals(Object o) {
    //         if (this == o) return true;
    //         if (o == null || getClass() != o.getClass()) return false;
    //         AreaAttri areaAttri = (AreaAttri) o;
    //         return Objects.equals(area_id, areaAttri.area_id);
    //     }

    //     @Override
    //     public int hashCode() {
    //         return Objects.hash(area_id);
    //     }
    // }

    public void generate_ISISAreaSums(ConfGraph confG){

    }
    // private void generate_ISISAreaSum(AreaAttri areaAttri, ConfGraph_ISIS confG){
  
    //     // it doesn't need in the ISIS
    // }
    private void generate_ISIS(ISIS isis){
        //self, we don't think about status of isis
        //FIXME for testing we must choose some proper value such as 0 etc.

        // ================look here================
        //it doesn't need in the ISIS

    }

    public void generate_ISISS(){
        for(var isis: isiss){
            generate_ISIS(isis);
        }
    }
    public void generate_ISIS_Daemons(){
        for(var daemon: isis_daemons){
            generate_ISIS_Daemon(daemon);
        }
    }
    private void generate_ISIS_Daemon(ISISDaemon daemon){
        //all attribute is random
        //FIXME for testing we must choose some important value such as 1, 65535 etc.
        
        if(generate.fastConvergence){
            daemon.setLspgenintervalLevel1(30);
            daemon.setLspgenintervalLevel2(30);
            daemon.setOverloadbitonstartup(1);
            daemon.setSetoverloadbit(false);
            daemon.setLspmtu(1492);
            daemon.setAdvertisehighmetrics(false);
        }
        else{
            daemon.setLspgenintervalLevel1(ranHelper.randomInt(1, 120));
            daemon.setLspgenintervalLevel2(ranHelper.randomInt(1, 120));
            daemon.setOverloadbitonstartup(ranHelper.randomInt(0, 86400));
            daemon.setSetoverloadbit(ranHelper.randomInt(0, 1) == 0);
            daemon.setLspmtu(ranHelper.randomInt(128, 4352));
            daemon.setAdvertisehighmetrics(ranHelper.randomInt(0, 1) == 0);
        }

        if(generate.fastConvergence){
            daemon.setSpfintervalLevel1(1);
            daemon.setSpfintervalLevel2(1);
        }
        else{
            daemon.setSpfintervalLevel1(ranHelper.randomInt(1, 120));
            daemon.setSpfintervalLevel2(ranHelper.randomInt(1, 120));
        }
        //daemon.setMetricStyle(ranHelper.randomElemOfList(Arrays.asList(ISISDaemon.metricstyle.values())));
    }

    private void generate_ISIS_Intf_by_same_network(List<ISISIntf> isisIntfs){
        //network
        //FIXME for testing we must choose some important value such as 1, 65535 etc.
        var psnpInterval = ranHelper.randomInt(1, 120);
        var helloInterval = ranHelper.randomInt(1, 600);
        var csnpInterval = ranHelper.randomInt(1, 600);
        var helloMultiplier = ranHelper.randomInt(2, 100);
        var ifthreeway = ranHelper.randomInt(0, 1) == 0;
        var ifhellopadding = ranHelper.randomInt(0, 1) == 0;
        if (generate.fastConvergence){
            helloInterval = 1;
            helloMultiplier = 2;
            psnpInterval = 1;
            csnpInterval = 1;
        }
        var netType = ISISIntf.ISISNetType.BROADCAST;
        if (isisIntfs.size() == 2){
            //IF the network only have two interface, it can be a single line
            netType = ranHelper.randomInt(0, 1) == 0 ? ISISIntf.ISISNetType.POINTTOPOINT : ISISIntf.ISISNetType.BROADCAST;
        }
        //set all intfs
        for(var isisIntf: isisIntfs){
            //self
            //FIXME for testing this ratio should be considered
            isisIntf.setPassive(false);
            isisIntf.setHelloPadding(ifhellopadding);
            isisIntf.setThreeWayHandshake(ifthreeway);
            //isisIntf.setCost(ranHelper.randomInt(1, 65535));
            isisIntf.setPriorityLevel1(ranHelper.randomInt(0, 127));
            isisIntf.setPriorityLevel2(ranHelper.randomInt(0, 127));

            isisIntf.setIprouteisis(true);
            //we should set helloMulti for testing
            //network
            isisIntf.setHelloMultiplierlevel1(helloMultiplier);
            isisIntf.setHelloMultiplierlevel2(helloMultiplier);
            // if (isisIntf.getHelloMultiplierlevel1() == 1) {
            //     //IF we don't set hello multiplier
            //     isisIntf.setHelloIntervalLevel1(helloInterval);
            // }else{
            //     //IF we set hello multiplier, other args should be set by hello-multiplier
            //     isisIntf.setHelloIntervalLevel2(helloInterval);
            // }
            // if(isisIntf.getHelloMultiplierlevel2() == 1){
            //     isisIntf.setHelloIntervalLevel2(helloInterval);
            // }else{
            //     isisIntf.setHelloIntervalLevel2(helloInterval);
            // }
            isisIntf.setHelloIntervalLevel1(helloInterval);
            isisIntf.setHelloIntervalLevel2(helloInterval);
            isisIntf.setPsnpIntervalLevel1(psnpInterval);
            isisIntf.setPsnpIntervalLevel2(psnpInterval);
            isisIntf.setCsnpIntervalLevel1(csnpInterval);
            isisIntf.setCsnpIntervalLevel2(csnpInterval);
            isisIntf.setNetType(netType);
            if(netType == ISISIntf.ISISNetType.BROADCAST){
                isisIntf.setThreeWayHandshake(true);
            }
            //isisIntf.setLevel(ranHelper.randomElemOfList(Arrays.asList(ISISIntf.ISISLEVEL.values())));
            //FIXME we need consider the metric
        }
        //we should ensure one isisIntf priority > 0
    }
    public void generate_ISIS_Intfs(){
        for(var isisintfs: networkToISISIntfs.values()){
            generate_ISIS_Intf_by_same_network(isisintfs);
        }
    }
    @Override
    public void generate(ConfGraph g, List<Router_ISIS> routers) {
        isis_daemons = new ArrayList<>();
        networkToISISIntfs = new HashMap<>();
        isiss = new ArrayList<>();
        areaToAreaId = new HashMap<>();

        // generate the unique area ID for each area
        for (Router_ISIS r : routers) {
            if (!areaToAreaId.containsKey(r.area)) {
                // generate areaId
                String areaId = String.format("49.%04d", r.area);
                areaToAreaId.put(r.area, areaId);
            }
        }
        //build each router and fill area, router_id
        for(int i = 0; i < routers.size(); i++){
            var r = routers.get(i);
            var r_name = NodeGen.getRouterName(i);
            var isis_name = NodeGen.getISISName(r_name);
            var isis_daemon_name = NodeGen.getISISDaemonName(isis_name);
            var isis = new ISIS(isis_name);

            // generate areaId and systemId
            String areaId = areaToAreaId.get(r.area);
            
            String systemId = String.format("%04d.%04d.%04d", 
                i / 10000,
                (i % 10000) / 100,
                i % 100);


            // generate NET
            NET net = NET.of(areaId + "." + systemId + ".00");
            isis.setNET(net);

            // set router type
            if(r.level == 0){
                isis.setRouterType(ISIS.RouterType.LEVEL1);
            }else if(r.level == 1){
                isis.setRouterType(ISIS.RouterType.LEVEL2);
            }else if(r.level == 2){
                isis.setRouterType(ISIS.RouterType.LEVEL1_2);
            }
            else{
                throw new RuntimeException("level should be 0, 1, 2");
            }
            g.addNode(isis);
            isiss.add(isis);
            var isis_daemon = new ISISDaemon(isis_daemon_name);
            isis_daemons.add(isis_daemon);
            g.addNode(isis_daemon);
            g.addISISRelation(isis_name, r_name);
            g.addISISDaemonRelation(isis_daemon_name, r_name);
            //isis.setRouterId(ID.of(i + 1)); //router id is not allowed to 0.0.0.0
            for(int j = 0; j < r.intfs.size(); j++){
                var intf_name  = NodeGen.getIntfName(r_name, j);
                var isis_intf_name = NodeGen.getISISIntfName(intf_name);
                var isis_intf = new ISISIntf(isis_intf_name);
                g.addNode(isis_intf);
                g.addISISIntfRelation(isis_intf_name, intf_name);
                //set circuit type for each interface
                if(r.level == 0){
                    isis_intf.setLevel(ISISIntf.ISISLEVEL.LEVEL1);
                }
                else if(r.level == 1){
                    isis_intf.setLevel(ISISIntf.ISISLEVEL.LEVEL2);
                }
                //if the router is level-1-2, the cuicuit type can be random
                else if(r.level == 2){
                    isis_intf.setLevel(ISISIntf.ISISLEVEL.LEVEL1_2);
                }
                else{
                    throw new RuntimeException("level should be 0, 1, 2");
                }

                //TODO we should use random area
                //var area_id = ID.of(r.intfs.get(j).area);
                //isis_intf.setArea(ID.of(r.intfs.get(j).area));
                //isis_intf.setCost(r.intfs.get(j).cost);


            }
            
        }



        //fill each network IP
        for(var s: g.getSwitches()){
            //FIXME we should consider the IPRange is big enough
            var prefix = ranHelper.randomInt(10, 20);
            var ipRange = IPRange.of(ranHelper.randomLong(0x80000000L, 0xE0000000L), prefix);
            networkToISISIntfs.put(ipRange, new ArrayList<>());
            var baseNum = ipRange.getAddressOfIp().IDtoLong();
            for(var intf: g.<Intf>getDstsByType(s.getName(), RelationEdge.EdgeType.INTF)){
                for(var target_intf: g.<Intf>getDstsByType(intf.getName(), RelationEdge.EdgeType.LINK)){
                    var isis_intf_name = NodeGen.getISISIntfName(target_intf.getName());
                    networkToISISIntfs.get(ipRange).add(g.getISISIntf(isis_intf_name));
                    target_intf.setIp(IP.of(baseNum++, prefix));
                }
            }
        }
        //TODO mutate other conf fields
        generate_ISIS_Daemons();
        generate_ISIS_Intfs();
        generate_ISISS();
        generate_ISISAreaSums(g);
        //System.out.println(g.toDot(false));
    }
}
