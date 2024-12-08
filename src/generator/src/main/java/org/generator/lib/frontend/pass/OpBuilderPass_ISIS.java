package org.generator.lib.frontend.pass;

import org.generator.lib.item.IR.Op_ISIS;
import org.generator.lib.item.IR.OpCtx_ISIS;
import org.generator.lib.frontend.lexical.LexCtx_ISIS;
import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;
import org.generator.util.net.NET;
import org.jetbrains.annotations.Nullable;


public class OpBuilderPass_ISIS {

    private String Arg(String name){
        return ctx.tokenMap.getOrDefault(name, null);
    }


    private OpCtx_ISIS setOpCtx(){
        //set formmat
        opctx.format.setLexDef(ctx.lexDef);

        //start set op
        var op = opctx.getOperation();
        //set op type
        op.setType(ctx.opType);

        //set op field
        var opType = ctx.opType;
        var lexDef = ctx.lexDef;
        var m = ctx.tokenMap;

        //check NAME NAME2
        if (m.containsKey("NAME")){
            op.setNAME(Arg("NAME"));
            //check range
            var res = lexDef.getStrListRange("NAME");
            if (res != null && !res.contains(op.getNAME())) return null;
        }
        if (m.containsKey("NAME2")){
            op.setNAME2(Arg("NAME2"));
            //check range
            var res = lexDef.getStrListRange("NAME2");
            if (res != null && !res.contains(op.getNAME2())) return null;
        }

        //check ID IP IPRANGE
        if (m.containsKey("ID")){
            var res = lexDef.getBoolRange("ID");
            if (res != null && res){
                try{
                    var idnum = Long.parseLong(Arg("ID"));
                    op.setID(ID.of(idnum));
                    opctx.format.setIDISNUM(true);
                }catch (NumberFormatException e){
                    op.setID(ID.of(Arg("ID")));
                    opctx.format.setIDISNUM(false);
                }
            }else{
                op.setID(ID.of(Arg("ID")));
                opctx.format.setIDISNUM(false);
            }
            //check range
            if (op.getID() == null) return null;
        }

        if (m.containsKey("IPRANGE")){
            op.setIPRANGE(IPRange.of(Arg("IPRANGE")));
            //check range
            if (op.getIPRANGE() == null) return null;
        }

        if (m.containsKey("IP")){
            op.setIP(IP.of(Arg("IP")));
            //check range
            if (op.getIP() == null) return null;
        }

        //check NET
        if (m.containsKey("NET")){
            op.setNET(NET.of(Arg("NET")));
            //check range
            if (op.getNET() == null) return null;
        }

        //check NUM NUM2 NUM3 LONGNUM
        try {
            if (m.containsKey("NUM")) {
                op.setNUM(Integer.parseInt(Arg("NUM")));
                var res = lexDef.getNumRange("NUM");
                var NUM = op.getNUM();
                //check range
                if (res  != null && !(res.first() <= NUM && NUM <= res.second())) return null;
            }

            if (m.containsKey("NUM2")) {
                op.setNUM2(Integer.parseInt(Arg("NUM2")));
                var res = lexDef.getNumRange("NUM2");
                var NUM = op.getNUM2();
                //check range
                if (res  != null && !(res.first() <= NUM && NUM <= res.second())) return null;
            }

            if (m.containsKey("NUM3")) {
                op.setNUM3(Integer.parseInt(Arg("NUM3")));
                var res = lexDef.getNumRange("NUM3");
                var NUM = op.getNUM3();
                //check range
                if (res  != null && !(res.first() <= NUM && NUM <= res.second())) return null;
            }

            if (m.containsKey("LONGNUM")) {
                op.setLONGNUM(Long.parseLong(Arg("LONGNUM")));
                var res = lexDef.getNumRange("LONGNUM");
                var NUM = op.getLONGNUM();
                //check range
                if (res  != null && !(res.first() <= NUM && NUM <= res.second())) return null;
            }

        }catch (NumberFormatException e){
            return null;
        }
        return opctx;
    }

    /** this will fill op from lexCtx and return OpCtx, if it's failed to check range, we will just return null
     * ctx or op null will return null
     * */
    @Nullable public OpCtx_ISIS solve(@Nullable LexCtx_ISIS  ctx, @Nullable Op_ISIS op){
        if (ctx == null) return null;
        if (op == null) return null;
        this.ctx = ctx;
        this.opctx = OpCtx_ISIS.of(op, 0);
        return setOpCtx();
    }
    LexCtx_ISIS ctx;
    OpCtx_ISIS opctx;
}
