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

    public String getNodeAtrriStr(){
        StringBuilder builder = new StringBuilder();
        Class<?> clazz = this.getClass();
        for(var field : clazz.getDeclaredFields()){
            var key = field.getName();
            field.setAccessible(true);
            try {
                var val = field.get(this);
                builder.append(String.format("%s : %s, ", key, val));
            }catch (Exception e){
                assert false: e;
            }
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return getName();
    }
}
