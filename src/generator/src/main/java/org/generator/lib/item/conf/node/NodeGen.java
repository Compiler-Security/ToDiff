package org.generator.lib.item.conf.node;

import org.generator.lib.item.conf.node.babel.BABEL;
import org.generator.lib.item.conf.node.babel.BABELIntf;
import org.generator.lib.item.conf.node.isis.ISIS;
import org.generator.lib.item.conf.node.isis.ISISDaemon;
import org.generator.lib.item.conf.node.isis.ISISIntf;
import org.generator.lib.item.conf.node.openfabric.FABRIC;
import org.generator.lib.item.conf.node.openfabric.FABRICIntf;
import org.generator.lib.item.conf.node.openfabric.FABRICDaemon;
import org.generator.lib.item.conf.node.ospf.*;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.phy.Router;
import org.generator.lib.item.conf.node.phy.Switch;
import org.generator.lib.item.conf.node.rip.RIP;
import org.generator.lib.item.conf.node.rip.RIPIntf;
import org.generator.util.exception.Unimplemented;
import org.generator.util.net.ID;
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

    static public String getIntfName(String r_name, int port){
        getPhyNodeTypeByName(r_name);
        return String.format("%s-eth%d", r_name, port);
    }

    //========OSPF===============
    static public String getOSPFName(String r_name){
        assert getPhyNodeTypeByName(r_name) == NodeType.Router: "only router can has ospf";
        return String.format("%s-OSPF", r_name);
    }
    static public String getOSPFDaemonName(String ospf_name){
        return String.format("%s-daemon", ospf_name);
    }
    static  public String getOSPFAreaName(String ospf_name, ID area){
        return String.format("%s-area-%s", ospf_name, area.toString());
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

    //==========RIP=====================
    static public String getRIPName(String r_name){
        assert getPhyNodeTypeByName(r_name) == NodeType.Router: "only router can has ospf";
        return String.format("%s-RIP", r_name);
    }

    static public String getRIPIntfName(String intf_name){
        return String.format("%s-rip", intf_name);
    }

    //MULTI:
    //=========ISIS=====================
    static public String getISISDaemonName(String isis_name){
        return String.format("%s-daemon", isis_name);
    }

    static public String getISISIntfName(String intf_name){
        return String.format("%s-isis",intf_name);
    }

    static public String getISISName(String r_name){
        assert getPhyNodeTypeByName(r_name) == NodeType.Router: "only router can has isis";
        return String.format("%s-ISIS", r_name);
    }

    //============BABEl==================
    static public String getBABELName(String r_name){
        assert getPhyNodeTypeByName(r_name) == NodeType.Router: "only router can has ospf";
        return String.format("%s-BABEL", r_name);
    }

    static public String getBABELIntfName(String intf_name){
        return String.format("%s-babel", intf_name);
    }

    //=========OpenFabric=====================
    static public String getOpenFabricName(String r_name){
        assert getPhyNodeTypeByName(r_name) == NodeType.Router: "only router can has openfabric";
        return String.format("%s-OpenFabric", r_name);
    }

    static public String getOpenFabricDaemonName(String openfabric_name){
        return String.format("%s-daemon", openfabric_name);
    }

    static public String getOpenFabricIntfName(String intf_name){
        return String.format("%s-openfabric",intf_name);
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

    public static RIP new_RIP(String name){ return new RIP(name);}

    public static RIPIntf new_RIP_Intf(String name){ return new RIPIntf(name);}
    
    //MULTI:
    public static ISIS new_ISIS(String name){
        return new ISIS(name);
    }

    public static ISISIntf new_ISIS_Intf(String name) {return new ISISIntf(name);}

    public static BABEL new_BABEL(String name){ return new BABEL(name);}

    public static BABELIntf new_BABEL_Intf(String name){ return new BABELIntf(name);}

    public static FABRIC new_OpenFabric(String name){
        return new FABRIC(name);
    }

    public static FABRICIntf new_OpenFabric_Intf(String name) {return new FABRICIntf(name);}


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
            case RIP -> {
                return new_RIP(name);
            }
            case RIPIntf -> {
                return new_RIP_Intf(name);
            }
            //MULTI:
            case ISIS -> {
                return new_ISIS(name);
            }
            case ISISIntf -> {
                return new_ISIS_Intf(name);
            }
            case ISISDaemon -> {
                return new ISISDaemon(name);
            }
            case BABEL -> {
                return new_BABEL(name);
            }

            case BABELIntf -> {
                return new_BABEL_Intf(name);
            }
            case FABRIC -> {
                return new_OpenFabric(name);
            }
            case FABRICIntf -> {
                return new_OpenFabric_Intf(name);
            }
            case FABRICDaemon -> {
                return new FABRICDaemon(name);
            }
            case null, default -> {
                new Unimplemented();
            }
        }
        return null;
    }
}
