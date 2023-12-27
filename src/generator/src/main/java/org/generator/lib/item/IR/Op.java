package org.generator.lib.item.IR;

import org.generator.lib.operation.operation.OpType;
import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;

public interface Op {
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


    public OpCtx getOpCtx();

    public void setOpCtx(OpCtx opCtx);

    public OpType Type();

    public void setType(OpType type);
}
