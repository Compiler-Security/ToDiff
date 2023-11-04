package org.generator.operation;
import org.generator.util.net.IPV4;

import java.util.Map;

public class StaticOp extends DynamicOp{
    public StaticOp(String template, OpType type) {
        super(template, type);
    }

    public DynamicOp getStaticOp(){
        store_to_dynamic();
        return (DynamicOp) this;
    }
    private void load_from_dynamic(){
        NAME = getArg("NAME");
        NAME2 = getArg("NAME2");
        DETAIL = getArg("DETAIL");
        IP = getIPArg("IP");
        ID = getIPArg("ID");
        IDNUM = getIntArg("IDNUM");
        NUM = getIntArg("NUM");
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
        setArg("NAME", NAME);
        setArg("NAME2", NAME2);
        setArg("DETAIL", DETAIL);
        setIpArg("IP", IP);
        setIpArg("ID", ID);
        setIntArg("IDNUM", IDNUM);
        setIntArg("NUM", NUM);
    }
    @Override
    public void encode(StringBuilder buf) {
        store_to_dynamic();
        super.encode(buf);
    }

    @Override
    public void setArgs(Map<String, String> args) {
        super.setArgs(args);
        load_from_dynamic();
    }

    @Override
    public Map<String, String> getArgs() {
        store_to_dynamic();
        return super.getArgs();
    }

    @Override
    public void setIntArg(String field_name, int val) {
        super.setIntArg(field_name, val);
        load_from_dynamic();
    }

    @Override
    public void setDoubleArg(String field_name, double val) {
        super.setDoubleArg(field_name, val);
        load_from_dynamic();
    }

    @Override
    public void setIpArg(String field_name, IPV4 ip) {
        super.setIpArg(field_name, ip);
        load_from_dynamic();
    }
    @Override
    public String getArg(String field_name) {
        store_to_dynamic();
        return super.getArg(field_name);
    }

    @Override
    public int getIntArg(String field_name) {
        store_to_dynamic();
        return super.getIntArg(field_name);
    }

    @Override
    public double getDoubleArg(String field_name) {
        store_to_dynamic();
        return super.getDoubleArg(field_name);
    }

    @Override
    public IPV4 getIPArg(String field_name) {
        store_to_dynamic();
        return super.getIPArg(field_name);
    }



    public String NAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String NAME2() {
        return NAME2;
    }

    public void setNAME2(String NAME2) {
        this.NAME2 = NAME2;
    }

    public String DETAIL() {
        return DETAIL;
    }

    public void setDETAIL(String DETAIL) {
        this.DETAIL = DETAIL;
    }

    public IPV4 IP() {
        return IP;
    }

    public void setIP(IPV4 IP) {
        this.IP = IP;
    }

    public IPV4 ID() {
        return ID;
    }

    public void setID(IPV4 ID) {
        this.ID = ID;
    }

    public int IDNUM() {
        return IDNUM;
    }

    public void setIDNUM(int IDNUM) {
        this.IDNUM = IDNUM;
    }

    public int NUM() {
        return NUM;
    }

    public void setNUM(int NUM) {
        this.NUM = NUM;
    }

    protected String NAME, NAME2;
    protected String DETAIL;
    protected IPV4 IP, ID;
    protected int IDNUM, NUM;
}
