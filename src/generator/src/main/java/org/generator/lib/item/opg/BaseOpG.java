package org.generator.lib.item.opg;
import java.util.ArrayList;
import java.util.List;

abstract public class BaseOpG<T> {
    protected BaseOpG(){
        opgroup = new ArrayList<>();
    }
    protected BaseOpG(List<T> ops){
        opgroup = new ArrayList<>();
        addOps(ops);
    }

    public List<T> getOps() {
        return opgroup;
    }

    public void addOp(T op) {
        opgroup.add(op);
    }

    public void addOps(List<T> ops) {
        opgroup.addAll(ops);
    }


    public String toString() {
        return opgroup.toString();
    }

    public void setOpgroup(List<T> opgroup) {
        this.opgroup = opgroup;
    }

    private List<T> opgroup;
}
