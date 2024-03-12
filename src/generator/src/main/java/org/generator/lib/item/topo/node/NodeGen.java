package org.generator.lib.item.topo.node;

import org.generator.lib.item.topo.node.ospf.*;
import org.generator.lib.item.topo.node.phy.Intf;
import org.generator.lib.item.topo.node.phy.Router;
import org.generator.lib.item.topo.node.phy.Switch;
import org.generator.util.exception.Unimplemented;
import org.generator.util.net.IPBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeGen {
    static public NodeType getPhyNodeTypeByName(String name){
        switch (name.charAt(0)){
            case 'r' -> {return NodeType.Router;}
            case 's' -> {return NodeType.Switch;}
            case 'h' -> {return NodeType.Host;}
            default -> {assert false: "phy node name error";}
        }
        return NodeType.Host;
    }

    static public String getOSPFName(String r_name){
        assert getPhyNodeTypeByName(r_name) == NodeType.Router: "only router can has ospf";
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

    static public String getOSPFDaemonName(String ospf_name){
        return String.format("%s-daemon", ospf_name);
    }
    static public String getIntfName(String r_name, int port){
        getPhyNodeTypeByName(r_name);
        return String.format("%s-eth%d", r_name, port);
    }

    static  public String getOSPFAreaName(IPBase area){
        return String.format("area%d", area.IDtoLong());
    }
    static public String getOSPFAreaSumName(String ospf_name, String area_name){
        return String.format("%s-%s", ospf_name, area_name);
    }

    static public String getAreaName(IPBase area){
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

    public static <T extends  AbstractNode> T newNode(String name, NodeType type){;
        return (T)new_node(name, type);
    }
    public static AbstractNode new_node(String name, NodeType type){
        switch (type){
            case Host -> {
                new Unimplemented();
            }
            case Intf -> {
                return new_Intf(name);
            }
            case Router -> {
                return new_Router(name);
            }
            case Switch -> {
                return new_Switch(name);
            }
            case OSPF -> {
                return new_OSPF(name);
            }
            case OSPFIntf -> {
                return new_OSPF_Intf(name);
            }
            case OSPFDaemon -> {
                return new OSPFDaemon(name);
            }
            case OSPFAreaSum -> {
                return new OSPFAreaSum(name);
            }
            case null, default -> {
                new Unimplemented();
            }
        }
        return null;
    }
}
