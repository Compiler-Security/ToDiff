package org.generator.topo.node;

public abstract class TopoNode {

    public String getName(){
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public String toString() {
        return getName();
    }
}
