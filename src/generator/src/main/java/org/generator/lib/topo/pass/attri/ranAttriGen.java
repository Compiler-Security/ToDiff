package org.generator.lib.topo.pass.attri;

import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.ospf.OSPF;
import org.generator.lib.item.conf.node.ospf.OSPFAreaSum;
import org.generator.lib.item.conf.node.ospf.OSPFDaemon;
import org.generator.lib.item.conf.node.ospf.OSPFIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.topo.item.base.Router;
import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;
import org.generator.util.ran.ranHelper;

import java.util.*;

public class ranAttriGen implements genAttri {

    List<OSPFDaemon> ospf_daemons;
    List<OSPF> ospfs;
    Map<IPRange, List<OSPFIntf>> networkToOSPFIntfs;

    Map<ID, AreaAttri> areas;

    Map<OSPF, Boolean> isABR;

    class AreaAttri {
        public AreaAttri(ID area_id){
            ospfs = new HashSet<>();
            intfs = new HashSet<>();
            this.area_id = area_id;
        }
        public Set<OSPF> ospfs;
        public Set<Intf> intfs;
        public ID area_id;
        public boolean stub;
        public boolean noSummary;
        public boolean nssa;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AreaAttri areaAttri = (AreaAttri) o;
            return Objects.equals(area_id, areaAttri.area_id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(area_id);
        }
    }

    public void generate_OSPFAreaSums(ConfGraph confG){
        for(var areaAttri: areas.values()){
            generate_OSPFAreaSum(areaAttri, confG);
        }
    }
    private void generate_OSPFAreaSum(AreaAttri areaAttri, ConfGraph confG){
        //each area has an areaAttri
        //each ospf has multiple areaSum, one areaSum represents one area
        for(var ospf: areaAttri.ospfs){
            if (isABR.get(ospf)){
                // FIXME we can set not ABR abr instruction
                // || (!isABR.get(ospf) && ranHelper.randomInt(0, 10) == 0)){
                var areaSum = new OSPFAreaSum(NodeGen.getOSPFAreaName(ospf.getName(), areaAttri.area_id));
                //each abr ospf has different areaSum because of areaSumEntry
                //TODO 1. set areaSum attri
                areaSum.setStub(areaAttri.stub);
                areaSum.setNosummary(areaAttri.noSummary);
                areaSum.setNssa(areaAttri.nssa);
                areaSum.setArea(areaAttri.area_id);
                //TODO 2. add areaSumEntries
                Set<IPRange> ips = new HashSet<>();
                //FIXME for simplicity we don't merge ips
                for(var intf: areaAttri.intfs){
                    var ip = intf.getIp();
                    ips.add(IPRange.of(ip.toNetString()));
                }
                for(var ipRange: ips){
                    var areaSumEntry = new OSPFAreaSum.OSPFAreaSumEntry();
                    areaSumEntry.setRange(ipRange);
                    areaSumEntry.setAdvertise(ranHelper.randomInt(0, 10) > 0);
                    if (areaSumEntry.isAdvertise()) {
                        if (ranHelper.randomInt(0, 1) > 0) {
                            if (ranHelper.randomInt(0, 1) > 0) {
                                areaSumEntry.setCost(ranHelper.randomInt(0, 65535));
                            }
                            if (ranHelper.randomInt(0, 1) > 0) {
                                areaSumEntry.setSubstitute(ranHelper.randomIP());
                            }
                        }
                    }
                    //TODO set a proper chance to set areaSumEntry
                    if (ranHelper.randomInt(0, 1) == 0){
                        areaSum.getSumEntries().put(areaSumEntry.getRange().toString(), areaSumEntry);
                    }
                }

                confG.addNode(areaSum);
                confG.addOSPFAreaSumRelation(areaSum.getName(), ospf.getName());
                assert  !confG.getOSPFAreaSumOfOSPF(ospf.getName()).isEmpty();
            }
        }
    }
    private void generate_OSPF(OSPF ospf){
        //self, we don't think about status of ospf
        //FIXME for testing we must choose some proper value such as 0 etc.
        ospf.setInitDelay(ranHelper.randomInt(0, 600000));
        ospf.setMinHoldTime(ranHelper.randomInt(0, 600000));
        ospf.setMaxHoldTime(ranHelper.randomInt(0, 600000));
        //FIXME we should think ABR_TYPE
    }

    public void generate_OSPFS(){
        for(var ospf: ospfs){
            generate_OSPF(ospf);
        }
    }
    public void generate_OSPF_Daemons(){
        for(var daemon: ospf_daemons){
            generate_OSPF_Daemon(daemon);
        }
    }
    private void generate_OSPF_Daemon(OSPFDaemon daemon){
        //all attribute is random
        //FIXME for testing we must choose some important value such as 1, 65535 etc.
        daemon.setMaxPaths(ranHelper.randomInt(1, 64));
        daemon.setWritemulti(ranHelper.randomInt(1, 100));
        daemon.setSocketPerInterface(ranHelper.randomInt(0, 1) == 0);
        daemon.setBufferrecv(ranHelper.randomLong(1, 4000000000L));
        daemon.setBuffersend(ranHelper.randomLong(1, 4000000000L));
    }

    private void generate_OSPF_Intf_by_same_network(List<OSPFIntf> ospfIntfs){
        //network
        //FIXME for testing we must choose some important value such as 1, 65535 etc.
        var deadInterval = ranHelper.randomInt(1, 65535);
        var helloInterval = ranHelper.randomInt(1, 65535);
        var retransInterval = ranHelper.randomInt(1, 65535);
        var transInterval = ranHelper.randomInt(1, 655353);
        var netType = OSPFIntf.OSPFNetType.BROADCAST;
        var GRHelloDelay = ranHelper.randomInt(1, 1800);
        if (ospfIntfs.size() == 2){
            //IF the network only have two interface, it can be a single line
            netType = ranHelper.randomInt(0, 1) == 0 ? OSPFIntf.OSPFNetType.BROADCAST : OSPFIntf.OSPFNetType.POINTTOPOINT;
        }
        //set all intfs
        for(var ospfIntf: ospfIntfs){
            //self
            //FIXME for testing this ratio should be considered
            ospfIntf.setPassive(ranHelper.randomInt(0, 1) != 0);
            ospfIntf.setCost(ranHelper.randomInt(1, 65535));
            ospfIntf.setPriority(ranHelper.randomInt(0, 255));
            //FIXME we should set helloMulti for testing
            //network
            ospfIntf.setHelloMulti(ranHelper.randomInt(1, 20));
            if (ospfIntf.getHelloMulti() == 1) {
                //IF we don't set hello multiplier
                ospfIntf.setHelloInterval(helloInterval);
                ospfIntf.setDeadInterval(deadInterval);
            }else{
                //IF we set hello multiplier, other args should be set by hello-multiplier
                ospfIntf.setDeadInterval(1);
                ospfIntf.setHelloInterval(0);
            }
            ospfIntf.setGRHelloDelay(GRHelloDelay);
            ospfIntf.setNetType(netType);
            ospfIntf.setRetansInter(retransInterval);
            ospfIntf.setTransDelay(transInterval);
        }
        //we should ensure one ospfIntf priority > 0
    }
    public void generate_OSPF_Intfs(){
        for(var ospfintfs: networkToOSPFIntfs.values()){
            generate_OSPF_Intf_by_same_network(ospfintfs);
        }
    }
    @Override
    public void generate(ConfGraph g, List<Router> routers) {
        ospf_daemons = new ArrayList<>();
        networkToOSPFIntfs = new HashMap<>();
        ospfs = new ArrayList<>();
        areas = new HashMap<>();
        isABR = new HashMap<>();
        //build each router and fill area, router_id
        for(int i = 0; i < routers.size(); i++){
            var r = routers.get(i);
            var r_name = NodeGen.getRouterName(i);
            var ospf_name = NodeGen.getOSPFName(r_name);
            var ospf_daemon_name = NodeGen.getOSPFDaemonName(ospf_name);
            var ospf = new OSPF(ospf_name);
            g.addNode(ospf);
            ospfs.add(ospf);
            var ospf_daemon = new OSPFDaemon(ospf_daemon_name);
            ospf_daemons.add(ospf_daemon);
            g.addNode(ospf_daemon);
            g.addOSPFRelation(ospf_name, r_name);
            g.addOSPFDaemonRelation(ospf_daemon_name, r_name);
            ospf.setRouterId(ID.of(i + 1)); //router id is not allowed to 0.0.0.0
            isABR.put(ospf, false);
            for(int j = 0; j < r.intfs.size(); j++){
                var intf_name  = NodeGen.getIntfName(r_name, j);
                var ospf_intf_name = NodeGen.getOSPFIntfName(intf_name);
                var ospf_intf = new OSPFIntf(ospf_intf_name);
                g.addNode(ospf_intf);
                g.addOSPFIntfRelation(ospf_intf_name, intf_name);
                //TODO we should use random area
                var area_id = ID.of(r.intfs.get(j).area);
                ospf_intf.setArea(ID.of(r.intfs.get(j).area));

                if (area_id.toLong() == 0L){
                    isABR.put(ospf, true);
                }

                if (!areas.containsKey(area_id)){
                    areas.put(area_id, new AreaAttri(area_id));
                }
                areas.get(area_id).intfs.add(g.getIntf(intf_name));
                areas.get(area_id).ospfs.add(ospf);
            }
        }
        //fill each network IP
        for(var s: g.getSwitches()){
            var baseID = ranHelper.randomID();
            //FIXME we should consider the IPRange is big enough
            var prefix = ranHelper.randomInt(10, 20);
            var ipRange = IPRange.of(baseID.toLong(), prefix);
            networkToOSPFIntfs.put(ipRange, new ArrayList<>());
            var baseNum = ipRange.getAddressOfIp().IDtoLong();
            for(var intf: g.<Intf>getDstsByType(s.getName(), RelationEdge.EdgeType.INTF)){
                for(var target_intf: g.<Intf>getDstsByType(intf.getName(), RelationEdge.EdgeType.LINK)){
                    var ospf_intf_name = NodeGen.getOSPFIntfName(target_intf.getName());
                    networkToOSPFIntfs.get(ipRange).add(g.getOSPFIntf(ospf_intf_name));
                    target_intf.setIp(IP.of(baseNum++, prefix));
                }
            }
        }
        //TODO mutate other conf fields
        generate_OSPF_Daemons();
        generate_OSPF_Intfs();
        generate_OSPFS();
        generate_OSPFAreaSums(g);
        //System.out.println(g.toDot(false));
    }
}
