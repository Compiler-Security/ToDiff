package org.generator.topo.node;

public abstract class AbstractNode {

    public String getName(){
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    private NodeType nodeType;

    abstract public void initFiled();

    public abstract String getNodeAtrriStr();

    @Override
    public String toString() {
        return getName();
    }
}
