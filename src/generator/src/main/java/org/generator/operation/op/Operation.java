package org.generator.operation.op;
import org.generator.util.net.IPV4;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Operation extends AbstractOperation {
    public Operation(OpType type) {
        super(type);
    }

    public AbstractOperation AbstractOp(){
        store_to_dynamic();
        return (AbstractOperation) this;
    }
    private void load_from_dynamic(){
        var m = super.Args();
        if (m.containsKey("NAME")) NAME = super.Arg("NAME");
        if (m.containsKey("NAME2")) NAME2 = super.Arg("NAME2");
        if (m.containsKey("DETAIL")) DETAIL = super.Arg("DETAIL");
        if (m.containsKey("IP")) IP = new IPV4(super.Arg("IP"));
        if (m.containsKey("IP2")) IP = new IPV4(super.Arg("IP2"));
        if (m.containsKey("ID")) ID = new IPV4(super.Arg("ID"));
        if (m.containsKey("IDNUM")) IDNUM = super.IntArg("IDNUM");
        if (m.containsKey("NUM")) NUM = super.IntArg("NUM");
        if (m.containsKey("NUM2")) NUM2 = super.IntArg("NUM2");
        if (m.containsKey("NUM3")) NUM3 = super.IntArg("NUM3");
    }
    @Override
    public boolean decode(String st) {
        if (super.decode(st)){
            load_from_dynamic();
            return true;
        }
        return false;
    }

    private  void store_to_dynamic(){
        if (NAME != null) super.putArg("NAME", NAME);
        if (NAME2 != null) super.putArg("NAME2", NAME2);
        if (DETAIL != null) super.putArg("DETAIL", DETAIL);
        if (IP != null) super.putIpArg("IP", IP);
        if (IP2 != null) super.putIpArg("IP2", IP2);
        if (ID != null) super.putIpArg("ID", ID);
        if (IDNUM != null) super.putIntArg("IDNUM", IDNUM);
        if (NUM != null) super.putIntArg("NUM", NUM);
        if (NUM2 != null) super.putIntArg("NUM2", NUM2);
        if (NUM3 != null) super.putIntArg("NUM3", NUM3);
    }
    @Override
    public void encode(StringBuilder buf) {
        store_to_dynamic();
        super.encode(buf);
    }

    @Override
    public void putArgs(Map<String, String> args) {
        super.putArgs(args);
        load_from_dynamic();
    }

    @Override
    public Map<String, String> Args() {
        store_to_dynamic();
        return super.Args();
    }

    @Override
    public void putIntArg(String field_name, Integer val) {
        super.putIntArg(field_name, val);
        load_from_dynamic();
    }

    @Override
    public void putDoubleArg(String field_name, Double val) {
        super.putDoubleArg(field_name, val);
        load_from_dynamic();
    }

    @Override
    public void putIpArg(String field_name, @NotNull IPV4 ip) {
        super.putIpArg(field_name, ip);
        load_from_dynamic();
    }
    @Override
    public String Arg(String field_name) {
        store_to_dynamic();
        return super.Arg(field_name);
    }

    @Override
    public int IntArg(String field_name) {
        store_to_dynamic();
        return super.IntArg(field_name);
    }

    @Override
    public double DoubleArg(String field_name) {
        store_to_dynamic();
        return super.DoubleArg(field_name);
    }

    @Override
    public IPV4 IPArg(String field_name) {
        store_to_dynamic();
        return super.IPArg(field_name);
    }


    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getNAME2() {
        return NAME2;
    }

    public void setNAME2(String NAME2) {
        this.NAME2 = NAME2;
    }

    public String getDETAIL() {
        return DETAIL;
    }

    public void setDETAIL(String DETAIL) {
        this.DETAIL = DETAIL;
    }

    public IPV4 getIP() {
        return IP;
    }

    public void setIP(IPV4 IP) {
        this.IP = IP;
    }

    public IPV4 getID() {
        return ID;
    }

    public void setID(IPV4 ID) {
        this.ID = ID;
    }

    public Integer getIDNUM() {
        return IDNUM;
    }

    public void setIDNUM(Integer IDNUM) {
        this.IDNUM = IDNUM;
    }

    public Integer getNUM() {
        return NUM;
    }

    public void setNUM(Integer NUM) {
        this.NUM = NUM;
    }

    protected String NAME, NAME2;
    protected String DETAIL;
    protected IPV4 IP;
    protected IPV4 ID;

    public IPV4 getIP2() {
        return IP2;
    }

    public void setIP2(IPV4 IP2) {
        this.IP2 = IP2;
    }

    protected IPV4 IP2;
    protected Integer IDNUM;
    protected Integer NUM;

    public Integer getNUM2() {
        return NUM2;
    }

    public void setNUM2(Integer NUM2) {
        this.NUM2 = NUM2;
    }

    public Integer getNUM3() {
        return NUM3;
    }

    public void setNUM3(Integer NUM3) {
        this.NUM3 = NUM3;
    }

    protected Integer NUM2;
    protected Integer NUM3;
}
