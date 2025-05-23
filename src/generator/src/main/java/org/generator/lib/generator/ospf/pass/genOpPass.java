package org.generator.lib.generator.ospf.pass;

import org.generator.lib.frontend.lexical.LexDef;
import org.generator.lib.frontend.lexical.OpType;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.item.IR.Op;
import org.generator.lib.item.IR.OpCtx;
import org.generator.lib.item.IR.OpOspf;
import org.generator.util.collections.Pair;
import org.generator.util.ran.ranHelper;

import java.util.List;
import java.util.Map;

public class genOpPass {
//    private static final List<OpType> intfOps, OspfOps;
//    static {
//        intfOps = new ArrayList<>();
//        OspfOps = new ArrayList<>();
//        for (var op_type: OpType.values()){
//            //FIXME areaVLINK
//            //if (op_type == OpType.AreaVLink) continue;
//            if (op_type.inOSPFINTF()){
//                intfOps.add(op_type); }
//            else if (op_type.inOSPFAREA() || op_type.inOSPFDAEMON() || op_type.inOSPFRouterWithTopo()){
//                OspfOps.add(op_type);
//            }
//        }
//    }
    private static  int getRanIntNum(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof Pair<?,?> p){
            return ranHelper.randomInt((int)((Long) p.first() + 0), (int) ((Long) p.second() + 0));
        }else return ranHelper.randomInt(0, 2100000000);
    }

    private static  int getRanNoIntNum(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof Pair<?,?> p) {
            if (ranHelper.randomInt(0, 1) == 0) {
                return ranHelper.randomInt(-2100000000, (int) ((Long) p.first() - 1));
            } else return ranHelper.randomInt((int) ((Long) p.second() + 1), 2100000000);
        }else return ranHelper.randomInt(0, 2100000000);
    }

    private static long getRanLongNum(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof Pair<?,?> p){
            return ranHelper.randomLong((long) p.first(), (long) p.second());
        }else return ranHelper.randomLong(0, 0xfffffffffL);
    }

    private static long getRanNoLongNum(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof Pair<?,?> p){
            if (ranHelper.randomInt(0, 1) == 0) {
                return ranHelper.randomLong(-210000000000L, (long) p.first() - 1);
            } else return ranHelper.randomLong((long) p.second() + 1, 5L * (long)p.second());
        }else return ranHelper.randomLong(0, 0xfffffffffL);
    }

    private static String getRanName(Map<String, Object> argRange, String field){
        if (argRange.containsKey(field) && argRange.get(field) instanceof List<?> p && !p.isEmpty()){
            return (String) p.get(ranHelper.randomInt(0, p.size() - 1));
        }else return ranHelper.randomStr();
    }

    public static OpCtx genRanOpOfType(OpType type){
        int chose_idx = 0;
        if (generate.ran){
            chose_idx = ranHelper.randomInt(0, LexDef.getLexDefNum(type) - 1);
        }
        var gen_op = OpCtx.of(type, chose_idx);
        var new_op = (OpOspf) gen_op.getOperation();
        var lexDef = gen_op.getFormmat().getLexDef();
        var args = lexDef.Args;
        var argsRange = lexDef.ArgsRange;
        for(var arg: args) {
            switch (arg) {
                case "ID" -> {
                    new_op.setID(ranHelper.randomID());
                }
                case "IPRANGE" -> {
                    new_op.setIPRANGE(ranHelper.randomIpRange());
                }
                case "IP" -> {
                    new_op.setIP(ranHelper.randomIP());
                }
                case "NUM" -> {
                    new_op.setNUM(getRanIntNum(argsRange, "NUM"));
                }
                case "NUM2" -> {
                    new_op.setNUM2(getRanIntNum(argsRange, "NUM2"));
                }
                case "NUM3" -> {
                    new_op.setNUM3(getRanIntNum(argsRange, "NUM3"));
                }
                case "LONGNUM" -> {
                    new_op.setLONGNUM(getRanLongNum(argsRange, "LONGNUM"));
                }
                //FIXME random NAME we should generate some meaningful name
                case "NAME" -> {
                    new_op.setNAME(getRanName(argsRange, "NAME"));
                }
                case "NAME2" -> {
                    new_op.setNAME(getRanName(argsRange, "NAME2"));
                }
                case "NET" -> {
                    new_op.setNET(ranHelper.randomNet());
                }
                //case "NUM" -> {new_op.setNUM(argsRange.get("NUM"));}
                default -> {
                    assert false : "%s mutate TODO %s".formatted(type, arg);
                }
            }
        }
        return gen_op;
    }
    public static OpCtx genRanOpByControl(boolean inIntf){
        OpType op_type;
        List<OpType> intfOps, RouterOps;
        intfOps = OpType.getIntfSetOps();
        RouterOps = OpType.getRouterSetOps();
        if (generate.protocol == generate.Protocol.ISIS){
            intfOps.remove(OpType.IPROUTERISIS);
            RouterOps.remove(OpType.NET);
            RouterOps.remove(OpType.ISTYPE);
        }
        if (generate.protocol == generate.Protocol.OpenFabric){
            intfOps.remove(OpType.IPROUTERFABRIC);
            RouterOps.remove(OpType.NET);
        }
        if (inIntf){
            op_type = intfOps.get(ranHelper.randomInt(0, intfOps.size() - 1));
        }else  {
            op_type = RouterOps.get(ranHelper.randomInt(0, RouterOps.size() - 1));
        }
        return genRanOpOfType(op_type);
    }

    public static void copyFileds(Op dst_op, Op src_op, List<String> argList){
        for(var arg: argList){
            switch (arg) {
                case "ID" -> {
                    dst_op.setID(src_op.getID());
                }
                case "IPRANGE" -> {
                    dst_op.setIPRANGE(src_op.getIPRANGE());
                }
                case "IP" -> {
                    dst_op.setIP(src_op.getIP());
                }
                case "NET" -> {
                    dst_op.setNET(src_op.getNET());
                }
                case "NUM" -> {
                    dst_op.setNUM(src_op.getNUM());
                }
                case "NUM2" -> {
                    dst_op.setNUM2(src_op.getNUM2());
                }
                case "NUM3" -> {
                    dst_op.setNUM3(src_op.getNUM3());
                }
                case "LONGNUM" -> {
                    dst_op.setLONGNUM(src_op.getLONGNUM());
                }
                case "NAME" -> {
                    dst_op.setNAME(src_op.getNAME());
                }
                case "NAME2" -> {
                    dst_op.setNAME(src_op.getNAME2());
                }
                default -> {
                    assert false : "mutate TODO";
                }
            }
        }
    }
}
