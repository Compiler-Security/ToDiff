package org.generator.lib.item.opg;
import org.generator.lib.item.IR.OpCtx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract public class BaseOpG<T> implements Iterable<T> {
    protected BaseOpG(){
        opgroup = new ArrayList<>();
    }
    protected BaseOpG(List<T> ops){
        opgroup = new ArrayList<>();
        addOps(ops);
    }

    //public void reset(List<T> ops) {opgroup.clear();opgroup.addAll(ops);}
    public List<T> getOps() {
        return opgroup;
    }

    public void addOp(T op) {
        opgroup.add(op);
    }

    public void addOps(List<T> ops) {
        opgroup.addAll(ops);
    }


    public void setOpgroup(List<T> opgroup) {
        this.opgroup = opgroup;
    }

    private List<T> opgroup;

    @Override
    public Iterator<T> iterator(){
        return opgroup.iterator();
    }
}
