package org.generator.topo.node;

public abstract class TopoNode {

    public int getId(){
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
}
