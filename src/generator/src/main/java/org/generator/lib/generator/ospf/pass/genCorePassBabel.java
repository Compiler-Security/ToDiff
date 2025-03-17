package org.generator.lib.generator.ospf.pass;

import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.item.conf.edge.RelationEdge;
import org.generator.lib.item.conf.graph.ConfGraph;
import org.generator.lib.item.conf.node.NodeGen;
import org.generator.lib.item.conf.node.babel.BABEL;
import org.generator.lib.item.conf.node.babel.BABELIntf;
import org.generator.lib.item.conf.node.phy.Intf;
import org.generator.lib.item.conf.node.rip.RIPIntf;
import org.generator.lib.item.opg.OpCtxG;
import org.generator.util.net.IPRange;
import org.generator.util.ran.ranHelper;
import org.jetbrains.annotations.NotNull;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class genCorePassBabel extends genCorePass {
    String r_name;
    ConfGraph confg;

    static String toFormattedLinkLocalIPv6(String ipv4) throws UnknownHostException {
        InetAddress ipv4Address = InetAddress.getByName(ipv4);
        byte[] ipv4Bytes = ipv4Address.getAddress();

        // Initialize IPv6 address with FE80::/10
        byte[] ipv6Bytes = new byte[16];
        ipv6Bytes[0] = (byte) 0xFE;
        ipv6Bytes[1] = (byte) 0x80;
        for (int i = 2; i < 8; i++) {
            ipv6Bytes[i] = 0x00;
        }

        // Use part of IPv4 to create "X:X::X:X" format
        ipv6Bytes[8] = (byte) (ipv4Bytes[0] ^ 0xAA);  // Use first IPv4 byte with XOR
        ipv6Bytes[9] = (byte) (ipv4Bytes[1] ^ 0x55);
        ipv6Bytes[10] = 0x00; // Set 0 to force "::"
        ipv6Bytes[11] = 0x00; // Keep "::"
        ipv6Bytes[12] = (byte) (ipv4Bytes[2] ^ 0x33);
        ipv6Bytes[13] = ipv4Bytes[3];
        ipv6Bytes[14] = 0x00; // Keep consistent format
        ipv6Bytes[15] = (byte) (ipv4Bytes[3] ^ 0x77);

        // Convert to IPv6 address
        InetAddress ipv6Address = Inet6Address.getByAddress(ipv6Bytes);
        return ipv6Address.getHostAddress().replace("::0", "::") + "/64";
    }

    private OpCtxG handleNetwork(){
        var opCtxg = OpCtxG.Of();
        addOp(opCtxg, OpType.RBABEL);
        //FIXME we should merge multiple interface to one
        for(var babel_intf: confg.getBABELIntfOfRouter(r_name)){
            Intf intf = (Intf) confg.getDstsByType(babel_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
            if (!confg.containsNode(NodeGen.getBABELIntfName(intf.getName()))){
                continue;
            }
            var op = addOp(opCtxg, OpType.BNETWORKINTF);
            op.setNAME(intf.getName());
        }
        return opCtxg;
    }

    OpCtxG handleRouter(){
        var opCtxg = OpCtxG.Of();
        BABEL babel = confg.getBABELOfRouter(r_name);
        if (babel == null) return opCtxg;
        addOp(opCtxg, OpType.RBABEL);
        {
            var op = addOp(opCtxg, OpType.BRESENDDELAY);
            op.setNUM(babel.getResendDelay());
        }
        {
            var op = addOp(opCtxg, OpType.BSOMMOTHING);
            op.setNUM(babel.getSmoothing());
        }
        {
            var op = addOp(opCtxg, OpType.BREDISTRIBUTE);
        }
        return opCtxg;
    }
    OpCtxG handleIntf(BABELIntf babel_intf){
        var opCtxG = OpCtxG.Of();
        Intf intf = (Intf) confg.getDstsByType(babel_intf.getName(), RelationEdge.EdgeType.INTF).stream().findAny().get();
        {
            var op = addOp(opCtxG, OpType.IntfName);
            op.setNAME(intf.getName());
        }
        {
            var op = addOp(opCtxG, OpType.BWIRE);
            if (babel_intf.isWired()){
                op.setNAME("wired");
            }else{
                op.setNAME("wireless");
            }
        }
        {
            if (!babel_intf.isSplitHorizon()) {
                var op = addOp(opCtxG, OpType.BSPLITHORIZON);
            }
        }
        {
            var op = addOp(opCtxG, OpType.BHELLOINTERVAL);
            op.setNUM(babel_intf.getHelloInterval());
        }
        {
            var op = addOp(opCtxG, OpType.BUPDATEINTERVAL);
            op.setNUM(babel_intf.getUpdateInterval());
        }
        {
            var op = addOp(opCtxG, OpType.BCHANELNOINTEFERING);
            if (babel_intf.isNointerfering()){
                op.setNAME("noninterfering");
            }else{
                op.setNAME("interfering");
            }
        }
        {
            var op = addOp(opCtxG, OpType.BRXCOST);
            op.setNUM(babel_intf.getRxcost());
        }
        {
            var op = addOp(opCtxG, OpType.BRTTDECAY);
            op.setNUM(babel_intf.getRttDecay());
        }
        {
            var op = addOp(opCtxG, OpType.BRTTMIN);
            op.setNUM(babel_intf.getRttMin());
        }
        {
            var op = addOp(opCtxG, OpType.BRTTMAX);
            op.setNUM(babel_intf.getRttMax());
        }
        {
            var op = addOp(opCtxG, OpType.BPENALTY);
            op.setNUM(babel_intf.getPenalty());
        }
        {
            if (babel_intf.isTimeStamps()){
                var op = addOp(opCtxG, OpType.BENABLETIMESTAMP);
            }
        }
        return opCtxG;
    }

    private List<OpCtxG> handleIntfs(){
        List<OpCtxG> opgs = new ArrayList<>();
        for(var intf: confg.getIntfsOfRouter(r_name)){
            if (intf.getIp() == null) continue;
            var opCtxG = OpCtxG.Of();
            {
                var op = addOp(opCtxG, OpType.IntfName);
                op.setNAME(intf.getName());
            }
            {
                var op = addOp(opCtxG, OpType.IPAddr);
                op.setIP(intf.getIp());
            }
            {
                var op = addOp(opCtxG, OpType.IPAddr6);
                try {
                    op.setNAME(toFormattedLinkLocalIPv6(intf.getIp().getAddress()));
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }
            opgs.add(opCtxG);
        }
        for(var babel_intf: confg.getBABELIntfOfRouter(r_name)){
            opgs.add(handleIntf(babel_intf));
        }
        return opgs;
    }
    @Override
    public List<OpCtxG> solve(ConfGraph confg, boolean ismissinglevel) {
        this.r_name = confg.getR_name();
        this.confg = confg;
        List<OpCtxG> opgs = new ArrayList<>();

        //intfs
        opgs.addAll(handleIntfs());

        opgs.add(handleRouter());
        //network
        opgs.add(handleNetwork());
        return opgs;
    }
}
