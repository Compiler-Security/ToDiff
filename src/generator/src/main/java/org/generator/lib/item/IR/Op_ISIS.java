package org.generator.lib.item.IR;

import org.generator.lib.frontend.lexical.OpType_isis;
import org.generator.util.net.IPRange;

public interface Op_ISIS {
    public String getNAME();

    public String getNAME2();

    public void setNAME2(String NAME2);

    public org.generator.util.net.IP getIP();

    public void setIP(org.generator.util.net.IP IP);

    public org.generator.util.net.ID getID();

    public void setID(org.generator.util.net.ID ID);

    public IPRange getIPRANGE();

    public void setIPRANGE(IPRange IPRANGE);

    public Integer getNUM();

    public void setNUM(Integer NUM);

    public Integer getNUM2();

    public void setNUM2(Integer NUM2);
    public Integer getNUM3();

    public void setNUM3(Integer NUM3);

    public Long getLONGNUM();

    public void setLONGNUM(Long LONGNUM);

    public void setNAME(String NAME);


    public OpCtx_ISIS getOpCtx();

    public void setOpCtx(OpCtx_ISIS opCtx);

    public OpType_isis Type();

    public void setType(OpType_isis type);
}
