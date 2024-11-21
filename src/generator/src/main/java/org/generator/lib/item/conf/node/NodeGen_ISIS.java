package org.generator.lib.item.conf.node;

import org.generator.lib.item.conf.node.isis.*;
import org.generator.lib.item.conf.node.phy.Intf_ISIS;
import org.generator.lib.item.conf.node.phy.Router_ISIS;
import org.generator.lib.item.conf.node.phy.Switch_ISIS;
import org.generator.util.exception.Unimplemented;
import org.generator.util.net.ID;
import org.generator.util.net.IPBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeGen_ISIS {
    static public NodeType_ISIS getPhyNodeTypeByName(String name){
        switch (name.charAt(0)){
            case 'r' -> {return NodeType_ISIS.Router;}
            case 's' -> {return NodeType_ISIS.Switch;}
            case 'h' -> {return NodeType_ISIS.Host;}
            default -> {assert false: "phy node name error";}
        }
        return NodeType_ISIS.Host;
    }

    static public String getISISName(String r_name){
        assert getPhyNodeTypeByName(r_name) == NodeType_ISIS.Router: "only router can has isis";
        return String.format("%s-ISIS", r_name);
    }

    static public String getSwitchName(int id){
        return "s%d".formatted(id);
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

    static public String getRouterName(int id){ return "r%d".formatted(id);}

    static public String getISISDaemonName(String isis_name){
        return String.format("%s-daemon", isis_name);
    }
    static public String getIntfName(String r_name, int port){
        getPhyNodeTypeByName(r_name);
        return String.format("%s-eth%d", r_name, port);
    }

    static  public String getISISAreaName(String isis_name, ID area){
        return String.format("%s-area-%s", isis_name, area.toString());
    }
    static public String getISISAreaSumName(String isis_name, String area_name){
        return String.format("%s-%s", isis_name, area_name);
    }

    static public String getAreaName(IPBase area){
        return String.format("area-%s", area.toString());
    }
    static public String getISISIntfName(String intf_name){
        return String.format("%s-isis",intf_name);
    }

    public static Router_ISIS new_Router(String name){
        return new Router_ISIS(name);
    }

    public static Switch_ISIS new_Switch(String name){
        return new Switch_ISIS(name);
    }

    public static ISIS new_ISIS(String name){
        return new ISIS(name);
    }

    public static Intf_ISIS new_Intf(String name) {return new Intf_ISIS(name);}

    public static ISISIntf new_ISIS_Intf(String name) {return new ISISIntf(name);}

    @SuppressWarnings("unchecked")
    public static <T extends  AbstractNode_ISIS> T newNode(String name, NodeType_ISIS type){;
        return (T)new_node(name, type);
    }
    public static AbstractNode_ISIS new_node(String name, NodeType_ISIS type){
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
            case ISIS -> {
                return new_ISIS(name);
            }
            case ISISIntf -> {
                return new_ISIS_Intf(name);
            }
            case ISISDaemon -> {
                return new ISISDaemon(name);
            }
            case ISISAreaSum -> {
                return new ISISAreaSum(name);
            }
            case null, default -> {
                new Unimplemented();
            }
        }
        return null;
    }
}
