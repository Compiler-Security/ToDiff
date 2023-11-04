package org.generator.operation;

import java.util.Map;

public interface operation {
    boolean decode(String st);
    void encode(StringBuilder buf);
    String encode_to_str();
    OpType getType();
    Map<String, String> getFields();

    void setField(String field_name, String val);

    default void setIntField(String field_name, int val){
        setField(field_name, String.valueOf(val));
    }

    default void setDoubleField(String field_name, double val){
        setField(field_name, String.valueOf(val));
    }

    default String getField(String field_name){
        return getFields().get(field_name);
    }

    default int getIntField(String field_name){
        return Integer.parseInt(getField(field_name));
    }

    default double getDoubleField(String field_name){
        return Double.parseDouble(field_name);
    }
}
