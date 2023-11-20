package org.generator.topo.node;

import org.generator.topo.Topo;
import org.generator.topo.node.ospf.OSPF;
import org.generator.topo.node.ospf.OSPFIntf;
import org.generator.topo.node.phy.Intf;
import org.generator.topo.node.phy.PhyNode;
import org.generator.topo.node.phy.Router;
import org.generator.topo.node.phy.Switch;
import org.generator.util.net.IPV4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TopoNodeGen {
    static public PhyNode.NodeType getPhyNodeTypeByName(String name){
        switch (name.charAt(0)){
            case 'r' -> {return PhyNode.NodeType.Router;}
            case 's' -> {return PhyNode.NodeType.Switch;}
            case 'h' -> {return PhyNode.NodeType.Host;}
            default -> {assert false: "phy node name error";}
        }
        return PhyNode.NodeType.Host;
    }

    static public String getOSPFName(String r_name){
        assert getPhyNodeTypeByName(r_name) == PhyNode.NodeType.Router: "only router can has ospf";
        return String.format("%s-OSPF", r_name);
    }

    static public String getPhyNodeNameFromIntfName(String intf_name){
        Pattern pattern = Pattern.compile("(\\w+)-\\w+");
        Matcher matcher = pattern.matcher(intf_name);
        if (matcher.matches()){
            return matcher.group(1);
        }else{
            assert false: String.format("intf name not right %s", intf_name);
        }
        return "";
    }


    static public String getIntfName(String r_name, int port){
        getPhyNodeTypeByName(r_name);
        return String.format("%s-eth%d", r_name, port);
    }

    static public String getAreaName(IPV4 area){
        return String.format("area-%s", area.toString());
    }
    static public String getOSPFIntfName(String intf_name){
        return String.format("%s-ospf",intf_name);
    }

    public static Router new_Router(String name){
        return new Router(name);
    }

    public static Switch new_Switch(String name){
        return new Switch(name);
    }

    public static OSPF new_OSPF(String name){
        return new OSPF(name);
    }

    public static Intf new_Intf(String name) {return new Intf(name);}

    public static OSPFIntf new_OSPF_Intf(String name) {return new OSPFIntf(name);}
}
