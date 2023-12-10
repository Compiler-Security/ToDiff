package org.generator.operation.op;
import org.generator.util.net.IPV4;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class Operation extends StrOperation {
    public Operation(OpType type) {
        super(type);
        this.IP = null;
        this.IP2 = null;
        this.ID = null;
        this.NAME = null;
        this.NAME2 = null;
        this.NUM = null;
        this.NUM2 = null;
        this.NUM3 = null;
        this.IDNUM = null;
        setCtxOp(null);
    }

    public StrOperation AbstractOp(){
        store_to_dynamic();
        return (StrOperation) this;
    }

    public Operation getMinimalUnsetOp(){
        var ori_index = getUnsetIndex();
        var ori_unset = unset;
        setUnsetIndex(0);
        setUnset(true);
        var mini = new Operation(Type());
        mini.decode(toString());
        mini.setCtxOp(getCtxOp());
        setUnsetIndex(ori_index);
        setUnset(ori_unset);
        return mini;
    }

    @Override
    /* equals we don't compare ctx!*/
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        if (this.isUnset() != operation.isUnset()) return false;
        if (!this.isUnset()){
            return this.toString().equals(operation.toString());
        }else{
            return this.getMinimalUnsetOp().toString().equals(operation.getMinimalUnsetOp().toString());
        }
    }

    @Override
    public int hashCode() {
        if (!this.isUnset()){
            return Objects.hash(this.toString());
        }else{
            return Objects.hash(this.getMinimalUnsetOp().toString());
        }
    }

    public Operation cloneOfType(OpType typ){
        var op = new Operation(typ);
        op.NAME = NAME;
        op.NAME2 = NAME2;
        op.DETAIL = DETAIL;
        if (IP != null) op.IP = IP.clone();
        else op.IP = null;
        if (IP2 != null) op.IP2 = IP2.clone();
        else op.IP2 = null;
        if (ID != null) op.ID = ID.clone();
        else op.ID = null;
        op.IDNUM = IDNUM;
        op.NUM = NUM;
        op.NUM2 = NUM2;
        op.NUM3 = NUM3;
        op.unset = unset;
        op.unsetIndex = unsetIndex;
        op.store_to_dynamic();
        //FIXME we don't clone ctxOp
        return op;
    }

    private boolean load_from_dynamic(){
        var m = super.Args();
        if (m.containsKey("NAME")) NAME = super.Arg("NAME"); else NAME = null;
        if (m.containsKey("NAME2")) NAME2 = super.Arg("NAME2"); else NAME2 = null;
        if (m.containsKey("DETAIL")) DETAIL = super.Arg("DETAIL"); else DETAIL = null;
        if (m.containsKey("IP")){
            IP = super.IPArg(super.Arg("IP"));
            if (IP == null) return false;
        }else IP = null;
        if (m.containsKey("IP2")){
            IP2 = super.IPArg(super.Arg("IP2"));
            if (IP2 == null) return false;
        } else IP2 = null;
        if (m.containsKey("ID")){ ID = super.IDArg(super.Arg("ID"));
            if (ID == null) return false;
        }
        else ID = null;
        try {
            if (m.containsKey("IDNUM")) IDNUM = super.LongArg("IDNUM");
            else IDNUM = null;
            if (m.containsKey("NUM")) NUM = super.IntArg("NUM");
            else NUM = null;
            if (m.containsKey("NUM2")) NUM2 = super.IntArg("NUM2");
            else NUM2 = null;
            if (m.containsKey("NUM3")) NUM3 = super.IntArg("NUM3");
            else NUM3 = null;
        }catch (NumberFormatException e){
            return false;
        }
        return true;
    }
    @Override
    public boolean decode(String st) {
        if (!Type().isToMatch()) return false;
        if (super.decode(st)){
            return load_from_dynamic();
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
        if (IDNUM != null) super.putLongArg("IDNUM", IDNUM);
        if (NUM != null) super.putIntArg("NUM", NUM);
        if (NUM2 != null) super.putIntArg("NUM2", NUM2);
        if (NUM3 != null) super.putIntArg("NUM3", NUM3);
    }
    @Override
    public boolean encode(StringBuilder buf) {
        store_to_dynamic();
        return super.encode(buf);
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
//    @Override
//    public String Arg(String field_name) {
//        store_to_dynamic();
//        return super.Arg(field_name);
//    }
//
//    @Override
//    public int IntArg(String field_name) {
//        store_to_dynamic();
//        return super.IntArg(field_name);
//    }
//
//    @Override
//    public double DoubleArg(String field_name) {
//        store_to_dynamic();
//        return super.DoubleArg(field_name);
//    }
//
//    @Override
//    public IPV4 IPArg(String field_name) {
//        store_to_dynamic();
//        return super.IPArg(field_name);
//    }


    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
        store_to_dynamic();
    }

    public String getNAME2() {
        return NAME2;
    }

    public void setNAME2(String NAME2) {
        this.NAME2 = NAME2;
        store_to_dynamic();
    }

    public String getDETAIL() {
        return DETAIL;
    }

    public void setDETAIL(String DETAIL) {
        this.DETAIL = DETAIL;
        store_to_dynamic();
    }

    public IPV4 getIP() {
        return IP;
    }

    public void setIP(IPV4 IP) {
        this.IP = IP;
        store_to_dynamic();
    }

    public IPV4 getID() {
        return ID;
    }

    public void setID(IPV4 ID) {
        this.ID = ID;
        store_to_dynamic();
    }

    public Long getIDNUM() {
        return IDNUM;
    }

    public void setIDNUM(Long IDNUM) {
        this.IDNUM = IDNUM;
        store_to_dynamic();
    }

    public Integer getNUM() {
        return NUM;
    }

    public void setNUM(Integer NUM) {
        this.NUM = NUM;
        store_to_dynamic();
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
        store_to_dynamic();
    }

    protected IPV4 IP2;
    protected Long IDNUM;
    protected Integer NUM;

    public Integer getNUM2() {
        return NUM2;
    }

    public void setNUM2(Integer NUM2) {
        this.NUM2 = NUM2;
        store_to_dynamic();
    }

    public Integer getNUM3() {
        return NUM3;
    }

    public void setNUM3(Integer NUM3) {
        this.NUM3 = NUM3;
        store_to_dynamic();
    }

    protected Integer NUM2;
    protected Integer NUM3;

    //FIXME we should move this to operationCtx instead of use it in operation
    public Operation getCtxOp() {
        return ctxOp;
    }

    public void setCtxOp(Operation ctxOp) {
        this.ctxOp = ctxOp;
    }

    Operation ctxOp;

    @Override
    public String toString() {
        store_to_dynamic();
        return super.toString();
    }

    @Override
    public String toString(int index){
        store_to_dynamic();
        return super.toString(index);
    }

    @SafeVarargs
    public final Operation checkOrInvalid(Predicate<Operation>... predicates){
        for(var predicate: predicates){
            if (!predicate.test(this)) return new Operation(OpType.INVALID);
        }
        return this;
    }
}
