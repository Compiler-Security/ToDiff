package org.generator.lib.item.conf.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class AbstractNode_ISIS {

    public String getName(){
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public NodeType_ISIS getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType_ISIS nodeType) {
        this.nodeType = nodeType;
    }

    private NodeType_ISIS nodeType;

    abstract public void initFiled();

    public String getNodeAtrriStr(){
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        Class<?> clazz = this.getClass();
        for(var field : clazz.getDeclaredFields()){
            var key = field.getName();
            field.setAccessible(true);
            try {
                var val = field.get(this);
                builder.append(String.format("\t%s:%s,\n", key, val));
            }catch (Exception e){
                assert false: e;
            }
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    public ObjectNode getJsonNode(){
        ObjectNode jsonObject = new ObjectMapper().createObjectNode();
        Class<?> clazz = this.getClass();
        for(var field : clazz.getDeclaredFields()){
            var key = field.getName();
            field.setAccessible(true);
            try {
                var val = field.get(this);
                jsonObject.put(key, String.format("%s", val));
            }catch (Exception e){
                assert false: e;
            }
        }
        return jsonObject;
    }
}
